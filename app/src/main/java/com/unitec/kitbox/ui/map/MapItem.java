package com.unitec.kitbox.ui.map;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapItem {

    public String getSiteName() {
        return SiteName;
    }

    public List<String> getImages() {
        return Images;
    }

    public String getLastUpdator() {
        return LastUpdator;
    }

    public List<Item> getItems() {
        return Items;
    }

    public String getCreator() {
        return Creator;
    }

    public GeoPoint getSiteLocation() {
        return SiteLocation;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public void setImages(ArrayList<String> images) {
        Images = images;
    }

    public void setLastUpdator(String lastUpdator) {
        LastUpdator = lastUpdator;
    }

    public void setItems(List<Item> items) {
        Items = items;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public void setSiteLocation(GeoPoint siteLocation) {
        SiteLocation = siteLocation;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    private String SiteName;
    private List<String> Images;
    private String LastUpdator;
    private List<Item> Items;
    private String Creator;
    private GeoPoint SiteLocation;
    private String LocationName;

    public MapItem(String SiteName ,
                   List<String> Images ,
                   String LastUpdator ,
                   List<Item> Items ,
                   String Creator ,
                   GeoPoint SiteLocation ,
                   String LocationName ) {
        this.Images = new ArrayList<String>();
        for (String s : Images) {
            this.Images.add(s);
        }
        this.Items = new ArrayList<Item>();
        this.SiteName =   SiteName;
        this.Images =   Images;
        this.LastUpdator =   LastUpdator;
        for (Item i : Items) {
            this.Items.add((Item) i.clone());
        }
        this.Creator =   Creator;
        this.SiteLocation =   SiteLocation;
        this.LocationName =   LocationName;


    }

    public MapItem(){

    }

}