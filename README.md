# JavaDevsProject
Модель, архитектура, интеграция, ревью.


# JavaDevsProject
ветка feat-sorting
Паттерн Strategy: три реализации алгоритмов сортировки.

Три реализации SortStrategy<Car> своими руками — без Arrays.sort, Collections.sort, List.sort:
BubbleSortStrategy<T> — сортировка пузырьком
InsertionSortStrategy<T> — сортировка вставками
SelectionSortStrategy<T> — сортировка выбором
Каждая реализация принимает массив и Comparator<T>, возвращает новый отсортированный массив (не мутирует исходный).
Сортировка работает с любым типом T через дженерики — конкретно с Car, но не завязана на него.
Обработка граничных случаев: пустой массив, один элемент, null-элементы — бросать IllegalArgumentException.

Пример скелета BubbleSortStrategy:

public class BubbleSortStrategy<T> implements SortStrategy<T> {
    @Override
    public T[] sort(T[] array, Comparator<T> comparator) {
        if (array == null) {
            throw new IllegalArgumentException("Массив не может быть null");
        }
        T[] result = Arrays.copyOf(array, array.length);
        for (int i = 0; i < result.length - 1; i++) {
            for (int j = 0; j < result.length - i - 1; j++) {
                if (result[j] == null || result[j + 1] == null) {
                    throw new IllegalArgumentException(
                        "Элементы массива не могут быть null");
                }
                if (comparator.compare(result[j], result[j + 1]) > 0) {
                    T tmp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = tmp;
                }
            }
        }
        return result;
    }
}

Ключевые файлы:

strategy/BubbleSortStrategy.java
strategy/InsertionSortStrategy.java
strategy/SelectionSortStrategy.java
=======


=======
Класс Car с тремя полями и паттерном Builder (ручная реализация, без Lombok).
Интерфейс SortStrategy<T> — основа паттерна Strategy.
Настройка Maven-проекта: pom.xml, структура пакетов, .gitignore.
Создание репозитория на GitHub, заведение всех 5 веток от main.
Финальный мердж всех веток в main через Pull Request с код-ревью.
Соблюдение Java Code Conventions во всех классах.

Скелет Car с Builder:

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

Интерфейс SortStrategy:

public interface SortStrategy<T> {
    T[] sort(T[] array, Comparator<T> comparator);
}

Ключевые файлы:

model/Car.java
strategy/SortStrategy.java
pom.xml
.gitignore
README.md


=======
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


# JavaDevsProject
feat-data-input
Источники заполнения массива данных.

Общий интерфейс DataFiller:

public interface DataFiller {
    Car[] fill(int length);
}

Три реализации:
FileFiller — чтение из текстового файла, парсинг строк формата мощность;модель;год, валидация каждой строки через CarValidator. При ошибке — строка пропускается с предупреждением в консоль.
RandomFiller — генерация случайных значений в валидных диапазонах: мощность 1–2000, модель из пула предустановленных названий, год 1886–текущий.
ManualFiller — построчный ввод с клавиатуры через Scanner, валидация каждого поля. При ошибке — повторный запрос поля.
Все три возвращают массив Car[] заданной длины, используя Car.Builder.
Длина массива передаётся как параметр; если данных из файла меньше — недостающие элементы не добавляются, возвращается массив фактического размера.

Пример RandomFiller:

public class RandomFiller implements DataFiller {
    private static final String[] MODELS = {
        "Toyota Camry", "BMW X5", "Lada Vesta", "Mercedes E200",
        "Honda Civic", "Audi A4", "Kia Rio", "Nissan Qashqai"
    };

    @Override
    public Car[] fill(int length) {
        Random rnd = new Random();
        Car[] cars = new Car[length];
        int currentYear = Year.now().getValue();

        for (int i = 0; i < length; i++) {
            cars[i] = new Car.Builder()
                .power(1 + rnd.nextInt(2000))
                .model(MODELS[rnd.nextInt(MODELS.length)])
                .manufactureYear(1886 + rnd.nextInt(currentYear - 1885))
                .build();
        }
        return cars;
    }
}

Ключевые файлы:

io/DataFiller.java
io/FileFiller.java
io/RandomFiller.java
io/ManualFiller.java
=======

