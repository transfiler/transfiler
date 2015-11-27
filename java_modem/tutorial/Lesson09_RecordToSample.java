import java.io.IOException;
import java.util.Calendar;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.audiofile.AudioFileType;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.RecordToSample;
import net.beadsproject.beads.ugens.WavePlayer;

/**
 * This lesson shows how to use the RecordToSample class so that instead of
 * generating audio that is played over the speakers, an AudioContext generates
 * audio that is saved to a file.
 */
public class Lesson09_RecordToSample {
	public static void main(String[] args) {
		/*
		 * This example shows how to save the audio an AudioContext generates to
		 * a file. You need two additional objects:
		 * 
		 * 1. The Sample object provides a buffer for storing the generated
		 * audio stream. In previous lessons, you've seen the Sample class used
		 * to store the information from opening an audio file. Samples can also
		 * be used to store information from audio generated at runtime.
		 * 
		 * 2. The RecordToSample class is a UGen that records the audio it
		 * receives into a Sample. You can think of it as the tool in the UGen
		 * chain that redirects the audio going through the chain into a
		 * buffer to be saved to a file later.
		 */
		final AudioContext ac = new AudioContext();
		// Create the Sample we'll use for our recording buffer
		final Sample outputSample = new Sample(5000D);
		// Create the RecordToSample we'll use to take the audio generated by
		// the AudioContect and send it to the Sample.
		final RecordToSample recordToSample = new RecordToSample(ac,
				outputSample, RecordToSample.Mode.INFINITE);

		/*
		 * This clock is very similar to the one used in Lesson 07: Music. It
		 * contains a Bead() that plays a tone every time the Clock ticks.
		 * 
		 * Notice that inside the Clock, we tell the AudioContext to stop. We
		 * don't necessarily have to do that inside of the clock, but when you
		 * record a sound to a file, it's wise to programmatically stop the
		 * AudioContext when you're done generating whatever audio you want to
		 * save to a file. This is because all of the audio that the
		 * RecordToSample listens to is stored in memory in its Sample, taking
		 * up a lot of memory, so you don't want to record any more audio than
		 * you actually intend to use.
		 */
		Clock clock = new Clock(ac, 10000);
		clock.addMessageListener(
		// this is the on-the-fly bead
		new Bead() {
			public void messageReceived(Bead message) {
				Clock c = (Clock) message;
				if (c.isBeat()) {
					
					WavePlayer freqModulator = new WavePlayer(ac, 50, Buffer.SINE);
					Function function = new Function(freqModulator) {
						int timer, i, actualMessage;
						
						// Modulowana wiadomo��
						String message = "111111110000000001000000000000000000";
						
						// Funkcja obliczaj�ca aktualn� cz�stotliwo��
					    public float calculate() {
					    	// Je�li wiadomo�� si� sko�czy�a wysy�aj sygna� o f = 200Hz
				    		if (i >= message.length()) return 200.0f;
				    		
				    		// Je�li nie, to co 100ms sprawdzaj kolejny bit wiadomo�ci;
					    	if(timer != Calendar.getInstance().get(Calendar.MILLISECOND) / 100) {
					    		timer = Calendar.getInstance().get(Calendar.MILLISECOND) / 100;
					    		
					    		if (message.charAt(i) == '1') {
					    			actualMessage = 1;
					    		} else {
					    			actualMessage = 0;
					    		}
					    		i++;
					    	}
					    	// Gdy actualMessage = 1, f=1000Hz, w przeciwnym razie f = 500Hz.
					    	return actualMessage * 500.0f + 500.0f;
					    }
					  };
					
					WavePlayer wp = new WavePlayer(ac, function, Buffer.SQUARE);
					Gain g = new Gain(ac, 1, 0.1f);					
					g.addInput(wp);
					ac.out.addInput(g);
//					((Envelope) g.getGainUGen()).addSegment(0.1f, 200);
//					((Envelope) g.getGainUGen()).addSegment(0, 500,
//							new KillTrigger(g));
				}
				if (c.getCount() >= 10) {
					/*
					 * At some point, we need to tell the recordToSample to stop
					 * recording. In this example, we do so after several clock
					 * ticks.
					 * 
					 * There are three steps to saving the recorded audio:
					 * 1. Clip the recorded sample. Internally, the sample's
					 * buffer is implemented as an array that increases in
					 * size when needed. This means that the buffer has an
					 * indeterminate amount of silence at the end, which we want
					 * to remove.
					 * 2. Write the sample to a file.
					 * 3. Stop the AudioContext since we won't be using any of
					 * the audio it generates after we save the audio to a file.
					 */
					recordToSample.clip();
					Sample sample = recordToSample.getSample();
					try {
						// Writes sound to file
						// In this example, we save the file to the same
						// location that you can find the 1234.aif file used
						// by some of the previous lessons.
						sample.write("audio/mod_message.wav",
								AudioFileType.WAV);
					} catch (IOException e) {
						System.out.println("Couldn't save sonification:");
						e.printStackTrace();
					}

					// Since we've saved the audio to a file and don't
					// intend to use it for anything else, stop the
					// AudioContext. If we don't, the program will eat
					// up a lot of memory as it continues to generate
					// audio and store it in the Sample's buffer!
					ac.stop();
				}
			}
		});

		// Before we start the AudioContext, we need to make sure that the
		// RecordToSample is listening to the AudioContext's output. So we add
		// it to the UGen chain after the AudioContext's output UGen.
		recordToSample.addInput(ac.out);
		// Also add the RecordToSample as a dependent to the AudioContext so
		// that it starts recording when the AudioContext is started and is
		// killed when the AudioContext is stopped.
		ac.out.addDependent(recordToSample);
		ac.out.addDependent(clock);
		
		/* 
		 * How you start the AudioContext determines whether you will hear the
		 * audio being generated as the RecordToSample records it, or if the
		 * AudioContext generates the audio without playing it through the
		 * user's speakers.
		 */
		/* AudioContext.start() will always play the sound through the user's
		 * speakers. */
		ac.start();
		/* AudioContext.runNonRealtime() will generate the audio stream
		 * silently. One major advantage of starting an AudioContext with
		 * runNonRealtime is that the AudioContext will generate the audio
		 * stream as quickly as possible, not at the rate one would listen to
		 * the stream. For example, it should take a typical computer a fraction
		 * of a second to generate several second's worth of audio
		 * information. */
		//ac.runNonRealTime();
	}
}