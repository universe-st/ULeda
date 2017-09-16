package ecnu.uleda.tool;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xuyanzhe on 14/9/17.
 */

public abstract class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetectorCompat mGestureDetectorCompat;
    private RecyclerView mRecyclerView;

    public RecyclerViewTouchListener(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childViewUnder != null) {
                    int childPosition = mRecyclerView.getChildLayoutPosition(childViewUnder);
                    onItemClick(childPosition, mRecyclerView.getChildViewHolder(childViewUnder));
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childViewUnder = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childViewUnder != null) {
                    int childPosition = mRecyclerView.getChildLayoutPosition(childViewUnder);
                    onItemLongClick(childPosition, mRecyclerView.getChildViewHolder(childViewUnder));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public abstract void onItemClick(int position, RecyclerView.ViewHolder viewHolder);
    public abstract void onItemLongClick(int position, RecyclerView.ViewHolder viewHolder);

}
