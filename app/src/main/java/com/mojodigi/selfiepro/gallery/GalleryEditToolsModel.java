package com.mojodigi.selfiepro.gallery;

public class GalleryEditToolsModel {

    public String toolName;
    public int toolIcon;
    public GalleryEditToolsType galleryEditToolsType;

    public GalleryEditToolsModel(String toolName, int toolIcon, GalleryEditToolsType galleryEditToolsType) {
        this.toolName = toolName;
        this.toolIcon = toolIcon;
        this.galleryEditToolsType = galleryEditToolsType;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public int getToolIcon() {
        return toolIcon;
    }

    public void setToolIcon(int toolIcon) {
        this.toolIcon = toolIcon;
    }

    public GalleryEditToolsType getGalleryEditToolsType() {
        return galleryEditToolsType;
    }

    public void setGalleryEditToolsType(GalleryEditToolsType galleryEditToolsType) {
        this.galleryEditToolsType = galleryEditToolsType;
    }
}
