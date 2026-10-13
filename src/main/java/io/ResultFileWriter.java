package io;

import model.Car;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Класс для сохранения результатов сортировки автомобилей в текстовый файл
 * в режиме добавления (append-режим), без перезаписи ранее сохранённых данных.
 */
public class ResultFileWriter {
    public static final String DEFAULT_OUTPUT_FILE = "sorted_cars.txt";

    private final Path filePath;

    public ResultFileWriter() {
        this(Path.of(DEFAULT_OUTPUT_FILE));
    }

    public ResultFileWriter(String filePath) {
        this(filePath == null || filePath.isBlank() ? null : Path.of(filePath.trim()));
    }

    public ResultFileWriter(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("Путь к файлу для записи не может быть null или пустым");
        }
        this.filePath = filePath;
    }

    public Path getFilePath() {
        return filePath;
    }

    /**
     * Записывает массив автомобилей в конец файла с заголовком по умолчанию.
     *
     * @param cars массив автомобилей для сохранения
     */
    public void appendSortedCars(Car[] cars) {
        appendSortedCars(cars, "Результат сортировки");
    }

    /**
     * Записывает массив автомобилей в конец файла (append-режим) с указанным заголовком.
     *
     * @param cars        массив автомобилей для сохранения
     * @param description описание или заголовок блока результатов
     */
    public void appendSortedCars(Car[] cars, String description) {
        if (cars == null) {
            throw new IllegalArgumentException("Массив автомобилей для записи не может быть null");
        }
        for (Car car : cars) {
            if (car == null) {
                throw new IllegalArgumentException("Элементы массива для записи не могут быть null");
            }
        }

        String header = (description == null || description.isBlank())
                ? "Результат сортировки"
                : description.trim();

        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(
                    filePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                writer.write("=== " + header + " (элементов: " + cars.length + ") ===");
                writer.newLine();
                for (int i = 0; i < cars.length; i++) {
                    Car car = cars[i];
                    writer.write(String.format("%d. %d;%s;%d | %s",
                            i + 1,
                            car.getPower(),
                            car.getModel(),
                            car.getManufactureYear(),
                            car));
                    writer.newLine();
                }
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось записать данные в файл: " + filePath, e);
        }
    }
}
