package app;

import comparator.ByPowerComparator;
import comparator.ByYearComparator;
import model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import strategy.BubbleSortStrategy;
import strategy.InsertionSortStrategy;
import strategy.SelectionSortStrategy;
import strategy.SortStrategy;

import java.util.List;

import static app.TestUtil.assertEquals;
import static app.TestUtil.captureOutput;
import static app.TestUtil.check;

/**
 * Тесты для {@link AppContext}.
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
        AppContextTest instance = new AppContextTest();
        instance.testSortsByPower();
        instance.testSourceArrayIsNotMutated();
        instance.testNothingConfigured();
        instance.testEachMissingSetting();
        instance.testInvalidLength();
        instance.testStrategyIsReplaceable();
        instance.testChangingLengthResetsData();
        instance.testEmptyResult();
        instance.testFillerErrorDoesNotCrash();
        System.out.println("AppContextTest: all tests passed");
    }

    @Test
    @DisplayName("Контекст сортирует массив по мощности")
    void testSortsByPower() {
        AppContext ctx = readyContext();
        captureOutput(ctx::executeSortAndPrint);
        Car[] sorted = ctx.getSortedData();
        assertEquals(100, sorted[0].getPower(), "sorted[0]");
        assertEquals(200, sorted[1].getPower(), "sorted[1]");
        assertEquals(300, sorted[2].getPower(), "sorted[2]");
    }

    @Test
    @DisplayName("Исходный массив в контексте не мутируется при сортировке")
    void testSourceArrayIsNotMutated() {
        AppContext ctx = readyContext();
        captureOutput(ctx::executeSortAndPrint);
        assertEquals(300, ctx.getData()[0].getPower(), "original keeps order");
    }

    @Test
    @DisplayName("Ненастроенный контекст просит задать способ заполнения")
    void testNothingConfigured() {
        AppContext ctx = new AppContext();
        String out = captureOutput(ctx::executeSortAndPrint);
        check(out.contains("способ заполнения"), "asks for filler first");
        check(ctx.getSortedData() == null, "nothing sorted");
    }

    @Test
    @DisplayName("Контекст проверяет каждую недостающую настройку по очереди")
    void testEachMissingSetting() {
        AppContext ctx = new AppContext();
        ctx.setDataFiller(length -> sample());
        check(captureOutput(ctx::executeSortAndPrint).contains("длину"), "asks for length");
        ctx.setArrayLength(3);
        check(captureOutput(ctx::executeSortAndPrint).contains("алгоритм"), "asks for strategy");
        ctx.setStrategy(new BubbleSortStrategy<>());
        check(captureOutput(ctx::executeSortAndPrint).contains("поле"), "asks for comparator");
    }

    @Test
    @DisplayName("Нулевая длина массива отклоняется")
    void testInvalidLength() {
        try {
            new AppContext().setArrayLength(0);
            throw new AssertionError("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    @Test
    @DisplayName("Стратегия сортировки в контексте взаимозаменяема")
    void testStrategyIsReplaceable() {
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

    @Test
    @DisplayName("Изменение длины массива сбрасывает ранее заполненные данные")
    void testChangingLengthResetsData() {
        AppContext ctx = readyContext();
        captureOutput(ctx::executeSortAndPrint);
        check(ctx.getData() != null, "data filled");
        ctx.setArrayLength(2);
        check(ctx.getData() == null, "data reset after length change");
    }

    @Test
    @DisplayName("Пустой результат заполнения обрабатывается с сообщением")
    void testEmptyResult() {
        AppContext ctx = readyContext();
        ctx.setDataFiller(length -> new Car[0]);
        String out = captureOutput(ctx::executeSortAndPrint);
        check(out.contains("Не удалось"), "message for empty result");
    }

    @Test
    @DisplayName("Ошибка в источнике данных не приводит к падению приложения")
    void testFillerErrorDoesNotCrash() {
        AppContext ctx = readyContext();
        ctx.setDataFiller(length -> {
            throw new IllegalStateException("boom");
        });
        String out = captureOutput(ctx::executeSortAndPrint);
        check(out.contains("Ошибка: boom"), "error is reported");
    }
}
