import ngrams.TimeSeries;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/** Unit Tests for the TimeSeries class.
 *  @author Josh Hug
 */
public class TimeSeriesTest {
    @Test
    public void testFromSpec() {
        TimeSeries catPopulation = new TimeSeries();
        catPopulation.put(1991, 0.0);
        catPopulation.put(1992, 100.0);
        catPopulation.put(1994, 200.0);

        TimeSeries dogPopulation = new TimeSeries();
        dogPopulation.put(1994, 400.0);
        dogPopulation.put(1995, 500.0);

        TimeSeries totalPopulation = catPopulation.plus(dogPopulation);
        // expected: 1991: 0,
        //           1992: 100
        //           1994: 600
        //           1995: 500

        List<Integer> expectedYears = new ArrayList<>
                (Arrays.asList(1991, 1992, 1994, 1995));

        assertThat(totalPopulation.years()).isEqualTo(expectedYears);

        List<Double> expectedTotal = new ArrayList<>
                (Arrays.asList(0.0, 100.0, 600.0, 500.0));

        for (int i = 0; i < expectedTotal.size(); i += 1) {
            assertThat(totalPopulation.data().get(i)).isWithin(1E-10).of(expectedTotal.get(i));
        }
    }

    @Test
    public void testEmptyBasic() {
        TimeSeries catPopulation = new TimeSeries();
        TimeSeries dogPopulation = new TimeSeries();

        assertThat(catPopulation.years()).isEmpty();
        assertThat(catPopulation.data()).isEmpty();

        TimeSeries totalPopulation = catPopulation.plus(dogPopulation);

        assertThat(totalPopulation.years()).isEmpty();
        assertThat(totalPopulation.data()).isEmpty();
    }

    @Test
    public void testPlus() {
        TimeSeries catPopulation = new TimeSeries();
        catPopulation.put(1991, 0.0);
        catPopulation.put(1992, 100.0);
        catPopulation.put(1994, 200.0);
        catPopulation.put(1995, 400.0);
        catPopulation.put(1996, 500.0);


        TimeSeries catPopulation2 = new TimeSeries();
        catPopulation2.put(1990, 30.0);
        catPopulation2.put(1994, 400.0);
        catPopulation2.put(1995, 500.0);
        catPopulation2.put(1996, 600.0);
        catPopulation2.put(1997, 700.0);

        TimeSeries totalCatPopulation = catPopulation.plus(catPopulation2);
        List<Integer> expectedYears = new ArrayList<>
                (Arrays.asList(1990, 1991, 1992, 1994, 1995, 1996, 1997));
        assertThat(totalCatPopulation.years()).isEqualTo(expectedYears);

        List<Double> expectedPopulation = new ArrayList<>
                (Arrays.asList(30.0, 0.0, 100.0, 600.0, 900.0, 1100.0, 700.0));
        assertThat(totalCatPopulation.data()).isEqualTo(expectedPopulation);
    }
} 