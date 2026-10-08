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

