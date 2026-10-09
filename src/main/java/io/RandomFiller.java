package io;

import model.Car;
import validation.CarValidator;

import java.time.Year;
import java.util.Random;

/**
 * Реализация {@link DataFiller}, генерирующая автомобили
 * со случайными валидными значениями параметров.
 */
public class RandomFiller implements DataFiller {
    private static final String[] MODELS = {
            "Toyota Camry", "BMW X5", "Lada Vesta", "Mercedes E200",
            "Honda Civic", "Audi A4", "Kia Rio", "Nissan Qashqai"
    };

    private final Random random;
    private final CarValidator validator = new CarValidator();

    public RandomFiller() {
        this(new Random());
    }

    public RandomFiller(Random random) {
        this.random = random;
    }

    @Override
    public Car[] fill(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Длина массива не может быть отрицательной");
        }

        Car[] cars = new Car[length];
        int currentYear = Year.now().getValue();

        for (int i = 0; i < length; i++) {
            Car car = new Car.Builder()
                    .power(1 + random.nextInt(2000))
                    .model(MODELS[random.nextInt(MODELS.length)])
                    .manufactureYear(1886 + random.nextInt(currentYear - 1885))
                    .build();

            validator.validate(car);
            cars[i] = car;
        }

        return cars;
    }
}
