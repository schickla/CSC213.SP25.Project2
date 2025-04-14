package edu.canisius.csc213.complaints.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.canisius.csc213.complaints.model.EmbeddingRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class EmbeddingLoader {

    /**
     * Loads complaint embeddings from a JSONL (newline-delimited JSON) file.
     * Each line must be a JSON object like:
     * {"id": "11454889", "embedding": [0.1, 0.2, 0.3, ...]}
     *
     * @param jsonlStream InputStream to the JSONL file
     * @return Map of complaint ID to embedding array
     * @throws IOException if the file cannot be read or parsed
     */
    public static Map<Long, double[]> loadEmbeddings(InputStream jsonlStream) throws IOException {
        if (jsonlStream == null) {
            System.err.println("❌ ERROR: jsonlStream is null. File could not be found!");
            throw new IllegalArgumentException("Embedding file not found in resources.");
        }

        Map<Long, double[]> embeddings = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonlStream));
        
        // Log the beginning of the file for verification
        //System.out.println("📂 Confirming first 200 chars from embedding file:");
        reader.mark(1000); // allow reset
        char[] buffer = new char[200];
        reader.read(buffer, 0, 200);
        //System.out.println(new String(buffer));
        reader.reset(); // rewind to the beginning

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            //System.out.println("🔍 Reading line: " + line);

            try {
                EmbeddingRecord record = mapper.readValue(line, EmbeddingRecord.class);
                long complaintId = Long.parseLong(record.getId());
                List<Double> list = record.getEmbedding();
                double[] array = new double[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    array[i] = list.get(i);
                }
                embeddings.put(complaintId, array);
            } catch (Exception e) {
                System.err.println("❌ Failed to parse line:");
                System.err.println(line);
                e.printStackTrace();
                throw e; // Keep this to fail fast in tests
            }
        }

        return embeddings;
    }
}
