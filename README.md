Домашнее задание 4. Implementor
Реализуйте класс Implementor, который будет генерировать реализации классов и интерфейсов.
Аргумент командной строки: полное имя класса/интерфейса, для которого требуется сгенерировать реализацию.
В результате работы должен быть сгенерирован java-код класса с суффиксом Impl, расширяющий (реализующий) указанный класс (интерфейс).
Сгенерированный класс должен компилироваться без ошибок.
Сгенерированный класс не должен быть абстрактным.
Методы сгенерированного класса должны игнорировать свои аргументы и возвращать значения по умолчанию.
В задании выделяются три варианта:
Простой — Implementor должен уметь реализовывать только интерфейсы (но не классы). Поддержка generics не требуется.
Сложный — Implementor должен уметь реализовывать и классы, и интерфейсы. Поддержка generics не требуется.
Бонусный — Implementor должен уметь реализовывать generic-классы и интерфейсы. Сгенерированный код должен иметь корректные параметры типов и не порождать UncheckedWarning.