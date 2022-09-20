import java.util.ArrayList;
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
            List<Integer> lArr = pool.submit(() -> arr.parallelStream().skip(1).filter(x -> x < key).collect(Collectors.toList())).get();
            List<Integer> mArr = pool.submit(() -> arr.parallelStream().filter(x -> x == key).collect(Collectors.toList())).get();
            List<Integer> rArr = pool.submit(() -> arr.parallelStream().skip(1).filter(x -> x > key).collect(Collectors.toList())).get();
            List<Integer> lRes = lArr.size() <= 1 ? lArr : sort(lArr, pool);
            List<Integer> rRes = rArr.size() <= 1 ? rArr : sort(rArr, pool);
            res.addAll(lRes);
            res.addAll(mArr);
            res.addAll(rRes);
        }
        return res;
    }

}
