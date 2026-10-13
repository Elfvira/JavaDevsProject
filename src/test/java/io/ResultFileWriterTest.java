package io;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static support.Cars.car;

/**
 * Тесты для {@link ResultFileWriter} (Дополнение 2: запись в файл в режиме добавления append).
 */
class ResultFileWriterTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Доп. 2: Несколько последовательных вызовов дописывают результаты в один файл без затирания")
    void appendsMultipleResultsToSameFile() throws IOException {
        Path outputFile = tempDir.resolve("sorted_results.txt");
        ResultFileWriter writer = new ResultFileWriter(outputFile);

        Car[] firstBatch = {
                car(100, "Lada Vesta", 2021),
                car(200, "Toyota Camry", 2020)
        };
        Car[] secondBatch = {
                car(150, "Honda Civic", 2019),
                car(300, "BMW X5", 2022)
        };

        writer.appendSortedCars(firstBatch, "Первая сортировка (по мощности)");
        writer.appendSortedCars(secondBatch, "Вторая сортировка (по году)");

        assertTrue(Files.exists(outputFile));
        String content = Files.readString(outputFile, StandardCharsets.UTF_8);

        // Проверяем, что в файле сохранились оба блока данных
        assertTrue(content.contains("Первая сортировка (по мощности)"), content);
        assertTrue(content.contains("100;Lada Vesta;2021"), content);
        assertTrue(content.contains("200;Toyota Camry;2020"), content);

        assertTrue(content.contains("Вторая сортировка (по году)"), content);
        assertTrue(content.contains("150;Honda Civic;2019"), content);
        assertTrue(content.contains("300;BMW X5;2022"), content);

        // Первый блок идёт раньше второго
        int firstIdx = content.indexOf("Первая сортировка");
        int secondIdx = content.indexOf("Вторая сортировка");
        assertTrue(firstIdx >= 0 && secondIdx > firstIdx);
    }

    @Test
    @DisplayName("Доп. 2: Автоматически создаёт родительские директории при записи")
    void createsParentDirectoriesIfMissing() throws IOException {
        Path nestedFile = tempDir.resolve("subdir/nested/results.txt");
        ResultFileWriter writer = new ResultFileWriter(nestedFile);

        writer.appendSortedCars(new Car[] {car(150, "Audi A4", 2020)});

        assertTrue(Files.exists(nestedFile));
        String content = Files.readString(nestedFile, StandardCharsets.UTF_8);
        assertTrue(content.contains("150;Audi A4;2020"));
    }

    @Test
    @DisplayName("Доп. 2: Ошибки аргументов и ошибки ввода-вывода обрабатываются корректно")
    void validatesArgumentsAndHandlesIoErrors() {
        assertThrows(IllegalArgumentException.class, () -> new ResultFileWriter((Path) null));
        assertThrows(IllegalArgumentException.class, () -> new ResultFileWriter((String) null));
        assertThrows(IllegalArgumentException.class, () -> new ResultFileWriter("   "));

        ResultFileWriter writer = new ResultFileWriter(tempDir.resolve("out.txt"));
        assertThrows(IllegalArgumentException.class, () -> writer.appendSortedCars(null));
        assertThrows(IllegalArgumentException.class,
                () -> writer.appendSortedCars(new Car[] {car(100, "A", 2020), null}));

        // Попытка записи в директорию вместо файла вызывает IllegalStateException
        ResultFileWriter dirWriter = new ResultFileWriter(tempDir);
        assertThrows(IllegalStateException.class,
                () -> dirWriter.appendSortedCars(new Car[] {car(100, "A", 2020)}));
    }
}
