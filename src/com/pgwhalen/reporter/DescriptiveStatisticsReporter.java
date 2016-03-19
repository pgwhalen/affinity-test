package com.pgwhalen.reporter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created by paul on 3/19/16.
 *
 * Wraps {@link org.apache.commons.math3.stat.descriptive.DescriptiveStatistics}, calculating
 * the distribution of differences
 */
public class DescriptiveStatisticsReporter extends Reporter {

    public DescriptiveStatisticsReporter(long[] timestamps) {
        super(timestamps);
    }

    @Override
    public String report() {
        return makeDescriptiveStatisticsFromDiffs().toString();
    }
}
