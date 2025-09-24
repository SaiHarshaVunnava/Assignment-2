package com.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {
    private final Map<String, Set<String>> documentWordMapping = new HashMap<>();

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> wordSet = new HashSet<>();
        for (Text value : values)
            wordSet.addAll(Arrays.asList(value.toString().split(",")));

        documentWordMapping.put(key.toString(), wordSet);
        for (Map.Entry<String, Set<String>> entry : documentWordMapping.entrySet()) {
            String existingDocument = entry.getKey();
            Set<String> existingWordSet = entry.getValue();
            if (existingDocument.equals(key.toString())) {
                continue;
            }

            // Calculate Jaccard Similarity
            Set<String> intersectionWordSet = new HashSet<>(existingWordSet);
            intersectionWordSet.retainAll(wordSet);

            Set<String> unionWordSet = new HashSet<>(existingWordSet);
            unionWordSet.addAll(wordSet);

            double jaccardSimilarity = (double) intersectionWordSet.size() / unionWordSet.size();
            double similarityPercentageOfWordSet = Math.round(jaccardSimilarity * 100.0) / 100.0;

            if (similarityPercentageOfWordSet >= 0.01) {
                String nameOfDocument1 = existingDocument;
                String nameOfDocument2 = key.toString();
                context.write(new Text("(" + nameOfDocument2 + ", " + nameOfDocument1 + ")"), new Text("-> " + similarityPercentageOfWordSet ));
            }
        }
    }
}