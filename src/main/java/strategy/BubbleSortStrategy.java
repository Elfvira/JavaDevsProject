package strategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Реализация сортировки методом пузырька (Bubble Sort).
 *
 * @param <T> тип сортируемых элементов
 */
public class BubbleSortStrategy<T> implements SortStrategy<T> {

    @Override
    public T[] sort(T[] array, Comparator<T> comparator) {
        validateInputs(array, comparator);
        T[] result = Arrays.copyOf(array, array.length);

        for (int i = 0; i < result.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < result.length - i - 1; j++) {
                if (comparator.compare(result[j], result[j + 1]) > 0) {
                    T tmp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return result;
    }

    @Override
    public T[] sortEvenOnly(T[] array, Comparator<T> comparator, ToIntFunction<T> numericExtractor) {
        validateInputs(array, comparator);
        if (numericExtractor == null) {
            throw new IllegalArgumentException("Функция извлечения числового поля не может быть null");
        }

        T[] result = Arrays.copyOf(array, array.length);
        int[] evenIndices = collectEvenIndices(result, numericExtractor);

        for (int i = 0; i < evenIndices.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < evenIndices.length - i - 1; j++) {
                int idxA = evenIndices[j];
                int idxB = evenIndices[j + 1];
                if (comparator.compare(result[idxA], result[idxB]) > 0) {
                    T tmp = result[idxA];
                    result[idxA] = result[idxB];
                    result[idxB] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return result;
    }

    private void validateInputs(T[] array, Comparator<T> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Массив не может быть null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("Компаратор не может быть null");
        }
        for (T element : array) {
            if (element == null) {
                throw new IllegalArgumentException("Элементы массива не могут быть null");
            }
        }
    }

    private int[] collectEvenIndices(T[] array, ToIntFunction<T> numericExtractor) {
        int count = 0;
        for (T item : array) {
            if (numericExtractor.applyAsInt(item) % 2 == 0) {
                count++;
            }
        }
        int[] indices = new int[count];
        int pos = 0;
        for (int i = 0; i < array.length; i++) {
            if (numericExtractor.applyAsInt(array[i]) % 2 == 0) {
                indices[pos++] = i;
            }
        }
        return indices;
    }
}
