package ecnu.uleda.model;

/**
 * Created by zhaoning on 2017/4/13.
 */

public class Friend {
    private String userId;
    private String userName;
    private String imageUrl;
    private String userTag;
    private int imageId;


    public Friend(String userid, String name,String userTag) {//TODO:头像的问题等会再说
        this.userId = userid;
        this.userName = name;
        this.imageUrl = imageUrl;
        this.userTag = userTag;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserTag() {
        return userTag;
    }

    public int getImageId() {
        return imageId;
    }
}