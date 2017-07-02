package ecnu.uleda.view_controller;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ecnu.uleda.R;

public class UserRegister extends AppCompatActivity implements View.OnClickListener{
    private TextView mRegisterBack;
    private Button MessageSendButton;
    private TimeCount time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        init();
    }
    protected void init()
    {
        mRegisterBack = (TextView)findViewById(R.id.register_back);
        mRegisterBack.setOnClickListener(this);
        time = new TimeCount(60000,1000);
        MessageSendButton = (Button)findViewById(R.id.Message_Send);
        MessageSendButton.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.register_back:
                finish();
                break;
            case R.id.Message_Send:
                time.start();
                break;
        }
    }
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            MessageSendButton.setBackgroundColor(Color.parseColor("#DD5A44"));
            MessageSendButton.setClickable(false);
            MessageSendButton.setBackground(getResources().getDrawable(R.drawable.login_shape_button));
            MessageSendButton.setText(""+millisUntilFinished / 1000 +"");
        }
        @Override
        public void onFinish() {
            MessageSendButton.setBackground(getResources().getDrawable(R.drawable.login_shape_button));
            MessageSendButton.setText("重新获取");
            MessageSendButton.setClickable(true);

            MessageSendButton.setBackgroundColor(Color.parseColor("#4EB84A"));
        }
    }
}
