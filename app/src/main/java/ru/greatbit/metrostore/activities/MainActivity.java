package ru.greatbit.metrostore.activities;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import ru.greatbit.metrostore.R;
import ru.greatbit.metrostore.beans.ParamKey;
import ru.greatbit.metrostore.beans.ResponseCode;
import ru.greatbit.metrostore.beans.SongConfiguration;
import ru.greatbit.metrostore.utils.sound.SoundPlayer;


public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private boolean playing = false;

    SongConfiguration configuration = new SongConfiguration();;

    private final long STANDART_DEVIATION = 10000;

    private EditText scale;
    private Button button;
    private TextView barCounter;
    private NumberPicker tempo;

    private ToggleButton quoters;
    private ToggleButton eights;
    private ToggleButton sixteenths;
    private ToggleButton triplets;

    private long quoterDuration;
    private long eightsDuration;
    private long sixteenthsDuration;
    private long tripletsDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tempo = (EditText) findViewById(R.id.tempo);
        scale = (EditText) findViewById(R.id.scale);
        button = (Button) findViewById(R.id.play);
        barCounter = (TextView) findViewById(R.id.barCounter);

        tempo = (NumberPicker) findViewById(R.id.tempoPicker);
        tempo.setMinValue(60);
        tempo.setMaxValue(250);
        tempo.setValue(120);

        quoters = (ToggleButton)findViewById(R.id.quoters);
        eights = (ToggleButton)findViewById(R.id.eights);
        sixteenths = (ToggleButton)findViewById(R.id.sixteenths);
        triplets = (ToggleButton)findViewById(R.id.triplets);

        SoundPlayer.initSounds(getApplicationContext());

        button.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSoundSelected(View view){
        configuration.getBeatsToPlay().put(view.getId(), ((ToggleButton) view).isChecked());

    }

    public void onTempoChanged(View view){
        configuration.setTempo(((NumberPicker) view).getValue());
        updateDurations();
    }

    public void onScaleChanged(View view) {
        Integer.parseInt(((EditText) view).getText().toString());
        updateDurations();
    }

    private void setText(final TextView text, final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

//    public void togglePlay(View view){
//        playing = !playing;
//        button.setText(playing ? "Stop" : "Start");
//        long quoterStartTime;
//        long eighthsStartTime;
//        long sixteenthsStartTime;
//        long tripletsStartTime;
//        Thread playThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int beatsCounter = 1;
//                configuration.setScale(Integer.parseInt(scale.getText().toString()));
//                configuration.setTempo(tempo.getValue());
//                updateDurations();
//
//                long now = System.nanoTime();
//                long quoterStartTime = now;
//                long eighthsStartTime = now;
//                long sixteenthsStartTime = now;
//                long tripletsStartTime = now;
//                while (playing){
//                    now = System.nanoTime();
//                    if (isBeatCloseEnough(now, quoterStartTime, quoterDuration)){
//                        SoundPlayer.playSound(getSoundId(beatsCounter));
//                        //Log.v(getClass().getName(), "Quoter Deviation is " + (now - quoterStartTime - quoterDuration));
//                        setText(barCounter, Integer.toString(beatsCounter));
//                        beatsCounter++;
//                        beatsCounter = beatsCounter > configuration.getScale() ? 1 : beatsCounter;
//                        quoterStartTime = now;
//                        eighthsStartTime = now;
//                        sixteenthsStartTime = now;
//                        tripletsStartTime = now;
//                    } else if(configuration.getBeatsToPlay().get(R.id.sixteenths)
//                            && isBeatCloseEnough(now, sixteenthsStartTime, sixteenthsDuration)) {
//                        //Log.v(getClass().getName(), "Sixteenths Deviation is " + (now - sixteenthsStartTime - sixteenthsDuration));
//                        SoundPlayer.playSound(2);
//                        eighthsStartTime = now;
//                        sixteenthsStartTime = now;
//                    } else if(configuration.getBeatsToPlay().get(R.id.eights)
//                            && isBeatCloseEnough(now, eighthsStartTime, eightsDuration)) {
//                        //Log.v(getClass().getName(), "Eighths Deviation is " + (now - eighthsStartTime - eightsDuration));
//                        SoundPlayer.playSound(2);
//                        eighthsStartTime = now;
//                    } else if(configuration.getBeatsToPlay().get(R.id.triplets)
//                            && isBeatCloseEnough(now, tripletsStartTime, tripletsDuration)) {
//                        //Log.v(getClass().getName(), "Triplets Deviation is " + (now - tripletsStartTime - tripletsDuration));
//                        SoundPlayer.playSound(2);
//                        tripletsStartTime = now;
//                    }
//                }
//            }
//        });
//        playThread.setPriority(Thread.MAX_PRIORITY);
//        playThread.start();
//    }

    public void goToList(View view){
        Intent intent = new Intent(this, ListActivity.class);
        startActivityForResult(intent, ResponseCode.LIST_ACTIVITY.value());
    }

    private int getSoundId(int beatsCounter) {
        if (beatsCounter == 1 && configuration.getBeatsToPlay().get(R.id.quoters)){
            return 1;
        }
        return 2;
    }

    private long getQuoterDuration(int bitrate){
        double beatsPerSecond = new Double(bitrate)/60d;
        double duration = 1000000000d / beatsPerSecond;
        return Math.round(duration);
    }

    private void onSave(){
        Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ResponseCode.LIST_ACTIVITY.value()
                && resultCode == RESULT_OK
                && data.getSerializableExtra(ParamKey.CONFIGURATION.value()) != null){
            configuration = (SongConfiguration) data.getSerializableExtra(ParamKey.CONFIGURATION.value());
            refreshView();
        }
    }

    private void refreshView() {
        scale.setText(configuration.getScale());
        tempo.setValue(configuration.getTempo());

        quoters.setPressed(configuration.getBeatsToPlay().get(quoters.getId()));
        eights.setPressed(configuration.getBeatsToPlay().get(eights.getId()));
        sixteenths.setPressed(configuration.getBeatsToPlay().get(sixteenths.getId()));
        triplets.setPressed(configuration.getBeatsToPlay().get(triplets.getId()));
    }

    private void updateDurations(){
        quoterDuration = getQuoterDuration(configuration.getTempo());
        eightsDuration = quoterDuration/2;
        sixteenthsDuration = quoterDuration/4;
        tripletsDuration = quoterDuration/3;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        configuration.setTempo(newVal);
        updateDurations();
    }

    private boolean isBeatCloseEnough(long now, long previous, long duration, long deviation){
        //return Math.abs((now - previous) - duration) < deviation;
//        return Math.abs((now + duration) - duration) < deviation;
//        return previous + duration >= now || (previous + duration - now) <= deviation;
//        return previous + duration >= now || (previous + duration - now) <= deviation;
//        return now - previous >= duration;
        return now >= previous + duration || Math.abs(now - previous - duration) <= deviation;
    }

    private boolean isBeatCloseEnough(long now, long previous, long duration){
        return isBeatCloseEnough(now, previous, duration, STANDART_DEVIATION);
    }

    /////////////////////////////////

    public void togglePlay(View view){
        playing = !playing;
        button.setText(playing ? "Stop" : "Start");
        Thread playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int sr = 44100;
                int buffsize = AudioTrack.getMinBufferSize(sr,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sr, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, buffsize,
                        AudioTrack.MODE_STREAM);
                audioTrack.play();
                short samples[] = new short[buffsize];
                int amp = 10000;
                while (playing){
                    for(int i=0; i < buffsize; i++){
                        samples[i] = (short) (amp*sr);
                    }
                }
                audioTrack.stop();
                audioTrack.release();
            }

        });
        playThread.setPriority(Thread.MAX_PRIORITY);
        playThread.start();
    }
}
