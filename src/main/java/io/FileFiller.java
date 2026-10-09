package io;

import model.Car;
import validation.CarValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация {@link DataFiller}, считывающая данные автомобилей
 * из текстового файла формата "power;model;year".
 * Некорректные строки пропускаются с выводом предупреждения в консоль.
 * Возвращает массив только реально созданных объектов (до запрошенной длины).
 */
public class FileFiller implements DataFiller {
    private final Path filePath;
    private final CarValidator validator = new CarValidator();

    public FileFiller() {
        this(Path.of("cars.txt"));
    }

    public FileFiller(String filePath) {
        this(filePath == null ? null : Path.of(filePath));
    }

    public FileFiller(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("Путь к файлу не может быть null");
        }
        this.filePath = filePath;
    }

    @Override
    public Car[] fill(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Длина массива не может быть отрицательной");
        }

        if (length == 0) {
            return new Car[0];
        }

        List<Car> cars = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            int lineNumber = 0;

            while (cars.size() < length && (line = reader.readLine()) != null) {
                lineNumber++;

                try {
                    String[] values = line.split(";", -1);

                    if (values.length != 3) {
                        throw new IllegalArgumentException("Ожидается три поля (power;model;year)");
                    }

                    int power = Integer.parseInt(values[0].trim());
                    String model = values[1].trim();
                    int year = Integer.parseInt(values[2].trim());

                    validator.validatePower(power);
                    validator.validateModel(model);
                    validator.validateYear(year);

                    Car car = new Car.Builder()
                            .power(power)
                            .model(model)
                            .manufactureYear(year)
                            .build();

                    validator.validate(car);
                    cars.add(car);
                } catch (RuntimeException e) {
                    System.out.println("Строка " + lineNumber + " пропущена: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать файл: " + filePath, e);
        }

        return cars.toArray(new Car[0]);
    }
}
