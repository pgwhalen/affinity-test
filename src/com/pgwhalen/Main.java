package com.pgwhalen;

import com.higherfrequencytrading.affinity.AffinityLock;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static final int loopCount = 10_000_000;
    static final int threadCount = 1;

    static final double[] percentiles = new double[]{25, 50, 75, 99, 99.9, 99.99, 99.999, 99.9999};
    static final double[] gapThresholdPercentiles = new double[]{99.9998};

    public static void main(String[] args) {

        List<NanoRunner> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            NanoRunner t = new NanoRunner(i, false);
            threads.add(t);
            t.start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (NanoRunner runner : threads) {
            System.out.println("\n*** Runner " + runner.threadNum + " | locked: " + runner.lockedToCPU);

//            reportDiffs(runner.timestamps);
//            reportStats(runner.timestamps);
            reportGaps(runner.timestamps);
        }
    }

    static void reportDiffs(long[] timestamps) {
        long[] diffs = diffs(timestamps);

        for (int i = loopCount / 2; i < (loopCount / 2) + 10; i++) {
            System.out.println("diff " + i + " = " + diffs[i]);
        }
    }

    static void reportGaps(long[] timestamps) {
        long[] diffs = diffs(timestamps);

        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < diffs.length; i++) {
            stats.addValue(diffs[i]);
        }

        for (double percentile : gapThresholdPercentiles) {
            double percentileThreshold = stats.getPercentile(percentile);
            System.out.println("Percentile " + percentile + " | threshold " + percentileThreshold);
            for (int i = 0; i < diffs.length; i++) {
                double diff = diffs[i];
                if (diff > percentileThreshold) {
                    System.out.println(diff + " .. " + i + " .. " + (timestamps[loopCount - 1] - timestamps[i]));
                }
            }
        }
    }

    private static long[] diffs(long[] timestamps) {
        long[] diffs = new long[timestamps.length - 1];
        for (int i = 1; i < timestamps.length; i++) {
            diffs[i - 1] = timestamps[i] - timestamps[i - 1];
        }
        return diffs;
    }

    static void reportStats(long[] timestamps) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 1; i < timestamps.length; i++) {
            stats.addValue(timestamps[i] - timestamps[i - 1]);
        }

        System.out.println("Stats: " + stats);
        for (double percentile : percentiles) {
            pct(stats, percentile);
        }
    }

    static void pct(DescriptiveStatistics stats, double pct) {
        System.out.println("PCT " + pct + ": " + stats.getPercentile(pct));
    }

    static class NanoRunner extends Thread {
        int threadNum;
        boolean lockedToCPU;
        volatile long[] timestamps;

        public NanoRunner(int threadNum, boolean lockedToCPU) {
            this.threadNum = threadNum;
            this.lockedToCPU = lockedToCPU;
        }

        @Override
        public void run() {
            System.out.println("Starting " + threadNum);

            long[] localTimestamps = new long[loopCount];
            if (lockedToCPU) {
                AffinityLock al = AffinityLock.acquireCore();
                try {
                    for (int i = 0; i < loopCount; i++) {
                        localTimestamps[i] = System.nanoTime();
                    }
                } finally {
                    al.release();
                }
            } else {
                for (int i = 0; i < loopCount; i++) {
                    localTimestamps[i] = System.nanoTime();
                }
            }


            timestamps = localTimestamps;
            System.out.println("Finished " + threadNum);

        }
    }
}
