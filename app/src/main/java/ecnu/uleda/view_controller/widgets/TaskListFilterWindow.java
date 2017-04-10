package ecnu.uleda.view_controller.widgets;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.tool.UPublicTool;

/**
 * Created by jimmyhsu on 2017/4/8.
 */

public class TaskListFilterWindow extends PopupWindow implements View.OnClickListener {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private static final int SELECTED_COLOR = 0xFFDD5A44;
    private static final int UNSELECTED_COLOR = 0xFF777777;
    private List<String> mItems;
    private int mSelectedPos;
    private LinearLayout mContentView;
    private OnItemSelectedListener mListener;

    public TaskListFilterWindow(Context context, List<String> items) {
        super(context);
        this.mItems = items;
        mSelectedPos = 0;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentView = new LinearLayout(context);
        mContentView.setBackgroundColor(0xFFFFFFFF);
        mContentView.setPadding((int)UPublicTool.dp2px(context, 8), 0, (int)UPublicTool.dp2px(context, 8), 0);
        mContentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mContentView.setOrientation(LinearLayout.VERTICAL);
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);
        a.recycle();
        mContentView.setDividerDrawable(divider);
        mContentView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        setContentView(mContentView);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        setAnimationStyle(R.style.spinner_window_anim);
        addItems(context);
    }

    private void addItems(Context context) {
        int paddingLeft = (int) UPublicTool.dp2px(context, 16);
        int paddingTop = (int) UPublicTool.dp2px(context, 8);
        for (int i = 0; i < mItems.size(); i++) {
            TextView itemView = new TextView(context);
            itemView.setTag(i);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            itemView.setBackgroundResource(R.drawable.selectable_item_main);
            itemView.setTextSize(16);
            if (i == mSelectedPos) {
                itemView.setTextColor(SELECTED_COLOR);
            } else {
                itemView.setTextColor(UNSELECTED_COLOR);
            }
            itemView.setText(mItems.get(i));
            itemView.setMaxLines(1);
            itemView.setEllipsize(TextUtils.TruncateAt.END);
            itemView.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
            itemView.setOnClickListener(this);
            mContentView.addView(itemView, i);
        }
    }

    private void setSelected(int pos) {
        TextView item = (TextView) mContentView.getChildAt(mSelectedPos);
        item.setTextColor(UNSELECTED_COLOR);
        item = (TextView) mContentView.getChildAt(pos);
        item.setTextColor(SELECTED_COLOR);
        mSelectedPos = pos;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }


    @Override
    public void onClick(View v) {
        int pos = (int) v.getTag();
        if (pos != mSelectedPos) {
            setSelected(pos);
            if (mListener != null) {
                mListener.OnItemSelected(v, pos);
                dismiss();
            }
        } else {
            dismiss();
        }
    }


    public interface OnItemSelectedListener {
        void OnItemSelected(View v, int pos);
    }
}
