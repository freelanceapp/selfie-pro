package com.mojodigi.selfiepro.collage;

public class CollageSubToolsModel {

    public String toolsRecyclerName;
    public int toolsRecyclerIcon;
    public CollageSubToolsType toolsRecyclerType;


    public CollageSubToolsModel(String toolsRecyclerName, int toolsRecyclerIcon, CollageSubToolsType toolsRecyclerType) {
        this.toolsRecyclerName = toolsRecyclerName;
        this.toolsRecyclerIcon = toolsRecyclerIcon;
        this.toolsRecyclerType = toolsRecyclerType;
    }

    public String getToolsRecyclerName() {
        return toolsRecyclerName;
    }

    public void setToolsRecyclerName(String toolsRecyclerName) {
        this.toolsRecyclerName = toolsRecyclerName;
    }

    public int getToolsRecyclerIcon() {
        return toolsRecyclerIcon;
    }

    public void setToolsRecyclerIcon(int toolsRecyclerIcon) {
        this.toolsRecyclerIcon = toolsRecyclerIcon;
    }

    public CollageSubToolsType getToolsRecyclerType() {
        return toolsRecyclerType;
    }

    public void setToolsRecyclerType(CollageSubToolsType toolsRecyclerType) {
        this.toolsRecyclerType = toolsRecyclerType;
    }
}
