package io;

import model.Car;
import validation.CarValidator;

import java.time.Year;
import java.util.Scanner;

/**
 * Реализация {@link DataFiller}, считывающая данные автомобилей
 * из стандартного потока ввода (или переданного {@link Scanner})
 * с валидацией каждого введённого значения и повтором при ошибках.
 */
public class ManualFiller implements DataFiller {
    private final Scanner scanner;
    private final CarValidator validator = new CarValidator();

    public ManualFiller() {
        this(new Scanner(System.in));
    }

    public ManualFiller(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Car[] fill(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Длина массива не может быть отрицательной");
        }

        Car[] cars = new Car[length];

        for (int i = 0; i < length; i++) {
            System.out.println("Автомобиль " + (i + 1));

            int power = readPower();
            String model = readModel();
            int year = readYear();

            validator.validate(power, model, year);

            cars[i] = new Car.Builder()
                    .power(power)
                    .model(model)
                    .manufactureYear(year)
                    .build();
        }

        return cars;
    }

    private int readPower() {
        while (true) {
            System.out.print("Мощность (1–2000 л.с.): ");
            String value = scanner.nextLine();

            try {
                int power = Integer.parseInt(value.trim());
                validator.validatePower(power);
                return power;
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число от 1 до 2000.");
            } catch (RuntimeException e) {
                System.out.println(e.getMessage() != null ? e.getMessage() : "Введите целое число от 1 до 2000.");
            }
        }
    }

    private String readModel() {
        while (true) {
            System.out.print("Модель: ");
            String model = scanner.nextLine();

            try {
                validator.validateModel(model);
                return model;
            } catch (RuntimeException e) {
                System.out.println(e.getMessage() != null ? e.getMessage() : "Модель должна содержать от 1 до 50 символов.");
            }
        }
    }

    private int readYear() {
        int currentYear = Year.now().getValue();

        while (true) {
            System.out.print("Год выпуска (1886–" + currentYear + "): ");
            String value = scanner.nextLine();

            try {
                int year = Integer.parseInt(value.trim());
                validator.validateYear(year);
                return year;
            } catch (NumberFormatException e) {
                System.out.println("Введите год от 1886 до " + currentYear + ".");
            } catch (RuntimeException e) {
                System.out.println(e.getMessage() != null ? e.getMessage() : "Введите год от 1886 до " + currentYear + ".");
            }
        }
    }
}
