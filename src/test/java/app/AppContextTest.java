package app;

import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import model.Car;
import strategy.BubbleSortStrategy;
import strategy.InsertionSortStrategy;
import strategy.SelectionSortStrategy;
import strategy.SortStrategy;

import java.util.List;

import static app.TestUtil.assertEquals;
import static app.TestUtil.captureOutput;
import static app.TestUtil.check;

/**
 * Ручные тесты для {@link AppContext}.
 */
public class AppContextTest {

    private static Car car(int power, String model, int year) {
        return new Car.Builder().power(power).model(model).manufactureYear(year).build();
    }

    private static Car[] sample() {
        return new Car[] {car(300, "BMW", 2020), car(100, "Audi", 2010), car(200, "Kia", 2015)};
    }

    private static AppContext readyContext() {
        AppContext ctx = new AppContext();
        ctx.setDataFiller(length -> sample());
        ctx.setArrayLength(3);
        ctx.setStrategy(new BubbleSortStrategy<>());
        ctx.setComparator(new ByPowerComparator());
        return ctx;
    }

    public static void main(String[] args) {
        testSortsByPower();
        testSourceArrayIsNotMutated();
        testNothingConfigured();
        testEachMissingSetting();
        testInvalidLength();
        testStrategyIsReplaceable();
        testChangingLengthResetsData();
        testEmptyResult();
        testFillerErrorDoesNotCrash();
        System.out.println("AppContextTest: all tests passed");
    }

    private static void testSortsByPower() {
        AppContext ctx = readyContext();
        captureOutput(ctx::executeSortAndPrint);
        Car[] sorted = ctx.getSortedData();
        assertEquals(100, sorted[0].getPower(), "sorted[0]");
        assertEquals(200, sorted[1].getPower(), "sorted[1]");
        assertEquals(300, sorted[2].getPower(), "sorted[2]");
    }

    private static void testSourceArrayIsNotMutated() {
        AppContext ctx = readyContext();
        captureOutput(ctx::executeSortAndPrint);
        assertEquals(300, ctx.getData()[0].getPower(), "original keeps order");
    }

    private static void testNothingConfigured() {
        AppContext ctx = new AppContext();
        String out = captureOutput(ctx::executeSortAndPrint);
        check(out.contains("способ заполнения"), "asks for filler first");
        check(ctx.getSortedData() == null, "nothing sorted");
    }

    private static void testEachMissingSetting() {
        AppContext ctx = new AppContext();
        ctx.setDataFiller(length -> sample());
        check(captureOutput(ctx::executeSortAndPrint).contains("длину"), "asks for length");
        ctx.setArrayLength(3);
        check(captureOutput(ctx::executeSortAndPrint).contains("алгоритм"), "asks for strategy");
        ctx.setStrategy(new BubbleSortStrategy<>());
        check(captureOutput(ctx::executeSortAndPrint).contains("поле"), "asks for comparator");
    }

    private static void testInvalidLength() {
        try {
            new AppContext().setArrayLength(0);
            throw new AssertionError("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    private static void testStrategyIsReplaceable() {
        List<SortStrategy<Car>> strategies = List.of(
                new BubbleSortStrategy<>(), new InsertionSortStrategy<>(), new SelectionSortStrategy<>());
        for (SortStrategy<Car> strategy : strategies) {
            AppContext ctx = readyContext();
            ctx.setStrategy(strategy);
            ctx.setComparator(new ByYearComparator());
            captureOutput(ctx::executeSortAndPrint);
            assertEquals(2010, ctx.getSortedData()[0].getManufactureYear(), "year min first");
            assertEquals(2020, ctx.getSortedData()[2].getManufactureYear(), "year max last");
        }
    }

    private static void testChangingLengthResetsData() {
        AppContext ctx = readyContext();
        captureOutput(ctx::executeSortAndPrint);
        check(ctx.getData() != null, "data filled");
        ctx.setArrayLength(2);
        check(ctx.getData() == null, "data reset after length change");
    }

    private static void testEmptyResult() {
        AppContext ctx = readyContext();
        ctx.setDataFiller(length -> new Car[0]);
        String out = captureOutput(ctx::executeSortAndPrint);
        check(out.contains("Не удалось"), "message for empty result");
    }

    private static void testFillerErrorDoesNotCrash() {
        AppContext ctx = readyContext();
        ctx.setDataFiller(length -> {
            throw new IllegalStateException("boom");
        });
        String out = captureOutput(ctx::executeSortAndPrint);
        check(out.contains("Ошибка: boom"), "error is reported");
    }
}
