package strategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Реализация сортировки методом вставок (Insertion Sort).
 *
 * @param <T> тип сортируемых элементов
 */
public class InsertionSortStrategy<T> implements SortStrategy<T> {

    @Override
    public T[] sort(T[] array, Comparator<T> comparator) {
        validateInputs(array, comparator);
        T[] result = Arrays.copyOf(array, array.length);

        for (int i = 1; i < result.length; i++) {
            T key = result[i];
            int j = i - 1;
            while (j >= 0 && comparator.compare(result[j], key) > 0) {
                result[j + 1] = result[j];
                j--;
            }
            result[j + 1] = key;
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

        for (int i = 1; i < evenIndices.length; i++) {
            T key = result[evenIndices[i]];
            int j = i - 1;
            while (j >= 0 && comparator.compare(result[evenIndices[j]], key) > 0) {
                result[evenIndices[j + 1]] = result[evenIndices[j]];
                j--;
            }
            result[evenIndices[j + 1]] = key;
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
