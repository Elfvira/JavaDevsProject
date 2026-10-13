package io;

import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.Console;
import support.Console.Captured;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class FileFillerTest {

    @TempDir
    Path tmp;

    private Path file(String... lines) throws IOException {
        return Files.write(tmp.resolve("cars.txt"), List.of(lines));
    }

    private static Captured<Car[]> fill(Path path, int length) {
        return Console.captureResult(() -> new FileFiller(path).fill(length));
    }

    // ---------- чтение корректных данных ----------

    @Test
    @DisplayName("Читает все строки формата power;model;year")
    void readsAllValidLines() throws IOException {
        Path path = file("150;Toyota Camry;2020", "250;BMW X5;2022", "105;Lada Vesta;2021");

        Car[] cars = fill(path, 10).result();

        assertEquals(3, cars.length);
        assertEquals(150, cars[0].getPower());
        assertEquals("Toyota Camry", cars[0].getModel());
        assertEquals(2020, cars[0].getManufactureYear());
        assertEquals("BMW X5", cars[1].getModel());
        assertEquals(105, cars[2].getPower());
    }

    @Test
    @DisplayName("Порядок машин совпадает с порядком строк файла")
    void keepsFileOrder() throws IOException {
        Path path = file("1;C;2000", "2;A;2001", "3;B;2002");

        Car[] cars = fill(path, 3).result();

        assertEquals("C", cars[0].getModel());
        assertEquals("A", cars[1].getModel());
        assertEquals("B", cars[2].getModel());
    }

    @Test
    @DisplayName("Пробелы вокруг полей обрезаются")
    void trimsWhitespace() throws IOException {
        Path path = file("  150 ;  Toyota Camry  ; 2020  ");

        Car car = fill(path, 1).result()[0];

        assertEquals(150, car.getPower());
        assertEquals("Toyota Camry", car.getModel());
        assertEquals(2020, car.getManufactureYear());
    }

    @Test
    @DisplayName("Кириллица в модели читается корректно (UTF-8)")
    void readsCyrillicModel() throws IOException {
        Path path = file("106;Лада Веста;2021");

        assertEquals("Лада Веста", fill(path, 1).result()[0].getModel());
    }

    @Test
    @DisplayName("Граничные допустимые значения принимаются")
    void acceptsBoundaryValues() throws IOException {
        int currentYear = Year.now().getValue();
        Path path = file("1;a;1886", "2000;" + "m".repeat(50) + ";" + currentYear);

        assertEquals(2, fill(path, 5).result().length);
    }

    // ---------- длина ----------

    @Test
    @DisplayName("Если запрошено меньше, чем в файле, возвращаются первые N")
    void returnsOnlyRequestedLength() throws IOException {
        Path path = file("1;A;2000", "2;B;2000", "3;C;2000", "4;D;2000");

        Car[] cars = fill(path, 2).result();

        assertEquals(2, cars.length);
        assertEquals("A", cars[0].getModel());
        assertEquals("B", cars[1].getModel());
    }

    @Test
    @DisplayName("Если в файле меньше, чем запрошено, массив фактического размера, без null")
    void returnsActualSizeWhenFileIsShort() throws IOException {
        Path path = file("1;A;2000", "2;B;2000");

        Car[] cars = fill(path, 100).result();

        assertEquals(2, cars.length);
        for (Car car : cars) {
            assertTrue(car != null);
        }
    }

    @Test
    void lengthZeroGivesEmptyArray() throws IOException {
        assertEquals(0, fill(file("1;A;2000"), 0).result().length);
    }

    @Test
    @DisplayName("При length == 0 файл не открывается (несуществующий путь допустим)")
    void lengthZeroDoesNotTouchFile() {
        assertEquals(0, fill(tmp.resolve("missing.txt"), 0).result().length);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
    void negativeLengthIsRejected(int length) {
        assertThrows(IllegalArgumentException.class, () -> new FileFiller(tmp.resolve("x.txt")).fill(length));
    }

    @Test
    void emptyFileGivesEmptyArray() throws IOException {
        Path path = Files.writeString(tmp.resolve("empty.txt"), "");

        assertEquals(0, fill(path, 5).result().length);
    }

    // ---------- некорректные строки ----------

    @ParameterizedTest(name = "строка \"{0}\" пропускается")
    @ValueSource(strings = {
            "abc;BMW;2020",          // мощность не число
            "150.5;BMW;2020",        // мощность дробная
            "150;BMW;twenty",        // год не число
            "150;BMW",               // мало полей
            "150",                   // одно поле
            "150;BMW;2020;extra",    // много полей
            ";BMW;2020",             // пустая мощность
            "150;;2020",             // пустая модель
            "150;   ;2020",          // модель из пробелов
            "0;BMW;2020",            // мощность ниже границы
            "2001;BMW;2020",         // мощность выше границы
            "-5;BMW;2020",           // отрицательная мощность
            "150;BMW;1885",          // год ниже границы
            "99999999999;BMW;2020",  // переполнение int
            "",                      // пустая строка
            "   "                    // строка из пробелов
    })
    void invalidLineIsSkippedWithWarning(String badLine) throws IOException {
        Path path = file(badLine, "100;Good;2000");

        Captured<Car[]> result = fill(path, 5);

        assertEquals(1, result.result().length);
        assertEquals("Good", result.result()[0].getModel());
        assertTrue(result.output().contains("Строка 1 пропущена"), result.output());
    }

    @Test
    @DisplayName("Год из будущего отклоняется")
    void futureYearIsSkipped() throws IOException {
        Path path = file("150;BMW;" + (Year.now().getValue() + 1), "100;Good;2000");

        assertEquals(1, fill(path, 5).result().length);
    }

    @Test
    @DisplayName("Модель длиннее 50 символов отклоняется")
    void tooLongModelIsSkipped() throws IOException {
        Path path = file("150;" + "m".repeat(51) + ";2000", "100;Good;2000");

        assertEquals(1, fill(path, 5).result().length);
    }

    @Test
    @DisplayName("В предупреждении указан правильный номер строки (считая пропущенные)")
    void warningHasCorrectLineNumber() throws IOException {
        Path path = file("1;A;2000", "2;B;2000", "bad", "4;D;2000", "oops");

        Captured<Car[]> result = fill(path, 10);

        assertEquals(3, result.result().length);
        assertTrue(result.output().contains("Строка 3 пропущена"), result.output());
        assertTrue(result.output().contains("Строка 5 пропущена"), result.output());
    }

    @Test
    @DisplayName("Пропущенные строки не занимают место в запрошенной длине")
    void skippedLinesDoNotCountTowardsLength() throws IOException {
        Path path = file("bad", "1;A;2000", "also;bad;line", "2;B;2000", "3;C;2000");

        Car[] cars = fill(path, 2).result();

        assertEquals(2, cars.length);
        assertEquals("A", cars[0].getModel());
        assertEquals("B", cars[1].getModel());
    }

    @Test
    @DisplayName("Если все строки некорректны — пустой массив, без исключения")
    void allLinesInvalidGivesEmptyArray() throws IOException {
        Path path = file("x", "y;z", "0;;0");

        assertEquals(0, fill(path, 5).result().length);
    }

    @Test
    @DisplayName("Строка после достижения нужной длины не читается и не логируется")
    void linesAfterRequestedLengthAreNotProcessed() throws IOException {
        Path path = file("1;A;2000", "bad line that must not be reported");

        Captured<Car[]> result = fill(path, 1);

        assertEquals(1, result.result().length);
        assertEquals("", result.output());
    }

    // ---------- ошибки ввода-вывода и конструкторы ----------

    @Test
    @DisplayName("Несуществующий файл → IllegalStateException с причиной IOException")
    void missingFileThrowsIllegalState() {
        Path missing = tmp.resolve("nope.txt");

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> fill(missing, 3));

        assertInstanceOf(IOException.class, e.getCause());
        assertTrue(e.getMessage().contains("nope.txt"), e.getMessage());
    }

    @Test
    @DisplayName("Путь к директории вместо файла → IllegalStateException")
    void directoryInsteadOfFileThrowsIllegalState() {
        assertThrows(IllegalStateException.class, () -> fill(tmp, 1));
    }

    @Test
    void nullStringPathIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FileFiller((String) null));
    }

    @Test
    void nullPathIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new FileFiller((Path) null));
    }

    @Test
    @DisplayName("Конструктор со String даёт тот же результат, что и с Path")
    void stringAndPathConstructorsAreEquivalent() throws IOException {
        Path path = file("1;A;2000", "2;B;2000");

        Car[] viaString = Console.captureResult(() -> new FileFiller(path.toString()).fill(5)).result();
        Car[] viaPath = fill(path, 5).result();

        assertEquals(viaPath.length, viaString.length);
        assertEquals(viaPath[1].getModel(), viaString[1].getModel());
    }

    @Test
    void defaultConstructorDoesNotThrow() {
        assertDoesNotThrow(() -> new FileFiller());
    }

    @Test
    @DisplayName("cars.txt из корня проекта читается без пропусков (пропускается, если файла нет)")
    void bundledCarsFileIsValid() {
        Path bundled = Path.of("cars.txt");
        assumeTrue(Files.exists(bundled), "cars.txt не найден в рабочей директории");

        Captured<Car[]> result = Console.captureResult(() -> new FileFiller().fill(100));

        assertTrue(result.result().length > 0);
        assertEquals("", result.output(), "в cars.txt не должно быть некорректных строк");
    }
}
