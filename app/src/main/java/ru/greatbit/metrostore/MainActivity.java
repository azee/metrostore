package ru.greatbit.metrostore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Date;

import ru.greatbit.metrostore.utils.SoundPlayer;


public class MainActivity extends AppCompatActivity {

    private boolean playing = false;

    private EditText bitrate;
    private EditText scale;
    private Button button;
    private TextView barCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bitrate = (EditText) findViewById(R.id.bitrate);
        scale = (EditText) findViewById(R.id.scale);
        button = (Button) findViewById(R.id.play);
        barCounter = (TextView) findViewById(R.id.barCounter);

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
                long startTime = new Date().getTime();
                long now;
                while (playing){
                    now = new Date().getTime();
                    if (now - startTime >= quoterDuration){
                        beatsCounter = beatsCounter > scaleVal ? 1 : beatsCounter;
                        SoundPlayer.playSound(getSoundId(beatsCounter));
                        setText(barCounter, Integer.toString(beatsCounter));
                        startTime = now;
                        beatsCounter++;
                    }
                }
            }
        }).start();
    }

    private int getSoundId(int beatsCounter) {
        if (beatsCounter == 1){
            return 1;
        }
        return 2;
    }

    private long getQuoterDuration(int bitrate){
        int beatsPerSecond = bitrate/60;
        return 1000 / beatsPerSecond;
    }

    private double getDuration(int bitrate, int scale){
        double barsInSecond = bitrate/60d;
        double quatersInSecond = barsInSecond * scale;
        return 1d/quatersInSecond/2d;
    }
}
