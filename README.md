# JavaDevsProject
Задача 2: Разработчик 2 (Ветка feat-sort)
Цель: Реализовать алгоритмы сортировки согласно паттерну Strategy. Запрещено использовать встроенные методы сортировки (Arrays.sort, Collections.sort).

Что делает участник:

Реализовать 3 класса-стратегии, реализующих интерфейс SortStrategy<Car>:
BubbleSortStrategy (пузырьковая)
InsertionSortStrategy (вставками)
SelectionSortStrategy (выбором)
Логика сортировки должна опираться исключительно на переданный Comparator.
Реализовать 3 компаратора для класса Car в пакете strategy:
Сортировка по мощности (ByPowerComparator)
Сортировка по модели (ByModelComparator) — лексикографически.
Сортировка по году выпуска (ByYearComparator).
Важно: Писать логику сравнения вручную (через if или Integer.compare / String.compareTo), не используя Comparator.comparing.
Ключевые файлы:

strategy/BubbleSortStrategy.java
strategy/InsertionSortStrategy.java
strategy/SelectionSortStrategy.java
strategy/ByPowerComparator.java, ByModelComparator.java, ByYearComparator.java
