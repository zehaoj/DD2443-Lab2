import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QSP3 {

    private int[] data;

    public QSP3(int[] data) {
        this.data = data;
    }

    public int[] compute(int threadNum) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadNum);
        ArrayList<Integer> arr = new ArrayList<>(data.length);
        for (int i: data) {
            arr.add(i);
        }
        List<Integer> resList = null;
        try {
            resList = sort(arr, forkJoinPool);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return resList.stream().mapToInt(x -> x).toArray();
    }

    static private List<Integer> sort(List<Integer> arr, ForkJoinPool pool) throws ExecutionException, InterruptedException {
//        System.out.print(pool.getQueuedSubmissionCount());
        List<Integer> res = new ArrayList<Integer>();
        if (!arr.isEmpty()) {
            int key = arr.get(0);
//            arr.get().forEach(x -> System.out.print(x + " "));
//            System.out.println();

//            if (arr.size() > 10) {
            List<Integer> lArr = pool.submit(() -> arr.parallelStream().skip(1).filter(x -> x < key).collect(Collectors.toList())).get();
            List<Integer> mArr = pool.submit(() -> arr.parallelStream().filter(x -> x == key).collect(Collectors.toList())).get();
            List<Integer> rArr = pool.submit(() -> arr.parallelStream().skip(1).filter(x -> x > key).collect(Collectors.toList())).get();
//            List<Integer> rArr = arr;
//            rArr.removeAll(lArr);
//            rArr.removeAll(mArr);
            List<Integer> lRes;
            List<Integer> rRes;
            if (lArr.size() <= 50000) {
                Collections.sort(lArr);
                lRes = lArr;
            } else {
                lRes = sort(lArr, pool);
            }

            if (rArr.size() <= 50000) {
                Collections.sort(rArr);
                rRes = rArr;
            } else {
                rRes = sort(rArr, pool);
            }

            res.addAll(lRes);
            res.addAll(mArr);
            res.addAll(rRes);
//            } else {
//                Collections.sort(arr);
//                res.addAll(arr);
//            }
        }
        return res;
    }

}
