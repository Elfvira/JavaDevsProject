package io;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.Console;
import support.Console.Captured;

import java.time.Year;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManualFillerTest {

    private static Captured<Car[]> fill(String input, int length) {
        return Console.captureResult(() -> new ManualFiller(new Scanner(input)).fill(length));
    }

    @Test
    @DisplayName("Один автомобиль: корректный ввод принимается с первого раза")
    void readsSingleCar() {
        Car[] cars = fill("150\nToyota Camry\n2020\n", 1).result();

        assertEquals(1, cars.length);
        assertEquals(150, cars[0].getPower());
        assertEquals("Toyota Camry", cars[0].getModel());
        assertEquals(2020, cars[0].getManufactureYear());
    }

    @Test
    void readsSeveralCarsInOrder() {
        Car[] cars = fill("100\nAudi\n2010\n200\nKia\n2015\n300\nBMW\n2020\n", 3).result();

        assertEquals(3, cars.length);
        assertEquals("Audi", cars[0].getModel());
        assertEquals("Kia", cars[1].getModel());
        assertEquals(300, cars[2].getPower());
    }

    @Test
    @DisplayName("Выводит заголовок «Автомобиль N» для каждой машины")
    void printsCarHeaders() {
        String out = fill("1\nA\n2000\n2\nB\n2001\n", 2).output();

        assertTrue(out.contains("Автомобиль 1"), out);
        assertTrue(out.contains("Автомобиль 2"), out);
    }

    @Test
    void lengthZeroReturnsEmptyArrayWithoutReadingInput() {
        Car[] cars = fill("", 0).result();

        assertEquals(0, cars.length);
    }

    @Test
    void negativeLengthIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> fill("", -1));
    }

    @Test
    @DisplayName("Пробелы вокруг чисел игнорируются")
    void numbersAreTrimmed() {
        Car car = fill(" 150 \nBMW\n 2020 \n", 1).result()[0];

        assertEquals(150, car.getPower());
        assertEquals(2020, car.getManufactureYear());
    }

    // ---------- повторные запросы при ошибках ----------

    @ParameterizedTest(name = "мощность \"{0}\" отклоняется, затем принимается 150")
    @ValueSource(strings = {"abc", "", "  ", "0", "-5", "2001", "15.5", "99999999999"})
    void invalidPowerIsRetried(String badPower) {
        Captured<Car[]> result = fill(badPower + "\n150\nBMW\n2020\n", 1);

        assertEquals(150, result.result()[0].getPower());
    }

    @ParameterizedTest(name = "год \"{0}\" отклоняется, затем принимается 2020")
    @ValueSource(strings = {"abc", "", "1885", "0", "-1", "99999999999"})
    void invalidYearIsRetried(String badYear) {
        Captured<Car[]> result = fill("150\nBMW\n" + badYear + "\n2020\n", 1);

        assertEquals(2020, result.result()[0].getManufactureYear());
    }

    @Test
    @DisplayName("Год из будущего отклоняется")
    void futureYearIsRetried() {
        int future = Year.now().getValue() + 1;

        Car car = fill("150\nBMW\n" + future + "\n2020\n", 1).result()[0];

        assertEquals(2020, car.getManufactureYear());
    }

    @ParameterizedTest(name = "модель \"{0}\" отклоняется")
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void blankModelIsRetried(String badModel) {
        Car car = fill("150\n" + badModel + "\nBMW\n2020\n", 1).result()[0];

        assertEquals("BMW", car.getModel());
    }

    @Test
    void tooLongModelIsRetried() {
        Car car = fill("150\n" + "x".repeat(51) + "\nBMW\n2020\n", 1).result()[0];

        assertEquals("BMW", car.getModel());
    }

    @Test
    void modelOfMaxLengthIsAccepted() {
        String model = "x".repeat(50);

        assertEquals(model, fill("150\n" + model + "\n2020\n", 1).result()[0].getModel());
    }

    @Test
    @DisplayName("Ошибки выводятся пользователю, ввод запрашивается заново")
    void errorsArePrintedAndFieldIsAskedAgain() {
        String out = fill("zzz\n0\n150\n\nBMW\n1700\n2020\n", 1).output();

        assertTrue(out.contains("Введите целое число"), out);
        assertTrue(out.contains("Мощность должна быть"), out);
        assertTrue(out.contains("Модель"), out);
        assertTrue(out.contains("Год"), out);
    }

    @Test
    @DisplayName("Ошибка в одном поле не сбрасывает уже введённые поля")
    void previouslyEnteredFieldsAreKept() {
        Car car = fill("150\nBMW\nbad\nbad2\n2020\n", 1).result()[0];

        assertEquals(150, car.getPower());
        assertEquals("BMW", car.getModel());
        assertEquals(2020, car.getManufactureYear());
    }

    @Test
    @DisplayName("Ввод закончился раньше времени → NoSuchElementException, а не бесконечный цикл")
    void exhaustedInputThrows() {
        assertThrows(NoSuchElementException.class, () -> fill("150\nBMW\n", 1));
        assertThrows(NoSuchElementException.class, () -> fill("bad\n", 1));
    }

    @Test
    @DisplayName("Если запрошено больше машин, чем введено, — исключение при исчерпании ввода")
    void notEnoughInputForRequestedLength() {
        assertThrows(NoSuchElementException.class, () -> fill("150\nBMW\n2020\n", 2));
    }
}
