package ecnu.uleda.view_controller.message;

import com.tencent.imsdk.TIMUserProfile;

import java.util.List;

/**
 * Created by zhaoning on 2017/7/25.
 * 好友信息接口
 */


public interface FriendInfoView {


    /**
     * 显示用户信息
     *
     * @param users 资料列表
     */
    void showUserInfo(List<TIMUserProfile> users);
}
