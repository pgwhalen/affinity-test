package com.pgwhalen;

import com.higherfrequencytrading.affinity.AffinityLock;

import java.util.ArrayList;
import java.util.List;

public class Main {

    static final int loopCount = 10_000_000;
    static final int threadCount = 8;

    public static void main(String[] args) {


        List<NanoRunner> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            NanoRunner t = new NanoRunner(i, i == 0);
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
            System.out.println("*** Runner " + runner.threadNum + " | locked: " + runner.lockedToCPU);
            long[] timestamps = runner.timestamps;
            long[] diffs = new long[timestamps.length - 1];
            for (int i = 1; i < timestamps.length; i++) {
                diffs[i - 1] = timestamps[i] - timestamps[i - 1];
            }


            for (int i = loopCount / 2; i < (loopCount / 2) + 10; i++) {
                System.out.println("diff " + i + " = " + diffs[i]);
            }
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
