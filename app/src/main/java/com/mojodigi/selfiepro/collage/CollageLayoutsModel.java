package com.mojodigi.selfiepro.collage;

public class CollageLayoutsModel {

    public String collageName;
    public int collageLayoutIdSmall;
    public int collageLayoutIdBig;
    public CollageLayoutsType collageLayoutType;


    public CollageLayoutsModel(String mCollageName, int mCollageLayoutIdSmall , CollageLayoutsType mCollageLayoutType) {
        this.collageName = mCollageName;
        this.collageLayoutIdSmall = mCollageLayoutIdSmall;
        this.collageLayoutType = mCollageLayoutType;
    }

    public CollageLayoutsModel(String collageName, int collageLayoutIdSmall, int collageLayoutIdBig, CollageLayoutsType collageLayoutType) {
        this.collageName = collageName;
        this.collageLayoutIdSmall = collageLayoutIdSmall;
        this.collageLayoutIdBig = collageLayoutIdBig;
        this.collageLayoutType = collageLayoutType;
    }

    public String getCollageName() {
        return collageName;
    }

    public void setCollageName(String collageName) {
        this.collageName = collageName;
    }

    public int getCollageLayoutIdSmall() {
        return collageLayoutIdSmall;
    }

    public void setCollageLayoutIdSmall(int collageLayoutIdSmall) {
        this.collageLayoutIdSmall = collageLayoutIdSmall;
    }

    public int getCollageLayoutIdBig() {
        return collageLayoutIdBig;
    }

    public void setCollageLayoutIdBig(int collageLayoutIdBig) {
        this.collageLayoutIdBig = collageLayoutIdBig;
    }

    public CollageLayoutsType getCollageLayoutType() {
        return collageLayoutType;
    }

    public void setCollageLayoutType(CollageLayoutsType collageLayoutType) {
        this.collageLayoutType = collageLayoutType;
    }


}
