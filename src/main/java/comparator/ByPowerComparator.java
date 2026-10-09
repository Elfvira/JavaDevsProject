package main.java.comparator;

import main.java.model.Car;
import java.util.Comparator;

public class ByPowerComparator implements Comparator<Car> {
    @Override
    public int compare(Car o1, Car o2) {
        return o1.getPower() - o2.getPower();
    }
}
