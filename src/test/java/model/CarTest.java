package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class CarTest {

    @Test
    @DisplayName("Builder передаёт все три поля в Car")
    void builderSetsAllFields() {
        Car car = new Car.Builder().power(150).model("Toyota Camry").manufactureYear(2020).build();

        assertEquals(150, car.getPower());
        assertEquals("Toyota Camry", car.getModel());
        assertEquals(2020, car.getManufactureYear());
    }

    @Test
    @DisplayName("Порядок вызова сеттеров билдера не важен")
    void builderOrderDoesNotMatter() {
        Car car = new Car.Builder().manufactureYear(1999).power(77).model("Lada").build();

        assertEquals(77, car.getPower());
        assertEquals("Lada", car.getModel());
        assertEquals(1999, car.getManufactureYear());
    }

    @Test
    @DisplayName("Методы билдера возвращают тот же билдер (fluent API)")
    void builderMethodsAreChainable() {
        Car.Builder builder = new Car.Builder();

        assertSame(builder, builder.power(1));
        assertSame(builder, builder.model("x"));
        assertSame(builder, builder.manufactureYear(1900));
    }

    @Test
    @DisplayName("Незаданные поля имеют значения по умолчанию: 0 / null / 0")
    void unsetFieldsHaveDefaults() {
        Car car = new Car.Builder().build();

        assertEquals(0, car.getPower());
        assertNull(car.getModel());
        assertEquals(0, car.getManufactureYear());
    }

    @Test
    @DisplayName("Car иммутабелен: изменение билдера после build() не влияет на готовый объект")
    void carIsNotAffectedByLaterBuilderChanges() {
        Car.Builder builder = new Car.Builder().power(100).model("A").manufactureYear(2000);
        Car first = builder.build();

        builder.power(999).model("B").manufactureYear(2024);
        Car second = builder.build();

        assertEquals(100, first.getPower());
        assertEquals("A", first.getModel());
        assertEquals(2000, first.getManufactureYear());
        assertEquals(999, second.getPower());
        assertNotSame(first, second);
    }

    @Test
    @DisplayName("toString печатает все поля в ожидаемом формате")
    void toStringFormat() {
        Car car = new Car.Builder().power(250).model("BMW X5").manufactureYear(2022).build();

        assertEquals("Car{power=250, model='BMW X5', year=2022}", car.toString());
    }

    @Test
    @DisplayName("toString не падает при model == null")
    void toStringWithNullModel() {
        assertEquals("Car{power=0, model='null', year=0}", new Car.Builder().build().toString());
    }
}
