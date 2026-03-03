package gov.uspto.common.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Criteria for Matching files within ZipReader; matches if all provided rules return true.
 * 
 * @author Brian G. Feldman (brian.feldman@uspto.gov)
 *
 */
public class FileFilterChain implements FileFilter {

    public List<FileFilter> matchRules = new ArrayList<>();

    public void addRule(FileFilter... rules) {
        Collections.addAll(matchRules, rules);
    }

    @Override
    public boolean accept(File file) {
        return matchRules.stream().allMatch(rule -> rule.accept(file));
    }

    @Override
    public String toString() {
        return "FileFilter [matchRules=" + matchRules + "]";
    }
}
