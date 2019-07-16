package com.mojodigi.selfiepro.model;
import com.mojodigi.selfiepro.enums.FrameType;

public class FrameModel {

    public String mFrameName;
    public int mFrameIdSmall;
    public int mFrameIdBig;
    public FrameType mFrameType;

    public FrameModel(String mFrameName, int mFrameIdSmall, FrameType mFrameType) {
        this.mFrameName = mFrameName;
        this.mFrameIdSmall = mFrameIdSmall;
        this.mFrameType = mFrameType;
    }

    public FrameModel(String mFrameName, int mFrameIdSmall, int mFrameIdBig, FrameType mFrameType) {
        this.mFrameName = mFrameName;
        this.mFrameIdSmall = mFrameIdSmall;
        this.mFrameIdBig = mFrameIdBig;
        this.mFrameType = mFrameType;
    }

    public String getmFrameName() {
        return mFrameName;
    }

    public void setmFrameName(String mFrameName) {
        this.mFrameName = mFrameName;
    }

    public int getmFrameIdSmall() {
        return mFrameIdSmall;
    }

    public void setmFrameIdSmall(int mFrameIdSmall) {
        this.mFrameIdSmall = mFrameIdSmall;
    }

    public int getmFrameIdBig() {
        return mFrameIdBig;
    }

    public void setmFrameIdBig(int mFrameIdBig) {
        this.mFrameIdBig = mFrameIdBig;
    }

    public FrameType getmFrameType() {
        return mFrameType;
    }

    public void setmFrameType(FrameType mFrameType) {
        this.mFrameType = mFrameType;
    }




}
