package ecnu.uleda.model;


import java.io.Serializable;

/**
 * Created by Shensheng on 2016/10/28.
 * 用户信息
 */

public class UserInfo implements Serializable {
    public static int FORBID=-1;
    public static int NORMAL=0;

    public static int MALE=0;
    public static int FEMALE=1;

    private String mUserName;
    private int mUserType;
    private int mSex;
    private String mBirthday;
    private String mAvatar;
    private String mSchool;
    private String mSchoolClass;
    private String mStudentId;
    private String mRealName;
    private String mPhone;
    private String mId;
    private String mSignature;

    public int getFriendStatus() {
        return mFriendStatus;
    }

    public UserInfo setFriendStatus(int friendStatus) {
        mFriendStatus = friendStatus;
        return this;
    }

    private int mFriendStatus;

    public String getSignature() {
        return mSignature;
    }

    public UserInfo setSignature(String signature) {
        mSignature = signature;
        return this;
    }

    public String getUserName(){
        return mUserName;
    }
    public UserInfo setUserName(String userName){
        mUserName=userName;
        return this;
    }
    public String getId() {
        return mId;
    }

    public UserInfo setId(String id) {
        mId = id;
        return this;
    }

    public int getUserType() {
        return mUserType;
    }

    public UserInfo setUserType(int userType) {
        mUserType = userType;
        return this;
    }

    public int getSex() {
        return mSex;
    }

    public UserInfo setSex(int sex) {
        mSex = sex;
        return this;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public UserInfo setBirthday(String birthday) {
        mBirthday = birthday;
        return this;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public UserInfo setAvatar(String avatar) {
        mAvatar = avatar;
        return this;
    }

    public String getSchool() {
        return mSchool;
    }

    public UserInfo setSchool(String school) {
        mSchool = school;
        return this;
    }

    public String getSchoolClass() {
        return mSchoolClass;
    }

    public UserInfo setSchoolClass(String schoolClass) {
        mSchoolClass = schoolClass;
        return this;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public UserInfo setStudentId(String studentId) {
        mStudentId = studentId;
        return this;
    }

    public String getRealName() {
        return mRealName;
    }

    public UserInfo setRealName(String realName) {
        mRealName = realName;
        return this;
    }

    public String getPhone() {
        return mPhone;
    }

    public UserInfo setPhone(String phone) {
        mPhone = phone;
        return this;
    }
}
