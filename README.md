# JavaDevsProject
feat-comparators-validation
Компараторы по трём полям + валидация данных.

Три компаратора для класса Car (ручная реализация, не через Comparator.comparing):
ByPowerComparator — по мощности (по возрастанию)
ByModelComparator — по модели (алфавитный порядок, String.compareTo)
ByYearComparator — по году производства (по возрастанию)
Каждый компаратор реализует Comparator<Car>.
Валидация — класс CarValidator с методами:
Мощность: положительное целое, диапазон 1–2000 л.с.
Модель: непустая строка, не null, длина 1–50 символов
Год производства: целое в диапазоне 1886–текущий год
При невалидных данных — бросать ValidationException с понятным сообщением.
Валидация применяется ко всем источникам: файл, рандом, ручной ввод.

Пример компаратора:

public class ByPowerComparator implements Comparator<Car> {
    @Override
    public int compare(Car c1, Car c2) {
        return Integer.compare(c1.getPower(), c2.getPower());
    }
}

Пример валидатора:

public class CarValidator {
    public void validatePower(int power) {
        if (power < 1 || power > 2000) {
            throw new ValidationException(
                "Мощность должна быть от 1 до 2000 л.с., получено: " + power);
        }
    }

    public void validateModel(String model) {
        if (model == null || model.isBlank() || model.length() > 50) {
            throw new ValidationException(
                "Модель: непустая строка длиной до 50 символов");
        }
    }

    public void validateYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < 1886 || year > currentYear) {
            throw new ValidationException(
                "Год: от 1886 до " + currentYear + ", получено: " + year);
        }
    }

    public void validate(Car car) {
        validatePower(car.getPower());
        validateModel(car.getModel());
        validateYear(car.getManufactureYear());
    }
}

Ключевые файлы:

comparator/ByPowerComparator.java
comparator/ByModelComparator.java
comparator/ByYearComparator.java
validation/CarValidator.java
validation/ValidationException.java

