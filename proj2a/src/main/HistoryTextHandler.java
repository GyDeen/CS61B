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

        String response = "";
        for (String word : words) {
            map.put(word, nGrammap.weightHistory(word, startYear, endYear));
        }

        StringBuilder wordResponse = new StringBuilder();
        for (String word: words) {
            wordResponse.append(word).append(": {").append(map.get(word).toString()).append("}\n");
        }

        return response;
    }
}
