package com.pgwhalen.reporter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created by paul on 3/19/16.
 * <p>
 * Reports what the longest pauses in the timestamp serious are and where they occurred.
 * <p>
 * Way overkill to use DescriptiveStatistics, but whatever, that's how I first did it.  There is a more efficient,
 * more interesting.
 */
public class GapReporter extends Reporter {

    // number of gaps to report on
    private int topGapCount;

    public GapReporter(long[] timestamps, int topGapCount) {
        super(timestamps);
        this.topGapCount = topGapCount;
    }

    @Override
    public String report() {
        DescriptiveStatistics diffsStats = makeDescriptiveStatisticsFromDiffs();
        double percentile = ((double) (timestamps.length - topGapCount) / timestamps.length) * 100;
        double percentileThreshold = diffsStats.getPercentile(percentile);

        StringBuilder sb = new StringBuilder();
        sb.append(topGapCount + " Biggest Gaps\n");
        sb.append("Total elapsed time: " + (timestamps[timestamps.length - 1] - timestamps[0]) + " ns\n");
        addTableHeader(sb, "N", "Diff", "From Start");

        long[] diffs = diffs();
        for (int i = 0; i < diffs.length; i++) {
            double diff = diffs[i];
            if (diff > percentileThreshold) {
                addTableRow(sb, i, diff, timestamps[i - 1] - timestamps[0]);
            }
        }

        return sb.toString();
    }
}
