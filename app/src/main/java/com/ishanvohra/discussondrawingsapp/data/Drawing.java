package com.ishanvohra.discussondrawingsapp.data;

public class Drawing {

    private String drawingId, imgPath, title;

    private long additionTime;

    public long getAdditionTime() {
        return additionTime;
    }

    public void setAdditionTime(long additionTime) {
        this.additionTime = additionTime;
    }

    public String getDrawingId() {
        return drawingId;
    }

    public void setDrawingId(String drawingId) {
        this.drawingId = drawingId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
