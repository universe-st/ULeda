package ecnu.uleda.view_controller.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by jimmyhsu on 2017/4/8.
 */

public class DrawableLeftCenterTextView extends AppCompatTextView {

    public DrawableLeftCenterTextView(Context context) {
        super(context);
    }

    public DrawableLeftCenterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableLeftCenterTextView(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawableLeft = drawables[0];
        if (drawableLeft != null) {
            float textWidth = getPaint().measureText(getText().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = 0;
            drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }


}
