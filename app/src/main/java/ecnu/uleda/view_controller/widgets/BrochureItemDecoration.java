package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jimmyhsu on 2017/4/14.
 */

public class BrochureItemDecoration extends RecyclerView.ItemDecoration {

        private float mWidth;

        public BrochureItemDecoration(Context context, int widthDp) {
            mWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp,
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
            if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, (int) Math.ceil(mWidth), 0);
            }
        }

}
