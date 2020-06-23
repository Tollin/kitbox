package com.unitec.kitbox.models;

import com.google.firebase.firestore.GeoPoint;

public class SiteModel {
    private String SiteName;
    private String LocationName;
    private String LastUpdator;
    private String Creator;
    private GeoPoint SiteLocation;
    private String[] Images;
    private ShareItem[] Items;

    public String getSiteName() {
        return SiteName;
    }

    public void setSiteName(String siteName) {
        SiteName = siteName;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public String getLastUpdator() {
        return LastUpdator;
    }

    public void setLastUpdator(String lastUpdator) {
        LastUpdator = lastUpdator;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public GeoPoint getSiteLocation() {
        return SiteLocation;
    }

    public void setSiteLocation(GeoPoint siteLocation) {
        SiteLocation = siteLocation;
    }

    public String[] getImages() {
        return Images;
    }

    public void setImages(String[] images) {
        Images = images;
    }

    public ShareItem[] getItems() {
        return Items;
    }

    public void setItems(ShareItem[] items) {
        Items = items;
    }
}
