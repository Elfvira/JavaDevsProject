package app;

import comparator.ByModelComparator;
import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import io.FileFiller;
import io.ManualFiller;
import io.RandomFiller;
import model.Car;
import strategy.BubbleSortStrategy;
import strategy.InsertionSortStrategy;
import strategy.SelectionSortStrategy;

import java.util.Comparator;

import static app.TestUtil.assertEquals;
import static app.TestUtil.check;
import static app.TestUtil.quiet;
import static app.TestUtil.scanner;

/**
 * Ручные тесты для {@link Menu}.
 */
public class MenuTest {

    public static void main(String[] args) {
        testReadIntValid();
        testReadIntInvalid();
        testReadLengthRetries();
        testChooseFiller();
        testChooseStrategy();
        testChooseComparator();
        testChooseRetriesOnInvalid();
        System.out.println("MenuTest: all tests passed");
    }

    private static void testReadIntValid() {
        assertEquals(5, Menu.readInt(scanner(" 5 \n")), "readInt valid");
    }

    private static void testReadIntInvalid() {
        assertEquals(Menu.INVALID, Menu.readInt(scanner("abc\n")), "readInt text");
        assertEquals(Menu.INVALID, Menu.readInt(scanner("\n")), "readInt empty");
    }

    private static void testReadLengthRetries() {
        int length = quiet(() -> Menu.readLength(scanner("0\nx\n-3\n100001\n7\n")));
        assertEquals(7, length, "readLength");
    }

    private static void testChooseFiller() {
        check(quiet(() -> Menu.chooseFiller(scanner("1\n\n"))) instanceof FileFiller, "file (default path)");
        check(quiet(() -> Menu.chooseFiller(scanner("1\nmy.txt\n"))) instanceof FileFiller, "file (custom path)");
        check(quiet(() -> Menu.chooseFiller(scanner("2\n"))) instanceof RandomFiller, "random");
        check(quiet(() -> Menu.chooseFiller(scanner("3\n"))) instanceof ManualFiller, "manual");
    }

    private static void testChooseStrategy() {
        check(quiet(() -> Menu.chooseStrategy(scanner("1\n"))) instanceof BubbleSortStrategy, "bubble");
        check(quiet(() -> Menu.chooseStrategy(scanner("2\n"))) instanceof InsertionSortStrategy, "insertion");
        check(quiet(() -> Menu.chooseStrategy(scanner("3\n"))) instanceof SelectionSortStrategy, "selection");
    }

    private static void testChooseComparator() {
        check(quiet(() -> Menu.chooseComparator(scanner("1\n"))) instanceof ByPowerComparator, "power");
        check(quiet(() -> Menu.chooseComparator(scanner("2\n"))) instanceof ByModelComparator, "model");
        check(quiet(() -> Menu.chooseComparator(scanner("3\n"))) instanceof ByYearComparator, "year");
    }

    private static void testChooseRetriesOnInvalid() {
        Comparator<Car> comparator = quiet(() -> Menu.chooseComparator(scanner("9\nabc\n\n2\n")));
        check(comparator instanceof ByModelComparator, "retry on invalid input");
    }
}
