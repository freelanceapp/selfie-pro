package com.mojodigi.selfiepro.model;

import com.mojodigi.selfiepro.enums.StickersRecyclerType;

public class StickersRecyclerModel {

    public String stickersRecyclerName;
    public int stickersRecyclerIcon;

    public StickersRecyclerModel(String stickersRecyclerName, int stickersRecyclerIcon, StickersRecyclerType stickersRecyclerType) {
        this.stickersRecyclerName = stickersRecyclerName;
        this.stickersRecyclerIcon = stickersRecyclerIcon;
        this.stickersRecyclerType = stickersRecyclerType;
    }

    public String getStickersRecyclerName() {
        return stickersRecyclerName;
    }

    public void setStickersRecyclerName(String stickersRecyclerName) {
        this.stickersRecyclerName = stickersRecyclerName;
    }

    public int getStickersRecyclerIcon() {
        return stickersRecyclerIcon;
    }

    public void setStickersRecyclerIcon(int stickersRecyclerIcon) {
        this.stickersRecyclerIcon = stickersRecyclerIcon;
    }

    public StickersRecyclerType getStickersRecyclerType() {
        return stickersRecyclerType;
    }

    public void setStickersRecyclerType(StickersRecyclerType stickersRecyclerType) {
        this.stickersRecyclerType = stickersRecyclerType;
    }

    public StickersRecyclerType stickersRecyclerType;
}
