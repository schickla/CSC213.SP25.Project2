package edu.canisius.csc213.complaints.storage;

import com.opencsv.bean.CsvToBeanBuilder;
import edu.canisius.csc213.complaints.model.Complaint;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Handles loading of complaints and embedding data,
 * and returns a fully hydrated list of Complaint objects.
 */
public class ComplaintLoader {

    /**
     * Loads complaints from a CSV file and merges with embedding vectors from a JSONL file.
     *
     * @param csvPath    Resource path to the CSV file
     * @param jsonlPath  Resource path to the JSONL embedding file
     * @return A list of Complaint objects with attached embedding vectors
     * @throws Exception if file reading or parsing fails
     */
    public static List<Complaint> loadComplaintsWithEmbeddings(String csvPath, String jsonlPath) throws Exception {
        // Load CSV resource
        InputStream csvStream = ComplaintLoader.class.getResourceAsStream(csvPath);
        if (csvStream == null) {
            System.err.println("❌ Could not load CSV resource: " + csvPath);
            throw new IllegalArgumentException("CSV resource not found.");
        } else {
            System.out.println("✅ CSV file found: " + csvPath);
        }

        // Load JSONL resource
        InputStream jsonlStream = ComplaintLoader.class.getResourceAsStream(jsonlPath);
        if (jsonlStream == null) {
            System.err.println("❌ Could not load JSONL resource: " + jsonlPath);
            throw new IllegalArgumentException("JSONL resource not found.");
        } else {
            System.out.println("✅ JSONL file found: " + jsonlPath);
        }

        // Parse complaints from CSV
        InputStreamReader csvReader = new InputStreamReader(csvStream, StandardCharsets.UTF_8);
        List<Complaint> complaints = new CsvToBeanBuilder<Complaint>(csvReader)
                .withType(Complaint.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();
        System.out.println("✅ Loaded " + complaints.size() + " complaints from CSV.");

        // Load embeddings
        Map<Long, double[]> embeddings = EmbeddingLoader.loadEmbeddings(jsonlStream);
        System.out.println("✅ Loaded " + embeddings.size() + " embeddings from JSONL.");

        // Merge them
        ComplaintMerger.mergeEmbeddings(complaints, embeddings);
        System.out.println("✅ Embeddings successfully merged into complaints.");

        return complaints;
    }
}



