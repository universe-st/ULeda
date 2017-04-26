package ecnu.uleda.view_controller.widgets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ecnu.uleda.R;
import ecnu.uleda.model.UCircle;
import ecnu.uleda.view_controller.UCircleListAdapter;

public class UcircleDetailActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ucircle_detail);
        init();
    }

    private ImageView mphotoImageView;
    private TextView mpublisher_nameTextView;
    private TextView mTitleTextView;
    private TextView marticleTextView;
    private ImageView mdynamic_photoImageView;
    private TextView mpublish_timeTextView;
    private TextView mGet_zanTextView;
    private TextView mBack;
    public void init()
    {
        mBack = (TextView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mphotoImageView = (ImageView) findViewById(R.id.photo);
        mpublisher_nameTextView = (TextView) findViewById(R.id.publisher_name);
        mTitleTextView = (TextView) findViewById(R.id.Title);
        marticleTextView  = (TextView) findViewById(R.id.article);
        mdynamic_photoImageView = (ImageView) findViewById(R.id.dynamic_photo);
        mpublish_timeTextView = (TextView) findViewById(R.id.publish_time);
        mGet_zanTextView = (TextView) findViewById(R.id.Get_zan);

        Intent intent = getIntent();

        String photo = intent.getStringExtra("photo");
        int INT_photo = Integer.valueOf(photo);
        String publisher_name = intent.getStringExtra("publisher_name");
        String Title = intent.getStringExtra("Title");
        String article = intent.getStringExtra("article");
        String dynamic_photo = intent.getStringExtra("dynamic_photo");
        int INT_dynamic_photo = Integer.valueOf(dynamic_photo);
        String publish_time = intent.getStringExtra("publish_time");
        String Get_zan = intent.getStringExtra("Get_zan");
        mphotoImageView.setImageResource(INT_photo);
        mpublisher_nameTextView.setText(publisher_name);
        mTitleTextView.setText(Title);
        marticleTextView.setText(article);
        if(INT_dynamic_photo != 0)
        {
            mdynamic_photoImageView.setImageResource(INT_dynamic_photo);
        }
        else
        {
            mdynamic_photoImageView.setVisibility(View.GONE);
        }
        mpublish_timeTextView.setText(publish_time);
        mGet_zanTextView.setText(Get_zan);
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back:
                finish();
                break;
        }
    }
}
