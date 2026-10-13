package service;

import model.Car;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Многопоточный сервис подсчёта количества вхождений элементов
 * по предикату, значению числового/строкового поля или полному совпадению объекта.
 * Данные реально разбиваются на части (чанки) и обрабатываются параллельными задачами в пуле потоков.
 */
public final class ConcurrentElementCounter {

    public static final int DEFAULT_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors());

    private ConcurrentElementCounter() {
    }

    /**
     * Многопоточно подсчитывает количество элементов коллекции, удовлетворяющих предикату.
     *
     * @param collection  коллекция элементов
     * @param predicate   условие отбора
     * @param threadCount количество потоков в пуле (не менее 1)
     * @param <T>         тип элементов
     * @return количество совпадений
     */
    public static <T> long countByPredicate(Collection<T> collection, Predicate<T> predicate, int threadCount) {
        if (collection == null) {
            throw new IllegalArgumentException("Коллекция не может быть null");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("Предикат не может быть null");
        }
        if (threadCount < 1) {
            throw new IllegalArgumentException("Количество потоков должно быть не меньше 1");
        }

        if (collection.isEmpty()) {
            return 0L;
        }

        List<T> list = new ArrayList<>(collection);
        for (T item : list) {
            if (item == null) {
                throw new IllegalArgumentException("Элементы коллекции не могут быть null");
            }
        }

        int size = list.size();
        int actualThreads = Math.min(threadCount, size);
        ExecutorService executor = Executors.newFixedThreadPool(actualThreads);

        try {
            List<Callable<Long>> tasks = new ArrayList<>(actualThreads);
            int baseChunkSize = size / actualThreads;
            int remainder = size % actualThreads;

            int currentStart = 0;
            for (int i = 0; i < actualThreads; i++) {
                int chunkSize = baseChunkSize + (i < remainder ? 1 : 0);
                int start = currentStart;
                int end = start + chunkSize;
                currentStart = end;

                List<T> subList = list.subList(start, end);
                tasks.add(() -> {
                    long localCount = 0L;
                    for (T element : subList) {
                        if (predicate.test(element)) {
                            localCount++;
                        }
                    }
                    return localCount;
                });
            }

            List<Future<Long>> futures = executor.invokeAll(tasks);
            long totalCount = 0L;
            for (Future<Long> future : futures) {
                totalCount += future.get();
            }
            return totalCount;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Многопоточный подсчёт был прерван", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException("Ошибка при выполнении задачи подсчёта", cause);
        } finally {
            shutdownExecutor(executor);
        }
    }

    /**
     * Многопоточно подсчитывает количество элементов массива, удовлетворяющих предикату.
     */
    public static <T> long countByPredicate(T[] array, Predicate<T> predicate, int threadCount) {
        if (array == null) {
            throw new IllegalArgumentException("Массив не может быть null");
        }
        for (T element : array) {
            if (element == null) {
                throw new IllegalArgumentException("Элементы массива не могут быть null");
            }
        }
        return countByPredicate(Arrays.asList(array), predicate, threadCount);
    }

    /**
     * Многопоточно подсчитывает количество вхождений заданного объекта в коллекции.
     */
    public static <T> long countElement(Collection<T> collection, T target, int threadCount) {
        if (target == null) {
            throw new IllegalArgumentException("Искомый элемент не может быть null");
        }
        return countByPredicate(collection, item -> Objects.equals(item, target), threadCount);
    }

    /**
     * Многопоточно подсчитывает количество вхождений заданного объекта в массиве.
     */
    public static <T> long countElement(T[] array, T target, int threadCount) {
        if (target == null) {
            throw new IllegalArgumentException("Искомый элемент не может быть null");
        }
        return countByPredicate(array, item -> Objects.equals(item, target), threadCount);
    }

    /**
     * Многопоточно подсчитывает количество автомобилей с заданной мощностью N.
     */
    public static long countByPower(Car[] cars, int targetPower, int threadCount) {
        return countByPredicate(cars, car -> car.getPower() == targetPower, threadCount);
    }

    /**
     * Многопоточно подсчитывает количество автомобилей с заданной мощностью N в коллекции.
     */
    public static long countByPower(Collection<Car> cars, int targetPower, int threadCount) {
        return countByPredicate(cars, car -> car.getPower() == targetPower, threadCount);
    }

    /**
     * Многопоточно подсчитывает количество автомобилей с заданным годом выпуска N.
     */
    public static long countByYear(Car[] cars, int targetYear, int threadCount) {
        return countByPredicate(cars, car -> car.getManufactureYear() == targetYear, threadCount);
    }

    /**
     * Многопоточно подсчитывает количество автомобилей с заданным годом выпуска N в коллекции.
     */
    public static long countByYear(Collection<Car> cars, int targetYear, int threadCount) {
        return countByPredicate(cars, car -> car.getManufactureYear() == targetYear, threadCount);
    }

    /**
     * Многопоточно подсчитывает количество автомобилей с заданной моделью.
     */
    public static long countByModel(Car[] cars, String targetModel, int threadCount) {
        if (targetModel == null || targetModel.isBlank()) {
            throw new IllegalArgumentException("Модель для поиска не может быть пустой");
        }
        String normalized = targetModel.trim();
        return countByPredicate(cars, car -> normalized.equalsIgnoreCase(car.getModel()), threadCount);
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
