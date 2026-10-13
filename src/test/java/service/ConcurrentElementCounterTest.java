package service;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static support.Cars.car;

/**
 * Тесты для {@link ConcurrentElementCounter} (Дополнение 4: многопоточный подсчёт вхождений).
 */
class ConcurrentElementCounterTest {

    @Test
    @DisplayName("Доп. 4: Пустая коллекция и пустой массив возвращают 0")
    void emptyCollectionReturnsZero() {
        assertEquals(0L, ConcurrentElementCounter.countByPower(Collections.emptyList(), 150, 4));
        assertEquals(0L, ConcurrentElementCounter.countByPower(new Car[0], 150, 4));
    }

    @Test
    @DisplayName("Доп. 4: Отсутствие совпадений возвращает 0")
    void noMatchesReturnsZero() {
        Car[] cars = {
                car(100, "Audi", 2018),
                car(200, "BMW", 2020),
                car(300, "Mercedes", 2022)
        };

        assertEquals(0L, ConcurrentElementCounter.countByPower(cars, 999, 3));
        assertEquals(0L, ConcurrentElementCounter.countByYear(cars, 1995, 3));
        assertEquals(0L, ConcurrentElementCounter.countByModel(cars, "Tesla", 3));
    }

    @Test
    @DisplayName("Доп. 4: Одно совпадение")
    void singleMatchReturnsOne() {
        Car[] cars = {
                car(100, "Audi", 2018),
                car(250, "BMW", 2020),
                car(300, "Mercedes", 2022)
        };

        assertEquals(1L, ConcurrentElementCounter.countByPower(cars, 250, 4));
        assertEquals(1L, ConcurrentElementCounter.countByYear(cars, 2020, 4));
        assertEquals(1L, ConcurrentElementCounter.countByModel(cars, "bmw", 4));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 8})
    @DisplayName("Доп. 4: Несколько совпадений дают одинаковый точный результат при любом числе потоков")
    void multipleMatchesAcrossVariousThreadCounts(int threads) {
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i % 5 == 0) {
                cars.add(car(150, "Toyota Camry", 2020)); // 20 совпадений
            } else {
                cars.add(car(200 + i, "Other " + i, 2010));
            }
        }

        assertEquals(20L, ConcurrentElementCounter.countByPower(cars, 150, threads));
        assertEquals(20L, ConcurrentElementCounter.countByYear(cars, 2020, threads));
        assertEquals(20L, ConcurrentElementCounter.countElement(
                cars, car(150, "Toyota Camry", 2020), threads));
    }

    @Test
    @DisplayName("Доп. 4: Некорректные аргументы вызывают IllegalArgumentException")
    void invalidArgumentsThrowIllegalArgumentException() {
        Car[] valid = {car(150, "BMW", 2020)};

        assertThrows(IllegalArgumentException.class,
                () -> ConcurrentElementCounter.countByPredicate((List<Car>) null, c -> true, 2));
        assertThrows(IllegalArgumentException.class,
                () -> ConcurrentElementCounter.countByPredicate(valid, null, 2));
        assertThrows(IllegalArgumentException.class,
                () -> ConcurrentElementCounter.countByPredicate(valid, c -> true, 0));
        assertThrows(IllegalArgumentException.class,
                () -> ConcurrentElementCounter.countElement(valid, null, 2));
    }
}
