# JavaDevsProject
Модель, архитектура, интеграция, ревью.


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
=======


=======
Класс Car с тремя полями и паттерном Builder (ручная реализация, без Lombok).
Интерфейс SortStrategy<T> — основа паттерна Strategy.
Настройка Maven-проекта: pom.xml, структура пакетов, .gitignore.
Создание репозитория на GitHub, заведение всех 5 веток от main.
Финальный мердж всех веток в main через Pull Request с код-ревью.
Соблюдение Java Code Conventions во всех классах.

Скелет Car с Builder:

public class Car {
    private final int power;          // л.с.
    private final String model;
    private final int manufactureYear;

    private Car(Builder b) {
        this.power = b.power;
        this.model = b.model;
        this.manufactureYear = b.manufactureYear;
    }

    public int getPower()           { return power; }
    public String getModel()        { return model; }
    public int getManufactureYear() { return manufactureYear; }

    @Override
    public String toString() {
        return String.format("Car{power=%d, model='%s', year=%d}",
            power, model, manufactureYear);
    }

    public static class Builder {
        private int power;
        private String model;
        private int manufactureYear;

        public Builder power(int v)           { this.power = v; return this; }
        public Builder model(String v)        { this.model = v; return this; }
        public Builder manufactureYear(int v) { this.manufactureYear = v; return this; }
        public Car build()                    { return new Car(this); }
    }
}

Интерфейс SortStrategy:

public interface SortStrategy<T> {
    T[] sort(T[] array, Comparator<T> comparator);
}

Ключевые файлы:

model/Car.java
strategy/SortStrategy.java
pom.xml
.gitignore
README.md

