package validation;

import java.time.Clock;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarValidator {
    public static final int MIN_POWER = 1;
    public static final int MAX_POWER = 2000;
    public static final int MAX_MODEL_LENGTH = 50;
    public static final int MIN_YEAR = 1886;

    private final Clock clock;

    public CarValidator() {
        this(Clock.systemDefaultZone());
    }
    public CarValidator(Clock clock) {
        this.clock = clock;
    }

    public Optional<String> checkPower(int power) {
        return (power < MIN_POWER || power > MAX_POWER)
                ? Optional.of("Мощность должна быть от " + MIN_POWER + " до " + MAX_POWER
                + " л.с., получено: " + power)
                : Optional.empty();
    }

    public Optional<String> checkModel(String model) {
        return (model == null || model.isBlank() || model.length() > MAX_MODEL_LENGTH)
                ? Optional.of("Модель: непустая строка длиной до " + MAX_MODEL_LENGTH + " символов")
                : Optional.empty();
    }

    public Optional<String> checkYear(int year) {
        int currentYear = Year.now(clock).getValue();
        return (year < MIN_YEAR || year > currentYear)
                ? Optional.of("Год: от " + MIN_YEAR + " до " + currentYear + ", получено: " + year)
                : Optional.empty();
    }

    // Для ввода из консоли
    public void validatePower(int power) { throwIfPresent(checkPower(power)); }
    public void validateModel(String model) { throwIfPresent(checkModel(model)); }
    public void validateYear(int year) { throwIfPresent(checkYear(year)); }

    // Для билдера
    public void validate(int power, String model, int year) {
        List<String> errors = new ArrayList<>();
        checkPower(power).ifPresent(errors::add);
        checkModel(model).ifPresent(errors::add);
        checkYear(year).ifPresent(errors::add);
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void throwIfPresent(Optional<String> error) {
        error.ifPresent(msg -> { throw new ValidationException(msg); });
    }
}
