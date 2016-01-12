package programowanie.zespolowe.tfbeta;


import android.app.Activity;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;


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
    public static TextView tv;
    // pole wyswietlajace czestotliwosc
    public static TextView fv;

    // przycisk stop
    public static Button stopButton;

    public static Button saveButton;

    protected Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sound);

        tv = ( TextView ) findViewById( R.id.message_view );
        fv = ( TextView ) findViewById( R.id.frequency_view );

        stopButton = ( Button ) findViewById( R.id.stop_record );
        saveButton = ( Button ) findViewById( R.id.save_file );

        handler = new Handler();

        handler.post( task );
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        stopButton.setEnabled( true );
        saveButton.setEnabled( false );

        rt = new RecorderThread();
        rt.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        rt.stopRecording();
    }

    // aktualizowanie pola tekstowego
    private Runnable task = new Runnable()
    {
        public void run()
        {
            startTime = System.currentTimeMillis();



            //long time = rt.timePassed;
            //tv.setText( Integer.toString( ( int ) time ) );
            int freq = rt.getFrequency();
            fv.setText( Integer.toString( freq ) );
            String data = rt.getData();
            tv.setText( data );

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

            if (rt.isDone()) {
                saveButton.setEnabled(true);
                stopButton.setEnabled(false);
            }

            handler.post(task);


        }
    };

    public void stopRecording( View view )
    {
        rt.stopRecording();
    }

    public void onClickSave( View view ) {
        saveButton.setEnabled(false);
        Log.d("AAAA", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "a.txt");
        try {
            byte[] byteArray = Converter.Convert2file(rt.getData());
            Converter.writeBinaryFile(byteArray, path);
        } catch( IOException e) {}

    }

}
