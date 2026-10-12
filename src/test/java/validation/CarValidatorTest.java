package validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarValidatorTest {

    /** Фиксированные «часы»: текущий год = 2025, чтобы тесты не зависели от даты запуска. */
    private static final Clock CLOCK_2025 =
            Clock.fixed(Instant.parse("2025-06-15T12:00:00Z"), ZoneOffset.UTC);

    private final CarValidator validator = new CarValidator(CLOCK_2025);

    @Test
    void constantsMatchSpecification() {
        assertEquals(1, CarValidator.MIN_POWER);
        assertEquals(2000, CarValidator.MAX_POWER);
        assertEquals(50, CarValidator.MAX_MODEL_LENGTH);
        assertEquals(1886, CarValidator.MIN_YEAR);
    }

    @Nested
    @DisplayName("Мощность: 1..2000")
    class Power {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 150, 1999, 2000})
        void validValues(int power) {
            assertTrue(validator.checkPower(power).isEmpty());
            assertDoesNotThrow(() -> validator.validatePower(power));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100, 2001, 5000, Integer.MAX_VALUE, Integer.MIN_VALUE})
        void invalidValues(int power) {
            assertTrue(validator.checkPower(power).isPresent());
            assertThrows(ValidationException.class, () -> validator.validatePower(power));
        }

        @Test
        void messageContainsBoundsAndActualValue() {
            String message = validator.checkPower(2001).orElseThrow();

            assertTrue(message.contains("1"), message);
            assertTrue(message.contains("2000"), message);
            assertTrue(message.contains("2001"), message);
        }
    }

    @Nested
    @DisplayName("Модель: непустая, до 50 символов")
    class Model {

        @ParameterizedTest
        @ValueSource(strings = {"a", "BMW X5", "Лада Веста", " a ", "Mercedes-Benz E 200 4MATIC"})
        void validValues(String model) {
            assertTrue(validator.checkModel(model).isEmpty());
            assertDoesNotThrow(() -> validator.validateModel(model));
        }

        @Test
        void exactlyMaxLengthIsValid() {
            assertTrue(validator.checkModel("a".repeat(50)).isEmpty());
        }

        @Test
        void oneCharOverMaxLengthIsInvalid() {
            assertTrue(validator.checkModel("a".repeat(51)).isPresent());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "     ", "\t", "\n", " \t\n "})
        void nullEmptyAndBlankAreInvalid(String model) {
            assertTrue(validator.checkModel(model).isPresent());
            assertThrows(ValidationException.class, () -> validator.validateModel(model));
        }

        @Test
        void messageMentionsMaxLength() {
            assertTrue(validator.checkModel(null).orElseThrow().contains("50"));
        }
    }

    @Nested
    @DisplayName("Год: 1886..текущий (через Clock)")
    class YearRange {

        @ParameterizedTest
        @ValueSource(ints = {1886, 1887, 1950, 2000, 2024, 2025})
        void validValues(int year) {
            assertTrue(validator.checkYear(year).isEmpty());
            assertDoesNotThrow(() -> validator.validateYear(year));
        }

        @ParameterizedTest
        @ValueSource(ints = {1885, 1800, 0, -2020, 2026, 3000, Integer.MAX_VALUE})
        void invalidValues(int year) {
            assertTrue(validator.checkYear(year).isPresent());
            assertThrows(ValidationException.class, () -> validator.validateYear(year));
        }

        @Test
        void messageContainsBoundsAndActualValue() {
            String message = validator.checkYear(2026).orElseThrow();

            assertTrue(message.contains("1886"), message);
            assertTrue(message.contains("2025"), message);
            assertTrue(message.contains("2026"), message);
        }

        @Test
        @DisplayName("Верхняя граница берётся из переданных часов, а не из системного времени")
        void upperBoundFollowsInjectedClock() {
            CarValidator in2030 = new CarValidator(
                    Clock.fixed(Instant.parse("2030-01-01T00:00:00Z"), ZoneOffset.UTC));

            assertTrue(in2030.checkYear(2030).isEmpty());
            assertTrue(in2030.checkYear(2031).isPresent());
            assertTrue(validator.checkYear(2030).isPresent());
        }

        @Test
        @DisplayName("Конструктор по умолчанию использует системное время")
        void defaultConstructorUsesCurrentYear() {
            CarValidator system = new CarValidator();
            int current = Year.now().getValue();

            assertTrue(system.checkYear(current).isEmpty());
            assertTrue(system.checkYear(current + 1).isPresent());
        }
    }

    @Nested
    @DisplayName("validate(power, model, year): общая проверка")
    class Combined {

        @Test
        void validDataPasses() {
            assertDoesNotThrow(() -> validator.validate(150, "Toyota Camry", 2020));
        }

        @Test
        void boundaryValuesPass() {
            assertDoesNotThrow(() -> validator.validate(1, "a", 1886));
            assertDoesNotThrow(() -> validator.validate(2000, "a".repeat(50), 2025));
        }

        @Test
        void oneInvalidFieldGivesOneError() {
            ValidationException e = assertThrows(ValidationException.class,
                    () -> validator.validate(0, "BMW", 2020));

            assertEquals(1, e.getErrors().size());
        }

        @Test
        @DisplayName("Все ошибки собираются разом, а не только первая")
        void allErrorsAreCollected() {
            ValidationException e = assertThrows(ValidationException.class,
                    () -> validator.validate(0, "", 1800));

            assertEquals(3, e.getErrors().size());
            assertEquals(String.join("; ", e.getErrors()), e.getMessage());
        }

        @Test
        void errorsKeepFieldOrderPowerModelYear() {
            ValidationException e = assertThrows(ValidationException.class,
                    () -> validator.validate(0, null, 3000));

            assertTrue(e.getErrors().get(0).startsWith("Мощность"), e.getErrors().get(0));
            assertTrue(e.getErrors().get(1).startsWith("Модель"), e.getErrors().get(1));
            assertTrue(e.getErrors().get(2).startsWith("Год"), e.getErrors().get(2));
        }

        @Test
        void invalidModelAloneIsReported() {
            ValidationException e = assertThrows(ValidationException.class,
                    () -> validator.validate(100, "   ", 2000));

            assertEquals(1, e.getErrors().size());
            assertTrue(e.getErrors().get(0).startsWith("Модель"));
        }
    }
}
