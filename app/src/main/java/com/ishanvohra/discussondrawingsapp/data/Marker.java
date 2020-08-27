package com.ishanvohra.discussondrawingsapp.data;

public class Marker {
    private String markerId, drawingId, type, contentEt;
    private float x,y;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getDrawingId() {
        return drawingId;
    }

    public void setDrawingId(String drawingId) {
        this.drawingId = drawingId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentEt() {
        return contentEt;
    }

    public void setContentEt(String contentEt) {
        this.contentEt = contentEt;
    }
}
