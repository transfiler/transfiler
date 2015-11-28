package com.example.qwerty;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.widget.TextView;

public class MainActivity extends Activity
{
	// czas uspienia watku aktualizujacego gui
	public static final int THREAD_SLEEP_TIME = 10;
	
	// watek zliczajacy czestotliwosc
	private RecorderThread rt;
	
	// pole wyswietlajace czestotliwosc
	private TextView tv;
	
	protected Handler handler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
		
		tv = ( TextView ) findViewById( R.id.freq_view );
		
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
			int freq = rt.frequency;
			
			tv.setText( Integer.toString( freq ) + "Hz" );
			
			handler.postDelayed( task, THREAD_SLEEP_TIME );
		}
	};
	
	public void stopRecord( View view )
	{
		rt.recording = false;
	}
}
