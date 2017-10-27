package ecnu.uleda.view_controller.message;

/**
 * Created by zhaoning on 2017/10/18.
 */

public class Invites {


    private String mInvitesId;
    private String mInvitesName;
    private String mImageUrl;
    private String mContent;


    public String getInvitesId() {
        return mInvitesId;
    }

    public Invites setInvitesId(String invitesId) {
        mInvitesId = invitesId;
        return this;
    }

    public String getInvitesName() {
        return mInvitesName;
    }

    public Invites setInvitesName(String invitesName) {
        mInvitesName = invitesName;
        return this;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public Invites setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public Invites setContent(String content) {
        mContent = content;
        return this;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Invites){
            Invites n = (Invites)o;
            return this.mInvitesId.equals(n.mInvitesId) && this.mInvitesName.equals(n.mInvitesName);
        }
        return false;
    }
}
