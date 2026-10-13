package strategy;

import java.util.Comparator;
import java.util.function.ToIntFunction;

/**
 * Интерфейс стратегии сортировки (паттерн Strategy).
 *
 * @param <T> тип сортируемых элементов
 */
public interface SortStrategy<T> {
    /**
     * Сортирует массив с использованием заданного компаратора,
     * возвращая новый отсортированный массив без изменения исходного.
     *
     * @param array      исходный массив
     * @param comparator компаратор для сравнения элементов
     * @return новый отсортированный массив
     */
    T[] sort(T[] array, Comparator<T> comparator);

    /**
     * Сортирует только те элементы массива, у которых выбранное числовое поле чётное.
     * Элементы с нечётным значением поля остаются на своих исходных индексах.
     *
     * @param array            исходный массив
     * @param comparator       компаратор для сравнения чётных элементов
     * @param numericExtractor функция извлечения числового поля для проверки чётности
     * @return новый массив с отсортированными чётными элементами
     */
    T[] sortEvenOnly(T[] array, Comparator<T> comparator, ToIntFunction<T> numericExtractor);
}
