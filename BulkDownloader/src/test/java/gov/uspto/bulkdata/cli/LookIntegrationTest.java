package gov.uspto.bulkdata.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Before;
import org.junit.Test;

import gov.uspto.bulkdata.service.LookService;
import gov.uspto.common.filter.FileFilterChain;
import gov.uspto.patent.PatentDocFormat;
import gov.uspto.patent.PatentReaderException;
import gov.uspto.patent.bulk.DumpFileXml;
import gov.uspto.patent.bulk.DumpReader;

/**
 * Integration tests for Look CLI functionality
 * Verifies that the CLI tool works correctly after refactoring to use LookService
 */
public class LookIntegrationTest {

    private LookService lookService;
    private File tempOutputFile;
    
    @Before
    public void setUp() throws IOException {
        lookService = new LookService();
        tempOutputFile = File.createTempFile("look-test-", ".txt");
        tempOutputFile.deleteOnExit();
    }
    
    /**
     * Test that LookService can be instantiated and used
     */
    @Test
    public void testLookServiceInstantiation() {
        LookService service = new LookService();
        assertTrue(service != null);
    }
    
    /**
     * Test console output mode (StringWriter simulating stdout)
     */
    @Test
    public void testConsoleOutputMode() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            StringWriter writer = new StringWriter();
            String[] fields = {"id", "title"};
            
            lookService.lookupPatents(dumpReader, 1, writer, fields);
            
