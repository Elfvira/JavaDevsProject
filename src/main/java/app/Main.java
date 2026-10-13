package app;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Точка входа приложения. Главный цикл работает, пока пользователь
 * не выберет пункт «Выход» (0).
 */
public final class Main {

    private Main() {
    }

    /**
     * Запускает приложение.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AppContext ctx = new AppContext();
        runLoop(scanner, ctx);
    }

    /**
     * Запускает интерактивный цикл меню с заданными {@link Scanner} и {@link AppContext}.
     *
     * @param scanner источник пользовательского ввода
     * @param ctx     контекст приложения
     */
    public static void runLoop(Scanner scanner, AppContext ctx) {
        try {
            while (true) {
                Menu.print();
                int choice = Menu.readInt(scanner);

                switch (choice) {
                    case 1 -> ctx.setDataFiller(Menu.chooseFiller(scanner));
                    case 2 -> ctx.setArrayLength(Menu.readLength(scanner));
                    case 3 -> ctx.setStrategy(Menu.chooseStrategy(scanner));
                    case 4 -> ctx.setComparator(Menu.chooseComparator(scanner));
                    case 5 -> ctx.executeSortAndPrint();
                    case 6 -> ctx.executeEvenSortAndPrint(Menu.chooseEvenSortField(scanner));
                    case 7 -> ctx.saveSortedResultToFile(Menu.readOutputFilePath(scanner));
                    case 8 -> ctx.demonstrateStreamCollections(Menu.askAddDuplicateForDemo(scanner));
                    case 9 -> Menu.runConcurrentCountDialog(scanner, ctx);
                    case 0 -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Некорректный ввод, повторите.");
                }
            }
        } catch (NoSuchElementException e) {
            // ввод закрыт (Ctrl+D / конец потока) — завершаем без стектрейса
            System.out.println();
            System.out.println("Ввод закрыт. До свидания!");
        }
    }
}
