package org.kumoricon.model.report;

public class ReportLine {
    public String description;
    public String value;

    public ReportLine() {}
    public ReportLine(String description) {
        this(description, "");
    }
    public ReportLine(String description, String value) {
        this.description = description;
        this.value = value;
    }
    public ReportLine(String description, Integer value) {
        this(description, value.toString());
    }
    public ReportLine(String description, Float value) {
        this(description, value.toString());
    }
    public ReportLine(String description, Long value) { this(description, value.toString()); }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
