package ras.asu.com.letsmeet;

/**
 * Created by Rakesh on 4/10/2016.
 */
public class User {
    private   String KEY_USERID ;
    private  String KEY_USERNAME;
    private String KEY_USERURL ;
    private String KEY_IS_FRIEND;

    public String getKEY_USERID() {
        return KEY_USERID;
    }

    public void setKEY_USERID(String KEY_USERID) {
        this.KEY_USERID = KEY_USERID;
    }

    public String getKEY_USERNAME() {
        return KEY_USERNAME;
    }

    public void setKEY_USERNAME(String KEY_USERNAME) {
        this.KEY_USERNAME = KEY_USERNAME;
    }

    public String getKEY_USERURL() {
        return KEY_USERURL;
    }

    public void setKEY_USERURL(String KEY_USERURL) {
        this.KEY_USERURL = KEY_USERURL;
    }

    public String getKEY_IS_FRIEND() {
        return KEY_IS_FRIEND;
    }

    public void setKEY_IS_FRIEND(String KEY_IS_FRIEND) {
        this.KEY_IS_FRIEND = KEY_IS_FRIEND;
    }
}
