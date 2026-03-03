package gov.uspto.patent.model;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import gov.uspto.patent.DateTextType;
import gov.uspto.patent.InvalidDataException;

/**
 * Represents a document date, using Java 8's java.time API internally
 * while maintaining backward compatibility with java.util.Date.
 */
public class DocumentDate {

    private static final DateTimeFormatter DATE_ISO_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final DateTimeFormatter DATE_PATENT_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private LocalDate localDate;
    private Date date;
    private String rawDate;

    public DocumentDate(String date) throws InvalidDataException {
        this.rawDate = date;
        setDate(date);
    }

    public DocumentDate(Date date) throws InvalidDataException {
        this.date = date;
        if (date != null) {
            this.localDate = date.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
        }
    }

    public int getYear() {
        return localDate.getYear();
    }

    public void setDate(String date) throws InvalidDataException {
        if (date != null && date.trim().length() == 8) {
            try {
                String trimmed = date.trim();
                // Handle lenient dates with zero month or day (e.g. "20101100" or "20100000")
                int month = Integer.parseInt(trimmed.substring(4, 6));
                int day = Integer.parseInt(trimmed.substring(6, 8));
                if (month == 0) {
                    month = 1;
                }
                if (day == 0) {
                    day = 1;
                }
                String normalizedDate = trimmed.substring(0, 4)
                        + String.format("%02d", month)
                        + String.format("%02d", day);
                this.localDate = LocalDate.parse(normalizedDate, DATE_PATENT_FORMAT);
                this.date = Date.from(this.localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (DateTimeParseException | NumberFormatException e) {
                throw new InvalidDataException("Invalid Date: " + date, e);
            }
        } else if (date != null && date.trim().length() == 4) {
            try {
                this.localDate = LocalDate.parse(date.trim() + "0101", DATE_PATENT_FORMAT);
                this.date = Date.from(this.localDate.atStartOfDay(ZoneOffset.UTC).toInstant());
            } catch (DateTimeParseException e) {
                throw new InvalidDataException("Invalid Date: " + date, e);
            }
        } else {
            throw new InvalidDataException("Invalid Date: " + date);
        }
    }

    /**
     * Returns the date as a legacy java.util.Date for backward compatibility.
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Returns the date as a Java 8 LocalDate.
     * @return the local date
     */
    public LocalDate getLocalDate() {
        return localDate;
    }

    public String getDateText(DateTextType dateType) {
        switch (dateType) {
        case RAW:
            return rawDate;
        case ISO:
            return getISOString();
        default:
            return rawDate;
        }
    }

    private String getISOString() {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay(ZoneOffset.UTC).format(DATE_ISO_FORMAT);
    }

    @Override
    public String toString() {
        return "DocumentDate [date=" + localDate + "]";
    }
}
