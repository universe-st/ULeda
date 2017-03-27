package ecnu.uleda.view_controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import ecnu.uleda.R;

public class UserRegister extends AppCompatActivity {
    private ImageButton mRegisterBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        init();
    }
    protected void init()
    {
        mRegisterBack = (ImageButton)findViewById(R.id.register_back);
        mRegisterBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
