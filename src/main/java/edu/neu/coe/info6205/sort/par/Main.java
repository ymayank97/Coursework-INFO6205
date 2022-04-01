package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import org.jfree.data.xy.XYSeries;
import edu.neu.coe.info6205.plotter;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        processArgs(args);
        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        plotter plot = new plotter("Cutoff vs Time", "Cutoff", "Time");
        Random random = new Random();
        int[] array = new int[2000000];
        ArrayList<Long> timeList = new ArrayList<>();

        for( int k = 1; k <= 32; k = k*2) {
            ParSort.threadCount = k;
            ParSort.threadPool = new ForkJoinPool(ParSort.threadCount);
            String seriesName = "" + k + " Threads";
            XYSeries timeSeries = new XYSeries(seriesName);
            double min = 99999;
            int minCutoff = 0;
            double avg = 0;
            for (int j = 50; j < 100; j++) {

                ParSort.cutoff = 10000 * (j + 1);
                // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                long time;
                long startTime = System.currentTimeMillis();
                for (int t = 0; t < 20; t++) {
                    for (int i = 0; i < array.length; i++)
                        array[i] = random.nextInt(10000000);
                    ParSort.sort(array, 0, array.length);
                }
                long endTime = System.currentTimeMillis();
                time = (endTime - startTime);
                avg += time/20;
                if(time/20 < min){
                    minCutoff = 10000 * (j + 1);
                    min = time/20;
                }
                timeList.add(time);
                timeSeries.add(10000 * (j + 1), time/20);
                System.out
                    .println("cutoff：" + (ParSort.cutoff) + "\t\t20 times Time:" + time + "ms");

            }
            System.out.println("For threads " + k + " min is = " + min + " at cutoff " + minCutoff + " and average is = " + avg/50);
            plot.addSeries(timeSeries);
        }

        plot.initUI();
        plot.setVisible(true);

        try {
            FileOutputStream fis = new FileOutputStream("./src/result.csv");
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            int j = 0;
            for (long i : timeList) {
                String content = (double) 10000 * (j + 1) / 2000000 + "," + (double) i / 20 + "\n";
                j++;
                bw.write(content);
                bw.flush();
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}