package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageVertify extends AppCompatActivity {
    private Button BackButton;
    private Button VertifyButton;
    private EditText PhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_vertify);
        BackButton = (Button) findViewById(R.id.vertify_back);
        VertifyButton = (Button) findViewById(R.id.button_vertify);


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        VertifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.button_vertify:
                        String textcontent = PhoneNumber.getText().toString();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
