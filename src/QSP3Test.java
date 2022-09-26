import java.util.Random;

public class QSP3Test {

    private final static int MAX_DATA_NUM = 1_000_000;
    private final static int SAMPLING_TIMES = 12;

    public static void main(String[] args){
        try {

            Random rand = new Random();
            int[] raw_data = new int[MAX_DATA_NUM];
            int[] data = new int[MAX_DATA_NUM];

            for (int i = 0; i < MAX_DATA_NUM; i++) {
                raw_data[i] = rand.nextInt(10 * MAX_DATA_NUM);
            }

//            System.out.println(Runtime.getRuntime().availableProcessors());

            Stopwatch stopwatch = new Stopwatch();

            for (int i = 0; i < 10; i++) {
                /**
                 * QuickSort (Parallel) implemented with parallel streams and lambda functions
                 */
                System.arraycopy(raw_data, 0, data, 0, raw_data.length);
                double beforeQsp3 = stopwatch.elapsedTime();
                QSP3 qSP3 = new QSP3(data);
                int[] streamedData = qSP3.compute(12);
                double afterQsp3 = stopwatch.elapsedTime();
                assertSort(streamedData);

                System.out.println(String.format("%.4f", (afterQsp3 - beforeQsp3)) + "s");
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