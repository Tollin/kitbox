package com.unitec.kitbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.CollectionReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private static String TAG="MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "kitbox";

    private static TextView loginUserDisplayName;
    private static TextView loginUserEmail;
    private static ImageView loginUserProfileIcon;
    private Context mContext;
    // firebase database
    private static FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static FirebaseUser currentUser;
    private static List<AuthUI.IdpConfig> providers; // login users method
    private static FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mDatabaseReference;
    private static final String CollectionName = "Sites";

    public NavController getNavController() {
        return navController;
    }

    private NavController navController;

    public CollectionReference getSitesCollection() {
        return sitesCollection;
    }

    private CollectionReference sitesCollection = null;
    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }

    private FirebaseFirestore firebaseFirestore;

    public static FirebaseUser getCurrentUser() {
        return MainActivity.currentUser;
    }

    public static void setCurrentUser(FirebaseUser currentUser) {
        Log.d(TAG, "Check if user login");
        MainActivity.currentUser = currentUser;
        Log.d(TAG, "The current user is: "+currentUser);
        if(currentUser != null){
            loginUserDisplayName.setText(currentUser.getDisplayName());
            loginUserEmail.setText(currentUser.getEmail());
            Uri userProfile = currentUser.getPhotoUrl();
            Log.d(TAG,"userPhotoUrl: "+userProfile);
            if(userProfile != null){
                Picasso.get().load(userProfile).into(loginUserProfileIcon);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        loginUserDisplayName = headerView.findViewById(R.id.login_user_displayName);
        loginUserEmail = headerView.findViewById(R.id.login_user_email);
        loginUserProfileIcon = headerView.findViewById(R.id.profileIcon);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow, R.id.nav_map)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        sitesCollection = firebaseFirestore.collection(CollectionName);
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
//                new AuthUI.IdpConfig.FacebookBuilder().build());

        setCurrentUser(firebaseAuth.getCurrentUser());

        fireBaseInit();

    }

    private void fireBaseInit(){
        if(getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
                Log.d(TAG, "Response: "+ response);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                toastMessage("Successfully signed in!");
                setCurrentUser(FirebaseAuth.getInstance().getCurrentUser());
                    }
                // ...
            } else {
                toastMessage("Login fail, please login again!" );
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem btnLogout = menu.findItem(R.id.action_logout);
        btnLogout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setCurrentUser(null);
                        fireBaseInit();
                    }
                });
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

}
