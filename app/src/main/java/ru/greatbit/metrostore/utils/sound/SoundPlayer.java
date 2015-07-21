package ru.greatbit.metrostore.utils.sound;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

import ru.greatbit.metrostore.R;

/**
 * Created by azee on 10.07.15.
 */
public class SoundPlayer {
    private static SoundPool soundPool = null;
    private static HashMap<Integer, Integer> soundPoolMap;
    private static final int STREAMS = 1;
    private static final float volume = 1;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void initSounds(Context context) {
        Log.d("Sound", "Initialize Recent or Deprecated API SoundPool.");

        // Initialize SoundPool, call specific dependent on SDK Version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("Sound", "Initialize Recent API SoundPool.");
            initializeRecentAPISoundPool();
        }
        else {
            Log.d("Sound", "Initialize Old API SoundPool.");
            initializeDeprecatedAPISoundPool();
        }

        addSoundsToPool(context);
    }

    /** 
     * Initialize the SoundPool for later API versions
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void initializeRecentAPISoundPool() {
        Log.d("SoundPool", "Initialize recent API Sound Pool");
        soundPool = new PullFactory().initializeRecentAPISoundPool(soundPool);
    }

    /** 
     * Intialize SoundPool for older API      versions
     */
    @SuppressWarnings("deprecation")
    private static void initializeDeprecatedAPISoundPool() {
        // Initialize SoundPool.
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    public static void playSound(int soundID) {
        soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }

    private static void addSoundsToPool(Context context){
        Log.d("Sound", "Set AudioAttributes for SoundPool.");
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(1, soundPool.load(context, R.raw.click1, 1));
        soundPoolMap.put(2, soundPool.load(context, R.raw.click2, 1));
    }

}
