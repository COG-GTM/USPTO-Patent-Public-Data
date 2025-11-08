package gov.uspto.bulkdata.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.NoSuchElementException;

import org.slf4j.MDC;

import gov.uspto.patent.PatentReader;
import gov.uspto.patent.PatentReaderException;
import gov.uspto.patent.bulk.DumpReader;
import gov.uspto.patent.model.DocumentId;
import gov.uspto.patent.model.Patent;

/**
 * LookService provides core business logic for looking up and displaying patent information.
 * This service is stateless and can be used by CLI tools or future REST APIs.
 * 
 * @author Extracted from Look.java for MBA-716
 */
public class LookService {

    /**
     * Look up patents from a DumpReader with a limit on the number of patents to process.
     * 
     * @param reader DumpReader to read patents from
     * @param limit Maximum number of patents to process
     * @param writer Writer to output patent information
     * @param fields Array of field names to display
     * @throws PatentReaderException if there's an error reading the patent
     * @throws IOException if there's an error writing output
     */
    public void lookupPatents(DumpReader reader, int limit, Writer writer, String[] fields)
            throws PatentReaderException, IOException {

        PatentReader patentReader = new PatentReader(reader.getPatentDocFormat());

        for (int i = 1; reader.hasNext() && i <= limit; i++) {
            MDC.put("DOCID", reader.getFile().getName() + ":" + reader.getCurrentRecCount());
            
            System.out.println(reader.getCurrentRecCount() + 1 + " --------------------------");

            String rawDocStr;
            try {
                rawDocStr = reader.next();
            } catch (NoSuchElementException e) {
                break;
            }

            if (fields.length == 1 && "raw".equalsIgnoreCase(fields[0])) {
                showPatentFields(null, rawDocStr, fields, writer);
            } else {
                try (StringReader rawText = new StringReader(rawDocStr)) {
                    Patent patent = patentReader.read(rawText);
                    showPatentFields(patent, rawDocStr, fields, writer);
                }
            }
        }

        reader.close();
    }

    /**
     * Look up a specific patent by document ID.
     * 
     * @param reader DumpReader to read patents from
     * @param docId Document ID to search for
     * @param writer Writer to output patent information
     * @param fields Array of field names to display
     * @throws PatentReaderException if there's an error reading the patent
     * @throws IOException if there's an error writing output
     */
    public void lookupPatentById(DumpReader reader, String docId, Writer writer, String[] fields)
            throws PatentReaderException, IOException {

        PatentReader patentReader = new PatentReader(reader.getPatentDocFormat());

        while (reader.hasNext()) {
            String rawDocStr;
            try {
                rawDocStr = reader.next();
            } catch (NoSuchElementException e) {
                break;
            }

            if (rawDocStr != null) {
                try (StringReader rawText = new StringReader(rawDocStr)) {
                    Patent patent = patentReader.read(rawText);
                    if (patent.getDocumentId().toText().equals(docId)) {
                        showPatentFields(patent, rawDocStr, fields, writer);
                        break;
                    }
                }
            }
        }

        reader.close();
    }

    /**
     * Display specified fields of a patent.
     * 
     * @param patent Patent object (can be null if only displaying raw document)
     * @param rawDoc Raw document string
     * @param fields Array of field names to display
     * @param writer Writer to output patent information
     * @throws IOException if there's an error writing output
     */
    private void showPatentFields(Patent patent, String rawDoc, String[] fields, Writer writer) throws IOException {
        for (String field : fields) {
            switch (field) {
            case "raw":
                writer.write("Patent RAW:\n");
                writer.write(rawDoc);
                writer.flush();
                break;
            case "id":
                writer.write("ID:\t" + patent.getDocumentId() + "\n");
                writer.flush();
                break;
            case "title":
                writer.write("TITLE:\t" + patent.getTitle() + "\n");
                writer.flush();
                break;
            case "abstract":
                writer.write("ABSTRACT:\t" + patent.getAbstract() + "\n");
                writer.flush();
                break;
            case "description":
                writer.write("DESCRIPTION:\t" + patent.getDescription().getAllPlainText() + "\n");
                writer.flush();
                break;
            case "citations":
                writer.write("CITATIONS:\t" + patent.getCitations() + "\n");
                writer.flush();
                break;
            case "claims":
                writer.write("CLAIMS:\t" + patent.getClaims() + "\n");
                writer.flush();
                break;
            case "assignee":
                writer.write("ASSIGNEE:\t" + patent.getAssignee() + "\n");
                writer.flush();
                break;
            case "inventor":
                writer.write("INVENTORS:\t" + patent.getInventors() + "\n");
                writer.flush();
                break;
            case "classification":
                writer.write("CLASSIFICATION:\t" + patent.getClassification() + "\n");
                writer.flush();
                break;
            case "object":
                writer.write(patent.toString());
                writer.flush();
                break;
            case "family":
                writer.write("FAMILY:\t\n");
                for (DocumentId docId : patent.getRelationIds()) {
                    writer.write("\t" + docId.getType().name() + " : " + docId.toText() + "\n");
                }
                writer.flush();
                break;
            }
        }
    }
}
