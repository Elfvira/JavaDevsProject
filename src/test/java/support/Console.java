package support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Утилиты для тестов: подмена System.in / System.out и создание Scanner.
 * Кодировка везде явно UTF-8, чтобы тесты с кириллицей не зависели от локали ОС.
 */
public final class Console {

    private Console() {
    }

    /** Результат вызова вместе с тем, что было напечатано в консоль. */
    public record Captured<T>(T result, String output) {
    }

    public static Scanner scanner(String input) {
        return new Scanner(input);
    }

    /** Выполняет действие и возвращает всё, что оно напечатало в System.out. */
    public static String capture(Runnable action) {
        return capture("", action);
    }

    /** Выполняет действие с заданным System.in и возвращает напечатанное в System.out. */
    public static String capture(String stdin, Runnable action) {
        PrintStream originalOut = System.out;
        InputStream originalIn = System.in;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        System.setIn(new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8)));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /** Выполняет действие, возвращающее значение, и перехватывает вывод. */
    public static <T> Captured<T> captureResult(Supplier<T> action) {
        return captureResult("", action);
    }

    public static <T> Captured<T> captureResult(String stdin, Supplier<T> action) {
        Object[] holder = new Object[1];
        String out = capture(stdin, () -> holder[0] = action.get());
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return new Captured<>(result, out);
    }
}
