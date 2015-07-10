package ru.greatbit.metrostore.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

import ru.greatbit.metrostore.R;

/**
 * Created by azee on 10.07.15.
 */
public class SoundPlayer {
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;
    private static final int STREAMS = 2;
    private static final float volume = 1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void initSounds(Context context) {
        soundPool = new SoundPool.Builder().setMaxStreams(STREAMS).build();
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(1, soundPool.load(context, R.raw.click1, 1));
        soundPoolMap.put(2, soundPool.load(context, R.raw.click2, 1));
    }

    public static void playSound(int soundID) {
        soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }

}
