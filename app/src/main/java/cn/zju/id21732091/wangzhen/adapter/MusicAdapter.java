package cn.zju.id21732091.wangzhen.adapter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.constraint.Constraints;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cn.zju.id21732091.wangzhen.R;
import cn.zju.id21732091.wangzhen.pojo.MusicInfo;


/**
 * Created by wzlab on 2018/6/20.
 */

public class MusicAdapter extends RecyclerView.Adapter{
    private ArrayList<MusicInfo> musicList;
    private Context context;
    private int selectPos = 0;


    public MusicAdapter(Context context, ArrayList<MusicInfo> musicList){
        this.musicList = musicList;
        this.context = context;
    }

    /*
 *  RecyclerView中item的点击事件
 */
    public interface OnItemClickListener{
        public void onItemClick(View view, int position);
    }
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    class MusicListViewHolder extends RecyclerView.ViewHolder{
        private TextView mTvMusic;
        private TextView mTvArtist;
        private TextView mTvDuration;
        private ImageView mIvAlbumImg;


        public MusicListViewHolder(final View itemView) {
            super(itemView);
            mTvMusic = itemView.findViewById(R.id.tv_music_name);
            mTvArtist = itemView.findViewById(R.id.tv_author);
            mTvDuration = itemView.findViewById(R.id.tv_duration);
            mIvAlbumImg = itemView.findViewById(R.id.iv_play_flag);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) itemView.getTag();
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(view,position);
                        notifyDataSetChanged();
                        selectPos = position;

                        notifyDataSetChanged();


                      //  mIvAlbumImg.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }

    @Override
    public MusicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MusicListViewHolder musicListViewHolder =  new MusicListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_music_info,parent,false));


        return musicListViewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rHolder, int position) {


        MusicListViewHolder holder = (MusicListViewHolder)rHolder;
        holder.itemView.setTag(position);
        MusicInfo musicInfo = musicList.get(position);
        Log.e("MusicListActivity", musicInfo.getArtist() );
        holder.mTvArtist.setText(musicInfo.getArtist() + " - " + musicInfo.getAlbum());
        holder.mTvMusic.setText(musicInfo.getTitle());
        holder.mTvDuration.setText(musicInfo.getDuration());
        long ID = musicInfo.getID();
        long albumId = musicInfo.getAlbumId();

        holder.itemView.setSelected(selectPos == position);
        ImageView mIvAlbumImg = holder.itemView.findViewById(R.id.iv_play_flag);

        if(selectPos == position){
            mIvAlbumImg.setVisibility(View.VISIBLE);
            holder.mTvArtist.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.mTvMusic.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.mTvDuration.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }else {
            mIvAlbumImg.setVisibility(View.GONE);
            holder.mTvArtist.setTextColor(context.getResources().getColor(R.color.gray));
            holder.mTvMusic.setTextColor(context.getResources().getColor(R.color.rv_music_title));
            holder.mTvDuration.setTextColor(context.getResources().getColor(R.color.gray));
        }

        // 获取专辑封面
//        Bitmap bitmap = getMusicBitmap(context,ID,albumId);
//        holder.mIvAlbumImg.setImageBitmap(bitmap);
//        musicInfo.setAlbumImg(bitmap);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }



}
