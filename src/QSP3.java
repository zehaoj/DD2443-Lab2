import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class QSP3 {

    private Integer[] data;

    public QSP3(Integer[] data) {
        this.data = data;
    }

    public Stream<Integer> compute() {
        return sort(() -> Stream.of(data));
    }

    static private Stream<Integer> sort(Supplier<Stream<Integer>> arr) {
        Optional<Integer> key = arr.get().findFirst();
        if (key.isPresent()) {
//            arr.get().forEach(x -> System.out.print(x + " "));
//            System.out.println();
            Stream<Integer> left = sort(() -> arr.get().parallel().skip(1).filter(x -> x < key.get()));
            Stream<Integer> right = sort(() -> arr.get().parallel().skip(1).filter(x -> x >= key.get()));
            return Stream.concat(Stream.concat(left, Stream.of(key.get())), right);
        }
        else {
            return Stream.empty();
        }
    }

}
