package ru.greatbit.metrostore;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;


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

//    public void togglePlay(View view){
//        playing = !playing;
//        button.setText(playing ? "Stop" : "Start");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int beatsCounter = 0;
//                int scaleVal = Integer.parseInt(scale.getText().toString());
//                int bitrateVal = Integer.parseInt(bitrate.getText().toString());
//                double duration = getDuration(bitrateVal, scaleVal);
//                while (playing){
//                    beatsCounter = beatsCounter >= scaleVal ? 0 : beatsCounter;
//                    playTone(getTone(scaleVal, beatsCounter), duration);
//                    playTone(0, duration);
//                    beatsCounter++;
//                }
//            }
//        }).start();
//    }

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
                        //playTone(500, 1d/10d);
                        MusicManager.getInstance().play(this, R.raw.my_sound);
                        setText(barCounter, Integer.toString(beatsCounter));
                        startTime = now;
                        beatsCounter++;
                    }
                }
            }
        }).start();
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

    private double getTone(int scale, int beatsCount){
        if (beatsCount == 0){
            return 500;
        }
        return 440;
    }

    public void playTone(double freqOfTone, double duration) {
        int sampleRate = 8000;              // a number

        double dnumSamples = duration * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];


        for (int i = 0; i < numSamples; ++i) {      // Fill the sample array
            sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalized.
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        int i = 0 ;

        int ramp = numSamples / 20 ;                                    // Amplitude ramp as a percent of sample count


        for (i = 0; i< ramp; ++i) {                                     // Ramp amplitude up (to avoid clicks)
            double dVal = sample[i];
            // Ramp up to maximum
            final short val = (short) ((dVal * 32767 * i/ramp));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }


        for (i = i; i< numSamples - ramp; ++i) {                        // Max amplitude for most of the samples
            double dVal = sample[i];
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = i; i< numSamples; ++i) {                               // Ramp amplitude down
            double dVal = sample[i];
            // Ramp down to zero
            final short val = (short) ((dVal * 32767 * (numSamples-i)/ramp ));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = null;                                   // Get audio track
        try {
            int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();                                          // Play the track
            audioTrack.write(generatedSnd, 0, generatedSnd.length);     // Load the track
        }
        catch (Exception e){
        }
        if (audioTrack != null) audioTrack.release();           // Track play done. Release track.
    }
}
