package model;

import java.util.Objects;

/**
 * Модель автомобиля с паттерном Builder.
 */
public class Car {
    private final int power;          // л.с.
    private final String model;
    private final int manufactureYear;

    private Car(Builder b) {
        this.power = b.power;
        this.model = b.model;
        this.manufactureYear = b.manufactureYear;
    }

    public int getPower() {
        return power;
    }

    public String getModel() {
        return model;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public int getYear() {
        return manufactureYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car car)) {
            return false;
        }
        return power == car.power
                && manufactureYear == car.manufactureYear
                && Objects.equals(model, car.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(power, model, manufactureYear);
    }

    @Override
    public String toString() {
        return String.format("Car{power=%d, model='%s', year=%d}",
                power, model, manufactureYear);
    }

    public static class Builder {
        private int power;
        private String model;
        private int manufactureYear;

        public Builder power(int v) {
            this.power = v;
            return this;
        }

        public Builder model(String v) {
            this.model = v;
            return this;
        }

        public Builder manufactureYear(int v) {
            this.manufactureYear = v;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }

    /**
     * Билдер для обратной совместимости с реализацией feat-sorting (#7).
     */
    public static class CarBuilder {
        private final Builder builder = new Builder();

        public CarBuilder(String model, int year) {
            builder.model(model).manufactureYear(year);
        }

        public CarBuilder setPower(int power) {
            builder.power(power);
            return this;
        }

        public Car build() {
            return builder.build();
        }
    }
}
