package gov.uspto.bulkdata.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import gov.uspto.bulkdata.service.LookService;
import gov.uspto.common.filter.FileFilterChain;
import gov.uspto.patent.PatentDocFormat;
import gov.uspto.patent.PatentDocFormatDetect;
import gov.uspto.patent.PatentReaderException;
import gov.uspto.patent.bulk.DumpFileAps;
import gov.uspto.patent.bulk.DumpFileXml;
import gov.uspto.patent.bulk.DumpReader;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Look is a CLI tool to a view a single patent document from Bulk Patent XML.
 *
 * View contents of listed fields
 * 	--source="download/ipa150101.zip" --limit 5 --skip 5 --fields=id,title,family
 * 
 * Dump a single Patent XML Document by location in zipfile; the 3rd document:
 * --source="download/ipa150305.zip" --num=3 --fields=xml --out=download/patent.xml
 * 
 * Dump a single Patent XML Document by ID (note it may be slow as it parse each document to check its id):
 * --source="download/ipa150305.zip" --id=3 --fields=xml --out=download/patent.xml
 * 
 * @author Brian G. Feldman (brian.feldman@uspto.gov)
 *
 */
public class Look {

    private LookService lookService = new LookService();

    public static void main(String[] args) throws PatentReaderException, IOException {
        System.out.println("--- Start ---");

        OptionParser parser = new OptionParser() {
            {
                accepts("source").withRequiredArg().ofType(String.class).describedAs("zip file").required();
                accepts("fields").withOptionalArg().ofType(String.class)
                        .describedAs(
                                "comma seperated list of fields; options: [xml,object,id,title,abstract,description,citations,claims,assignee,inventor,classsification,family]")
                        .defaultsTo("object");
                accepts("num").withOptionalArg().ofType(Integer.class).describedAs("Record Number to retrive");
                accepts("id").withOptionalArg().ofType(String.class).describedAs("Patent Id");
                accepts("limit").withOptionalArg().ofType(Integer.class).describedAs("record limit").defaultsTo(1);
                accepts("skip").withOptionalArg().ofType(Integer.class).describedAs("records to skip").defaultsTo(0);
                accepts("out").withOptionalArg().ofType(String.class).describedAs("out file");
                accepts("xmlBodyTag").withOptionalArg().ofType(String.class)
                        .describedAs("XML Body Tag which wrapps document: [us-patent, PATDOC, patent-application]")
                        .defaultsTo("us-patent");
                accepts("out").withOptionalArg().ofType(String.class).describedAs("out file");
                accepts("addHtmlEntities").withOptionalArg().ofType(Boolean.class)
                        .describedAs("Add Html Entities DTD to XML; Needed when reading Patents in PAP format.")
                        .defaultsTo(false);
                accepts("aps").withOptionalArg().ofType(Boolean.class)
                        .describedAs("Read APS - Greenbook Patent Document Format").defaultsTo(false);
            }
        };

        OptionSet options = parser.parse(args);
        String inFileStr = (String) options.valueOf("source");
        File inputFile = new File(inFileStr);

        int skip = (Integer) options.valueOf("skip");
        int limit = (Integer) options.valueOf("limit");
        String xmlBodyTag = (String) options.valueOf("xmlBodyTag");
        boolean addHtmlEntities = (Boolean) options.valueOf("addHtmlEntities");
        boolean aps = (Boolean) options.valueOf("aps");

        if (options.has("num")) {
            skip = ((Integer) options.valueOf("num")) - 1;
            limit = 1;
        }

        String[] fields = ((String) options.valueOf("fields")).split(",");
        Look look = new Look();

        FileFilterChain filters = new FileFilterChain();

        DumpReader dumpReader;
        if (aps) {
            dumpReader = new DumpFileAps(inputFile);
            //filter.addRule(new SuffixFileFilter("txt"));
        } else {
            PatentDocFormat patentDocFormat = new PatentDocFormatDetect().fromFileName(inputFile);
            switch (patentDocFormat) {
            case Greenbook:
                aps = true;
                dumpReader = new DumpFileAps(inputFile);
                //filters.addRule(new PathFileFilter(""));
                //filters.addRule(new SuffixFilter("txt"));
                break;
            default:
                DumpFileXml dumpXml = new DumpFileXml(inputFile);
    			if (PatentDocFormat.Pap.equals(patentDocFormat) || addHtmlEntities) {
    				dumpXml.addHTMLEntities();
    			}
                dumpReader = dumpXml;
                filters.addRule(new SuffixFileFilter("xml"));
            }
        }

        dumpReader.setFileFilter(filters);

        dumpReader.open();
        dumpReader.skip(skip);

        Writer writer = null;
        if (options.has("out")) {
            String outStr = (String) options.valueOf("out");
            Path outFilePath = Paths.get(outStr);
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outFilePath.toFile()), Charset.forName("UTF-8")));
        } else {
            writer = new BufferedWriter(new OutputStreamWriter(System.out, Charset.forName("UTF-8")));
        }

        try {
            if (options.has("id")) {
                String docid = (String) options.valueOf("id");
                look.lookService.lookupPatentById(dumpReader, docid, writer, fields);
            } else {
                look.lookService.lookupPatents(dumpReader, limit, writer, fields);
            }
        } finally {
            dumpReader.close();
            writer.close();
        }

        System.out.println("--- Finished ---");
    }

}
