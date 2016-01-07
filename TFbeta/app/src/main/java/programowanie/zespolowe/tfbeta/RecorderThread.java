package programowanie.zespolowe.tfbeta;



import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.AudioFormat;
import android.util.Log;

class RecorderThread extends Thread
{
    // czestotliwosc probkowania
    public static final int SAMPLE_RATE = 44100;

    //public static final int BUFFER_SIZE = 4096;

    // tryb kodowania
    public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    // 'sterowanie' nagrywaniem
    public boolean recording;

    public long timePassed;

    // zmienna przechowujaca ostatnio zarejestrowana czestotliwosc
    public int frequency;

    private AudioRecord recorder = null;

    // ##############################################

    // Uwaga na bufor przy czestotliwosci probkowania >8000
    // rozmiar klatki moze przekroczyc rozmiar glownego bufora
    // np. przy probkowaniu 44,1kHz i buforze 4kB, wtedy czas bufora <100ms
    public static final int TIME_FRAME = 10; // ms

    // czestotliwosci wiadomosci
    // Hz
    public static final int HIGHER_PITCH = 1500;
    public static final int LOWER_PITCH = 1000;

    public static final int KEY_FREQ = 750;

    // wymagana ilosc identycznych prob
    public static final int testSamplesRequired = 8;

    // ostatnia czestotliwosc
    private int lastFrequency = 0;
    private int currentTestSample = 0;

    public boolean reading = false;

    public boolean isDone = false;

    public String message = "";

    // ##############################################

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

        try
        {
            bufferSize = AudioRecord.getMinBufferSize( SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, ENCODING );
            //bufferSize = BUFFER_SIZE;
            //bufferSize = 1024 * 50 * 60;
            // uwaga na przepelnienie bufora!
            recorder = new AudioRecord( AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, ENCODING, 2 * bufferSize );

            data = new short[ bufferSize ];

            recording = true;

            //while( recorder.getState() != android.media.AudioRecord.STATE_INITIALIZED );
            //RecordSound.stopButton.setEnabled(true);
            recorder.startRecording();


            recorder.read( data, 0, bufferSize );

            while( recording )
            {
                long startTime = System.currentTimeMillis();
                // sprawdzanie, czy nagrywanie zainicjowane
                if( recorder.getState() == android.media.AudioRecord.STATE_INITIALIZED )
                {
                    // sprawdzanie, czy nagrywanie jest w toku
                    if( recorder.getRecordingState() == android.media.AudioRecord.RECORDSTATE_STOPPED ) recorder.startRecording();
                        // gdy nagrywanie jest w toku
                    else
                    {
                        // czytanie danych z mikrofonu
                        recorder.read( data, 0, bufferSize );

                        int samplesFrame = SAMPLE_RATE * TIME_FRAME / 1000;
                        zeroCrossings = 0;

                        for( int j = 0; j + samplesFrame < bufferSize - 1; j += samplesFrame )
                        {
                            zeroCrossings = 0;

                            for( int i = j; i < j + samplesFrame; i++ )
                            {
                                if( data[i] > 0 && data[i + 1] <= 0 ) zeroCrossings++;
                                if( data[i] < 0 && data[i + 1] >= 0 ) zeroCrossings++;
                            }

                            frequency = SAMPLE_RATE * zeroCrossings / samplesFrame / 2;

                            handleFrequency();

                            if( isDone )
                            {
                                recording = false;
                                break;
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            // przerwanie nagrywania
            if( recorder.getState() == android.media.AudioRecord.RECORDSTATE_RECORDING ) recorder.stop();

            recorder.release();
            recorder = null;

        }
    }

    private void handleFrequency()
    {
        if( isWithinRange( frequency, HIGHER_PITCH ) )
        {
            if( lastFrequency == HIGHER_PITCH )
            {
                currentTestSample++;
            }
            else
            {
                lastFrequency = HIGHER_PITCH;
                currentTestSample = 1;
            }
        }
        else if( isWithinRange( frequency, LOWER_PITCH ) )
        {
            if( lastFrequency == LOWER_PITCH )
            {
                currentTestSample++;
            }
            else
            {
                lastFrequency = LOWER_PITCH;
                currentTestSample = 1;
            }
        }
        else if( isWithinRange( frequency, KEY_FREQ ) )
        {
            if( lastFrequency == KEY_FREQ )
            {
                currentTestSample++;
            }
            else
            {
                lastFrequency = KEY_FREQ;
                currentTestSample = 1;
            }
        }
        else
        {
            lastFrequency = 0;
            currentTestSample = 0;
        }

        if( currentTestSample == testSamplesRequired )
        {
            currentTestSample = 0;

            switch( lastFrequency )
            {
                case HIGHER_PITCH:
                    message += '1';
                    break;
                case LOWER_PITCH:
                    message += '0';
                    break;
                case KEY_FREQ:
                    //message += 'E';
                    isDone = true;
                    break;
                default:
                    break;
            }

            lastFrequency = 0;
        }
    }

    public void stopRecording()
    {
        if( recorder.getState() == android.media.AudioRecord.RECORDSTATE_RECORDING ) recorder.stop();

        recorder.release();
        //recorder = null;
    }

    // czy liczba w zakresie +- szukanej czestotliwosci
    public static boolean isWithinRange( int number, int freq )
    {
        //return number >= freq - freq / 10 && number <= freq + freq / 10;
        return number >= freq - 50 && number <= freq + 50;
    }
}