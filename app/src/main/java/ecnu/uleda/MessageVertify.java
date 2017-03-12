package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageVertify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_vertify);
        Button button1 = (Button) findViewById(R.id.vertify_back);
        Button button2 = (Button) findViewById(R.id.button_vertify);
        final EditText editText = (EditText) findViewById(R.id.editText_phoneNumber);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageVertify.this,ForgetPassword.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.button_vertify:
                        String textcontent = editText.getText().toString();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
