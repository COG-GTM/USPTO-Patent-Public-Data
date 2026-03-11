package gov.uspto.common.filter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

public class SuffixFilter implements FileFilter, StringFilter {
    private final String[] suffixes;

    public SuffixFilter(String... suffixes) {
        this.suffixes = suffixes;
    }

    @Override
    public boolean accept(String valueStr) {
        String lowerVal = valueStr.toLowerCase();
        return Arrays.stream(suffixes).anyMatch(lowerVal::endsWith);
    }

    @Override
    public boolean accept(File file) {
        return accept(file.getName());
    }

    @Override
    public String toString() {
        return "SuffixFilter [suffixes=" + Arrays.toString(suffixes) + "]";
    }
}
