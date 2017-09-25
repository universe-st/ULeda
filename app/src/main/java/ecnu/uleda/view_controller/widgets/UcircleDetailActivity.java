package ecnu.uleda.view_controller.widgets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private CircleImageView mphotoImageView;
    private TextView mpublisher_nameTextView;
    private TextView mTitleTextView;
    private TextView marticleTextView;
    private ImageView mdynamic_photoImageView1;
    private ImageView mdynamic_photoImageView2;
    private ImageView mdynamic_photoImageView3;
    private TextView mpublish_timeTextView;
    private TextView mGet_zanTextView;
    private TextView mBack;
    public void init()
    {
        mBack = (TextView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
        mphotoImageView = (CircleImageView) findViewById(R.id.photo);
        mpublisher_nameTextView = (TextView) findViewById(R.id.publisher_name);
        mTitleTextView = (TextView) findViewById(R.id.Title);
        marticleTextView  = (TextView) findViewById(R.id.article);
        mdynamic_photoImageView1 = (ImageView) findViewById(R.id.dynamic_photo1);
        mdynamic_photoImageView2 = (ImageView) findViewById(R.id.dynamic_photo2);
        mdynamic_photoImageView3 = (ImageView) findViewById(R.id.dynamic_photo3);
        mpublish_timeTextView = (TextView) findViewById(R.id.publish_time);
        mGet_zanTextView = (TextView) findViewById(R.id.Get_zan);

        Intent intent = getIntent();

        String photo = intent.getStringExtra("photo");

        String publisher_name = intent.getStringExtra("publisher_name");
        String Title = intent.getStringExtra("Title");
        String article = intent.getStringExtra("article");

        String dynamic_photo1 = intent.getStringExtra("dynamic_photo1");
        String dynamic_photo2 = intent.getStringExtra("dynamic_photo2");
        String dynamic_photo3 = intent.getStringExtra("dynamic_photo3");

        String publish_time = intent.getStringExtra("publish_time");
        String Get_zan = intent.getStringExtra("Get_zan");

        Glide.with(this)
                .load("http://118.89.156.167/uploads/avatars/"+photo )
                .into(mphotoImageView);

        mpublisher_nameTextView.setText(publisher_name);
        mTitleTextView.setText(Title);
        marticleTextView.setText(article);
        if(!dynamic_photo1.equals("null"))
        {
            Glide.with(this)
                    .load("http://118.89.156.167/uploads/avatars/"+dynamic_photo1 )
                    .into(mdynamic_photoImageView1);
        }
        else
        {
            mdynamic_photoImageView1.setVisibility(View.GONE);
        }
        if(!dynamic_photo2.equals("null"))
        {
            Glide.with(this)
                    .load("http://118.89.156.167/uploads/avatars/"+dynamic_photo2 )
                    .into(mdynamic_photoImageView2);
        }
        else
        {
            mdynamic_photoImageView2.setVisibility(View.GONE);
        }
        if(!dynamic_photo3.equals("null"))
        {
            Glide.with(this)
                    .load("http://118.89.156.167/uploads/avatars/"+dynamic_photo3 )
                    .into(mdynamic_photoImageView3);
        }
        else
        {
            mdynamic_photoImageView3.setVisibility(View.GONE);
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
