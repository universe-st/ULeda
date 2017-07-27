package ecnu.uleda.view_controller.task.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import butterknife.ButterKnife
import ecnu.uleda.R
import ecnu.uleda.function_module.UserOperatorController

/**
 * Created by jimmyhsu on 2017/5/18.
 */
@SuppressLint("WrongConstant")
abstract class BaseDetailsActivity: AppCompatActivity() {

    val mPopupWindow by lazy {
        PopupWindow(this)
    }

    var mUserChatItems = arrayListOf<TaskDetailsActivity.UserChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initContentView()
        ButterKnife.bind(this)
        initActivity(savedInstanceState)
    }

    abstract fun initActivity(savedInstanceState: Bundle?)
    abstract fun initContentView()
    abstract fun getChatView(uci: TaskDetailsActivity.UserChatItem): View
    abstract fun onSubmitComment(comment: String)

    fun showCommentPopup() {
        val view: View = View.inflate(this, R.layout.activity_addcomment, null)
        val sendBtn: Button = view.findViewById(R.id.send) as Button
        val postCommentEdit: EditText = view.findViewById(R.id.comment_edit) as EditText
        postCommentEdit.requestFocus()
        postCommentEdit.setTextColor(0xff000000.toInt())
        sendBtn.setOnClickListener {
            val text: String = postCommentEdit.text.toString()
            if (text.isEmpty()) {
                toast("评论内容不可以为空哦！")
                return@setOnClickListener
            }
            onSubmitComment(text)
        }
        view.setOnClickListener {
            mPopupWindow.dismiss()
        }
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        val comment = view.findViewById(R.id.comment) as LinearLayout
        comment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.push_bottom_in))
        mPopupWindow.width = ViewGroup.LayoutParams.MATCH_PARENT
        mPopupWindow.height = ViewGroup.LayoutParams.WRAP_CONTENT
        mPopupWindow.isFocusable = true
        mPopupWindow.isOutsideTouchable = true
        mPopupWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.WHITE))
        mPopupWindow.contentView = view
        mPopupWindow.showAtLocation(comment, Gravity.BOTTOM, 0, 0)
        if (Build.VERSION.SDK_INT != 24) {
            mPopupWindow.update()
        }
    }

    protected fun addCommentView(target: ViewGroup, index: Int) {
        mUserChatItems.forEach {
            val commentItem = getChatView(it)
            target.addView(commentItem, index)
        }
    }

    protected fun addCommentView(comment: String, target: ViewGroup, index: Int) {
        val uoc = UserOperatorController.getInstance()
        val uci = TaskDetailsActivity.UserChatItem(Integer.parseInt(uoc.id),
                comment,
                uoc.userName,
                "test",
                -1,
                System.currentTimeMillis() / 1000)
        mUserChatItems.add(uci)
        val commentItem = getChatView(uci)
        target.addView(commentItem, index)
        mPopupWindow.dismiss()
    }

    protected fun setChatItems(items: List<TaskDetailsActivity.UserChatItem>) {
        mUserChatItems = items as ArrayList<TaskDetailsActivity.UserChatItem>
    }


    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}