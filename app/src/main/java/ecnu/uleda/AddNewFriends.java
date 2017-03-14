package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewFriends extends AppCompatActivity {

    private Button back = (Button) findViewById(R.id.Add_Back);
    private Button Phone = (Button)findViewById(R.id.findByPhoneNumber);
    private  Button paint = (Button)findViewById(R.id.findByPaint);
    private EditText content = (EditText)findViewById(R.id.editText_add);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewFriends.this,MessageFragment.class);
            }
        });
    }
}
