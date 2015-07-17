package ru.greatbit.metrostore.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

import ru.greatbit.metrostore.R;

/**
 * Created by azee on 10.07.15.
 */
public class SoundPlayer {
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;
    private static final int STREAMS = 1;
    private static final float volume = 1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void initSounds(Context context) {
        Log.d("Sound", "Initialize Audio Attributes.");
        // Initialize AudioAttributes.
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        Log.d("Sound", "Set AudioAttributes for SoundPool.");
        // Set the audioAttributes for the SoundPool and specify maximum number of streams.
        soundPool = new SoundPool.Builder().setMaxStreams(STREAMS)
                .setAudioAttributes(attributes)
                .build();
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(1, soundPool.load(context, R.raw.click1, 1));
        soundPoolMap.put(2, soundPool.load(context, R.raw.click2, 1));
    }

    public static void playSound(int soundID) {
        soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }

}
