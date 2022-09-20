import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class QSP1 implements Runnable{
    private int[] data;
    private int begin = 0;
    private int end = 0;
    private ExecutorService executorService;
    private List<Future> futures;

    private static int numOfProcessors = Runtime.getRuntime().availableProcessors();
//    private static int numOfProcessors = 0;

    public QSP1(int[] arr, int begin, int end, ExecutorService executorService, List<Future> futures){
        this.data = arr;
        this.begin = begin;
        this.end = end;
        this.executorService = executorService;
        this.futures = futures;
//        this.numOfProcessors = i;
    }

    @Override
    public void run(){
        parallelSort(data, begin, end);
    }

    private void sort(int[] arr, int left, int right){
        if (left < right){
            int p = partition(arr, left, right);
            sort(arr, left, p - 1);
            sort(arr, p + 1, right);
        }
    }

    private int partition(int[] arr, int left, int right){
        if (left >= right)
            return -1;
        int pivot = left;
        int key = arr[pivot];
        int l = left;
        int r = right;
        while (l < r){
            while (l < r && arr[r] >= key)
                r--;
            if(l < r) {
                arr[l] = arr[r];
                l++;
                while(arr[l] < key && l < r)
                    l++;
                if(l < r) {
                    arr[r] = arr[l];
                    r--;
                }
            }
        }
        arr[l] = key;
        return l;
    }

    private void parallelSort(int[] arr, int left, int right) {

        int l = partition(arr, left, right);

        if (l - left > 1) {
            if (futures.size() < numOfProcessors) {
                futures.add(executorService.submit(new QSP1(arr, left, l - 1, executorService, futures)));
            } else {
                sort(arr, left, l - 1);
            }
//            executorService.execute(new QSP1(arr, left, l - 1, executorService, futures));
        }
        if (right - l > 1) {
            if (futures.size() < numOfProcessors) {
                futures.add(executorService.submit(new QSP1(arr, l + 1, right, executorService, futures)));
            } else {
                sort(arr, l + 1, right);
            }
//            executorService.execute(new QSP1(arr, l + 1, right, executorService, futures));
//        }
        }
    }
}
