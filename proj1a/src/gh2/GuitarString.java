package gh2;
import deque.*;




//Note: This file will not compile until you complete the Deque61B implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private final Deque61B<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {

        int capacity = (int) Math.round(SR / frequency), i = 0;
        this.buffer = new LinkedListDeque61B<>();

        while (i < capacity) {
            buffer.addFirst(0.0);
            i++;
        }

    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {

        int i = 0;
        while (i < buffer.size()) {
            double r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addFirst(r);
            i++;
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {

        // applying Karplus_strong algorithm
        double first = buffer.removeFirst();
        double second = buffer.get(0);
        double newSample = (first + second) / 2 * DECAY;
        buffer.addLast(newSample);


    }

    /* Return the double at the front of the buffer. */
    public double sample() {

        return buffer.get(0);
    }
}

