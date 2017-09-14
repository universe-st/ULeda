package ecnu.uleda.view_controller.task.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ecnu.uleda.R;
import ecnu.uleda.model.UserInfo;
import ecnu.uleda.tool.UPublicTool;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;

public class TakersAdapter extends RecyclerView.Adapter<TakersAdapter.TakerViewHolder> {

    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_TICK = 1;

    private Context mContext;
    private List<UserInfo> mDatas;
    private boolean isVerifiedTaker = false;

    public TakersAdapter(Context context, List<UserInfo> datas) {
        mContext = context;
        mDatas = datas;
    }

    public void setVerifiedTaker(boolean verifiedTaker) {
        this.isVerifiedTaker = verifiedTaker;
        if (verifiedTaker) {
            mDatas.add(new UserInfo());
            notifyItemInserted(mDatas.size() - 1);
        }
    }

    public boolean isVerifiedTaker() {
        return this.isVerifiedTaker;
    }

    @Override
    public int getItemViewType(int position) {
        return isVerifiedTaker && position == getItemCount() - 1 ? ITEM_TYPE_TICK : ITEM_TYPE_NORMAL;
    }

    @Override
    public TakerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout container = new LinearLayout(mContext);
        int width = (int) UPublicTool.dp2px(mContext, 50);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(viewType == ITEM_TYPE_NORMAL ? width : RecyclerView.LayoutParams.WRAP_CONTENT, width);
        lp.rightMargin = (int) UPublicTool.dp2px(mContext, 6);
        container.setLayoutParams(lp);
        if (viewType == ITEM_TYPE_NORMAL) {
            final SketchImageView imageView = new SketchImageView(mContext);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            DisplayOptions options = new DisplayOptions();
            options.setImageShaper(new CircleImageShaper());
            imageView.setOptions(options);
            container.addView(imageView, 0);
        } else if (viewType == ITEM_TYPE_TICK) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.taker_selected, parent, false);
            if (mDatas.size() > 0) {
                ((TextView)view.findViewById(R.id.id_taker_name)).setText(mDatas.get(0).getUserName() + " (" + mDatas.get(0).getRealName() + ")");
            }
            container.addView(view);
        }
        return new TakerViewHolder(container, viewType);
    }

    @Override
    public void onBindViewHolder(TakerViewHolder holder, int position) {
        if (!isVerifiedTaker || position < getItemCount() - 1) {
            holder.avatar.displayResourceImage(R.drawable.xiaohong);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setDatas(List<UserInfo> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    static class TakerViewHolder extends RecyclerView.ViewHolder {

        SketchImageView avatar;

        TakerViewHolder(View itemView, int itemType) {
            super(itemView);
            if (itemType == ITEM_TYPE_NORMAL)
                this.avatar = (SketchImageView) ((LinearLayout)itemView).getChildAt(0);
        }
    }
}