package strategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Реализация сортировки методом выбора (Selection Sort).
 *
 * @param <T> тип сортируемых элементов
 */
public class SelectionSortStrategy<T> implements SortStrategy<T> {

    @Override
    public T[] sort(T[] array, Comparator<T> comparator) {
        validateInputs(array, comparator);
        T[] result = Arrays.copyOf(array, array.length);

        for (int i = 0; i < result.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < result.length; j++) {
                if (comparator.compare(result[j], result[minIdx]) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                T tmp = result[i];
                result[i] = result[minIdx];
                result[minIdx] = tmp;
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
            int minEvenPos = i;
            for (int j = i + 1; j < evenIndices.length; j++) {
                if (comparator.compare(result[evenIndices[j]], result[evenIndices[minEvenPos]]) < 0) {
                    minEvenPos = j;
                }
            }
            if (minEvenPos != i) {
                int idxA = evenIndices[i];
                int idxB = evenIndices[minEvenPos];
                T tmp = result[idxA];
                result[idxA] = result[idxB];
                result[idxB] = tmp;
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
