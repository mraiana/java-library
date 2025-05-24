import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException; // Для обработки ошибок даты
import java.text.SimpleDateFormat; // Для форматирования даты
import java.util.Calendar;
import java.util.Date; // Для объектов даты
import java.util.HashMap;
import java.util.Map;

public class LibraryGUI extends JFrame implements ActionListener {

    // Карта для хранения названий книг и их количества
    private Map<String, Integer> books = new HashMap<>();
    // Карта для хранения авторов книг
    private Map<String, String> bookAuthors = new HashMap<>();
    // Карта для хранения годов выпуска книг
    private Map<String, Integer> bookYears = new HashMap<>();
    // Карта для хранения возрастных ограничений книг
    private Map<String, Integer> bookAgeRestrictions = new HashMap<>();
    // Карта для хранения читателей по их ID
    private Map<Integer, String> readers = new HashMap<>();
    // Карта для хранения годов рождения читателей по их ID
    private Map<Integer, Integer> readerBirthYears = new HashMap<>();
    // Карта для хранения выданных книг и их дат возврата.
    private Map<String, Date> borrowedBooksDueDates = new HashMap<>();
    // Следующий доступный ID для нового читателя
    private int nextReaderId = 1;

    // Отступы для кнопок: сверху, слева, снизу и справа
    private EmptyBorder buttonPadding = new EmptyBorder(10, 20, 10, 20);

