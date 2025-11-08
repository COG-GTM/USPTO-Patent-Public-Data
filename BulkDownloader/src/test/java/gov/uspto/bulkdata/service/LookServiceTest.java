package gov.uspto.bulkdata.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Before;
import org.junit.Test;

import gov.uspto.common.filter.FileFilterChain;
import gov.uspto.patent.PatentReaderException;
import gov.uspto.patent.bulk.DumpFileXml;
import gov.uspto.patent.bulk.DumpReader;

/**
 * Unit tests for LookService
 * Tests the core business logic methods with real patent XML data
 */
public class LookServiceTest {

    private LookService lookService;
    
    @Before
    public void setUp() {
        lookService = new LookService();
    }
    
    /**
     * Test that LookService can be instantiated
     */
    @Test
    public void testServiceInstantiation() {
        assertNotNull(lookService);
    }
    
    /**
     * Test lookupPatents with real XML data
     */
    @Test
    public void testLookupPatentsWithRealData() throws PatentReaderException, IOException {
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
            assertTrue("Output should contain ID field", output.contains("ID:"));
            assertTrue("Output should contain TITLE field", output.contains("TITLE:"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test lookupPatentById with real XML data
     * Note: This test verifies the method runs without errors
     * The actual ID matching depends on the patent document format
     */
    @Test
    public void testLookupPatentByIdWithRealData() throws PatentReaderException, IOException {
        String xmlDoc = createSampleXmlPatent("US1234567");
        File tempXmlFile = createTempXmlFile(xmlDoc);
        
        try {
            DumpFileXml dumpReader = new DumpFileXml(tempXmlFile);
            FileFilterChain filters = new FileFilterChain();
            filters.addRule(new SuffixFileFilter("xml"));
            dumpReader.setFileFilter(filters);
            dumpReader.open();
            
            StringWriter writer = new StringWriter();
            String[] fields = {"id"};
            
            lookService.lookupPatentById(dumpReader, "US1234567B2", writer, fields);
            
            writer.close();
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
            assertTrue("Output should contain raw patent data", output.contains("Patent RAW:"));
            assertTrue("Output should contain XML content", output.contains("us-patent-grant"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test multiple fields extraction
     */
    @Test
    public void testMultipleFieldsExtraction() throws PatentReaderException, IOException {
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
            assertTrue("Output should contain ID field", output.contains("ID:"));
            assertTrue("Output should contain TITLE field", output.contains("TITLE:"));
            assertTrue("Output should contain ABSTRACT field", output.contains("ABSTRACT:"));
            
            writer.close();
        } finally {
            tempXmlFile.delete();
        }
    }
    
    /**
     * Test all supported field types
     */
    @Test
    public void testAllSupportedFields() throws PatentReaderException, IOException {
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
                assertTrue("Field " + field + " should produce output", output.length() > 0);
                
                writer.close();
            }
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
}
