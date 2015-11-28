package com.example.wavplayer;

import android.media.MediaPlayer;

import android.content.Context;

public class Player
{
	private MediaPlayer mediaPlayer;
	
	public Player()
	{
		mediaPlayer = new MediaPlayer();
	}
	
	public void play( String path )
	{
		try
		{
			mediaPlayer.setDataSource( path );
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void play( Context context, int id )
	{
		try
		{
			mediaPlayer = MediaPlayer.create( context, id );
			mediaPlayer.start();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void pause()
	{
		mediaPlayer.pause();
	}
}