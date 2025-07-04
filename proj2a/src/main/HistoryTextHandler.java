package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import ngrams.NGramMap;
import ngrams.TimeSeries;

import java.util.HashMap;
import java.util.List;

public class HistoryTextHandler extends NgordnetQueryHandler {

    NGramMap nGrammap;

    public HistoryTextHandler(NGramMap map) {
        nGrammap = map;
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> words = q.words();
        int startYear = q.startYear();
        int endYear = q.endYear();

        HashMap<String, TimeSeries> map = new HashMap<>();

        for (String word : words) {
            map.put(word, nGrammap.weightHistory(word, startYear, endYear));
        }

        String respond = "";
        for (String word: words) {
            respond += word + ": " + map.get(word).toString() + "\n";
        }

        return respond;
    }
}
