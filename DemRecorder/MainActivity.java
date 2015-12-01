package com.example.mk.app1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.TextView;

import com.example.qwerty.R;

import java.util.Calendar;

public class MainActivity extends Activity
{
	// czas uspienia watku aktualizujacego gui
	long timer;
	int beforeStartGets = 0;
	String message = "";
	
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
        setContentView( R.layout.mainactivity);
		
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

			handler.post(task);
		}
	};
	
	public void stopRecord( View view )
	{
		rt.recording = false;
	}
}
