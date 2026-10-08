public class Car {

    private String model;
    private int year;

    private int power; // Optional

    private Car(CarBuilder carBuilder) {
        this.model = carBuilder.model;
        this.year = carBuilder.year;
        this.power = carBuilder.power;
    }

    public static class CarBuilder {
        private String model;
        private int year;
        private int power; // Optional

        public CarBuilder(String model, int year) {
            this.model = model;
            this.year = year;
        }

        public CarBuilder setPower(int power) {
            this.power = power;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getPower() {
        return power;
    }
}