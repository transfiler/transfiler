package programowanie.zespolowe.tfbeta;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.zip.CRC32;


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

    // Czy otrzymany hash jest zgodny
    private boolean invalidHash = false;

    EditText et;

    protected Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sound);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        tv = ( TextView ) findViewById( R.id.message_view );
        fv = ( TextView ) findViewById( R.id.frequency_view );

        et = (EditText) findViewById(R.id.fileNameET);

        handler = new Handler();

        handler.post(task);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        invalidHash = false;
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

            updateGUI( rt.getFrequency(), rt.getData() );

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
                doneRecording( rt.getData() );

                return;
            }

            handler.post(task);


        }
    };

    private void updateGUI( int frequency, String data )
    {
        fv.setText(Integer.toString(frequency));
        tv.setText( data );
    }

    private void doneRecording( String data )
    {
        // Sprawdzanie poprawnosci CRC
        if( data.length() < 64 )
        {
            invalidHash = true;
        }
        else
        {
            String strhash = data.substring( data.length() - 1 - 64 );

            long hash = 0;

            for( int i = 0; i < strhash.length(); i++ )
            {
                hash <<= 1;
                hash += strhash.charAt( i ) == '1' ? 1 : 0;
            }

            CRC32 crc = new CRC32();
            // Start index - inclusive, end index - exclusive
            crc.update( data.substring( 0, data.length() - 64 ).getBytes() );

            invalidHash = hash != crc.getValue();
        }

        Context context = getApplicationContext();
        CharSequence text;

        if( invalidHash ) text = "File upload failed";
        else text = "File uploaded successfully";

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText( context, text, duration );
        toast.show();

        tv.setText( data );
    }

    public void stopRecording( View view )
    {
        rt.stopRecording();
    }

    public void onClickSave( View view ) {

        Log.d("AAAA", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  et.getText().toString());
        try {
            String data = rt.getData();
            
            if( !invalidHash ) data = data.substring( 0, data.length() - 64 );

            byte[] byteArray = Converter.Convert2file( data );
            Converter.writeBinaryFile(byteArray, path);
        } catch( IOException e) {}

    }
    public void backListening(View v){
        finish();
    }

}
