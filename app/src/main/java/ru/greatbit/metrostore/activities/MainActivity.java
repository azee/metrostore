package ru.greatbit.metrostore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import ru.greatbit.metrostore.beans.SongConfiguration;
import ru.greatbit.metrostore.utils.sound.SoundPlayer;


public class MainActivity extends AppCompatActivity {

    private boolean playing = false;

    SongConfiguration configuration;

    //private EditText tempo;
    private EditText scale;
    private Button button;
    private TextView barCounter;

    private NumberPicker tempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configuration = (SongConfiguration) getIntent().getExtras().get("configuration");
        if (configuration == null){
            configuration = new SongConfiguration();
            //tempo = (EditText) findViewById(R.id.tempo);
            scale = (EditText) findViewById(R.id.scale);
            button = (Button) findViewById(R.id.play);
            barCounter = (TextView) findViewById(R.id.barCounter);

            tempo = (NumberPicker) findViewById(R.id.tempoPicker);
            tempo.setMinValue(60);
            tempo.setMaxValue(250);
        }

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
                configuration.setScale(Integer.parseInt(scale.getText().toString()));
//                configuration.setTempo(Integer.parseInt(tempo.getText().toString()));
                configuration.setTempo(tempo.getValue());
                long quoterDuration = getQuoterDuration(configuration.getTempo());
                long eightsDuration = quoterDuration/2;
                long sixteenthsDuration = quoterDuration/4;
                long tripletsDuration = quoterDuration/3;

                boolean playEights = configuration.getBeatsToPlay().get(R.id.eights);
                boolean playSixteethns = configuration.getBeatsToPlay().get(R.id.sixteenths);
                boolean playTriplets = configuration.getBeatsToPlay().get(R.id.triplets);

                long now = System.nanoTime();
                long quoterStartTime = now;
                long eighthsStartTime = now;
                long sixteenthsStartTime = now;
                long tripletsStartTime = now;
                while (playing){
                    now = System.nanoTime();
                    if (now - quoterStartTime >= quoterDuration){
                        beatsCounter = beatsCounter > configuration.getScale() ? 1 : beatsCounter;
                        setText(barCounter, Integer.toString(beatsCounter));
                        SoundPlayer.playSound(getSoundId(beatsCounter));
                        beatsCounter++;
                        quoterStartTime = now;
                        eighthsStartTime = now;
                        sixteenthsStartTime = now;
                        tripletsStartTime = now;
                    } else if(playEights && now - eighthsStartTime >= eightsDuration) {
                        SoundPlayer.playSound(2);
                        eighthsStartTime = now;
                        sixteenthsStartTime = now;
                    } else if(playSixteethns && now - sixteenthsStartTime >= sixteenthsDuration) {
                        SoundPlayer.playSound(2);
                        sixteenthsStartTime = now;
                    } else if(playTriplets && now - tripletsStartTime >= tripletsDuration) {
                        SoundPlayer.playSound(2);
                        tripletsStartTime = now;
                    }
                }
            }
        }).start();
    }

    public void goToList(View view){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
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
}
