package app;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Вспомогательные методы для ручных тестов (без внешних библиотек).
 */
final class TestUtil {

    private TestUtil() {
    }

    static Scanner scanner(String input) {
        return new Scanner(input);
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertEquals(Object expected, Object actual, String name) {
        if (!expected.equals(actual)) {
            throw new AssertionError(name + ": expected " + expected + ", got " + actual);
        }
    }

    /** Выполняет действие, подменяя System.out, и возвращает напечатанный текст. */
    static String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer, true));
        try {
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString();
    }

    static <T> T quiet(Supplier<T> action) {
        Object[] holder = new Object[1];
        captureOutput(() -> holder[0] = action.get());
        @SuppressWarnings("unchecked")
        T result = (T) holder[0];
        return result;
    }
}
