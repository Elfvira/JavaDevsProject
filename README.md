# JavaDevsProject
ветка feat-sorting
Паттерн Strategy: три реализации алгоритмов сортировки.

Три реализации SortStrategy<Car> своими руками — без Arrays.sort, Collections.sort, List.sort:
BubbleSortStrategy<T> — сортировка пузырьком
InsertionSortStrategy<T> — сортировка вставками
SelectionSortStrategy<T> — сортировка выбором
Каждая реализация принимает массив и Comparator<T>, возвращает новый отсортированный массив (не мутирует исходный).
Сортировка работает с любым типом T через дженерики — конкретно с Car, но не завязана на него.
Обработка граничных случаев: пустой массив, один элемент, null-элементы — бросать IllegalArgumentException.

Пример скелета BubbleSortStrategy:

public class BubbleSortStrategy<T> implements SortStrategy<T> {
    @Override
    public T[] sort(T[] array, Comparator<T> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Массив не может быть null");
        }
        T[] result = Arrays.copyOf(array, array.length);
        for (int i = 0; i < result.length - 1; i++) {
            for (int j = 0; j < result.length - i - 1; j++) {
                if (result[j] == null || result[j + 1] == null) {
                    throw new IllegalArgumentException(
                        "Элементы массива не могут быть null");
                }
                if (comparator.compare(result[j], result[j + 1]) > 0) {
                    T tmp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = tmp;
                }
            }
        }
        return result;
    }
}

Ключевые файлы:

strategy/BubbleSortStrategy.java
strategy/InsertionSortStrategy.java
strategy/SelectionSortStrategy.java

