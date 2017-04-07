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

public class HorizontalItemDecoration extends RecyclerView.ItemDecoration {

    private float mHeight;

    public HorizontalItemDecoration(Context context, int heightDp) {
        mHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp,
                context.getResources().getDisplayMetrics());
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0 ||
                parent.getChildAdapterPosition(view) == state.getItemCount() - 2) { // 有footer
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, (int) Math.ceil(mHeight));
        }
    }
}
