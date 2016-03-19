package com.pgwhalen.reporter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created by paul on 3/19/16.
 *
 * Abstract class that for reporters that produce a string report of a given run.
 */
public abstract class Reporter {

    protected long[] timestamps;

    public Reporter(long[] timestamps) {
        this.timestamps = timestamps;
    }

    public abstract String report();

    /**
     * @return array of length timestamps.length - 1 with differences between each timestamp
     */
    protected long[] diffs() {
        long[] diffs = new long[timestamps.length - 1];
        for (int i = 1; i < timestamps.length; i++) {
            diffs[i - 1] = timestamps[i] - timestamps[i - 1];
        }
        return diffs;
    }

    protected DescriptiveStatistics makeDescriptiveStatisticsFromDiffs() {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (long timestamp : diffs()) {
            stats.addValue(timestamp);
        }
        return stats;
    }

    protected void addTableHeader(StringBuilder sb, String... columnNames) {
        for (String columnName : columnNames) {
            sb.append(columnName);
            sb.append(":\t\t\t\t| ");
        }
        sb.append("\n");
    }

    protected void addTableRow(StringBuilder sb, Object... values) {
        //            sb.append(percentile + " \t\t| " + stats.getPercentile(percentile) + "\n");
        for (Object value : values) {
            sb.append(value.toString() + "\t\t\t\t| ");
        }
        sb.append("\n");
    }
}
