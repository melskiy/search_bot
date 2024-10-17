- **Лабораторная работа 1**
Счетчик слов
В произвольном текстовом документе посчитать и напечатать в консоли сколько раз встречается каждое слово. Текст можно сформировать генератором lorem ipsum. Необходимо использовать регулярные выражения (regexp), должны корректно обрабатываться любые знаки препинания в любом количестве.

- **Лабораторная работа 2**
  Поисковый робот для исходного текста программы
  Для Java проекта (локальной папки) построить и напечатать в консоли обратный индекс наследования классов. Для каждого класса необходимо найти (напечатать) классы, для которых он является базовым (родительским). Должны корректно обрабатываться ключевые слова class, interface, extends, implements. Необходимо использовать интерфейс Map, метод getOrDefault(). Желательно использовать Stream API.

- **Лабораторная работа 3**
  Поисковый робот с независимыми потоками
  Усовершенствовать программу из задания 2, чтобы для обработки каждого файла исходного текста создавался отдельный поток (Thread). Взаимодействия потоков не требуется! Для ожидания завершения потоков можно использовать метод join(), желательно CountDownLatch. Работоспособность программы должна быть продемонстрирована на большом проекте с GitHub, например, Spring Framework.

- **Лабораторная работа 4**
  Поисковый робот с синхронизацией доступа потоков к данным
  Обработка файлов исходного текста должна выполняться в отдельных потоках. Результат должен собираться в единый индекс (использовать Map). Доступ к единому индексу должен быть защищен объектом синхронизации. Основной поток должен ожидать завершения всех потоков и только потом печатать в консоли единый индекс. Можно использовать ключевое слово synchronized, лучше использовать ReenterantLock.
- **Лабораторная работа 5**
  Поисковый робот с Executor Service
  В отличии от программы из задания 4 количество потоков должно быть ограниченным. Следует использовать один из вариантов Java Executor Service. Формирование единого индекса должно осуществляться с использованием Future. Все объекты Future необходимо собрать в список и только когда последнее “задание” на обработку будет выдано начинать считывать результаты (метод get()).
- **Лабораторная работа 6**
  Поисковый робот с Blocking Queue реализующий Map-Reduce
  Основной поток должен «выдавать задания» на обработку файлов исходного текста потокам-исполнителям. Количество потоков-исполнителей фиксировано. Задания и единый индекс должны храниться в очереди Blocking Queue. Основной поток должен ожидать выполнения всех выданных заданий и тогда печатать в консоль единый индекс. Следует создать две очереди и фиксированное количество потоков; основной поток отправляет “задания” в первую очередь; потоки-исполнители выполняют обработку и помещают результат во вторую очередь; основной поток берет результаты из второй очереди. Потоки можно не завершать или же завершать с использованием poison pill.