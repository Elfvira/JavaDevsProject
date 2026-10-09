package io;

import model.Car;

/**
 * Интерфейс для источников заполнения массива автомобилей.
 */
public interface DataFiller {
    /**
     * Создаёт и заполняет массив объектов {@link Car}.
     *
     * @param length требуемое количество элементов в массиве
     * @return массив заполненных объектов Car
     */
    Car[] fill(int length);
}
