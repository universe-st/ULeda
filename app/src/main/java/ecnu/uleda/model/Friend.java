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


    public Friend(String userid, String name, String imageUrl,String userTag) {
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