package cn.zju.id21732091.wangzhen.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.zju.id21732091.wangzhen.R;

import cn.zju.id21732091.wangzhen.pojo.StatusInfo;

/**
 * Created by wzlab on 2018/6/21.
 */

public class StatusAdapter extends RecyclerView.Adapter{

    private Context context;
    private ArrayList<StatusInfo> statusList;


    // 设置Item监听接口
    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public StatusAdapter(Context context, ArrayList<StatusInfo> statusList){
        this.context = context;
        this.statusList = statusList;
    }

    @Override
    public StatusListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StatusListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_status_info,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StatusListViewHolder mHolder = (StatusListViewHolder)holder;
        StatusInfo statusInfo = statusList.get(position);
        mHolder.mTvUserId.setText(statusInfo.getUserId());
        mHolder.mTvCreateAt.setText(statusInfo.getCreatAt());
        String content = statusInfo.getContent();
        if(content.length()>60){
            content = content.substring(0,60) + "...";
        }
        mHolder.mTvContent.setText(content);
        Bitmap userImg = statusInfo.getUserImg();
        if(userImg != null){
            mHolder.mIvUserImg.setImageBitmap(userImg);
        }

        mHolder.itemView.setTag(position);



    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    class StatusListViewHolder extends RecyclerView.ViewHolder{

        private ImageView mIvUserImg;
        private TextView mTvUserId;
        private TextView mTvContent;
        private TextView mTvCreateAt;

        public StatusListViewHolder(final View itemView) {
            super(itemView);
            mIvUserImg = itemView.findViewById(R.id.iv_user_img);
            mTvUserId = itemView.findViewById(R.id.tv_user_id);
            mTvContent = itemView.findViewById(R.id.tv_message);
            mTvCreateAt = itemView.findViewById(R.id.tv_create_at);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(view,(int)itemView.getTag());
                    }
                }
            });
        }
    }
}
