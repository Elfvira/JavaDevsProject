# JavaDevsProject
Модель, архитектура, интеграция, ревью.

Класс Car с тремя полями и паттерном Builder (ручная реализация, без Lombok).
Интерфейс SortStrategy<T> — основа паттерна Strategy.
Настройка Maven-проекта: pom.xml, структура пакетов, .gitignore.
Создание репозитория на GitHub, заведение всех 5 веток от main.
Финальный мердж всех веток в main через Pull Request с код-ревью.
Соблюдение Java Code Conventions во всех классах.

Скелет Car с Builder:

public class Car {
    private final int power;          // л.с.
    private final String model;
    private final int manufactureYear;

    private Car(Builder b) {
        this.power = b.power;
        this.model = b.model;
        this.manufactureYear = b.manufactureYear;
    }

    public int getPower()           { return power; }
    public String getModel()        { return model; }
    public int getManufactureYear() { return manufactureYear; }

    @Override
    public String toString() {
        return String.format("Car{power=%d, model='%s', year=%d}",
            power, model, manufactureYear);
    }

    public static class Builder {
        private int power;
        private String model;
        private int manufactureYear;

        public Builder power(int v)           { this.power = v; return this; }
        public Builder model(String v)        { this.model = v; return this; }
        public Builder manufactureYear(int v) { this.manufactureYear = v; return this; }
        public Car build()                    { return new Car(this); }
    }
}

Интерфейс SortStrategy:

public interface SortStrategy<T> {
    T[] sort(T[] array, Comparator<T> comparator);
}

Ключевые файлы:

model/Car.java
strategy/SortStrategy.java
pom.xml
.gitignore
README.md
