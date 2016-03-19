package com.pgwhalen.reporter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created by paul on 3/19/16.
 *
 * Prints out the specified percentiles of the timestamp diffs
 */
public class PercentileReporter extends Reporter {

    static final double[] percentiles = new double[]{25, 50, 75, 99, 99.9, 99.99, 99.999, 99.9999};

    public PercentileReporter(long[] timestamps) {
        super(timestamps);
    }

    @Override
    public String report() {
        StringBuilder sb = new StringBuilder();
        addTableHeader(sb, "Percentile", "Diff");

        DescriptiveStatistics stats = makeDescriptiveStatisticsFromDiffs();
        for (double percentile : percentiles) {
            addTableRow(sb, percentile, stats.getPercentile(percentile));
        }
        return sb.toString();
    }
}
