package com.pgwhalen;

import com.higherfrequencytrading.affinity.AffinityLock;
import com.pgwhalen.reporter.DescriptiveStatisticsReporter;
import com.pgwhalen.reporter.GapReporter;
import com.pgwhalen.reporter.PercentileReporter;
import com.pgwhalen.reporter.Reporter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static final int loopCount = 10_000_000;
    static final int threadCount = 1;

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
            Reporter reporter = new GapReporter(runner.timestamps, 10);
            System.out.println(reporter.report());
        }
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
