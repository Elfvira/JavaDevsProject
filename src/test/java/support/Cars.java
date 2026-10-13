package support;

import model.Car;

/** Фабрика тестовых автомобилей. */
public final class Cars {

    private Cars() {
    }

    public static Car car(int power, String model, int year) {
        return new Car.Builder().power(power).model(model).manufactureYear(year).build();
    }
}
