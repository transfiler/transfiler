package com.example.wavplayer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import android.view.View;

import android.content.Context;

import android.widget.Button;

public class MainActivity extends Activity
{
	private Player player;
	
	private boolean playing = false;
	
	private Button button;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
		
		button = ( Button ) findViewById( R.id.button1 );
		
		player = new Player();
    }
	
	public void handleButton( View view )
	{
		if( !playing )
		{
			playing = true;
			player.play( this, R.raw.sound );
			button.setText( "Stop" );
		}
		else
		{
			playing = false;
			player.pause();
			button.setText( "Play" );
		}
	}
}
