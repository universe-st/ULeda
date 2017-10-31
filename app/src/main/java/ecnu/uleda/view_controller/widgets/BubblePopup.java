package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;

/**
 * Created by xuyanzhe on 26/9/17.
 */

public class BubblePopup extends PopupWindow {

    private TextView mPromptText;

    public BubblePopup(Context context) {
        this(context, null);
    }

    public BubblePopup(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BubblePopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BubbleView bubbleView = new BubbleView(context);
        bubbleView.setLayoutParams(new ViewGroup.LayoutParams((int)UPublicTool.dp2px(context, 150), (int)UPublicTool.dp2px(context, 50)));
        mPromptText = new TextView(context);
        mPromptText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mPromptText.setGravity(Gravity.CENTER);
        mPromptText.setText("");
        bubbleView.addView(mPromptText);
        setFocusable(true);
        setWidth((int)UPublicTool.dp2px(context, 150));
        setHeight((int)UPublicTool.dp2px(context, 50));
        setContentView(bubbleView);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        setTouchable(true);
        setOutsideTouchable(true);
        setAnimationStyle(R.style.spinner_window_anim);
    }

    public void setPromptText(String text) {
        mPromptText.setText(text);
    }
}
