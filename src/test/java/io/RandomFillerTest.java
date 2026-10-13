package io;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import validation.CarValidator;

import java.time.Year;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomFillerTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 10, 1000})
    void returnsRequestedLength(int length) {
        assertEquals(length, new RandomFiller().fill(length).length);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -50})
    void negativeLengthIsRejected(int length) {
        assertThrows(IllegalArgumentException.class, () -> new RandomFiller().fill(length));
    }

    @Test
    @DisplayName("Все сгенерированные автомобили проходят валидацию и не равны null")
    void generatedCarsAreValid() {
        CarValidator validator = new CarValidator();

        Car[] cars = new RandomFiller().fill(5000);

        for (Car car : cars) {
            assertNotNull(car);
            validator.validate(car.getPower(), car.getModel(), car.getManufactureYear());
        }
    }

    @Test
    @DisplayName("Значения лежат в диапазонах: мощность 1..2000, год 1886..текущий")
    void valuesAreWithinRanges() {
        int currentYear = Year.now().getValue();

        for (Car car : new RandomFiller().fill(5000)) {
            assertAll(
                    () -> assertTrue(car.getPower() >= 1 && car.getPower() <= 2000, "power " + car.getPower()),
                    () -> assertTrue(car.getManufactureYear() >= 1886 && car.getManufactureYear() <= currentYear,
                            "year " + car.getManufactureYear()));
        }
    }

    @Test
    @DisplayName("Модели берутся из пула: не пустые и в нескольких вариантах")
    void modelsComeFromPoolWithVariety() {
        Set<String> models = new HashSet<>();
        for (Car car : new RandomFiller(new Random(1)).fill(500)) {
            assertTrue(car.getModel() != null && !car.getModel().isBlank());
            models.add(car.getModel());
        }

        assertTrue(models.size() > 1, "ожидалось несколько разных моделей, получено: " + models);
    }

    @Test
    @DisplayName("С одинаковым seed результат воспроизводим")
    void sameSeedGivesSameResult() {
        Car[] first = new RandomFiller(new Random(42)).fill(50);
        Car[] second = new RandomFiller(new Random(42)).fill(50);

        assertEquals(Arrays.toString(first), Arrays.toString(second));
    }

    @Test
    @DisplayName("С разными seed результаты различаются")
    void differentSeedsGiveDifferentResults() {
        Car[] first = new RandomFiller(new Random(1)).fill(50);
        Car[] second = new RandomFiller(new Random(2)).fill(50);

        assertNotEquals(Arrays.toString(first), Arrays.toString(second));
    }

    @Test
    void elementsAreDistinctObjects() {
        Car[] cars = new RandomFiller().fill(3);

        assertTrue(cars[0] != cars[1] && cars[1] != cars[2] && cars[0] != cars[2]);
    }
}
