package com.unitec.kitbox.ui.capture;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unitec.kitbox.MainActivity;
import com.unitec.kitbox.R;
import com.unitec.kitbox.common.CommonFragment;
import com.unitec.kitbox.models.ShareItem;
import com.unitec.kitbox.models.SiteModel;
import com.unitec.kitbox.tensorflowlite.Classifier;
import com.unitec.kitbox.tensorflowlite.TensorFlowImageClassifier;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;

import static android.content.Context.LOCATION_SERVICE;

public class CaptureFragment extends CommonFragment {

    private static String TAG = "CaptureFragment";

    private CaptureViewModel mCaptureViewModel;

    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();
    private Timestamp ExpireDate;
    // Views
    private TextView textViewResult;
    private Button btnDetectObject, btnSubmitItem;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private TextView textViewExpiredDate;
    private TextView textViewSiteName;
    private TextView textViewObject;
    private TextView textViewAddress;
    private TextView textViewObjectCount;
    private TextView textViewLongitude;
    private TextView textViewLatitude;


    // DatePickerDialog
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    // Location
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private LatLng mLatLng;
    private GeoPoint mGeoPoint;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = false;
    private static final String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 9001;
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL = 9002;

    // Firebase
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private StorageReference imageRef;
    private static FirebaseUser currentUser;
    private String objectPicUrl;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_capture, container, false);

        cameraView = root.findViewById(R.id.cameraView);
        imageViewResult = root.findViewById(R.id.imageViewResult);
        textViewResult = root.findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());
        textViewExpiredDate = root.findViewById(R.id.expiredDateTextView);
        textViewSiteName = root.findViewById(R.id.siteNameTextView);
        textViewAddress = root.findViewById(R.id.addressTextView);
        textViewObject = root.findViewById(R.id.objectTextView);
        textViewObjectCount = root.findViewById(R.id.objectCountTextView);
        textViewLongitude = root.findViewById(R.id.longitudeTextView);
        textViewLatitude = root.findViewById(R.id.latitudeTextView);


        btnSubmitItem = root.findViewById(R.id.btnSubmitItem);
        btnDetectObject = root.findViewById(R.id.btnDetectObject);

        // for firebase storage
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        // Expired date
        textViewExpiredDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                textViewExpiredDate.setText(date);
            }
        };

        // Get Location
        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        boolean networkLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "NWLE" + String.valueOf(networkLocationEnabled));
        boolean gpsLocationEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "PGSL"+String.valueOf(gpsLocationEnabled));

        mLocationListener = new LocationListener() {


            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, String.valueOf(location));
                updateUILocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Camera capture image and run TensorFlowLite to detect the image

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
                Log.d(TAG, results.toString());
                textViewResult.setText(results.toString());
                String str = results.toString().replaceAll("[^a-z^A-Z^]", "");
                textViewObject.setText(str);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        // Active the Camera and start the addCameraKitListener and get the User location

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    cameraView.captureImage();
                }catch (Exception ex){
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // Get user location

                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                getLocationPermission();

                if (mLocationPermissionGranted) {

                    Log.d(TAG, "Location permission approved");

                    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

                    mFusedLocationProviderClient.getLocationAvailability().addOnSuccessListener(getActivity(), new OnSuccessListener<LocationAvailability>() {
                        @Override
                        public void onSuccess(LocationAvailability locationAvailability) {
                            Log.d(TAG, "onSuccess: locationAvailability.isLocationAvailable " + locationAvailability.isLocationAvailable());
                            if (!locationAvailability.isLocationAvailable()) {
                                toastMessage("Please enable GPS signal");
                            }
                            Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
                            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Location location = task.getResult();
                                    Log.d(TAG, "Location information!"+ String.valueOf(location));
                                    if (location == null) {
                                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            return;
                                        }
                                        mLocationManager.requestSingleUpdate("gps", mLocationListener, null);
                                    }
                                    else {
                                        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                updateUILocation(location);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });


                }
            }
        });

        // Submit information to Firebase
        btnSubmitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload image to Firebase storage
                uploadImage();

            }
        });

        initTensorFlowAndLoadModel();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getActivity().getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                } catch (final Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateUILocation (Location location) {
        if (location !=null) {
            try {
                Log.d(TAG, "Update LatLng information!");
                textViewLongitude.setText(String.valueOf(location.getLongitude()));
                textViewLatitude.setText(String.valueOf(location.getLatitude()));
                mLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                Log.d(TAG, "get location "+location.getLongitude()+" "+location.getLatitude());
                mGeoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                Log.d(TAG, mGeoPoint.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: get location permissions");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        Log.d(TAG, String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) // M is for Marshmallow, API 23
        {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    fineLocationPermission) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        coarseLocationPermission) == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                } else {
                    ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSIONS_REQUEST_ACCESS_LOCATION);
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        }
    }

    private void uploadImage() {

        imageRef = mStorageRef.child(textViewObject.getText().toString()+System.currentTimeMillis()+".jpg");

        imageRef.putBytes(getByteArray(imageViewResult)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();

                objectPicUrl = String.valueOf(downloadUrl);
                Log.d(TAG, objectPicUrl);
                uploadData();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMessage(e.getCause().getLocalizedMessage());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                toastMessage("Information is uploading!");
            }
        });
    }

    private void uploadData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, objectPicUrl);
        if (mGeoPoint != null){
            Log.d(TAG, mGeoPoint.toString());
        }
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = format.parse(textViewExpiredDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SiteModel site = new SiteModel();
        site.setCreator(currentUser.getDisplayName());
        ArrayList images = new ArrayList<String>();
        images.add(objectPicUrl);
        site.setImages(images);
        ShareItem item = new ShareItem();
        item.setName(textViewObject.getText().toString());
        item.setCount(Integer.valueOf(textViewObjectCount.getText().toString()));
        ExpireDate = new Timestamp(date);
        item.setExpireDate(ExpireDate);
        site.setLastUpdator(currentUser.getDisplayName());
        site.setSiteLocation(mGeoPoint);
        site.setLocationName(textViewAddress.getText().toString());
        site.setSiteName(textViewSiteName.getText().toString());
        ArrayList<ShareItem> shareItems = new ArrayList<>();
        shareItems.add(item);
        site.setItems(shareItems);

        Log.d(TAG, "textViewExpiredDate.getText().toString()");
        // Add a new document with a generated ID
        db.collection("Sites")
                .add(site)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        toastMessage("Upload finished!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
    }

    // Retrieve the detected image from ImageView
    public byte[] getByteArray(ImageView imageView){
        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

}
