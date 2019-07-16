package com.mojodigi.selfiepro.model;

import com.mojodigi.selfiepro.enums.BackgroundType;

public class BackgroundModel {

    public String backgroundName;
    public int backgroundIdSmall;
    public int backgroundIdBig;
    public BackgroundType backgroundType;

    public BackgroundModel(String backgroundName, int backgroundIdSmall, BackgroundType backgroundType) {
        this.backgroundName = backgroundName;
        this.backgroundIdSmall = backgroundIdSmall;
        this.backgroundType = backgroundType;
    }

    public BackgroundModel(String backgroundName, int backgroundIdSmall, int backgroundIdBig, BackgroundType backgroundType) {
        this.backgroundName = backgroundName;
        this.backgroundIdSmall = backgroundIdSmall;
        this.backgroundIdBig = backgroundIdBig;
        this.backgroundType = backgroundType;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public void setBackgroundName(String backgroundName) {
        this.backgroundName = backgroundName;
    }

    public int getBackgroundIdSmall() {
        return backgroundIdSmall;
    }

    public void setBackgroundIdSmall(int backgroundIdSmall) {
        this.backgroundIdSmall = backgroundIdSmall;
    }

    public int getBackgroundIdBig() {
        return backgroundIdBig;
    }

    public void setBackgroundIdBig(int backgroundIdBig) {
        this.backgroundIdBig = backgroundIdBig;
    }

    public BackgroundType getBackgroundType() {
        return backgroundType;
    }

    public void setBackgroundType(BackgroundType backgroundType) {
        this.backgroundType = backgroundType;
    }



}
