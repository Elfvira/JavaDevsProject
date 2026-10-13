package service;

import model.Car;
import validation.CarValidator;
import validation.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис для заполнения трёх различных типов коллекций ({@link ArrayList},
 * {@link LinkedList}, {@link HashSet}) с использованием Stream API и валидацией объектов.
 */
public class StreamCollectionService {

    private final CarValidator validator;

    public StreamCollectionService() {
        this(new CarValidator());
    }

    public StreamCollectionService(CarValidator validator) {
        if (validator == null) {
            throw new IllegalArgumentException("Валидатор не может быть null");
        }
        this.validator = validator;
    }

    /**
     * Контейнер с тремя заполненными коллекциями разных типов.
     */
    public record CollectionsBundle(
            ArrayList<Car> arrayList,
            LinkedList<Car> linkedList,
            HashSet<Car> hashSet
    ) {
    }

    /**
     * Заполняет три коллекции через Stream API из исходного массива автомобилей
     * с обязательной валидацией каждого элемента.
     *
     * @param cars исходный массив автомобилей
     * @return набор из ArrayList, LinkedList и HashSet
     */
    public CollectionsBundle fillFromArray(Car[] cars) {
        if (cars == null) {
            throw new IllegalArgumentException("Исходный массив не может быть null");
        }

        List<Car> validated = Arrays.stream(cars)
                .peek(this::validateCarNotNull)
                .toList();

        ArrayList<Car> arrayList = validated.stream()
                .collect(Collectors.toCollection(ArrayList::new));

        LinkedList<Car> linkedList = validated.stream()
                .collect(Collectors.toCollection(LinkedList::new));

        HashSet<Car> hashSet = validated.stream()
                .collect(Collectors.toCollection(HashSet::new));

        return new CollectionsBundle(arrayList, linkedList, hashSet);
    }

    /**
     * Заполняет три коллекции через Stream API с помощью генератора (Supplier)
     * заданного количества элементов и их валидации.
     *
     * @param supplier поставщик объектов Car
     * @param count    количество генерируемых элементов
     * @return набор из ArrayList, LinkedList и HashSet
     */
    public CollectionsBundle fillFromSupplier(Supplier<Car> supplier, int count) {
        if (supplier == null) {
            throw new IllegalArgumentException("Поставщик (Supplier) не может быть null");
        }
        if (count < 0) {
            throw new IllegalArgumentException("Количество элементов не может быть отрицательным");
        }

        Car[] generated = Stream.generate(supplier)
                .limit(count)
                .peek(this::validateCarNotNull)
                .toArray(Car[]::new);

        return fillFromArray(generated);
    }

    /**
     * Заполняет три коллекции через Stream API из строковых представлений формата "power;model;year",
     * создавая объекты через {@link Car.Builder} и валидируя каждый из них.
     *
     * @param lines коллекция строк формата "power;model;year"
     * @return набор из ArrayList, LinkedList и HashSet
     */
    public CollectionsBundle fillFromStrings(Collection<String> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("Коллекция строк не может быть null");
        }

        Car[] parsed = lines.stream()
                .map(this::parseAndValidate)
                .toArray(Car[]::new);

        return fillFromArray(parsed);
    }

    /**
     * Выводит содержимое всех трёх коллекций в консоль.
     *
     * @param bundle заполненные коллекции
     */
    public void printCollections(CollectionsBundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("Набор коллекций не может быть null");
        }

        System.out.println("--- 1. ArrayList<Car> (размер: " + bundle.arrayList().size() + ") ---");
        printCollectionElements(bundle.arrayList());

        System.out.println("--- 2. LinkedList<Car> (размер: " + bundle.linkedList().size() + ") ---");
        printCollectionElements(bundle.linkedList());

        System.out.println("--- 3. HashSet<Car> (уникальных элементов: " + bundle.hashSet().size() + ") ---");
        printCollectionElements(bundle.hashSet());

        int duplicatesRemoved = bundle.arrayList().size() - bundle.hashSet().size();
        if (duplicatesRemoved > 0) {
            System.out.println("(В HashSet автоматически удалено дубликатов: " + duplicatesRemoved + ")");
        }
    }

    private void printCollectionElements(Collection<Car> collection) {
        if (collection.isEmpty()) {
            System.out.println("  [пусто]");
            return;
        }
        int idx = 1;
        for (Car car : collection) {
            System.out.println("  " + (idx++) + ". " + car);
        }
    }

    private void validateCarNotNull(Car car) {
        if (car == null) {
            throw new ValidationException("Элемент коллекции не может быть null");
        }
        validator.validate(car);
    }

    private Car parseAndValidate(String line) {
        if (line == null || line.isBlank()) {
            throw new ValidationException("Строка данных не может быть пустой");
        }
        String[] parts = line.split(";", -1);
        if (parts.length != 3) {
            throw new ValidationException("Ожидается три поля (power;model;year)");
        }
        int power;
        int year;
        try {
            power = Integer.parseInt(parts[0].trim());
            year = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Мощность и год должны быть целыми числами");
        }
        String model = parts[1].trim();
        validator.validate(power, model, year);

        return new Car.Builder()
                .power(power)
                .model(model)
                .manufactureYear(year)
                .build();
    }
}
