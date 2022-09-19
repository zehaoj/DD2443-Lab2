import java.util.stream.Stream;

public class QSP3 {

    private Integer[] data;

    public QSP3(Integer[] data) {
        this.data = data;
    }

    public Stream<Integer> compute() {
        return sort(Stream.of(data));
    }

    private Stream<Integer> sort(Stream<Integer> arr) {
        Integer key = arr.findFirst().orElse(null);
        if (key != null) {
            Stream<Integer> left = sort(arr.skip(1).parallel().filter(x -> x.compareTo(key) < 0));
            Stream<Integer> right = sort(arr.skip(1).parallel().filter(x -> x.compareTo(key) >= 0));
            return Stream.concat(Stream.concat(left, Stream.of(key)), right);
        }
        else {
            return Stream.empty();
        }
    }

}
