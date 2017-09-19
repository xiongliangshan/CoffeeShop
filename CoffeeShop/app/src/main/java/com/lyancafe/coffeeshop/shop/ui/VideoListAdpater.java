package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.utils.BitmapCache;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/9/15.
 */

public class VideoListAdpater extends RecyclerView.Adapter<VideoListAdpater.ViewHolder> {



    private List<VideoBean> videos;
    private Context context;

    public VideoListAdpater(List<VideoBean> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_list_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final VideoBean videoBean = videos.get(i);
        holder.tvTitle.setText(videoBean.getTitle());
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = MyVedioActivity.newIntent(context, videoBean.getUrl(), videoBean.getTitle());
                context.startActivity(intent);
            }
        });

       new VideoFrameTask(holder.videoImg).execute(videoBean);


    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public List<VideoBean> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoBean> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.video_img)
        ImageView videoImg;
        @BindView(R.id.rl_container)
        RelativeLayout rlContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class VideoFrameTask extends AsyncTask<VideoBean,Integer,Bitmap>{

        private ImageView imageView;

        public VideoFrameTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(VideoBean... params) {
            VideoBean videoBean = params[0];
            Bitmap  cacheBitmap =  BitmapCache.getInst().getBitmapFromCache(videoBean.getTitle());
            if(cacheBitmap==null){
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoBean.getUrl(), new HashMap<String, String>());
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1);
                mediaMetadataRetriever.release();
                BitmapCache.getInst().addBitmapToCache(videoBean.getTitle(),bitmap);
                return bitmap;
            }else{
                return cacheBitmap;
            }
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
