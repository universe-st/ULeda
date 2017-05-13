package ecnu.uleda.view_controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import ecnu.uleda.R;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.LoadListener;
import me.xiaopan.sketch.request.LoadResult;

/**
 * Created by jimmyhsu on 2017/5/11.
 */

public class CommonBigImageActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "extra_url";
    private PhotoView mPhotoView;
    private int uiOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_big_image);
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null) {
            mPhotoView = (PhotoView) findViewById(R.id.photo_view);
            if (url.startsWith("http")) {
                Sketch.with(this).load(url, new LoadListener() {
                    @Override
                    public void onCompleted(LoadResult loadResult) {
                        Bitmap bm = loadResult.getBitmap();
                        mPhotoView.setImageBitmap(bm);
                    }

                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onError(ErrorCause errorCause) {
                        Toast.makeText(CommonBigImageActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCanceled(CancelCause cancelCause) {
                        finish();
                    }
                });
            } else {
                mPhotoView.setImageResource(Integer.parseInt(url));
            }
        } else {
            Toast.makeText(this, "url 异常", Toast.LENGTH_SHORT).show();
        }
        mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                fullScreen();
            }
        });
        fullScreen();
    }

    private boolean isImmersiveModeEnabled() {
        return ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
    }

    public void fullScreen() {

        uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled = isImmersiveModeEnabled();

        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }


    public static void startActivity(Context context, String url) {
        context.startActivity(new Intent(context, CommonBigImageActivity.class)
                .putExtra(EXTRA_URL, url));
    }

    public static void startActivity(Context context, String url, ActivityOptionsCompat options) {
        context.startActivity(new Intent(context, CommonBigImageActivity.class)
                .putExtra(EXTRA_URL, url), options.toBundle());
    }
}
