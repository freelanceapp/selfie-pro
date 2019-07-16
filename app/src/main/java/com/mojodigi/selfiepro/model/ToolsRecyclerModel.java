package com.mojodigi.selfiepro.model;

import com.mojodigi.selfiepro.enums.ToolsRecyclerType;

public class ToolsRecyclerModel {

    public String toolsRecyclerName;
    public int toolsRecyclerIcon;
    public ToolsRecyclerType toolsRecyclerType;


    public ToolsRecyclerModel(String toolsRecyclerName, int toolsRecyclerIcon, ToolsRecyclerType toolsRecyclerType) {
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

    public ToolsRecyclerType getToolsRecyclerType() {
        return toolsRecyclerType;
    }

    public void setToolsRecyclerType(ToolsRecyclerType toolsRecyclerType) {
        this.toolsRecyclerType = toolsRecyclerType;
    }
}
