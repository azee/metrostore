package ru.greatbit.metrostore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.greatbit.metrostore.utils.SoundPlayer;


public class MainActivity extends AppCompatActivity {

    private boolean playing = false;

    private EditText bitrate;
    private EditText scale;
    private Button button;
    private TextView barCounter;
    Map<Integer, Boolean> beatsToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bitrate = (EditText) findViewById(R.id.bitrate);
        scale = (EditText) findViewById(R.id.scale);
        button = (Button) findViewById(R.id.play);
        barCounter = (TextView) findViewById(R.id.barCounter);

        SoundPlayer.initSounds(getApplicationContext());

        beatsToPlay = new HashMap<>(4);
        beatsToPlay.put(R.id.quoters, true);
        beatsToPlay.put(R.id.eights, false);
        beatsToPlay.put(R.id.sixteens, false);
        beatsToPlay.put(R.id.triols, false);

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
        beatsToPlay.put(view.getId(), ((ToggleButton) view).isChecked());
    }

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    public void togglePlay(View view){
        playing = !playing;
        button.setText(playing ? "Stop" : "Start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int beatsCounter = 1;
                int scaleVal = Integer.parseInt(scale.getText().toString());
                int bitrateVal = Integer.parseInt(bitrate.getText().toString());
                long quoterDuration = getQuoterDuration(bitrateVal);

                long now = new Date().getTime();
                long quoterStartTime = now;
                long eighthsStartTime = now;
                long sixteensStartTime = now;
                long triolsStartTime = now;
                while (playing){
                    now = new Date().getTime();
                    if (now - quoterStartTime >= quoterDuration){
                        beatsCounter = beatsCounter > scaleVal ? 1 : beatsCounter;
                        SoundPlayer.playSound(getSoundId(beatsCounter));
                        setText(barCounter, Integer.toString(beatsCounter));
                        quoterStartTime = now;
                        beatsCounter++;
                        eighthsStartTime = now;
                        sixteensStartTime = now;
                        triolsStartTime = now;
                    } else if(now - eighthsStartTime >= Math.round(new Double(quoterDuration)/2d) && beatsToPlay.get(R.id.eights)) {
                        SoundPlayer.playSound(2);
                        eighthsStartTime = now;
                        sixteensStartTime = now;
                    } else if(now - sixteensStartTime >= Math.round(new Double(quoterDuration)/4d) && beatsToPlay.get(R.id.sixteens)) {
                        SoundPlayer.playSound(2);
                        sixteensStartTime = now;
                    } else if(now - triolsStartTime >= Math.round(new Double(quoterDuration)/3d) && beatsToPlay.get(R.id.triols)) {
                        SoundPlayer.playSound(2);
                        triolsStartTime = now;
                    }
                }
            }
        }).start();
    }

    private int getSoundId(int beatsCounter) {
        if (beatsCounter == 1 && beatsToPlay.get(R.id.quoters)){
            return 1;
        }
        return 2;
    }

    private long getQuoterDuration(int bitrate){
        double beatsPerSecond = new Double(bitrate)/60d;
        double duration = 1000d / beatsPerSecond;
        return Math.round(duration);
    }
}
