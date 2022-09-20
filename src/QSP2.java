import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveAction;

public class QSP2 extends RecursiveAction {
    private int[] data;
    private int begin = 0;
    private int end = 0;

    private static int numOfProcessors = Runtime.getRuntime().availableProcessors();
    private static int count = 0;

    public QSP2(int[] arr, int begin, int end){
        this.data = arr;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public void compute(){
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

    private void parallelSort(int[] arr, int left, int right){

        List<QSP2> futures = new Vector<>();
        int l = partition(arr, left, right);

        if (l - left > 1){
//            System.out.println(count + " " + numOfProcessors);
            if (count < numOfProcessors){
                count++;
                //System.out.println("l: " + l + "\tleft: " + left);
                QSP2 leftTask = new QSP2(arr, left, l - 1);
                futures.add(leftTask);
            }
            else{
                sort(arr, left, l - 1);
            }
        }
        if (right - l > 1){
//            System.out.println(count + " " + numOfProcessors);
            if (count < numOfProcessors){
                count++;
                //System.out.println("right: " + right + "\tl: " + l);
                QSP2 rightTask = new QSP2(arr, l + 1, right);
                futures.add(rightTask);
            }
            else{
                sort(arr, l + 1, right);
            }
        }
        if (!futures.isEmpty())
            invokeAll(futures);
        count--;
    }
}