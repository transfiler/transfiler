package com.example.transfiler.transfiler;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Bundle;

public class PlaySound extends Activity {

    String message = "10111100100101101011110010010110";
    double frequencies[] = new double[30];


    private final int duration = 3;
    private final int sampleRate = 10000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private double freqOfTone = 440; // hz

    private final byte generatedSnd[] = new byte[2 * numSamples];

    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_sound);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < 30; i++) {
            if (message.charAt(i) == '1') {
                frequencies[i] = 1000;
            } else {
                frequencies[i] = 500;
            }
        }

        // Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                genTone();
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
    }

    void genTone(){

        int j = 0;
        for (int i = 0; i < numSamples; ++i) {
            if (i % 1000 == 0) {
                freqOfTone = frequencies[j];
                j++;
            }
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }


        int idx = 0;
        for (final double dVal : sample) {
            final short val = (short) ((dVal * 32767));
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
}
