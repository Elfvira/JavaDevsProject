package comparator;

import model.Car;
import java.util.Comparator;

public class ByModelComparator implements Comparator<Car> {
    @Override
    public int compare(Car o1, Car o2) {
        return o1.getModel().compareTo(o2.getModel());
    }
}
