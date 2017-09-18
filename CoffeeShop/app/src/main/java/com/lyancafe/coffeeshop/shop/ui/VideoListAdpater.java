package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.VideoBean;
import com.lyancafe.coffeeshop.http.RxHelper;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;

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

        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(videoBean.getUrl(), new HashMap<String, String>());
                Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(100);
                e.onNext(bitmap);
                e.onComplete();
            }
        }).compose(RxHelper.<Bitmap>io_main())
          .subscribe(new Consumer<Bitmap>() {
            @Override
            public void accept(@NonNull Bitmap bitmap) throws Exception {
                holder.videoImg.setImageBitmap(bitmap);
            }
        });


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
}
