package edu.guilford;

import java.util.Random;

public class ThreadExample {
    public static void main(String[] args) {
        int nThreads = 1;
        PartitionData[] partitionData = new PartitionData[nThreads];
        Thread[] threads = new Thread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            partitionData[i] = new PartitionData("Run " + i);
            threads[i] = new Thread(partitionData[i]);
            threads[i].start();    
        }
    }
}
