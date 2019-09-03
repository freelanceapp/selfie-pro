package com.mojodigi.selfiepro.adjust;

public class AdjustToolsModel {
    public String toolName;
    public int toolIcon;
    public AdjustToolsType galleryAdjustToolsType;

    public AdjustToolsModel(String toolName, int toolIcon, AdjustToolsType galleryAdjustToolsType) {
        this.toolName = toolName;
        this.toolIcon = toolIcon;
        this.galleryAdjustToolsType = galleryAdjustToolsType;
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

    public AdjustToolsType getGalleryAdjustToolsType() {
        return galleryAdjustToolsType;
    }

    public void setGalleryAdjustToolsType(AdjustToolsType galleryAdjustToolsType) {
        this.galleryAdjustToolsType = galleryAdjustToolsType;
    }

    @Override
    public String toString() {
        return "GalleryAdjustToolsModel{" +
                "toolName='" + toolName + '\'' +
                ", toolIcon=" + toolIcon +
                ", galleryAdjustToolsType=" + galleryAdjustToolsType +
                '}';
    }
}
