package ras.asu.com.letsmeet;

import android.graphics.Bitmap;

/**
 * Created by SahanaSekhar on 3/31/16.
 */


public class RowItem {
    private String imageId;
    private String title;
    private Bitmap image;
    //private String desc;

    public RowItem( String title, String imageId,Bitmap image) {
        this.imageId = imageId;
        this.title = title;
        this.image = image;
    }
    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    /*public String getDesc() {
        return desc;
    }*/
    //public void setDesc(String desc) {
      //  this.desc = desc;
    //}
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    /*@Override
    public String toString() {
        return title + "\n" + desc;
    }*/
}
