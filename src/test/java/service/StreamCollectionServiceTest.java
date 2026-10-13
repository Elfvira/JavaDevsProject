package service;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.StreamCollectionService.CollectionsBundle;
import support.Console;
import validation.ValidationException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static support.Cars.car;

/**
 * Тесты для {@link StreamCollectionService} (Дополнение 3: заполнение трёх разных
 * коллекций через Stream API и валидация объектов).
 */
class StreamCollectionServiceTest {

    private final StreamCollectionService service = new StreamCollectionService();

    @Test
    @DisplayName("Доп. 3: Заполняет ArrayList, LinkedList и HashSet через Stream API; HashSet удаляет дубликаты")
    void fillsThreeCollectionsAndRemovesDuplicatesInHashSet() {
        Car camry1 = car(150, "Toyota Camry", 2020);
        Car camryDuplicate = car(150, "Toyota Camry", 2020);
        Car bmw = car(250, "BMW X5", 2022);
        Car vesta = car(105, "Lada Vesta", 2021);

        Car[] source = {camry1, bmw, camryDuplicate, vesta};

        CollectionsBundle bundle = service.fillFromArray(source);

        assertEquals(4, bundle.arrayList().size());
        assertEquals(4, bundle.linkedList().size());
        // В HashSet дубликат удалён
        assertEquals(3, bundle.hashSet().size());

        assertTrue(bundle.hashSet().contains(camry1));
        assertTrue(bundle.hashSet().contains(bmw));
        assertTrue(bundle.hashSet().contains(vesta));
    }

    @Test
    @DisplayName("Доп. 3: Заполнение из строк через Stream API парсит и валидирует объекты")
    void fillsFromStringsAndValidates() {
        List<String> lines = List.of(
                "150;Toyota Camry;2020",
                "250;BMW X5;2022",
                "150;Toyota Camry;2020"
        );

        CollectionsBundle bundle = service.fillFromStrings(lines);

        assertEquals(3, bundle.arrayList().size());
        assertEquals(3, bundle.linkedList().size());
        assertEquals(2, bundle.hashSet().size());
    }

    @Test
    @DisplayName("Доп. 3: Заполнение через Supplier и Stream.generate")
    void fillsFromSupplier() {
        AtomicInteger counter = new AtomicInteger(100);
        CollectionsBundle bundle = service.fillFromSupplier(
                () -> car(counter.getAndIncrement(), "Model", 2020),
                5
        );

        assertEquals(5, bundle.arrayList().size());
        assertEquals(5, bundle.linkedList().size());
        assertEquals(5, bundle.hashSet().size());
    }

    @Test
    @DisplayName("Доп. 3: Валидация отклоняет некорректные автомобили при заполнении через Stream API")
    void rejectsInvalidCarsDuringStreamFilling() {
        Car invalidPower = car(0, "BMW", 2020);
        assertThrows(ValidationException.class,
                () -> service.fillFromArray(new Car[] {invalidPower}));

        Car invalidModel = car(150, "   ", 2020);
        assertThrows(ValidationException.class,
                () -> service.fillFromArray(new Car[] {invalidModel}));

        Car invalidYear = car(150, "BMW", 1700);
        assertThrows(ValidationException.class,
                () -> service.fillFromArray(new Car[] {invalidYear}));

        assertThrows(ValidationException.class,
                () -> service.fillFromStrings(List.of("bad_power;BMW;2020")));
    }

    @Test
    @DisplayName("Доп. 3: Вывод содержимого всех трёх коллекций в консоль")
    void printsCollectionsContent() {
        Car c1 = car(150, "Toyota Camry", 2020);
        Car c2 = car(150, "Toyota Camry", 2020);
        CollectionsBundle bundle = service.fillFromArray(new Car[] {c1, c2});

        String output = Console.capture(() -> service.printCollections(bundle));

        assertTrue(output.contains("ArrayList<Car> (размер: 2)"), output);
        assertTrue(output.contains("LinkedList<Car> (размер: 2)"), output);
        assertTrue(output.contains("HashSet<Car> (уникальных элементов: 1)"), output);
        assertTrue(output.contains("удалено дубликатов: 1"), output);
    }
}
