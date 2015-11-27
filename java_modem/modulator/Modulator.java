import java.io.IOException;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.audiofile.AudioFileType;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.RecordToSample;
import net.beadsproject.beads.ugens.WavePlayer;

public class Modulator {
	
	static String bitMessage = "1000111111000100101010010110010101001010111100000111";
	static long startTimer;

	
	public static void main(String[] args) {
	
		final AudioContext ac = new AudioContext();
		final Sample outputSample = new Sample(5000D);
		final RecordToSample recordToSample = new RecordToSample(ac,
				outputSample, RecordToSample.Mode.INFINITE);


		Clock clock = new Clock(ac, 10000);
		clock.addMessageListener(
		new Bead() {
			public void messageReceived(Bead message) {
				Clock c = (Clock) message;
				if (c.isBeat()) {
					startTimer = System.currentTimeMillis();

					WavePlayer freqModulator = new WavePlayer(ac, 100, Buffer.SINE);
					Function function = new Function(freqModulator) {
						int i, actualMessage;
						long timer;
						boolean beforeStart;
						
						
						// Funkcja obliczaj¹ca aktualn¹ czêstotliwoœæ
					    public float calculate() {
					    	
					    	beforeStart = (System.currentTimeMillis() < startTimer + 500);
					    	if (beforeStart) return 700.0f;
					    	// Jeœli wiadomoœæ siê skoñczy³a wysy³aj sygna³ o f = 200Hz
				    		if (i >= bitMessage.length()) return 800.0f;
				    		
				    		
				    		// Jeœli nie, to co 100ms sprawdzaj kolejny bit wiadomoœci;
					    	if(timer != System.currentTimeMillis() / 100) {
					    		timer = System.currentTimeMillis() / 100;
					    		
					    		if (bitMessage.charAt(i) == '1') {
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

				}

				if (System.currentTimeMillis() > startTimer + bitMessage.length() * 100 + 2000) {
					recordToSample.clip();
					Sample sample = recordToSample.getSample();
					try {
						sample.write("audio/mod_message.wav",
								AudioFileType.WAV);
					} catch (IOException e) {
						System.out.println("Couldn't save sonification:");
						e.printStackTrace();
					}

					ac.stop();
				}
			}
		});

		recordToSample.addInput(ac.out);

		ac.out.addDependent(recordToSample);
		ac.out.addDependent(clock);
		

		ac.start();

	}
}