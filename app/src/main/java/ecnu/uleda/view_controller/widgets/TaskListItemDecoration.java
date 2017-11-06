package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jimmyhsu on 2017/4/8.
 */

public class TaskListItemDecoration extends RecyclerView.ItemDecoration {

    private boolean mHasFooter;
    private float mHeight;

    public TaskListItemDecoration(Context context, int heightDp, boolean hasFooter) {
        mHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp,
                context.getResources().getDisplayMetrics());
        mHasFooter = hasFooter;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, (int) Math.ceil(mHeight));
    }
}
