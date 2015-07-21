package ru.greatbit.metrostore.beans;

import java.util.HashMap;
import java.util.Map;

import ru.greatbit.metrostore.R;

/**
 * Created by azee on 22.07.15.
 */
public class SongConfiguration {
    protected String name;
    protected int scale;
    protected int tempo;
    protected Map<Integer, Boolean> beatsToPlay = new HashMap<>(4);

    public SongConfiguration() {
        beatsToPlay.put(R.id.quoters, true);
        beatsToPlay.put(R.id.eights, false);
        beatsToPlay.put(R.id.sixteens, false);
        beatsToPlay.put(R.id.triols, false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public Map<Integer, Boolean> getBeatsToPlay() {
        return beatsToPlay;
    }
}
