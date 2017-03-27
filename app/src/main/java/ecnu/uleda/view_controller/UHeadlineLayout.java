package ecnu.uleda.view_controller;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ecnu.uleda.R;

/**
 * Created by Shensheng on 2017/1/22.
 */

public class UHeadlineLayout extends LinearLayout {
    private View mView;

    private LinearLayout mRedLayout;
    private LinearLayout mWhiteLayout;
    private Button mRedBackButton;
    private Button mWhileBackButton;
    private Button mRedPlusButton;
    private Button mWhitePlusButton;
    private TextView mRedTitle;
    private TextView mWhiteTitle;
    public UHeadlineLayout(Context context, AttributeSet abs){
        super(context,abs);
        initView();
    }
    public UHeadlineLayout(Context context){
        super(context,null);
    }

    public void initView(){
        LayoutInflater inflater=LayoutInflater.from(getContext());
        mView=inflater.inflate(R.layout.u_headline_layout,null);
        addView(mView);
        mRedLayout=(LinearLayout)mView.findViewById(R.id.red_headline);
        mWhiteLayout=(LinearLayout)mView.findViewById(R.id.white_headline);
        mRedBackButton=(Button)mView.findViewById(R.id.red_headline_back);
        mWhileBackButton=(Button)mView.findViewById(R.id.white_headline_back);
        mRedPlusButton=(Button)mView.findViewById(R.id.red_headline_plus);
        mWhitePlusButton=(Button)mView.findViewById(R.id.white_headline_plus);
        mRedTitle=(TextView)mView.findViewById(R.id.red_headline_title);
        mWhiteTitle=(TextView)mView.findViewById(R.id.white_headline_title);
        mRedLayout.setAlpha(1.0f);
        mWhiteLayout.setAlpha(0.0f);
    }

    public void setTitle(CharSequence str){
        mRedTitle.setText(str);
        mWhiteTitle.setText(str);
    }

    public void setTitleRed(CharSequence str){
        mRedTitle.setText(str);
    }

    public void setTitleWhite(CharSequence str){
        mWhiteTitle.setText(str);
    }
    public void setBackButtonClickListener(View.OnClickListener c){
        mRedBackButton.setOnClickListener(c);
        mWhileBackButton.setOnClickListener(c);
    }
    public void setPlusButtonClickListener(View.OnClickListener c){
        mRedPlusButton.setOnClickListener(c);
        mWhitePlusButton.setOnClickListener(c);
    }

    public void setRedAlpha(float f){
        mRedLayout.setAlpha(f);
        mWhiteLayout.setAlpha(1.0f-f);
    }
}
