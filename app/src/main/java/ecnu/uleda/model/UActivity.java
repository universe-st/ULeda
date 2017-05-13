package ecnu.uleda.model;

import java.util.List;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class UActivity {
    private String avatarUrl;
    private String username;
    private long releaseTime;
    private long actTime;
    private String location;
    private String tag;
    private String title;
    private List<String> contentUrls;

    public UActivity(String avatarUrl, String username, long releaseTime, String tag, String title,
                     long actTime, String location) {
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.releaseTime = releaseTime;
        this.tag = tag;
        this.title = title;
        this.actTime = actTime;
        this.location = location;
    }

    public UActivity(String avatarUrl, String username, long releaseTime, String tag, String title,
                     long actTime, String location, List<String> contentUrls) {
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.releaseTime = releaseTime;
        this.tag = tag;
        this.title = title;
        this.contentUrls = contentUrls;
        this.actTime = actTime;
        this.location = location;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContentUrls() {
        return contentUrls;
    }

    public void setContentUrls(List<String> contentUrls) {
        this.contentUrls = contentUrls;
    }

    public long getActTime() {
        return actTime;
    }

    public void setActTime(long actTime) {
        this.actTime = actTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
