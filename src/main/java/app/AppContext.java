package app;

import io.DataFiller;
import model.Car;
import strategy.SortStrategy;

import java.util.Comparator;

/**
 * Контекст приложения (контекст паттерна Strategy): хранит выбранные
 * источник данных, длину, алгоритм и компаратор, а также сам массив.
 * Смена стратегии в меню подменяет реализацию {@link SortStrategy} здесь.
 */
public class AppContext {

    private DataFiller dataFiller;
    private int arrayLength;
    private SortStrategy<Car> strategy;
    private Comparator<Car> comparator;
    private Car[] data;
    private Car[] sortedData;

    /**
     * Задаёт источник данных. Ранее заполненный массив сбрасывается.
     *
     * @param dataFiller источник данных
     */
    public void setDataFiller(DataFiller dataFiller) {
        this.dataFiller = dataFiller;
        this.data = null;
    }

    /**
     * Задаёт длину массива. Ранее заполненный массив сбрасывается.
     *
     * @param arrayLength длина массива, не меньше 1
     * @throws IllegalArgumentException если длина меньше 1
     */
    public void setArrayLength(int arrayLength) {
        if (arrayLength < 1) {
            throw new IllegalArgumentException("Длина массива должна быть не меньше 1");
        }
        this.arrayLength = arrayLength;
        this.data = null;
    }

    /**
     * Подменяет алгоритм сортировки.
     *
     * @param strategy стратегия сортировки
     */
    public void setStrategy(SortStrategy<Car> strategy) {
        this.strategy = strategy;
    }

    /**
     * Задаёт поле сортировки.
     *
     * @param comparator компаратор по выбранному полю
     */
    public void setComparator(Comparator<Car> comparator) {
        this.comparator = comparator;
    }

    /**
     * @return исходный (несортированный) массив или {@code null}, если он ещё не заполнен
     */
    public Car[] getData() {
        return data;
    }

    /**
     * @return результат последней сортировки или {@code null}
     */
    public Car[] getSortedData() {
        return sortedData;
    }

    /**
     * Проверяет настройки, при необходимости заполняет массив, сортирует и
     * печатает исходный и отсортированный массивы. Ошибки ввода и валидации
     * выводятся сообщением, приложение при этом не падает.
     */
    public void executeSortAndPrint() {
        String missing = findMissingSetting();
        if (missing != null) {
            System.out.println("Сначала задайте: " + missing + ".");
            return;
        }
        try {
            if (data == null) {
                data = dataFiller.fill(arrayLength);
            }
            if (data.length == 0) {
                data = null;
                System.out.println("Не удалось получить ни одного элемента. Проверьте источник данных.");
                return;
            }
            System.out.println("Исходный массив:");
            print(data);
            sortedData = strategy.sort(data, comparator);
            System.out.println("Отсортированный массив:");
            print(sortedData);
        } catch (RuntimeException e) {
            data = null;
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private String findMissingSetting() {
        if (dataFiller == null) {
            return "способ заполнения (пункт 1)";
        }
        if (arrayLength < 1) {
            return "длину массива (пункт 2)";
        }
        if (strategy == null) {
            return "алгоритм сортировки (пункт 3)";
        }
        if (comparator == null) {
            return "поле сортировки (пункт 4)";
        }
        return null;
    }

    private static void print(Car[] cars) {
        for (int i = 0; i < cars.length; i++) {
            System.out.println((i + 1) + ". " + cars[i]);
        }
    }
}
