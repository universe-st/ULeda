package ecnu.uleda.model;

/**
 * Created by zhaoning on 2017/10/6.
 */

public class Conversation {
    private String mConversationuId;
    private String mConversationName;
    private String mImageUrl;
    private String mContent;


    public String getConversationuId() {
        return mConversationuId;
    }

    public Conversation setConversationuId(String conversationuId) {
        mConversationuId = conversationuId;
        return this;
    }

    public String getConversationName() {
        return mConversationName;
    }

    public Conversation setConversationName(String conversationName) {
        mConversationName = conversationName;
        return this;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public Conversation setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Conversation setContent(String content) {
        mContent = content;
        return this;
    }

}
