import java.util.Calendar;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;


public class Lesson03_FMSynthesis {

	public static void main(String[] args) {
		AudioContext ac;

		  ac = new AudioContext();
		  /*
		   * In the last example, we used an Envelope to
		   * control the frequency of a WavePlayer.
		   * 
		   * In this example, we'll use another WavePlayer.
		   * This is called FM synthesis.
		   * 
		   * Here's the modulating WavePlayer. It has a low 
		   * frequency.
		   */
		  WavePlayer freqModulator = new WavePlayer(ac, 4, Buffer.SINE);
		  /*
		   * The next line might look outrageous if you're not
		   * experienced in Java. Basically we're defining a 
		   * Function on the fly which takes the freqModulator
		   * and maps it to a sensible range. Since the input
		   * to the function is a sine wave (freqModulator), the
		   * output will be a sine wave that goes from 500 to 700,
		   * 50 times a second.
		   */
		  Function function = new Function(freqModulator) {
			int timer, i, actualMessage;
			String message = "1111111111000000000011100111110000110000";
		    public float calculate() {
	    		if (i >= message.length()) return 200.0f;
		    	if(timer != Calendar.getInstance().get(Calendar.MILLISECOND) / 100) {
		    		timer = Calendar.getInstance().get(Calendar.MILLISECOND) / 100;
		    		
		    		if (message.charAt(i) == '1') {
		    			actualMessage = 1;
		    		} else {
		    			actualMessage = 0;
		    		}
		    		i++;
		    	}
		    	return actualMessage * 500.0f + 500.0f;
		    }
		  };
		  /*
		   * Here's the WavePlayer that will actually play.
		   * Now we plug in the function. Compare this to the previous
		   * example, where we plugged in an envelope.
		   */
		  WavePlayer wp = new WavePlayer(ac, function, Buffer.SINE);
		  /*
		   * Connect it all together as before.
		   */

		  
		  Gain g = new Gain(ac, 1, 0.1f);
		  g.addInput(wp);
		  ac.out.addInput(g);
		  ac.start();
		
	}
}
