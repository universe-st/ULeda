package ecnu.uleda.view_controller.message;

import android.util.Log;

import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendFutureMeta;
import com.tencent.imsdk.ext.sns.TIMGetFriendFutureListSucc;
import com.tencent.imsdk.ext.sns.TIMPageDirectionType;

/**
 * Created by zhaoning on 2017/7/25.
 */

public class FriendshipManagerPresenter {
    private static final String TAG = "FriendManagerPresenter";

    private FriendshipMessageView friendshipMessageView;
    private FriendshipManageView friendshipManageView;
    private FriendInfoView friendInfoView;
    private final int PAGE_SIZE = 20;
    private int index;
    private long pendSeq, decideSeq, recommendSeq;
    private boolean isEnd;

    public FriendshipManagerPresenter(FriendshipMessageView view){
        this(view, null, null);
    }

    public FriendshipManagerPresenter(FriendInfoView view){
        this(null, null, view);
    }

    public FriendshipManagerPresenter(FriendshipManageView view){
        this(null, view, null);
    }

    public FriendshipManagerPresenter(FriendshipMessageView view1, FriendshipManageView view2, FriendInfoView view3){
        friendshipManageView = view2;
        friendshipMessageView = view1;
        friendInfoView = view3;
    }


}
