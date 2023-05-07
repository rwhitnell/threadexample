package edu.guilford;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DecimalFormat;

public class PartitionData implements Runnable {
    private String name;
    private long duration = 0;

    private Random rand = new Random();
    private DecimalFormat df = new DecimalFormat("#.#####E0");


    // Constructor with name
    public PartitionData(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("Starting thread " + name);
        partitionData();
    }

    public void partitionData() {

        ArrayList<Double> values = new ArrayList<Double>();
        ArrayList<ArrayList<Double>> partitions = new ArrayList<ArrayList<Double>>();
        double currentSum[] = new double[2];
        int nValues = 10000;
        int nSteps = 250000;

        Path dataLocation = null;
        FileReader dataFile = null;
        Scanner scanFile = null;
        // Read values from a file
        try {
            dataLocation = Paths.get(PartitionData.class.getResource("/partitionValues.txt").toURI());
            dataFile = new FileReader(dataLocation.toString());
            BufferedReader dataBuffer = new BufferedReader(dataFile);

            scanFile = new Scanner(dataBuffer);
            int iValue = 0;
            while (scanFile.hasNext() && iValue < nValues) {
                values.add(scanFile.nextDouble());
                iValue++;
            }
            nValues = iValue;
        } catch (FileNotFoundException | URISyntaxException ex) {
            Logger.getLogger(PartitionData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (scanFile != null) {
                scanFile.close();
            }
        }

        // Uncomment the following to generate a random set of numbers
        // while (iValue < nValues) {
        // values.add(new Double(maxValue * rand.nextDouble()));
        // iValue = iValue + 1;
        // }

        // Uncomment the following line to sort the values before partitioning
        // Collections.sort(values);

        // Uncomment the following line to shuffle the values before partitioning
        // Collections.shuffle(values);

        System.out.println(name + " " + "Sum of all values: " + df.format(sumList(values)));

        // Start by dividing the data into two equally sized partitions and calculate
        // the initial sums
        partitions.add(new ArrayList<Double>(values.subList(0, nValues / 2)));
        partitions.add(new ArrayList<Double>(values.subList(nValues / 2, values.size())));
        currentSum[0] = sumList(partitions.get(0));
        currentSum[1] = sumList(partitions.get(1));

        System.out.println(name + " " + "Initial sums: " + df.format(currentSum[0]) + " " +
                df.format(currentSum[1]));

        double smallestDiff = Math.abs(currentSum[0] - currentSum[1]);
        int smallestN = 0;
        int n = 0;
        ArrayList<Double> small1 = new ArrayList<>();
        ArrayList<Double> small2 = new ArrayList<>();
        long startTime = System.nanoTime();
        while (n < nSteps) {
            int partition = rand.nextInt(2);
            int otherPartition = 1 - partition;
            ArrayList<Double> partA = partitions.get(partition);
            ArrayList<Double> partB = partitions.get(otherPartition);
            currentSum[0] = sumList(partA);
            currentSum[1] = sumList(partB);
            double currentDiff = Math.abs(currentSum[0] - currentSum[1]);

            int item = rand.nextInt(partA.size());
            double value = partA.get(item);
            double newDiff = Math.abs(sumList(partA) - value - (sumList(partB) + value));
            // Uncomment for the more complex approach
            if (newDiff < currentDiff || Math.exp(-newDiff) > rand.nextDouble()) {
                // Uncomment for the simple approach
                // if (newDiff < currentDiff) {
                partB.add(partA.remove(item));

                if (newDiff < smallestDiff) {
                    System.out.println(name + " " + "Step: " + n + " " + df.format(sumList(partA)) + " "
                            + df.format(sumList(partB)));
                    System.out.println(name + " " + "Smaller difference: " + df.format(newDiff) + " "
                            + df.format(smallestDiff));
                    // Uncomment to save the partitions with the smallest difference
                    // small1.clear();
                    // small2.clear();
                    // // copy partA to small1
                    // for (Double val : partA) {
                    //     small1.add(val);
                    // }
                    // // copy partB to small2
                    // for (Double val : partB) {
                    //     small2.add(val);
                    // }
                    
                    smallestDiff = newDiff;
                    smallestN = n;
                }
            }

            n = n + 1;
        }
        long endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println(name + " result: " + smallestN + " " + df.format(smallestDiff));
        System.out.println(name + " partition size: " + partitions.get(0).size() + " "
                + partitions.get(1).size());
        System.out.println(name + " duration: " + df.format(duration / 1.e6) + " ms");

    }

    private double sumList(ArrayList<Double> valueList) {
        double sum = 0;

        for (Double value : valueList) {
            sum = sum + value;
        }

        return sum;

    }


}
