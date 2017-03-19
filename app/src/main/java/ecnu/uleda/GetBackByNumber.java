package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GetBackByNumber extends AppCompatActivity {
    EditText editText;
    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_back_by_number);
        button1 = (Button) findViewById(R.id.getBackWord_Back);
        button2 = (Button) findViewById(R.id.button_forgotten);
        editText = (EditText) findViewById(R.id.editText_forgotten);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.button_forgotten:
                        String inputText = editText.getText().toString();
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
