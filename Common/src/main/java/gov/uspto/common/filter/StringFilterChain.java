package gov.uspto.common.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Criteria for Matching files; matches if all provided rules return true.
 * 
 * @author Brian G. Feldman (brian.feldman@uspto.gov)
 *
 */
public class StringFilterChain implements StringFilter {

    public List<StringFilter> filters = new ArrayList<>();

    public void addRule(StringFilter... rules) {
        Collections.addAll(filters, rules);
    }

    @Override
    public boolean accept(String filename) {
        return filters.stream().allMatch(rule -> rule.accept(filename));
    }

    @Override
    public String toString() {
        return "StringFilter [filters=" + Arrays.toString(filters.toArray()) + "]";
    }
}
