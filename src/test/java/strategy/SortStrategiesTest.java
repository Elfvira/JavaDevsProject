package strategy;

import comparator.ByModelComparator;
import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static support.Cars.car;

/**
 * Комплексные тесты трёх алгоритмов сортировки (Bubble, Insertion, Selection)
 * в обычном режиме и в режиме сортировки только чётных значений (Дополнение 1).
 */
class SortStrategiesTest {

    static Stream<SortStrategy<Car>> strategies() {
        return Stream.of(
                new BubbleSortStrategy<>(),
                new InsertionSortStrategy<>(),
                new SelectionSortStrategy<>()
        );
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Сортировка по мощности по возрастанию")
    void sortsByPowerAscending(SortStrategy<Car> strategy) {
        Car[] input = {
                car(300, "BMW", 2020),
                car(100, "Lada", 2021),
                car(250, "Audi", 2019),
                car(150, "Toyota", 2022)
        };

        Car[] result = strategy.sort(input, new ByPowerComparator());

        assertEquals(List.of(100, 150, 250, 300),
                Arrays.stream(result).map(Car::getPower).toList());
        // Исходный массив не мутирован
        assertEquals(300, input[0].getPower());
        assertNotSame(input, result);
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Сортировка по модели в алфавитном порядке")
    void sortsByModelAlphabetically(SortStrategy<Car> strategy) {
        Car[] input = {
                car(150, "Toyota", 2020),
                car(200, "Audi", 2019),
                car(100, "Lada", 2021),
                car(250, "BMW", 2022)
        };

        Car[] result = strategy.sort(input, new ByModelComparator());

        assertEquals(List.of("Audi", "BMW", "Lada", "Toyota"),
                Arrays.stream(result).map(Car::getModel).toList());
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Сортировка по году производства по возрастанию")
    void sortsByYearAscending(SortStrategy<Car> strategy) {
        Car[] input = {
                car(150, "Toyota", 2022),
                car(200, "Audi", 1998),
                car(100, "Lada", 2015),
                car(250, "BMW", 2005)
        };

        Car[] result = strategy.sort(input, new ByYearComparator());

        assertEquals(List.of(1998, 2005, 2015, 2022),
                Arrays.stream(result).map(Car::getManufactureYear).toList());
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Граничные случаи: пустой массив и массив из одного элемента")
    void handlesEmptyAndSingleElementArrays(SortStrategy<Car> strategy) {
        Car[] empty = new Car[0];
        Car[] sortedEmpty = strategy.sort(empty, new ByPowerComparator());
        assertEquals(0, sortedEmpty.length);
        assertNotSame(empty, sortedEmpty);

        Car[] single = {car(150, "BMW", 2020)};
        Car[] sortedSingle = strategy.sort(single, new ByPowerComparator());
        assertEquals(1, sortedSingle.length);
        assertEquals(150, sortedSingle[0].getPower());
        assertNotSame(single, sortedSingle);
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Граничные случаи: null-массив, null-компаратор и null-элементы вызывают IllegalArgumentException")
    void rejectsNullArgumentsAndNullElements(SortStrategy<Car> strategy) {
        assertThrows(IllegalArgumentException.class,
                () -> strategy.sort(null, new ByPowerComparator()));

        assertThrows(IllegalArgumentException.class,
                () -> strategy.sort(new Car[] {car(100, "A", 2000)}, null));

        assertThrows(IllegalArgumentException.class,
                () -> strategy.sort(new Car[] {car(100, "A", 2000), null}, new ByPowerComparator()));
    }

    // ---------- Дополнение 1: Сортировка только чётных значений ----------

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Доп. 1: Пример из задания [5, 8, 3, 2, 10, 7] -> [5, 2, 3, 8, 10, 7]")
    void sortsOnlyEvenPowerValuesKeepingOddFixed(SortStrategy<Car> strategy) {
        Car[] input = {
                car(5, "M1", 2020),
                car(8, "M2", 2020),
                car(3, "M3", 2020),
                car(2, "M4", 2020),
                car(10, "M5", 2020),
                car(7, "M6", 2020)
        };

        Car[] result = strategy.sortEvenOnly(input, new ByPowerComparator(), Car::getPower);

        int[] actualPowers = Arrays.stream(result).mapToInt(Car::getPower).toArray();
        assertArrayEquals(new int[] {5, 2, 3, 8, 10, 7}, actualPowers);

        // Нечётные элементы остались строго на своих индексах (0, 2, 5)
        assertEquals("M1", result[0].getModel());
        assertEquals("M3", result[2].getModel());
        assertEquals("M6", result[5].getModel());

        // Исходный массив не изменён
        assertEquals(8, input[1].getPower());
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Доп. 1: Сортировка только чётных годов выпуска, нечётные годы остаются на местах")
    void sortsOnlyEvenYearsKeepingOddYearsFixed(SortStrategy<Car> strategy) {
        Car[] input = {
                car(100, "A", 2021), // нечёт
                car(100, "B", 2024), // чёт
                car(100, "C", 2019), // нечёт
                car(100, "D", 1998), // чёт
                car(100, "E", 2010), // чёт
                car(100, "F", 2015)  // нечёт
        };

        Car[] result = strategy.sortEvenOnly(input, new ByYearComparator(), Car::getManufactureYear);

        int[] actualYears = Arrays.stream(result).mapToInt(Car::getManufactureYear).toArray();
        assertArrayEquals(new int[] {2021, 1998, 2019, 2010, 2024, 2015}, actualYears);
    }

    @ParameterizedTest
    @MethodSource("strategies")
    @DisplayName("Доп. 1: Граничные случаи — все нечётные, все чётные, пустой массив, один элемент")
    void sortEvenOnlyBoundaryCases(SortStrategy<Car> strategy) {
        // Все нечётные — порядок вообще не меняется
        Car[] allOdd = {car(9, "A", 2020), car(3, "B", 2020), car(1, "C", 2020)};
        Car[] resOdd = strategy.sortEvenOnly(allOdd, new ByPowerComparator(), Car::getPower);
        assertArrayEquals(new int[] {9, 3, 1}, Arrays.stream(resOdd).mapToInt(Car::getPower).toArray());

        // Все чётные — сортируется весь массив
        Car[] allEven = {car(10, "A", 2020), car(2, "B", 2020), car(6, "C", 2020)};
        Car[] resEven = strategy.sortEvenOnly(allEven, new ByPowerComparator(), Car::getPower);
        assertArrayEquals(new int[] {2, 6, 10}, Arrays.stream(resEven).mapToInt(Car::getPower).toArray());

        // Пустой массив
        Car[] empty = new Car[0];
        assertEquals(0, strategy.sortEvenOnly(empty, new ByPowerComparator(), Car::getPower).length);

        // Один элемент
        Car[] single = {car(4, "A", 2020)};
        assertEquals(4, strategy.sortEvenOnly(single, new ByPowerComparator(), Car::getPower)[0].getPower());

        // Null-проверки
        assertThrows(IllegalArgumentException.class,
                () -> strategy.sortEvenOnly(null, new ByPowerComparator(), Car::getPower));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.sortEvenOnly(single, null, Car::getPower));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.sortEvenOnly(single, new ByPowerComparator(), null));
    }
}
