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

    public static void main(String[] args){
        try {

            Random rand = new Random();
            int[] raw_data = new int[MAX_DATA_NUM];
            int[] data = new int[MAX_DATA_NUM];

            for (int i = 0; i < MAX_DATA_NUM; i++) {
                raw_data[i] = rand.nextInt(MAX_DATA_NUM);
//                System.out.println(raw_data[i]);
            }

            System.out.println(Runtime.getRuntime().availableProcessors());

            Stopwatch stopwatch = new Stopwatch();


            for (int i = 4; i <= Runtime.getRuntime().availableProcessors(); i++) {
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
                double beforeQsp = stopwatch.elapsedTime();
                List<Future> futures = new Vector<>();
                ExecutorService executorService = Executors.newFixedThreadPool(i);
                QSP1 mainQspTask = new QSP1(data, 0, data.length - 1, executorService, futures);
                futures.add(executorService.submit(mainQspTask));
                while (!futures.isEmpty()) {
                    Future topFuture = futures.remove(0);
                    try {
                        topFuture.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

                /**
                 * QuickSort (Parallel) implemented with parallel streams and lambda functions
                 */
    //            System.arraycopy(raw_data, 0, data, 0, raw_data.length);
    //            double beforeQsp3 = stopwatch.elapsedTime();
    //            QSP3 qSP3 = new QSP3(Arrays.stream(data).boxed().toArray(Integer[]::new));
    //            Stream<Integer> streamData = qSP3.compute();
    //            double afterQsp3 = stopwatch.elapsedTime();
    //            assertSort(data);

                System.out.println("QuickSort takes " + String.format("%.4f", (afterQs - beforeQs)) + "s");
                System.out.println("QuickSortParallel takes " + String.format("%.4f", (afterQsp - beforeQsp)) + "s");
                System.out.println("QuickSortParallel2 takes " + String.format("%.4f", (afterQsp2 - beforeQsp2)) + "s");
    //            System.out.println("QuickSortParallel3 takes " + String.format("%.4f", (afterQsp3 - beforeQsp3)) + "s");

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void assertSort(int[] arr){
        for (int i = 1; i < arr.length; i++){
            assert arr[i - 1] <= arr[i];
        }
    }

}