    // Формат даты для ввода/вывода (день.месяц.год)
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public LibraryGUI() {
        setTitle("Школьная библиотека");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        // Создаем кнопку "Добавить книгу"
        JButton addBookButton = new JButton("Добавить книгу");
        addBookButton.setBorder(buttonPadding);
        addBookButton.addActionListener(this);
        add(addBookButton);

        // Создаем кнопку "Посмотреть книги"
        JButton viewBooksButton = new JButton("Посмотреть книги");
        viewBooksButton.setBorder(buttonPadding);
        viewBooksButton.addActionListener(this);
        add(viewBooksButton);

        // Создаем кнопку "Добавить читателя"
        JButton addReaderButton = new JButton("Добавить читателя");
        addReaderButton.setBorder(buttonPadding);
        addReaderButton.addActionListener(this);
        add(addReaderButton);

        // Создаем кнопку "Посмотреть читателей"
        JButton viewReadersButton = new JButton("Посмотреть читателей");
        viewReadersButton.setBorder(buttonPadding);
        viewReadersButton.addActionListener(this);
        add(viewReadersButton);

        // Создаем кнопку "Выдать книгу"
        JButton borrowBookButton = new JButton("Выдать книгу");
        borrowBookButton.setBorder(buttonPadding);
        borrowBookButton.addActionListener(this);
        add(borrowBookButton);

        // Создаем кнопку "Вернуть книгу"
        JButton returnBookButton = new JButton("Вернуть книгу");
        returnBookButton.setBorder(buttonPadding);
        returnBookButton.addActionListener(this);
        add(returnBookButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Получаем источник события (нажатую кнопку)
        JButton source = (JButton) e.getSource();
        // Получаем текст с кнопки
        String buttonText = source.getText();

        // Проверяем, какая кнопка была нажата
        if (buttonText.equals("Добавить книгу")) {
            // Запрашиваем у пользователя название книги
            String title = JOptionPane.showInputDialog(this, "Введите название книги:");
            // Проверяем, что название не пустое
            if (title != null && !title.isEmpty()) {
                String author = JOptionPane.showInputDialog(this, "Введите автора книги:");
                if (author == null) return; // Если пользователь отменил ввод, выходим
                String yearStr = JOptionPane.showInputDialog(this, "Введите год выпуска книги:");
                if (yearStr == null) return;
                String ageRestrictionStr = JOptionPane.showInputDialog(this, "Введите возрастное ограничение (например, 0 для всех возрастов, 12 для 12+):");
                if (ageRestrictionStr == null) return;

                try {
                    // Считываем год выпуска, возрастное ограничение
                    int year = Integer.parseInt(yearStr);
                    int ageRestriction = Integer.parseInt(ageRestrictionStr);

                    String quantityStr = JOptionPane.showInputDialog(this, "Введите количество экземпляров:");
                    if (quantityStr == null) return; // Если пользователь отменил ввод, выходим

                    // Считываем количество экземпляров
                    int quantity = Integer.parseInt(quantityStr);
                    // Если книга уже есть в библиотеке, увеличиваем количество экземпляров
                    if (books.containsKey(title)) {
                        books.put(title, books.get(title) + quantity);
                        JOptionPane.showMessageDialog(this, "Количество экземпляров книги \"" + title + "\" увеличено.");
                    } else {
                        // Иначе добавляем новую книгу
                        books.put(title, quantity);
                        bookAuthors.put(title, author);
                        bookYears.put(title, year);
                        bookAgeRestrictions.put(title, ageRestriction);
                        JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" добавлена в библиотеку.");
                    }
                } catch (NumberFormatException ex) {
                    // Обработка ошибки, если введены некорректные данные
                    JOptionPane.showMessageDialog(this, "Некорректный формат года, возрастного ограничения или количества.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (buttonText.equals("Посмотреть книги")) {
            // Если книг нет, то выводится сообщение
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "В библиотеке пока нет книг.");
            } else {
                // Формируем список книг для вывода
                StringBuilder bookList = new StringBuilder("Каталог книг:\n");
                for (Map.Entry<String, Integer> entry : books.entrySet()) {
                    String title = entry.getKey();
                    Integer quantity = entry.getValue();
                    String author = bookAuthors.getOrDefault(title, "Неизвестен"); // Получаем автора или "Неизвестен"
                    Integer year = bookYears.getOrDefault(title, 0); // Получаем год или 0
                    Integer ageRestriction = bookAgeRestrictions.getOrDefault(title, 0); // Получаем ограничение или 0

                    bookList.append("- ").append(title)
                            .append(" (Автор: ").append(author)
                            .append(", Год: ").append(year)
                            .append(", Ограничение: ").append(ageRestriction).append("+")
                            .append(", доступно: ").append(quantity).append(" экз.)\n");
                }
                // Выводим список книг
                JOptionPane.showMessageDialog(this, bookList.toString());
            }
        } else if (buttonText.equals("Добавить читателя")) {
            // Запрашиваем ФИО читателя
            String name = JOptionPane.showInputDialog(this, "Введите ФИО читателя:");
            if (name != null && !name.isEmpty()) {
                // Запрашиваем год рождения
                String birthYearStr = JOptionPane.showInputDialog(this, "Введите год рождения читателя:");
                if (birthYearStr == null) return;

                try {
                    // Считываем год рождения
                    int birthYear = Integer.parseInt(birthYearStr);
                    // Добавляем читателя, его год рождения в карту
                    readers.put(nextReaderId, name);
                    readerBirthYears.put(nextReaderId, birthYear);
                    JOptionPane.showMessageDialog(this, "Читатель \"" + name + "\" (Год рождения: " + birthYear + ") зарегистрирован под ID: " + nextReaderId);
                    // Увеличиваем ID для следующего читателя
                    nextReaderId++;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный формат года рождения.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (buttonText.equals("Посмотреть читателей")) {
            if (readers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "В библиотеке пока нет зарегистрированных читателей.");
            } else {
                // Формируем список читателей для вывода
                StringBuilder readerList = new StringBuilder("Список читателей:\n");
                // Получаем текущий год для расчета возраста
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                for (Map.Entry<Integer, String> entry : readers.entrySet()) {
                    Integer readerId = entry.getKey();
                    String readerName = entry.getValue();
                    Integer birthYear = readerBirthYears.getOrDefault(readerId, 0); // Получаем год рождения или 0
                    int age = (birthYear != 0) ? currentYear - birthYear : 0;

                    readerList.append("- ID: ").append(readerId)
                            .append(", ФИО: ").append(readerName)
                            .append(", Год рождения: ").append(birthYear);
                    if (age > 0) {
                        readerList.append(" (Возраст: ").append(age).append(" лет)");
                    }
                    readerList.append("\n");
                }
                // Выводим список читателей
                JOptionPane.showMessageDialog(this, readerList.toString());
            }
        } else if (buttonText.equals("Выдать книгу")) {
            // Запрашиваем ID читателя
            String readerIdStr = JOptionPane.showInputDialog(this, "Введите ID читателя:");
            try {
                int readerId = Integer.parseInt(readerIdStr);
                // Проверяем, существует ли читатель с таким ID
                if (!readers.containsKey(readerId)) {
                    JOptionPane.showMessageDialog(this, "Читатель с ID " + readerId + " не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Запрашиваем название книги для выдачи
                String title = JOptionPane.showInputDialog(this, "Введите название книги для выдачи:");
                if (title != null && !title.isEmpty()) {
                    // Проверяем наличие книги и доступных экземпляров
                    if (!books.containsKey(title) || books.get(title) <= 0) {
                        JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" отсутствует или нет доступных экземпляров.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Получаем возраст читателя
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    Integer readerBirthYear = readerBirthYears.getOrDefault(readerId, 0);
                    int readerAge = (readerBirthYear != 0) ? currentYear - readerBirthYear : 0;

                    // Получаем возрастное ограничение книги
                    Integer bookRestriction = bookAgeRestrictions.getOrDefault(title, 0);

                    // Проверяем возрастное ограничение
                    if (readerAge < bookRestriction) {
                        JOptionPane.showMessageDialog(this,
                                "Читатель \"" + readers.get(readerId) + "\" (Возраст: " + readerAge + ") слишком молод для книги \"" + title + "\" (Ограничение: " + bookRestriction + "+).",
                                "Возрастное ограничение", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Запрос даты возврата
                    String dueDateStr = JOptionPane.showInputDialog(this, "Введите дату возврата книги (дд.мм.гггг):");
                    if (dueDateStr == null || dueDateStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Дата возврата не указана. Выдача отменена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        // Считываем введенную дату возврата
                        Date dueDate = dateFormat.parse(dueDateStr);
                        // Сохраняем дату возврата для выданной книги
                        borrowedBooksDueDates.put(title, dueDate);
                        // Уменьшаем количество доступных экземпляров книги
                        books.put(title, books.get(title) - 1);
                        JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" выдана читателю " + readers.get(readerId) + " до " + dateFormat.format(dueDate) + ".");
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(this, "Некорректный формат даты. Используйте дд.мм.гггг.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректный формат ID читателя.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else if (buttonText.equals("Вернуть книгу")) {
            // Запрашиваем название возвращаемой книги
            String title = JOptionPane.showInputDialog(this, "Введите название возвращаемой книги:");
            if (title != null && !title.isEmpty()) {
                // Проверяем, числится ли книга как выданная
                if (!borrowedBooksDueDates.containsKey(title)) {
                    JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" не числится как выданная.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Получаем дату возврата книги
                    Date dueDate = borrowedBooksDueDates.get(title);
                    Date returnDate = new Date(); // Текущая дата как дата возврата

                    // Проверка своевременности возврата
                    String message = "Книга \"" + title + "\" возвращена в библиотеку.";
                    // Если текущая дата позже даты возврата, книга просрочена
                    if (returnDate.after(dueDate)) {
                        long diff = returnDate.getTime() - dueDate.getTime();
                        long diffDays = diff / (1000 * 60 * 60 * 24); // Переводим разницу в днях
                        message += "\nКнига просрочена на " + diffDays + " дней.";
                    } else {
                        message += "\nКнига возвращена в срок!";
                    }

                    // Удаляем книгу из списка выданных
                    borrowedBooksDueDates.remove(title);
                    // Увеличиваем количество доступных экземпляров книги
                    books.put(title, books.get(title) + 1);
                    JOptionPane.showMessageDialog(this, message);
                }
            }
        }
    }
}
