package app;

import comparator.ByModelComparator;
import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import io.FileFiller;
import io.ManualFiller;
import io.RandomFiller;
import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import strategy.BubbleSortStrategy;
import strategy.InsertionSortStrategy;
import strategy.SelectionSortStrategy;
import support.Console;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Scanner;

import static app.TestUtil.assertEquals;
import static app.TestUtil.check;
import static app.TestUtil.quiet;
import static app.TestUtil.scanner;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты для {@link Menu} и полного интерактивного цикла {@link Main}.
 */
public class MenuTest {

    @TempDir
    Path tempDir;

    public static void main(String[] args) {
        MenuTest instance = new MenuTest();
        instance.testReadIntValid();
        instance.testReadIntInvalid();
        instance.testReadLengthRetries();
        instance.testChooseFiller();
        instance.testChooseStrategy();
        instance.testChooseComparator();
        instance.testChooseRetriesOnInvalid();
        System.out.println("MenuTest: all tests passed");
    }

    @Test
    @DisplayName("Чтение корректного целого числа")
    void testReadIntValid() {
        assertEquals(5, Menu.readInt(scanner(" 5 \n")), "readInt valid");
    }

    @Test
    @DisplayName("Чтение некорректного целого числа возвращает INVALID")
    void testReadIntInvalid() {
        assertEquals(Menu.INVALID, Menu.readInt(scanner("abc\n")), "readInt text");
        assertEquals(Menu.INVALID, Menu.readInt(scanner("\n")), "readInt empty");
    }

    @Test
    @DisplayName("Повторный запрос длины массива при некорректном вводе")
    void testReadLengthRetries() {
        int length = quiet(() -> Menu.readLength(scanner("0\nx\n-3\n100001\n7\n")));
        assertEquals(7, length, "readLength");
    }

    @Test
    @DisplayName("Выбор источника данных (файл, рандом, вручную)")
    void testChooseFiller() {
        check(quiet(() -> Menu.chooseFiller(scanner("1\n\n"))) instanceof FileFiller, "file (default path)");
        check(quiet(() -> Menu.chooseFiller(scanner("1\nmy.txt\n"))) instanceof FileFiller, "file (custom path)");
        check(quiet(() -> Menu.chooseFiller(scanner("2\n"))) instanceof RandomFiller, "random");
        check(quiet(() -> Menu.chooseFiller(scanner("3\n"))) instanceof ManualFiller, "manual");
    }

    @Test
    @DisplayName("Выбор стратегии сортировки (пузырьком, вставками, выбором)")
    void testChooseStrategy() {
        check(quiet(() -> Menu.chooseStrategy(scanner("1\n"))) instanceof BubbleSortStrategy, "bubble");
        check(quiet(() -> Menu.chooseStrategy(scanner("2\n"))) instanceof InsertionSortStrategy, "insertion");
        check(quiet(() -> Menu.chooseStrategy(scanner("3\n"))) instanceof SelectionSortStrategy, "selection");
    }

    @Test
    @DisplayName("Выбор компаратора (мощность, модель, год)")
    void testChooseComparator() {
        check(quiet(() -> Menu.chooseComparator(scanner("1\n"))) instanceof ByPowerComparator, "power");
        check(quiet(() -> Menu.chooseComparator(scanner("2\n"))) instanceof ByModelComparator, "model");
        check(quiet(() -> Menu.chooseComparator(scanner("3\n"))) instanceof ByYearComparator, "year");
    }

    @Test
    @DisplayName("Повторный запрос при неверном выборе пункта подменю")
    void testChooseRetriesOnInvalid() {
        Comparator<Car> comparator = quiet(() -> Menu.chooseComparator(scanner("9\nabc\n\n2\n")));
        check(comparator instanceof ByModelComparator, "retry on invalid input");
    }

    @Test
    @DisplayName("Полный интерактивный сценарий Main.runLoop со всеми пунктами меню и выходом")
    void fullInteractiveMenuScenario() throws Exception {
        Path outPath = tempDir.resolve("saved_output.txt");
        // Сценарий:
        // 1 -> 3 (ручной ввод)
        // 2 -> 3 (длина 3)
        // 3 -> 1 (пузырьком)
        // 4 -> 1 (по мощности)
        // 5 -> выполнить сортировку (вводим 3 машины: 250/BMW/2022, 151/Audi/2020, 100/Lada/2021)
        // 6 -> 1 (сортировка только чётных по мощности)
        // 7 -> outPath (сохранить в файл)
        // 8 -> 2 (три коллекции через Stream API с дубликатом)
        // 9 -> 1 -> 100 -> 2 (многопоточный подсчёт по мощности 100 в 2 потока)
        // 0 -> Выход
        String script = String.join("\n",
                "1", "3",
                "2", "3",
                "3", "1",
                "4", "1",
                "5",
                "250", "BMW X5", "2022",
                "151", "Audi A4", "2020",
                "100", "Lada Vesta", "2021",
                "6", "1",
                "7", outPath.toString(),
                "8", "2",
                "9", "1", "100", "2",
                "0"
        ) + "\n";

        String consoleOut = Console.capture(() ->
                Main.runLoop(new Scanner(script), new AppContext()));

        assertTrue(consoleOut.contains("Отсортированный массив:"), consoleOut);
        assertTrue(consoleOut.contains("Массив после сортировки только чётных значений"), consoleOut);
        assertTrue(consoleOut.contains("Результат успешно дописан (append) в файл"), consoleOut);
        assertTrue(Files.exists(outPath));
        assertTrue(consoleOut.contains("ArrayList<Car>"), consoleOut);
        assertTrue(consoleOut.contains("LinkedList<Car>"), consoleOut);
        assertTrue(consoleOut.contains("HashSet<Car>"), consoleOut);
        assertTrue(consoleOut.contains("Найдено вхождений: 1"), consoleOut);
        assertTrue(consoleOut.contains("До свидания!"), consoleOut);
    }
}
