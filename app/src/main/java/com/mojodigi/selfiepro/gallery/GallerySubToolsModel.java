package com.mojodigi.selfiepro.gallery;

public class GallerySubToolsModel {

    public String gallerySubToolName;
    public int gallerySubToolIcon;
    public GallerySubToolsType gallerySubToolsType;

    public GallerySubToolsModel(String gallerySubToolName, int gallerySubToolIcon, GallerySubToolsType gallerySubToolsType) {
        this.gallerySubToolName = gallerySubToolName;
        this.gallerySubToolIcon = gallerySubToolIcon;
        this.gallerySubToolsType = gallerySubToolsType;
    }

    public String getGallerySubToolName() {
        return gallerySubToolName;
    }

    public void setGallerySubToolName(String gallerySubToolName) {
        this.gallerySubToolName = gallerySubToolName;
    }

    public int getGallerySubToolIcon() {
        return gallerySubToolIcon;
    }

    public void setGallerySubToolIcon(int gallerySubToolIcon) {
        this.gallerySubToolIcon = gallerySubToolIcon;
    }

    public GallerySubToolsType getGallerySubToolsType() {
        return gallerySubToolsType;
    }

    public void setGallerySubToolsType(GallerySubToolsType gallerySubToolsType) {
        this.gallerySubToolsType = gallerySubToolsType;
    }



}
