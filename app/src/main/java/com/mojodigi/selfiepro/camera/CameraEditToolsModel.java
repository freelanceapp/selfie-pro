package com.mojodigi.selfiepro.camera;

public class CameraEditToolsModel {

    public String cameraToolName;
    public int cameraToolIcon;
    public CameraEditToolsType cameraEditToolsType;

    public CameraEditToolsModel(String cameraToolName, int cameraToolIcon, CameraEditToolsType cameraEditToolsType) {
        this.cameraToolName = cameraToolName;
        this.cameraToolIcon = cameraToolIcon;
        this.cameraEditToolsType = cameraEditToolsType;
    }

    public String getCameraToolName() {
        return cameraToolName;
    }

    public void setCameraToolName(String cameraToolName) {
        this.cameraToolName = cameraToolName;
    }

    public int getCameraToolIcon() {
        return cameraToolIcon;
    }

    public void setCameraToolIcon(int cameraToolIcon) {
        this.cameraToolIcon = cameraToolIcon;
    }

    public CameraEditToolsType getCameraEditToolsType() {
        return cameraEditToolsType;
    }

    public void setCameraEditToolsType(CameraEditToolsType cameraEditToolsType) {
        this.cameraEditToolsType = cameraEditToolsType;
    }
}
