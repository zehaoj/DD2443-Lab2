import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class MainClass {

    private final static int MAX_DATA_NUM = 1_000_000;
    private final static int SAMPLING_TIMES = 12;

    public static void main(String[] args){
        try {

            Random rand = new Random();
            int[] raw_data = new int[MAX_DATA_NUM];
            int[] data = new int[MAX_DATA_NUM];

            double[] qsMean = new double[SAMPLING_TIMES];
            double[] qspMean= new double[SAMPLING_TIMES];
            double[] qsp1Mean = new double[SAMPLING_TIMES];

            for (int j = 0; j < 10; j++) {
                int sampleCount = 0;

                for (int i = 0; i < MAX_DATA_NUM; i++) {
                    raw_data[i] = rand.nextInt(10 * MAX_DATA_NUM);
                }

                System.out.println(Runtime.getRuntime().availableProcessors());

                Stopwatch stopwatch = new Stopwatch();

                int i = 1;
                while (i <= Runtime.getRuntime().availableProcessors()) {
                    /**
                     * QuickSort (Serial)
                     */
                    System.arraycopy(raw_data, 0, data, 0, raw_data.length);
                    QS qs = new QS(data);
                    double beforeQs = stopwatch.elapsedTime();
                    qs.sort(0, data.length - 1);
                    double afterQs = stopwatch.elapsedTime();
                    assertSort(data);

                    /**
                     * QuickSort (Parallel) implemented with ExecutorService and Future List
                     */
                    System.arraycopy(raw_data, 0, data, 0, raw_data.length);
                    List<Future> futures = new Vector<>();
                    ExecutorService executorService = Executors.newFixedThreadPool(i);
                    double beforeQsp = stopwatch.elapsedTime();
                    QSP1 mainQspTask = new QSP1(data, 0, data.length - 1, executorService, futures);
                    futures.add(executorService.submit(mainQspTask));
                    while (!futures.isEmpty()) {
                        Future topFuture = futures.remove(0);
                        try {
                            topFuture.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        invokeAll(futures);
                    }
                    executorService.shutdown();
                    double afterQsp = stopwatch.elapsedTime();
                    assertSort(data);

                    /**
                     * QuickSort (Parallel) implemented with RecursiveAction and ForkJoinPool
                     */
                    System.arraycopy(raw_data, 0, data, 0, raw_data.length);
                    double beforeQsp2 = stopwatch.elapsedTime();
                    final ForkJoinPool forkJoinPoolQsp = new ForkJoinPool(i);
                    forkJoinPoolQsp.invoke(new QSP2(data, 0, data.length - 1));
                    double afterQsp2 = stopwatch.elapsedTime();
                    assertSort(data);

                    qsMean[sampleCount] += (afterQs - beforeQs);
                    qspMean[sampleCount] += (afterQsp - beforeQsp);
                    qsp1Mean[sampleCount] += (afterQsp2 - beforeQsp2);
                    System.out.println("QuickSort takes " + String.format("%.4f", (afterQs - beforeQs)) + "s");
                    System.out.println("QuickSortParallel takes " + String.format("%.4f", (afterQsp - beforeQsp)) + "s");
                    System.out.println("QuickSortParallel2 takes " + String.format("%.4f", (afterQsp2 - beforeQsp2)) + "s");
                    sampleCount++;
                    if (i < 4) {
                        i++;
                    } else if (i < 8) {
                        i = i + 2;
                    } else if (i < 16) {
                        i = i + 4;
                    } else {
                        i = i * 2;
                    }
                }
            }

            for (int i = 0; i < SAMPLING_TIMES; i++) {
                System.out.println(String.format("%.4f", (qsMean[i] / 10)) + "s");
                System.out.println(String.format("%.4f", (qspMean[i] / 10)) + "s");
                System.out.println(String.format("%.4f", (qsp1Mean[i] / 10)) + "s");
                System.out.println();

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void assertSort(int[] arr){
        for (int i = 1; i < arr.length; i++){
            if (arr[i - 1] > arr[i]) {
                System.out.println("wrong!!!");
            }
        }
    }

}