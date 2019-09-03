package com.mojodigi.selfiepro.camera;

public class CameraSubToolsModel {

    public String cameraSubToolName;
    public int cameraSubToolIcon;
    public CameraSubToolsType cameraSubToolsType;

    public CameraSubToolsModel(String cameraSubToolName, int cameraSubToolIcon, CameraSubToolsType cameraSubToolsType) {
        this.cameraSubToolName = cameraSubToolName;
        this.cameraSubToolIcon = cameraSubToolIcon;
        this.cameraSubToolsType = cameraSubToolsType;
    }

    public String getCameraSubToolName() {
        return cameraSubToolName;
    }

    public void setCameraSubToolName(String cameraSubToolName) {
        this.cameraSubToolName = cameraSubToolName;
    }

    public int getCameraSubToolIcon() {
        return cameraSubToolIcon;
    }

    public void setCameraSubToolIcon(int cameraSubToolIcon) {
        this.cameraSubToolIcon = cameraSubToolIcon;
    }

    public CameraSubToolsType getCameraSubToolsType() {
        return cameraSubToolsType;
    }

    public void setCameraSubToolsType(CameraSubToolsType cameraSubToolsType) {
        this.cameraSubToolsType = cameraSubToolsType;
    }
}
