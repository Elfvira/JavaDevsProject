package app;

import comparator.ByModelComparator;
import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import io.DataFiller;
import io.FileFiller;
import io.ManualFiller;
import io.RandomFiller;
import io.ResultFileWriter;
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
        System.out.println("6. Сортировка только чётных значений (доп. 1)");
        System.out.println("7. Сохранить результат сортировки в файл [append] (доп. 2)");
        System.out.println("8. Заполнить 3 коллекции через Stream API (доп. 3)");
        System.out.println("9. Многопоточный подсчёт вхождений элемента (доп. 4)");
        System.out.println("0. Выход");
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

    /**
     * Запрашивает числовое поле для сортировки только чётных значений (Дополнение 1).
     *
     * @param scanner источник ввода
     * @return 1 — Мощность, 2 — Год выпуска
     */
    public static int chooseEvenSortField(Scanner scanner) {
        System.out.println("Числовое поле для сортировки только чётных значений:");
        System.out.println("1. Мощность (л.с.)");
        System.out.println("2. Год выпуска");
        return readInRange(scanner, 1, 2);
    }

    /**
     * Запрашивает путь к файлу для сохранения результатов в режиме добавления (Дополнение 2).
     *
     * @param scanner источник ввода
     * @return путь к файлу
     */
    public static String readOutputFilePath(Scanner scanner) {
        System.out.print("Путь к файлу для сохранения (Enter - " + ResultFileWriter.DEFAULT_OUTPUT_FILE + "): ");
        String path = scanner.nextLine().trim();
        return path.isEmpty() ? ResultFileWriter.DEFAULT_OUTPUT_FILE : path;
    }

    /**
     * Запрашивает режим демонстрации трёх коллекций через Stream API (Дополнение 3).
     *
     * @param scanner источник ввода
     * @return true, если нужно добавить дубликат первого элемента для наглядной демонстрации HashSet
     */
    public static boolean askAddDuplicateForDemo(Scanner scanner) {
        System.out.println("Режим заполнения 3 коллекций (ArrayList, LinkedList, HashSet):");
        System.out.println("1. Из текущего массива как есть");
        System.out.println("2. С добавлением дубликата 1-й машины (наглядно показать удаление дубликатов в HashSet)");
        int choice = readInRange(scanner, 1, 2);
        return choice == 2;
    }

    /**
     * Проводит диалог многопоточного подсчёта вхождений элемента и вызывает подсчёт в контексте (Дополнение 4).
     *
     * @param scanner источник ввода
     * @param ctx     контекст приложения
     */
    public static void runConcurrentCountDialog(Scanner scanner, AppContext ctx) {
        System.out.println("Критерий многопоточного подсчёта вхождений:");
        System.out.println("1. По мощности (число N)");
        System.out.println("2. По году выпуска (число N)");
        System.out.println("3. По названию модели");
        int searchType = readInRange(scanner, 1, 3);

        int numericVal = 0;
        String modelVal = "";

        if (searchType == 1) {
            System.out.println("Укажите искомую мощность N (1-2000):");
            numericVal = readInRange(scanner, 1, 2000);
        } else if (searchType == 2) {
            System.out.println("Укажите искомый год выпуска N (1886-2100):");
            numericVal = readInRange(scanner, 1886, 2100);
        } else {
            while (modelVal.isBlank()) {
                System.out.print("Введите название модели для поиска: ");
                modelVal = scanner.nextLine().trim();
                if (modelVal.isBlank()) {
                    System.out.println("Модель не может быть пустой.");
                }
            }
        }

        System.out.println("Количество потоков (1-16):");
        int threads = readInRange(scanner, 1, 16);

        ctx.executeConcurrentCount(searchType, numericVal, modelVal, threads);
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