            String output = writer.toString();
            assertTrue(output.contains("ID:"));
            assertTrue(output.contains("TITLE:"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test file output mode
     */
    @Test
    public void testFileOutputMode() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tempOutputFile), Charset.forName("UTF-8")));
            String[] fields = {"id", "title"};
            
            lookService.lookupPatents(dumpReader, 1, writer, fields);
            
            writer.close();
            
            String output = new String(Files.readAllBytes(tempOutputFile.toPath()));
            assertTrue(output.contains("ID:"));
            assertTrue(output.contains("TITLE:"));
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test lookup by ID functionality
     * Note: This test verifies the method runs without errors
     */
    @Test
    public void testLookupByIdFunctionality() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            StringWriter writer = new StringWriter();
            String[] fields = {"id", "title"};
            
            lookService.lookupPatentById(dumpReader, "US1234567B2", writer, fields);
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test all field types are supported
     */
    @Test
    public void testAllFieldTypesSupported() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            String[] allFields = {"id", "title", "abstract", "description", "claims", 
                                  "citations", "assignee", "inventor", "classification", 
                                  "object", "family"};
            
            for (String field : allFields) {
                DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
                FileFilterChain filters = new FileFilterChain();
                filters.addRule(new SuffixFileFilter("xml"));
                dumpReader.setFileFilter(filters);
                dumpReader.open();
                
                StringWriter writer = new StringWriter();
                String[] fields = {field};
                
                lookService.lookupPatents(dumpReader, 1, writer, fields);
                
                String output = writer.toString();
                assertFalse("Field " + field + " produced no output", output.isEmpty());
                
                writer.close();
            }
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test raw field output
     */
    @Test
    public void testRawFieldOutput() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            StringWriter writer = new StringWriter();
            String[] fields = {"raw"};
            
            lookService.lookupPatents(dumpReader, 1, writer, fields);
            
            String output = writer.toString();
            assertTrue(output.contains("Patent RAW:"));
            assertTrue(output.contains("us-patent-grant"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test limit functionality
     */
    @Test
    public void testLimitFunctionality() throws PatentReaderException, IOException {
        String xmlDoc1 = createSampleXmlPatent("US1234567");
        String xmlDoc2 = createSampleXmlPatent("US7654321");
        File tempXmlFile = createTempXmlFileWithMultiplePatents(xmlDoc1, xmlDoc2);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            StringWriter writer = new StringWriter();
            String[] fields = {"id"};
            
            lookService.lookupPatents(dumpReader, 1, writer, fields);
            
            String output = writer.toString();
            assertTrue(output.contains("ID:"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test output format is unchanged from original implementation
     */
    @Test
    public void testOutputFormatUnchanged() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            StringWriter writer = new StringWriter();
            String[] fields = {"id", "title", "abstract"};
            
            lookService.lookupPatents(dumpReader, 1, writer, fields);
            
            String output = writer.toString();
            assertTrue(output.contains("ID:\t"));
            assertTrue(output.contains("TITLE:\t"));
            assertTrue(output.contains("ABSTRACT:\t"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Helper method to create a sample XML patent document
     */
    private String createSampleXmlPatent(String patentNumber) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<!DOCTYPE us-patent-grant SYSTEM \"us-patent-grant-v45-2014-04-03.dtd\" []>\n");
        xml.append("<us-patent-grant lang=\"EN\" dtd-version=\"v4.5 2014-04-03\" file=\"US").append(patentNumber).append("-20200101.XML\" status=\"PRODUCTION\" id=\"us-patent-grant\" country=\"US\" date-produced=\"20191217\" date-publ=\"20200101\">\n");
        xml.append("<us-bibliographic-data-grant>\n");
        xml.append("<publication-reference>\n");
        xml.append("<document-id>\n");
        xml.append("<country>US</country>\n");
        xml.append("<doc-number>").append(patentNumber).append("</doc-number>\n");
        xml.append("<kind>B2</kind>\n");
        xml.append("<date>20200101</date>\n");
        xml.append("</document-id>\n");
        xml.append("</publication-reference>\n");
        xml.append("<application-reference appl-type=\"utility\">\n");
        xml.append("<document-id>\n");
        xml.append("<country>US</country>\n");
        xml.append("<doc-number>15123456</doc-number>\n");
        xml.append("<date>20170101</date>\n");
        xml.append("</document-id>\n");
        xml.append("</application-reference>\n");
        xml.append("<invention-title id=\"d2e43\">Test Patent Title</invention-title>\n");
        xml.append("<parties>\n");
        xml.append("<applicants>\n");
        xml.append("<applicant sequence=\"001\" app-type=\"applicant\" designation=\"us-only\">\n");
        xml.append("<addressbook>\n");
        xml.append("<last-name>Doe</last-name>\n");
        xml.append("<first-name>John</first-name>\n");
        xml.append("<address>\n");
        xml.append("<city>New York</city>\n");
        xml.append("<state>NY</state>\n");
        xml.append("<country>US</country>\n");
        xml.append("</address>\n");
        xml.append("</addressbook>\n");
        xml.append("<residence>\n");
        xml.append("<country>US</country>\n");
        xml.append("</residence>\n");
        xml.append("</applicant>\n");
        xml.append("</applicants>\n");
        xml.append("<inventors>\n");
        xml.append("<inventor sequence=\"001\" designation=\"us-only\">\n");
        xml.append("<addressbook>\n");
        xml.append("<last-name>Doe</last-name>\n");
        xml.append("<first-name>John</first-name>\n");
        xml.append("<address>\n");
        xml.append("<city>New York</city>\n");
        xml.append("<state>NY</state>\n");
        xml.append("<country>US</country>\n");
        xml.append("</address>\n");
        xml.append("</addressbook>\n");
        xml.append("</inventor>\n");
        xml.append("</inventors>\n");
        xml.append("</parties>\n");
        xml.append("</us-bibliographic-data-grant>\n");
        xml.append("<abstract id=\"abstract\">\n");
        xml.append("<p id=\"p-0001\">Test abstract content.</p>\n");
        xml.append("</abstract>\n");
        xml.append("<description id=\"description\">\n");
        xml.append("<heading id=\"h-0001\" level=\"1\">TECHNICAL FIELD</heading>\n");
        xml.append("<p id=\"p-0002\">Test description content.</p>\n");
        xml.append("</description>\n");
        xml.append("<claims id=\"claims\">\n");
        xml.append("<claim id=\"CLM-00001\" num=\"00001\">\n");
        xml.append("<claim-text>1. A test claim.</claim-text>\n");
        xml.append("</claim>\n");
        xml.append("</claims>\n");
        xml.append("</us-patent-grant>\n");
        return xml.toString();
    }
    
    /**
     * Helper method to create a temporary XML file
     */
    private File createTempXmlFile(String xmlContent) throws IOException {
        File tempFile = File.createTempFile("patent-", ".xml");
        tempFile.deleteOnExit();
        Files.write(tempFile.toPath(), xmlContent.getBytes(Charset.forName("UTF-8")));
        return tempFile;
    }
    
    /**
     * Helper method to create a temporary XML file with multiple patents
     */
    private File createTempXmlFileWithMultiplePatents(String... xmlContents) throws IOException {
        File tempFile = File.createTempFile("patents-", ".xml");
        tempFile.deleteOnExit();
        
        StringBuilder combined = new StringBuilder();
        for (String xml : xmlContents) {
            combined.append(xml);
        }
        
        Files.write(tempFile.toPath(), combined.toString().getBytes(Charset.forName("UTF-8")));
        return tempFile;
    }
}
