package com.lyancafe.coffeeshop.utils;

/**
* Created by Administrator on 2017/4/21.
*/


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundPoolUtil {

    private static SoundPool mSoundPool;

    public static SoundPool create(Context context, int resId) {
        /**
         * 初始化声音池
         * 参数1：在声音池中最大的数量
         * 参数2：流类型 默认使用AudioManager.STREAM_MUSIC
         * 参数3:质量  默认为0 这个值暂时没影响
         */
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        /**
         * load加载声音
         * 参数1：上下文
         * 参数2：音频文件
         * 参数3：优先级 默认1 暂时没影响
         */
        final int soundId = mSoundPool.load(context, resId, 1);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                /**
                 * 参数1：加载返回的声音Id
                 * 参数2：左声道
                 * 参数3：右声道
                 * 参数4：优先级
                 * 参数5：是否循环播放 0：不循环 -1：循环
                 * 参数6：速率  0.5--2.0
                 */
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.05f);
            }
        });
        return mSoundPool;
    }

    /**
     * @Title: dismisSoundPool @Description: 释放播放池 @param 设定文件 @return void
     *         返回类型 @throws
     */
    public static void dismisSoundPool() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

}

