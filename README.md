# JavaDevsProject
feat-app-menu
Меню, главный цикл, связывание всех компонентов.

Класс Main с точкой входа и бесконечным циклом (while (true)) — выход только по выбору пользователя (пункт «Выход» → return или System.exit(0)).
Текстовое меню через Scanner:
Выбрать способ заполнения (1 — файл, 2 — рандом, 3 — вручную)
Указать длину массива
Выбрать алгоритм сортировки (1 — пузырь, 2 — вставки, 3 — выбор)
Выбрать поле сортировки (1 — мощность, 2 — модель, 3 — год)
Выполнить сортировку и вывести результат
Выход
AppContext — хранит текущие SortStrategy, Comparator, массив Car[].
Паттерн Strategy: выбор пункта меню подменяет реализацию SortStrategy в контексте.
Вывод исходного и отсортированного массивов в консоль.
Обработка некорректного ввода в меню — возврат к началу цикла с сообщением.
Проверка: перед сортировкой убедиться, что массив уже заполнен.

Скелет Main:

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AppContext ctx = new AppContext();

        while (true) {
            Menu.print();
            int choice = Menu.readInt(scanner);

            switch (choice) {
                case 1 -> ctx.setDataFiller(Menu.chooseFiller(scanner));
                case 2 -> ctx.setArrayLength(Menu.readLength(scanner));
                case 3 -> ctx.setStrategy(Menu.chooseStrategy(scanner));
                case 4 -> ctx.setComparator(Menu.chooseComparator(scanner));
                case 5 -> ctx.executeSortAndPrint();
                case 6 -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Некорректный ввод, повторите.");
            }
        }
    }
}

Ключевые файлы:

app/Main.java
app/Menu.java
app/AppContext.java
