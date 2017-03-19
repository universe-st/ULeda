package ecnu.uleda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddNewFriends extends AppCompatActivity {

    //private Button button_phoneNumber ;
    //private Button button_paint ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friends);

         Button button_back =(Button) findViewById(R.id.add_Back);
       // button_phoneNumber.findViewById(R.id.findByPhoneNumber);
        //button_paint.findViewById(R.id.findByPaint);

       button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*button_phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewFriends.this,)
            }
        });*/
    }
}
