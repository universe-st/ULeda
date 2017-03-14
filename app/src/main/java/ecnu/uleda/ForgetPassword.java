package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ForgetPassword extends AppCompatActivity {
    private Button findBack;
    private Button messageLogin;
    private Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        findBack = (Button) findViewById(R.id.findBackPassword);
        messageLogin = (Button) findViewById(R.id.messageLogin);
        cancel = (Button) findViewById(R.id.cancelFindBack);

        findBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this,GetBackByNumber.class);
                startActivity(intent);
            }
        });

        messageLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetPassword.this,MessageVertify.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
