package programowanie.zespolowe.tfbeta;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Bundle;

import java.util.zip.CRC32;

public class PlaySound extends Activity {

    String message = "1101011E";
    //double frequencies[] = new double[message.length() + 30];
    double frequencies[];


    //private final int duration = 2 + message.length() / 10;
    private int duration;
    private final int sampleRate = 10000;
    //private final int numSamples = duration * sampleRate;
    private int numSamples;
    //private final double sample[] = new double[numSamples];
    private double sample[];
    private double freqOfTone = 440; // hz

    //private final byte generatedSnd[] = new byte[2 * numSamples];
    private byte generatedSnd[];

    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_sound);

        Bundle extras = getIntent().getExtras();

        /*
        if( extras != null )
        {
            message = extras.getString( "message" );
        }
        */

        String msg = "";

        if( extras != null )
        {
            msg = extras.getString( "message" );
        }

        // ################ CRC ################
        CRC32 crc = new CRC32();
        crc.update( msg.getBytes() );

        long hash = crc.getValue();

        String strhash = "";

        // Konwersja long hash na binarny String
        for( int i = 64 - 1; i >= 0; i-- )
        {
            strhash += ( ( hash >> i ) & 1 ) == 1 ? '1' : '0';
        }

        // hash na koniec
        msg += strhash;

        // Znaki specjalne
        message = "";

        for( int i = 0; i < msg.length(); i++ )
        {
            message += msg.charAt( i );

            if( ( i - 1 ) % 4 == 0 )
            {
                message += 'x';
            }
        }

        message += 'E';

        frequencies = new double[message.length() + 30];

        duration = 2 + message.length() / 10;
        numSamples = duration * sampleRate;
        sample = new double[numSamples];
        generatedSnd = new byte[2 * numSamples];
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < 10; i++) {
            frequencies[i] = 150;
        }
        for (int i = 10; i < 10 + message.length(); i++) {
            if (message.charAt(i - 10) == '1') {
                frequencies[i] = 1500;
            } else if( message.charAt( i - 10 ) == '0' ){
                frequencies[i] = 1000;
            }
            else if( message.charAt( i - 10 ) == 'x' )
            {
                frequencies[ i ] = 1250;
            }
            else
            {
                frequencies[ i ] = 750;
            }
        }
        for (int i = 10 + message.length(); i < frequencies.length; i++) {
            frequencies[i] = 150;
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
