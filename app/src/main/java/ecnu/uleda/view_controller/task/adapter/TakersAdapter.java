package ecnu.uleda.view_controller.task.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.UPublicTool;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

public abstract class TakersAdapter extends RecyclerView.Adapter<TakersAdapter.TakerViewHolder> {

    private Context mContext;
    private List<UserInfo> mDatas;

    public TakersAdapter(Context context, List<UserInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public TakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout container = new LinearLayout(mContext);
        int width = (int) UPublicTool.dp2px(mContext, 50);
        container.setLayoutParams(new RecyclerView.LayoutParams(width, width));
        final SketchImageView imageView = new SketchImageView(mContext);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayOptions options = new DisplayOptions();
        options.setImageShaper(new CircleImageShaper());
        imageView.setOptions(options);
        container.addView(imageView, 0);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) imageView.getTag();
                onItemClick(v, pos);
            }
        });
        return new TakerViewHolder(container);
    }

    @Override
    public void onBindViewHolder(TakerViewHolder holder, int position) {
        holder.avatar.setTag(position);
        holder.avatar.displayResourceImage(R.drawable.xiaohong);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setDatas(List<UserInfo> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    protected abstract void onItemClick(View v, int pos);

    class TakerViewHolder extends RecyclerView.ViewHolder {

        SketchImageView avatar;

        public TakerViewHolder(View itemView) {
            super(itemView);
            this.avatar = (SketchImageView) ((LinearLayout)itemView).getChildAt(0);
        }
    }
}