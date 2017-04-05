package ecnu.uleda.view_controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by jimmyhsu on 2017/4/4.
 */

public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> data;
    private int layoutId;

    public CommonAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected Context getContext() { return context; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommonViewHolder viewHolder = CommonViewHolder.getInstance(context, convertView,
                parent, layoutId, position);
        bindView(viewHolder, (T) getItem(position));

        return viewHolder.getConvertView();
    }

    public abstract void bindView(CommonViewHolder viewHolder, T t);
}