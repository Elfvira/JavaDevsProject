package comparator;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import strategy.BubbleSortStrategy;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static support.Cars.car;

class ComparatorsTest {

    private static List<String> sortedModels(Comparator<Car> comparator, Car... cars) {
        Car[] sorted = new BubbleSortStrategy<Car>().sort(cars, comparator);
        return Arrays.stream(sorted).map(Car::getModel).toList();
    }

    @Nested
    @DisplayName("ByPowerComparator")
    class ByPower {
        private final Comparator<Car> cmp = new ByPowerComparator();

        @Test
        void lessPowerComesFirst() {
            assertTrue(cmp.compare(car(100, "a", 2000), car(200, "b", 2000)) < 0);
        }

        @Test
        void morePowerComesLast() {
            assertTrue(cmp.compare(car(200, "a", 2000), car(100, "b", 2000)) > 0);
        }

        @Test
        void equalPowerGivesZeroEvenIfOtherFieldsDiffer() {
            assertEquals(0, cmp.compare(car(150, "a", 1990), car(150, "z", 2024)));
        }

        @Test
        void comparingWithItselfGivesZero() {
            Car c = car(150, "a", 2000);
            assertEquals(0, cmp.compare(c, c));
        }

        @Test
        void isAntisymmetric() {
            Car a = car(100, "a", 2000);
            Car b = car(300, "b", 2000);
            assertEquals(Integer.signum(cmp.compare(a, b)), -Integer.signum(cmp.compare(b, a)));
        }

        @Test
        void sortsListAscending() {
            assertEquals(List.of("low", "mid", "high"),
                    sortedModels(cmp, car(300, "high", 2000), car(100, "low", 2000), car(200, "mid", 2000)));
        }

        @Test
        void doesNotOverflowOnExtremeValues() {
            assertTrue(cmp.compare(car(Integer.MAX_VALUE, "a", 2000), car(-5, "b", 2000)) > 0);
        }
    }

    @Nested
    @DisplayName("ByModelComparator")
    class ByModel {
        private final Comparator<Car> cmp = new ByModelComparator();

        @Test
        void alphabeticalOrder() {
            assertTrue(cmp.compare(car(1, "Audi A4", 2000), car(1, "BMW X5", 2000)) < 0);
            assertTrue(cmp.compare(car(1, "BMW X5", 2000), car(1, "Audi A4", 2000)) > 0);
        }

        @Test
        void equalModelsGiveZero() {
            assertEquals(0, cmp.compare(car(100, "Kia Rio", 2000), car(900, "Kia Rio", 2024)));
        }

        @Test
        void prefixComesBeforeLongerString() {
            assertTrue(cmp.compare(car(1, "Kia", 2000), car(1, "Kia Rio", 2000)) < 0);
        }

        @Test
        @DisplayName("Сравнение чувствительно к регистру (String.compareTo): заглавные раньше строчных")
        void isCaseSensitive() {
            assertTrue(cmp.compare(car(1, "Zebra", 2000), car(1, "audi", 2000)) < 0);
        }

        @Test
        void cyrillicModels() {
            assertTrue(cmp.compare(car(1, "Лада Веста", 2000), car(1, "Москвич", 2000)) < 0);
        }

        @Test
        void sortsListAlphabetically() {
            assertEquals(List.of("Audi A4", "BMW X5", "Kia Rio"),
                    sortedModels(cmp, car(1, "Kia Rio", 2000), car(1, "Audi A4", 2000), car(1, "BMW X5", 2000)));
        }
    }

    @Nested
    @DisplayName("ByYearComparator")
    class ByYear {
        private final Comparator<Car> cmp = new ByYearComparator();

        @Test
        void olderComesFirst() {
            assertTrue(cmp.compare(car(1, "a", 1999), car(1, "b", 2020)) < 0);
        }

        @Test
        void newerComesLast() {
            assertTrue(cmp.compare(car(1, "a", 2020), car(1, "b", 1999)) > 0);
        }

        @Test
        void equalYearsGiveZero() {
            assertEquals(0, cmp.compare(car(1, "a", 2010), car(500, "b", 2010)));
        }

        @Test
        void sortsListAscending() {
            assertEquals(List.of("old", "mid", "new"),
                    sortedModels(cmp, car(1, "new", 2022), car(1, "old", 1990), car(1, "mid", 2005)));
        }

        @Test
        void doesNotOverflowOnExtremeYears() {
            assertTrue(cmp.compare(car(1, "a", Integer.MAX_VALUE), car(1, "b", -5)) > 0);
        }
    }
}
