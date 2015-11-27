import java.util.Calendar;

import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.Frequency;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.SamplePlayer;

public class Demodulator {

	int i, timer;
	AudioContext ac;
	PowerSpectrum ps;
	Frequency f;
	static String output = "";
	static boolean endOfMessage = false;


	void setup() {
	  
	  ac = new AudioContext(); 
	  
	  Gain g = new Gain(ac, 2, (float) 0.3);
	  ac.out.addInput(g);
	  
	  SamplePlayer player = null;
	  try
	  {
		Sample sample = new Sample("audio/mod_message.wav");
	    player = new SamplePlayer(ac, sample); 
	    g.addInput(player); 
	  }
	  catch(Exception e)
	  {
	    e.printStackTrace(); 
	  }

	 
	  ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
	  sfs.addInput(ac.out);
	  
	
	  FFT fft = new FFT();
	  sfs.addListener(fft); 
	  
	  ps = new PowerSpectrum(); 
	  fft.addListener(ps); 
	  f = new Frequency(44100.0f);
	  ps.addListener(f);

	  ac.out.addDependent(sfs); 
	  ac.start(); 
	}

	
	void draw() {
		if(timer != Calendar.getInstance().get(Calendar.MILLISECOND) / 100) {
    		timer = Calendar.getInstance().get(Calendar.MILLISECOND) / 100;

    		if (f.getFeatures() != null) {
    			float inputFrequency = f.getFeatures();
    			if (inputFrequency > 475 && inputFrequency < 525) {
    				output += "0";
    			} else if (inputFrequency > 975 && inputFrequency < 1025) {
    				output += "1";
    			} else if (inputFrequency > 775 && inputFrequency < 825) {
    				endOfMessage = true;
    			}
    			i++;
    		}
		}

	}


	public static void main(String[] args) {

		Demodulator dem2 = new Demodulator();
		dem2.setup();
		while (!endOfMessage) {
			dem2.draw();
		}
		System.out.println(output);
	}

}
