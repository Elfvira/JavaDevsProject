package app;

import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import io.DataFiller;
import io.ResultFileWriter;
import model.Car;
import service.ConcurrentElementCounter;
import service.StreamCollectionService;
import service.StreamCollectionService.CollectionsBundle;
import strategy.SortStrategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.ToIntFunction;

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
            if (!ensureDataFilled()) {
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

    /**
     * Дополнение 1: Сортировка только чётных значений по мощности или году производства.
     * Нечётные элементы остаются на своих исходных индексах.
     *
     * @param fieldChoice 1 — по мощности, 2 — по году выпуска
     */
    public void executeEvenSortAndPrint(int fieldChoice) {
        if (dataFiller == null) {
            System.out.println("Сначала задайте: способ заполнения (пункт 1).");
            return;
        }
        if (arrayLength < 1) {
            System.out.println("Сначала задайте: длину массива (пункт 2).");
            return;
        }
        if (strategy == null) {
            System.out.println("Сначала задайте: алгоритм сортировки (пункт 3).");
            return;
        }

        try {
            if (!ensureDataFilled()) {
                return;
            }

            Comparator<Car> evenComparator;
            ToIntFunction<Car> extractor;
            String fieldName;

            if (fieldChoice == 1) {
                evenComparator = new ByPowerComparator();
                extractor = Car::getPower;
                fieldName = "мощность";
            } else {
                evenComparator = new ByYearComparator();
                extractor = Car::getManufactureYear;
                fieldName = "год выпуска";
            }

            System.out.println("Исходный массив:");
            print(data);

            sortedData = strategy.sortEvenOnly(data, evenComparator, extractor);

            System.out.println("Массив после сортировки только чётных значений (поле: " + fieldName + "):");
            print(sortedData);
        } catch (RuntimeException e) {
            data = null;
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Дополнение 2: Сохранение последнего результата сортировки в файл в режиме добавления (append).
     *
     * @param filePath путь к файлу
     */
    public void saveSortedResultToFile(String filePath) {
        if (sortedData == null || sortedData.length == 0) {
            System.out.println("Нет отсортированных данных для сохранения. Сначала выполните сортировку (пункт 5 или 6).");
            return;
        }
        try {
            ResultFileWriter writer = new ResultFileWriter(filePath);
            writer.appendSortedCars(sortedData, "Результат сортировки автомобилей");
            System.out.println("Результат успешно дописан (append) в файл: " + writer.getFilePath().toAbsolutePath());
        } catch (RuntimeException e) {
            System.out.println("Ошибка при записи файла: " + e.getMessage());
        }
    }

    /**
     * Дополнение 3: Заполнение трёх разных типов коллекций (ArrayList, LinkedList, HashSet)
     * через Stream API и вывод их содержимого.
     *
     * @param addDuplicateForDemo если true и массив не пуст, добавляет дубликат первого элемента
     *                            для наглядной демонстрации удаления дубликатов в HashSet
     */
    public void demonstrateStreamCollections(boolean addDuplicateForDemo) {
        if (dataFiller == null) {
            System.out.println("Сначала задайте: способ заполнения (пункт 1).");
            return;
        }
        if (arrayLength < 1) {
            System.out.println("Сначала задайте: длину массива (пункт 2).");
            return;
        }
        try {
            if (!ensureDataFilled()) {
                return;
            }

            Car[] sourceForStreams = data;
            if (addDuplicateForDemo && data.length > 0) {
                sourceForStreams = Arrays.copyOf(data, data.length + 1);
                sourceForStreams[data.length] = data[0];
                System.out.println("(Для наглядной проверки HashSet в поток добавлен дубликат 1-го автомобиля: " + data[0] + ")");
            }

            StreamCollectionService service = new StreamCollectionService();
            CollectionsBundle bundle = service.fillFromArray(sourceForStreams);
            service.printCollections(bundle);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при заполнении коллекций: " + e.getMessage());
        }
    }

    /**
     * Дополнение 4: Многопоточный подсчёт вхождений заданного значения.
     *
     * @param searchType  1 — по мощности, 2 — по году выпуска, 3 — по модели
     * @param numericVal  искомое числовое значение (для типов 1 и 2)
     * @param modelVal    искомая модель (для типа 3)
     * @param threadCount количество потоков
     */
    public void executeConcurrentCount(int searchType, int numericVal, String modelVal, int threadCount) {
        if (dataFiller == null) {
            System.out.println("Сначала задайте: способ заполнения (пункт 1).");
            return;
        }
        if (arrayLength < 1) {
            System.out.println("Сначала задайте: длину массива (пункт 2).");
            return;
        }
        try {
            if (!ensureDataFilled()) {
                return;
            }

            long count;
            String criterionDesc;
            if (searchType == 1) {
                count = ConcurrentElementCounter.countByPower(data, numericVal, threadCount);
                criterionDesc = "мощность = " + numericVal + " л.с.";
            } else if (searchType == 2) {
                count = ConcurrentElementCounter.countByYear(data, numericVal, threadCount);
                criterionDesc = "год выпуска = " + numericVal;
            } else {
                count = ConcurrentElementCounter.countByModel(data, modelVal, threadCount);
                criterionDesc = "модель = '" + modelVal + "'";
            }

            System.out.println("Многопоточный подсчёт завершён (потоков: " + threadCount + ").");
            System.out.println("Критерий: " + criterionDesc + " -> Найдено вхождений: " + count);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при подсчёте: " + e.getMessage());
        }
    }

    private boolean ensureDataFilled() {
        if (data == null) {
            data = dataFiller.fill(arrayLength);
        }
        if (data.length == 0) {
            data = null;
            System.out.println("Не удалось получить ни одного элемента. Проверьте источник данных.");
            return false;
        }
        return true;
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
