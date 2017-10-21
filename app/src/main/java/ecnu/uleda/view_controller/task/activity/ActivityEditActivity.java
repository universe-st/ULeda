package ecnu.uleda.view_controller.task.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.jakewharton.rxbinding2.view.RxView;

import net.phalapi.sdk.PhalApiClientResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import ecnu.uleda.R;
import ecnu.uleda.function_module.ServerAccessApi;
import ecnu.uleda.function_module.UserOperatorController;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.view_controller.LocationListActivity;
import ecnu.uleda.view_controller.TaskPostActivity;
import ecnu.uleda.view_controller.task.adapter.ImageChooseAdapter;
import ecnu.uleda.view_controller.widgets.InfiniteGridView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xuyanzhe on 5/10/17.
 */

public class ActivityEditActivity extends AppCompatActivity {

    private static final String EXTRA_ACTIVITY = "extra_activity";
    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_LOCATION = 200;

    private UActivity mActivity;
    private static List<String> ACTIVITY_CATEGORIES;

    private ImageChooseAdapter mImageChooseAdapter;

    static {
        ACTIVITY_CATEGORIES.add("运动");
        ACTIVITY_CATEGORIES.add("社团");
        ACTIVITY_CATEGORIES.add("公益");
    }

    @BindView(R.id.activity_post_title)
    EditText mTitleView;
    @BindView(R.id.activity_post_activity_time)
    TextView mTimeView;
    @BindView(R.id.activity_post_location)
    TextView mLocationView;
    @BindView(R.id.activity_post_category)
    TextView mCategoryView;
    @BindView(R.id.max_people_count)
    EditText mMaxPeopleView;
    @BindView(R.id.activity_post_description)
    EditText mDescView;
    @BindView(R.id.img_choose_grid)
    InfiniteGridView mImgChooseView;
    @BindView(R.id.id_activity_edit_submit)
    Button mSubmitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_edit_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initGridView();
        spinnerInit();
        timePickerInit();
        initLocation();
        initSubmit();
    }

    private void initSubmit() {
        RxView.clicks(mSubmitView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(new Function<Object, PhalApiClientResponse>() {
                    @Override
                    public PhalApiClientResponse apply(@NonNull Object o) throws Exception {
                        mSubmitView.setEnabled(false);
                        mSubmitView.setText("提交中...");
                        mSubmitView.setBackgroundColor(ContextCompat.getColor(ActivityEditActivity.this,
                                android.R.color.darker_gray));
                        UserOperatorController uoc = UserOperatorController.getInstance();
                        return ServerAccessApi.editActivity(uoc.getId(), uoc.getPassport(),
                                String.valueOf(mActivity.getId()), mActivity.getTitle(),
                                mActivity.getDescription(), String.valueOf(mActivity.getHoldTime()),
                                String.valueOf(mActivity.getLat()), String.valueOf(mActivity.getLon()),
                                String.valueOf(mActivity.getTakersCount()), mActivity.getLocation());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PhalApiClientResponse>() {
                    @Override
                    public void accept(PhalApiClientResponse response) throws Exception {
                        mSubmitView.setEnabled(true);
                        mSubmitView.setText("修改");
                        mSubmitView.setBackgroundColor(ContextCompat.getColor(ActivityEditActivity.this,
                                R.color.colorUMain));
                        if (response.getRet() == 200 && response.getData().equals("success")) {
                            Toast.makeText(ActivityEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ActivityEditActivity.this, "修改失败：" + response.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mSubmitView.setEnabled(true);
                        mSubmitView.setText("修改");
                        mSubmitView.setBackgroundColor(ContextCompat.getColor(ActivityEditActivity.this,
                                R.color.colorUMain));
                        Toast.makeText(ActivityEditActivity.this, "修改失败：网络异常", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void initLocation() {
        mLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseLocation();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mActivity = (UActivity) intent.getSerializableExtra(EXTRA_ACTIVITY);
            if (mActivity != null) {
                mTitleView.setText(mActivity.getTitle());
                mTimeView.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(new Date(mActivity.getHoldTime())));
                mLocationView.setText(mActivity.getLocation());
                mCategoryView.setText(mActivity.getTag());
                mMaxPeopleView.setText(mActivity.getTakersCount());
                mDescView.setText(mActivity.getDescription());
            }
        }
    }

    private void initGridView() {
        mImgChooseView.setAdapter(mImageChooseAdapter = new ImageChooseAdapter(this, mActivity.getImgUrls(), 60, 7));
        mImgChooseView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mImageChooseAdapter.getCount() - 1 && mActivity.getImgUrls().size() < 7) {
                    selectImage();
                } else {
                    removeImage(position);
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void removeImage(final int pos) {
        new AlertDialog.Builder(this)
                .setMessage("确认删除")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.getImgUrls().remove(pos);
                        mImageChooseAdapter.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
    }

    private void chooseLocation() {
        Intent intent = new Intent(ActivityEditActivity.this, LocationListActivity.class);
        startActivityForResult(intent, REQUEST_LOCATION);
    }

    private void spinnerInit() {
        final OptionsPickerView catOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int i, int i1, int i2, View view) {
                mActivity.setTag(ACTIVITY_CATEGORIES.get(i));
                mCategoryView.setText(ACTIVITY_CATEGORIES.get(i));
            }
        }).setCancelText("取消")
                .setCancelColor(Color.GRAY)
                .setContentTextSize(24)
                .setTitleText("类别")
                .setTitleColor(Color.GRAY)
                .setOutSideCancelable(true)
                .setSubmitText("确定")
                .setSubmitColor(ContextCompat.getColor(this, R.color.colorUMain))
                .build();
        catOptions.setPicker(ACTIVITY_CATEGORIES);
        mCategoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catOptions.setSelectOptions(ACTIVITY_CATEGORIES.indexOf(mActivity.getTag()) < 0 ?
                        0 : ACTIVITY_CATEGORIES.indexOf(mActivity.getTag()));
                catOptions.show();
            }
        });
    }

    private void timePickerInit() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE) + 1);
        endDate.set(endDate.get(Calendar.YEAR) + 1, startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE));

        final TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            private SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
            @Override
            public void onTimeSelect(Date date, View v) {
                Calendar cal = Calendar.getInstance();
                if (date.getTime() - cal.getTime().getTime() < 24 * 3600 * 1000) {
                    Toast.makeText(ActivityEditActivity.this, "活动时间至少为24小时以后!", Toast.LENGTH_SHORT).show();
                } else {
                    cal.setTime(date);
                    mActivity.setHoldTime(cal.getTimeInMillis());
                    mTimeView.setText(df.format(date));
                }
            }
        }).setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("选择活动时间")
                .setContentSize(17)
                .setOutSideCancelable(true)
                .isCyclic(false)
                .setTitleColor(Color.GRAY)
                .setSubmitColor(ContextCompat.getColor(this, R.color.colorUMain))
                .setCancelColor(Color.GRAY)
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(true)
                .setRangDate(startDate, endDate)
                .build();
        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mActivity.getHoldTime());
                pvTime.setDate(calendar);
                pvTime.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {
            mLocationView.setText(data.getStringExtra("title"));
            mActivity.setLat(data.getFloatExtra("lat", 0f));
            mActivity.setLon(data.getFloatExtra("lng", 0f));
        } else if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            onImageChosen(data);
        }
    }

    private void onImageChosen(Intent data) {
        Uri uri = data.getData();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            mActivity.getImgUrls().add(path);
            mImageChooseAdapter.notifyDataSetChanged();
            mImgChooseView.requestLayout();
            if (mActivity.getImgUrls().size() == 7) {
                Toast.makeText(this, "已到达最大图片数", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static void startActivity(Context context, UActivity activity) {
        Intent intent = new Intent(context, ActivityEditActivity.class);
        intent.putExtra(EXTRA_ACTIVITY, activity);
        context.startActivity(intent);
    }
}
