package com.lyancafe.coffeeshop.shop.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.CustomMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Administrator on 2017/9/18.
 */

public class MyVedioActivity extends AppCompatActivity implements CacheListener {

    private static final String TAG  ="MyVedioActivity";
    @BindView(R.id.video_view)
    IjkVideoView videoView;
    private String videoUrl;
    private String videoTitle;
    private int currentPosition = 0;

    private CustomMediaController mMediaController;

    private boolean mBackPressed = false;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, MyVedioActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);
        ButterKnife.bind(this);
        LogUtil.d(TAG,"onCreate");
        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("videoPath");
        videoTitle = intent.getStringExtra("videoTitle");

        if (!TextUtils.isEmpty(videoUrl)) {
            new RecentMediaStorage(this).saveUrlAsync(videoUrl);
        }
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mMediaController = new CustomMediaController(this.getApplicationContext(),false);
        videoView.setMediaController(mMediaController);


        if(TextUtils.isEmpty(videoUrl)){
            ToastUtil.show(this,"视频文件不存在");
            return;
        }else{
            startProxy(videoUrl);
        }

        LogUtil.d(TAG,"url = "+videoUrl);

        videoView.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG,"onNewIntent");
        videoUrl = intent.getStringExtra("videoPath");
        videoTitle = intent.getStringExtra("videoTitle");
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG,"onResume curPosition = "+currentPosition);
        if(currentPosition!=0){
            videoView.seekTo(currentPosition);
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        currentPosition = videoView.getCurrentPosition();
        LogUtil.d(TAG,"onPause curPosition = "+currentPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !videoView.isBackgroundPlayEnabled()) {
            videoView.stopPlayback();
            videoView.release(true);
            videoView.stopBackgroundPlay();
        } else {
            videoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CSApplication.getProxy(this).unregisterCacheListener(this);
    }


    private void startProxy(String url) {
        HttpProxyCacheServer proxy = CSApplication.getProxy(this);
        proxy.registerCacheListener(this, url);
        videoView.setVideoPath(proxy.getProxyUrl(url));
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        LogUtil.d(TAG,"cacheFile = "+cacheFile==null?null:cacheFile.getName()+" | url = "+url+" | percentsAvailable="+percentsAvailable);
      /*  if(percentsAvailable==100 && !cacheFile.getName().endsWith("download")){
            MyUtil.encrypt(cacheFile.getAbsolutePath());
        }*/

    }
}
