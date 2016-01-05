package com.example.transfiler.transfiler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import com.example.transfiler.transfiler.R;

import java.util.Calendar;

public class RecordSound extends Activity
{
    // docelowy czas uspienia watku aktualizujacego gui
    private final long targetTime = 10; // 10 ms

    // poczatek pomiaru czasu
    private long startTime;

    int beforeStartGets = 0;
    String message = "";

    // watek zliczajacy czestotliwosc
    private RecorderThread rt;

    // pole wyswietlajace odebrana wiadomosc
    private TextView tv;
    // pole wyswietlajace czestotliwosc
    private TextView fv;

    // przycisk stop
    private Button stopButton;

    protected Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_record_sound );

        tv = ( TextView ) findViewById( R.id.message_view );
        fv = ( TextView ) findViewById( R.id.frequency_view );

        stopButton = ( Button ) findViewById( R.id.stop_record );

        rt = new RecorderThread();
        rt.start();

        handler = new Handler();

        handler.post( task );
    }


    // aktualizowanie pola tekstowego
    private Runnable task = new Runnable()
    {
        public void run()
        {
            startTime = System.currentTimeMillis();

            /*
            if(timer != System.currentTimeMillis() / 100) {
                timer = System.currentTimeMillis() / 100;
                //Log.d("1", Long.toString(timer));
                int freq = rt.frequency;
                if (freq < 1550 && freq > 1450) {
                    beforeStartGets++;
                }

                if (beforeStartGets >= 5) {



                    if (freq < 1450 && freq > 1250) {
                        message += " 1 ";

                    } else if (freq < 1250 && freq > 1150) {
                        message += " 0 ";
                    }
                    if (freq < 1150 && freq > 1000) {
                        tv.setText(message);
                        //Log.d("1", message);
                        beforeStartGets = 0;
                        message = "";
                    }
                }

            }
            */

            //long time = rt.timePassed;
            //tv.setText( Integer.toString( ( int ) time ) );
            int freq = rt.frequency;
            fv.setText( Integer.toString( freq ) );
            String msg = rt.message;
            tv.setText( msg );

            // ile czasu minelo w ms
            long timePassed = System.currentTimeMillis() - startTime;

            if( timePassed < targetTime )
            {
                try
                {
                    Thread.sleep( targetTime - timePassed );
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                }
            }

            handler.post(task);


        }
    };

    public void stopRecord( View view )
    {
        rt.recording = false;
    }

    public void stopRecording( View view )
    {
        rt.recording = false;
        rt.stopRecording();
    }
}
