package ngrams;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.Collection;
import java.util.HashMap;

import static ngrams.TimeSeries.MAX_YEAR;
import static ngrams.TimeSeries.MIN_YEAR;

/**
 * An object that provides utility methods for making queries on the
 * Google NGrams dataset (or a subset thereof).
 *
 * An NGramMap stores pertinent data from a "words file" and a "counts
 * file". It is not a map in the strict sense, but it does provide additional
 * functionality.
 *
 * @author Josh Hug
 */
public class NGramMap {

    private static final int WORD = 0;
    private static final int WORD_YEAR = 1;
    private static final int WORD_COUNT = 2;

    private static final int COUNT_YEAR = 0;
    private static final int WORDS_COUNT = 1;

    HashMap<String, TimeSeries> theMap;
    TimeSeries wordsForYear;


    /**
     * Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME.
     */
    public NGramMap(String wordsFilename, String countsFilename) {
        try {
            BufferedReader brWords = new BufferedReader(new FileReader(wordsFilename));
            BufferedReader brCounts = new BufferedReader(new FileReader(countsFilename));

            String text;

            theMap = new HashMap<>();
            while ((text = brWords.readLine()) != null) {
                String[] parts = text.split("\t");

                /* If the words is not in the current NGramMap, using the words as the key to the TimeSeries
                *  that stores the frequency of each year. If it is in the current NGramMap, add the words frequency
                *  to current records of that year frequency */
                if (!theMap.containsKey(parts[WORD])) {
                    TimeSeries newTs = new TimeSeries();
                    newTs.put(Integer.parseInt(parts[WORD_YEAR]), Double.parseDouble(parts[WORD_COUNT]));
                    theMap.put(parts[WORD], newTs);
                } else {
                    TimeSeries newTs = new TimeSeries();
                    newTs.put(Integer.parseInt(parts[WORD_YEAR]), Double.parseDouble(parts[WORD_COUNT]));

                    TimeSeries combined = theMap.get(parts[WORD]).plus(newTs);
                    theMap.put(parts[WORD], combined);
                }
            }

            wordsForYear = new TimeSeries();
            while ((text = brCounts.readLine()) != null) {
                String[] parts = text.split(",");
                wordsForYear.put(Integer.parseInt(parts[COUNT_YEAR]), Double.parseDouble(parts[WORDS_COUNT]));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }


    }

    /**
     * Provides the history of WORD between STARTYEAR and ENDYEAR, inclusive of both ends. The
     * returned TimeSeries should be a copy, not a link to this NGramMap's TimeSeries. In other
     * words, changes made to the object returned by this function should not also affect the
     * NGramMap. This is also known as a "defensive copy". If the word is not in the data files,
     * returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word, int startYear, int endYear) {
        return new TimeSeries(theMap.get(word), startYear, endYear);
    }

    /**
     * Provides the history of WORD. The returned TimeSeries should be a copy, not a link to this
     * NGramMap's TimeSeries. In other words, changes made to the object returned by this function
     * should not also affect the NGramMap. This is also known as a "defensive copy". If the word
     * is not in the data files, returns an empty TimeSeries.
     */
    public TimeSeries countHistory(String word) {
        TimeSeries ts = theMap.get(word);
        if (ts == null) { return new TimeSeries();}
        return new TimeSeries().plus(theMap.get(word));
    }

    /**
     * Returns a defensive copy of the total number of words recorded per year in all volumes.
     */
    public TimeSeries totalCountHistory() {
        return new TimeSeries().plus(wordsForYear);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD between STARTYEAR
     * and ENDYEAR, inclusive of both ends. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        TimeSeries wordCounts = countHistory(word, startYear, endYear);
        TimeSeries totalCounts = totalCountHistory();
        return wordCounts.dividedBy(totalCounts);
    }

    /**
     * Provides a TimeSeries containing the relative frequency per year of WORD compared to all
     * words recorded in that year. If the word is not in the data files, returns an empty
     * TimeSeries.
     */
    public TimeSeries weightHistory(String word) {
        TimeSeries wordCounts = countHistory(word);
        TimeSeries totalCounts = totalCountHistory();
        return wordCounts.dividedBy(totalCounts);
    }

    /**
     * Provides the summed relative frequency per year of all words in WORDS between STARTYEAR and
     * ENDYEAR, inclusive of both ends. If a word does not exist in this time frame, ignore it
     * rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words,
                                          int startYear, int endYear) {

        TimeSeries total_sum = new TimeSeries();
        for (String word : words) {
            total_sum = total_sum.plus(weightHistory(word, startYear, endYear));
        }
        return total_sum;
    }

    /**
     * Returns the summed relative frequency per year of all words in WORDS. If a word does not
     * exist in this time frame, ignore it rather than throwing an exception.
     */
    public TimeSeries summedWeightHistory(Collection<String> words) {
        TimeSeries total_sum = new TimeSeries();
        for (String word : words) {
            total_sum = total_sum.plus(weightHistory(word));
        }
        return total_sum;
    }
}
