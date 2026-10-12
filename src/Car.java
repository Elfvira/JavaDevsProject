//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Car {
    private final int power;          // л.с.
    private final String model;
    private final int manufactureYear;

    private Car(Builder b) {
        this.power = b.power;
        this.model = b.model;
        this.manufactureYear = b.manufactureYear;
    }

    public int getPower()           { return power; }
    public String getModel()        { return model; }
    public int getManufactureYear() { return manufactureYear; }

    @Override
    public String toString() {
        return String.format("Car{power=%d, model='%s', year=%d}",
                power, model, manufactureYear);
    }

    public static class Builder {
        private int power;
        private String model;
        private int manufactureYear;

        public Builder power(int v)           { this.power = v; return this; }
        public Builder model(String v)        { this.model = v; return this; }
        public Builder manufactureYear(int v) { this.manufactureYear = v; return this; }
        public Car build()                    { return new Car(this); }
    }
}
