package app;

import comparator.ByModelComparator;
import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import io.DataFiller;
import io.FileFiller;
import io.ManualFiller;
import io.RandomFiller;
import model.Car;
import strategy.BubbleSortStrategy;
import strategy.InsertionSortStrategy;
import strategy.SelectionSortStrategy;
import strategy.SortStrategy;

import java.util.Comparator;
import java.util.Scanner;

/**
 * Текстовое меню: вывод пунктов и чтение выбора пользователя.
 * Все методы чтения работают построчно ({@code nextLine}), чтобы не
 * ломать ввод в {@link ManualFiller}, который делит тот же {@link Scanner}.
 */
public final class Menu {

    /** Максимально допустимая длина массива. */
    public static final int MAX_LENGTH = 100_000;

    /** Значение, возвращаемое {@link #readInt(Scanner)} при нечисловом вводе. */
    public static final int INVALID = -1;

    private static final String DEFAULT_FILE = "cars.txt";

    private Menu() {
    }

    /**
     * Печатает главное меню.
     */
    public static void print() {
        System.out.println();
        System.out.println("===== Сортировка автомобилей =====");
        System.out.println("1. Выбрать способ заполнения (файл / рандом / вручную)");
        System.out.println("2. Указать длину массива");
        System.out.println("3. Выбрать алгоритм сортировки");
        System.out.println("4. Выбрать поле сортировки");
        System.out.println("5. Выполнить сортировку");
        System.out.println("6. Выход");
        System.out.print("Ваш выбор: ");
    }

    /**
     * Читает целое число из строки ввода.
     *
     * @param scanner источник ввода
     * @return введённое число или {@link #INVALID}, если введено не число
     */
    public static int readInt(Scanner scanner) {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return INVALID;
        }
    }

    /**
     * Предлагает выбрать способ заполнения массива.
     *
     * @param scanner источник ввода
     * @return выбранный источник данных
     */
    public static DataFiller chooseFiller(Scanner scanner) {
        System.out.println("Способ заполнения:");
        System.out.println("1. Из файла");
        System.out.println("2. Случайно");
        System.out.println("3. Вручную");
        int choice = readInRange(scanner, 1, 3);
        switch (choice) {
            case 1:
                System.out.print("Путь к файлу (Enter - " + DEFAULT_FILE + "): ");
                String path = scanner.nextLine().trim();
                return new FileFiller(path.isEmpty() ? DEFAULT_FILE : path);
            case 2:
                return new RandomFiller();
            default:
                return new ManualFiller(scanner);
        }
    }

    /**
     * Запрашивает длину массива.
     *
     * @param scanner источник ввода
     * @return длина от 1 до {@link #MAX_LENGTH}
     */
    public static int readLength(Scanner scanner) {
        System.out.println("Длина массива (1-" + MAX_LENGTH + ")");
        return readInRange(scanner, 1, MAX_LENGTH);
    }

    /**
     * Предлагает выбрать алгоритм сортировки (паттерн Strategy).
     *
     * @param scanner источник ввода
     * @return выбранная стратегия сортировки
     */
    public static SortStrategy<Car> chooseStrategy(Scanner scanner) {
        System.out.println("Алгоритм сортировки:");
        System.out.println("1. Пузырьком");
        System.out.println("2. Вставками");
        System.out.println("3. Выбором");
        int choice = readInRange(scanner, 1, 3);
        switch (choice) {
            case 1:
                return new BubbleSortStrategy<>();
            case 2:
                return new InsertionSortStrategy<>();
            default:
                return new SelectionSortStrategy<>();
        }
    }

    /**
     * Предлагает выбрать поле сортировки.
     *
     * @param scanner источник ввода
     * @return компаратор по выбранному полю
     */
    public static Comparator<Car> chooseComparator(Scanner scanner) {
        System.out.println("Поле сортировки:");
        System.out.println("1. Мощность");
        System.out.println("2. Модель");
        System.out.println("3. Год выпуска");
        int choice = readInRange(scanner, 1, 3);
        switch (choice) {
            case 1:
                return new ByPowerComparator();
            case 2:
                return new ByModelComparator();
            default:
                return new ByYearComparator();
        }
    }

    private static int readInRange(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Ваш выбор [" + min + "-" + max + "]: ");
            int value = readInt(scanner);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Некорректный ввод, повторите.");
        }
    }
}
