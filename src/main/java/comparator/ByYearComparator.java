package comparator;

import model.Car;
import java.util.Comparator;

/**
 * Компаратор для сортировки автомобилей по году производства (по возрастанию).
 */
public class ByYearComparator implements Comparator<Car> {
    @Override
    public int compare(Car o1, Car o2) {
        return Integer.compare(o1.getManufactureYear(), o2.getManufactureYear());
    }
}
