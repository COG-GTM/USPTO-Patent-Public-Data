package gov.uspto.patent.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Patent Description
 * 
 *<p><ul>
 *There are three to four sections to a Patent Description:
 *<li>Other Patent Relations "RELAPP"
 *<li>Brief Summary "BRFSUM"
 *<li>Brief Description of Drawings "DRWDESC"
 *<li>Detailed Description "DETDESC"
 *</ul></p>
 *
 */
public class Description {

	private List<DescriptionSection> sections = new ArrayList<DescriptionSection>();
	private List<Figure> figures = new ArrayList<Figure>(); // from Brief Description of Drawings "DRWDESC"

	public List<DescriptionSection> getSections() {
		return sections;
	}

	public List<Figure> getFigures() {
		return figures;
	}

	public void addFigures(Collection<Figure> figures) {
		this.figures.addAll(figures);
	}

	public Figure getFigure(String id) {
		return figures.stream()
				.filter(fig -> fig.hasId(id))
				.findFirst()
				.orElse(null);
	}

	public DescriptionSection getSection(DescSection section) {
		return sections.stream()
				.filter(sec -> sec.getSection().equals(section))
				.findFirst()
				.orElse(null);
	}

	public void addSection(DescriptionSection section) {
		sections.add(section);
	}

	public void setSections(List<DescriptionSection> sections) {
		this.sections = sections;
	}

	public String getAllRawText() {
		String joined = sections.stream()
				.map(DescriptionSection::getRawText)
				.collect(Collectors.joining("\n"));
		return joined.isEmpty() ? "" : joined + "\n";
	}

	public String getRawText(DescSection... descSections) {
		List<DescSection> wanted = Arrays.asList(descSections);
		String joined = sections.stream()
				.filter(sec -> wanted.contains(sec.getSection()))
				.map(DescriptionSection::getRawText)
				.collect(Collectors.joining("\n"));
		return joined.isEmpty() ? "" : joined + "\n";
	}

	public String getAllPlainText() {
		String joined = sections.stream()
				.map(DescriptionSection::getPlainText)
				.collect(Collectors.joining("\n"));
		return joined.isEmpty() ? "" : joined + "\n";
	}

	public String getPlainText(DescSection... descSections) {
		List<DescSection> wanted = Arrays.asList(descSections);
		String joined = sections.stream()
				.filter(sec -> wanted.contains(sec.getSection()))
				.map(DescriptionSection::getPlainText)
				.collect(Collectors.joining("\n"));
		return joined.isEmpty() ? "" : joined + "\n";
	}

	public String getSimpleHtml() {
		String joined = sections.stream()
				.map(DescriptionSection::getSimpleHtml)
				.collect(Collectors.joining("\n"));
		return joined.isEmpty() ? "" : joined + "\n";
	}

	
	public String getSimpleHtml(DescSection... descSections) {
		List<DescSection> wanted = Arrays.asList(descSections);
		String joined = sections.stream()
				.filter(sec -> wanted.contains(sec.getSection()))
				.map(DescriptionSection::getSimpleHtml)
				.collect(Collectors.joining("\n"));
		return joined.isEmpty() ? "" : joined + "\n";
	}

	@Override
	public String toString() {
		return "Description [sections=" + sections + ", figures=" + figures + "]";
	}

}
