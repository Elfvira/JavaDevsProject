import java.util.Comparator;

public interface SortStrategy<T> {
    T[] sort(T[] array, Comparator<T> comparator);
}
