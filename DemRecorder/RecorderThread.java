package com.example.mk.app1;

import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.AudioFormat;

class RecorderThread extends Thread
{
	// czestotliwosc probkowania
	public static final int SAMPLE_RATE = 8000;
	
	// tryb kodowania
	public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
	// 'sterowanie' nagrywaniem
	public boolean recording;
	
	// zmienna przechowujaca ostatnio zarejestrowana czestotliwosc
	public int frequency;
	
	private AudioRecord recorder = null;
	
	public RecorderThread()
	{
	}
	
	@Override
	public void run()
	{
		// dane z mikrofonu w formacie pcm
		short data[];
		
		// rozmiar bufora
		int bufferSize;
		
		// ilosc przeciec z osia czasu
		int zeroCrossings;
		
		bufferSize = AudioRecord.getMinBufferSize( SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, ENCODING );
		recorder = new AudioRecord( AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, ENCODING, bufferSize );
		
		data = new short[bufferSize];
		
		recording = true;
		
		while( recording )
		{
			// sprawdzanie, czy nagrywanie zainicjowane
			if( recorder.getState() == AudioRecord.STATE_INITIALIZED )
			{
				// sprawdzanie, czy nagrywanie jest w toku
				if( recorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED ) recorder.startRecording();
				// gdy nagrywanie jest w toku
				else
				{
					// czytanie danych z mikrofonu
					recorder.read( data, 0, bufferSize );
					
					zeroCrossings = 0;
					
					// zliczanie przeciec z osia czasu
					for( int i = 0; i < bufferSize - 1; i++ )
					{
						if( data[i] > 0 && data[i + 1] <= 0 ) zeroCrossings++;
						if( data[i] < 0 && data[i + 1] >= 0 ) zeroCrossings++;
					}
					
					frequency = SAMPLE_RATE * zeroCrossings / bufferSize;
				}
			}
        }
		// przerwanie nagrywania
        if( recorder.getState() == AudioRecord.RECORDSTATE_RECORDING ) recorder.stop();
		
        recorder.release();
        recorder = null;
	}
}