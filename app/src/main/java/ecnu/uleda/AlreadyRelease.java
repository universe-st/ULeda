package ecnu.uleda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AlreadyRelease extends AppCompatActivity {
    private ImageButton mback;
    private void init()
    {
        mback = (ImageButton)findViewById(R.id.Release_back);
        mback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_release);
        init();

    }
}
