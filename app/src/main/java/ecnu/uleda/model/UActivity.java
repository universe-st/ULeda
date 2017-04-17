package ecnu.uleda.model;

import java.util.List;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class UActivity {
    private String avatarUrl;
    private String username;
    private long releaseTime;
    private String tag;
    private String content;
    private List<String> contentUrls;

    public UActivity(String avatarUrl, String username, long releaseTime, String tag, String content) {
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.releaseTime = releaseTime;
        this.tag = tag;
        this.content = content;
    }

    public UActivity(String avatarUrl, String username, long releaseTime, String tag, String content,
                     List<String> contentUrls) {
        this.avatarUrl = avatarUrl;
        this.username = username;
        this.releaseTime = releaseTime;
        this.tag = tag;
        this.content = content;
        this.contentUrls = contentUrls;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getContentUrls() {
        return contentUrls;
    }

    public void setContentUrls(List<String> contentUrls) {
        this.contentUrls = contentUrls;
    }
}
