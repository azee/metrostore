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
import ru.greatbit.metrostore.beans.ParamKey;
import ru.greatbit.metrostore.beans.ResponseCode;
import ru.greatbit.metrostore.beans.SongConfiguration;
import ru.greatbit.metrostore.utils.sound.SoundPlayer;


public class MainActivity extends AppCompatActivity {

    private boolean playing = false;

    SongConfiguration configuration = new SongConfiguration();;

    //private EditText tempo;
    private EditText scale;
    private Button button;
    private TextView barCounter;
    private NumberPicker tempo;

    private ToggleButton quoters;
    private ToggleButton eights;
    private ToggleButton sixteenths;
    private ToggleButton triplets;

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
                configuration.setTempo(tempo.getValue());
                long quoterDuration = getQuoterDuration(configuration.getTempo());
                long eightsDuration = quoterDuration/2;
                long sixteenthsDuration = quoterDuration/4;
                long tripletsDuration = quoterDuration/3;

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
                    } else if(configuration.getBeatsToPlay().get(R.id.eights)
                            && now - eighthsStartTime >= eightsDuration) {
                        SoundPlayer.playSound(2);
                        eighthsStartTime = now;
                        sixteenthsStartTime = now;
                    } else if(configuration.getBeatsToPlay().get(R.id.sixteenths)
                            && now - sixteenthsStartTime >= sixteenthsDuration) {
                        SoundPlayer.playSound(2);
                        sixteenthsStartTime = now;
                    } else if(configuration.getBeatsToPlay().get(R.id.triplets) && now - tripletsStartTime >= tripletsDuration) {
                        SoundPlayer.playSound(2);
                        tripletsStartTime = now;
                    }
                }
            }
        }).start();
    }

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
}
