import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class LibraryGUI extends JFrame implements ActionListener {

    // строка создает "табличку", где будут храниться названия книг и количество их экземпляров
    private Map<String, Integer> books = new HashMap<>();
    private Map<Integer, String> readers = new HashMap<>();
    private Map<String, Integer> borrowedBooks = new HashMap<>();
    private int nextReaderId = 1;

    // отступы сверху, слева, снизу и справа
    private EmptyBorder buttonPadding = new EmptyBorder(10, 20, 10, 20);

    public LibraryGUI() {
        setTitle("Школьная библиотека");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));

        // создаем и добавляем кнопку "Добавить книгу"
        JButton addBookButton = new JButton("Добавить книгу");
        addBookButton.setBorder(buttonPadding);
        addBookButton.addActionListener(this);
        add(addBookButton);

        // создаем и добавляем кнопку "Посмотреть книги"
        JButton viewBooksButton = new JButton("Посмотреть книги");
        viewBooksButton.setBorder(buttonPadding);
        viewBooksButton.addActionListener(this);
        add(viewBooksButton);

        // создаем и добавляем кнопку "Добавить читателя"
        JButton addReaderButton = new JButton("Добавить читателя");
        addReaderButton.setBorder(buttonPadding);
        addReaderButton.addActionListener(this);
        add(addReaderButton);

        // создаем и добавляем кнопку "Посмотреть читателей"
        JButton viewReadersButton = new JButton("Посмотреть читателей");
        viewReadersButton.setBorder(buttonPadding);
        viewReadersButton.addActionListener(this);
        add(viewReadersButton);

        // создаем и добавляем кнопку "Выдать книгу"
        JButton borrowBookButton = new JButton("Выдать книгу");
        borrowBookButton.setBorder(buttonPadding);
        borrowBookButton.addActionListener(this);
        add(borrowBookButton);

        // создаем и добавляем кнопку "Вернуть книгу"
        JButton returnBookButton = new JButton("Вернуть книгу");
        returnBookButton.setBorder(buttonPadding);
        returnBookButton.addActionListener(this);
        add(returnBookButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource(); // отслеживание события (нажатую кнопку)
        String buttonText = source.getText(); // получаем текст кнопки, чтобы определить, что делать

        if (buttonText.equals("Добавить книгу")) {
            String title = JOptionPane.showInputDialog(this, "Введите название книги:");
            if (title != null && !title.isEmpty()) {
                String quantityStr = JOptionPane.showInputDialog(this, "Введите количество экземпляров:");
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    if (books.containsKey(title)) {
                        books.put(title, books.get(title) + quantity);
                        JOptionPane.showMessageDialog(this, "Количество экземпляров книги \"" + title + "\" увеличено.");
                    } else {
                        books.put(title, quantity);
                        JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" добавлена в библиотеку.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный формат количества.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (buttonText.equals("Посмотреть книги")) {
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "В библиотеке пока нет книг.");
            } else {
                StringBuilder bookList = new StringBuilder("Каталог книг:\n");
                for (Map.Entry<String, Integer> entry : books.entrySet()) {
                    bookList.append("- ").append(entry.getKey()).append(" (доступно: ").append(entry.getValue()).append(" экз.)\n");
                }
                JOptionPane.showMessageDialog(this, bookList.toString());
            }
        } else if (buttonText.equals("Добавить читателя")) {
            String name = JOptionPane.showInputDialog(this, "Введите ФИО читателя:");
            if (name != null && !name.isEmpty()) {
                readers.put(nextReaderId, name);
                JOptionPane.showMessageDialog(this, "Читатель \"" + name + "\" зарегистрирован под ID: " + nextReaderId);
                nextReaderId++;
            }
        } else if (buttonText.equals("Посмотреть читателей")) {
            if (readers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "В библиотеке пока нет зарегистрированных читателей.");
            } else {
                StringBuilder readerList = new StringBuilder("Список читателей:\n");
                for (Map.Entry<Integer, String> entry : readers.entrySet()) {
                    readerList.append("- ID: ").append(entry.getKey()).append(", ФИО: ").append(entry.getValue()).append("\n");
                }
                JOptionPane.showMessageDialog(this, readerList.toString());
            }
        } else if (buttonText.equals("Выдать книгу")) {
            String readerIdStr = JOptionPane.showInputDialog(this, "Введите ID читателя:");
            try {
                int readerId = Integer.parseInt(readerIdStr);
                if (!readers.containsKey(readerId)) {
                    JOptionPane.showMessageDialog(this, "Читатель с ID " + readerId + " не найден.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String title = JOptionPane.showInputDialog(this, "Введите название книги для выдачи:");
                if (title != null && !title.isEmpty()) {
                    if (!books.containsKey(title) || books.get(title) <= 0) {
                        JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" отсутствует или нет доступных экземпляров.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } else {
                        borrowedBooks.put(title, readerId);
                        books.put(title, books.get(title) - 1);
                        JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" выдана читателю " + readers.get(readerId) + ".");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Некорректный формат ID читателя.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } else if (buttonText.equals("Вернуть книгу")) {
            String title = JOptionPane.showInputDialog(this, "Введите название возвращаемой книги:");
            if (title != null && !title.isEmpty()) {
                if (!borrowedBooks.containsKey(title)) {
                    JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" не числится как выданная.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else {
                    borrowedBooks.remove(title);
                    books.put(title, books.get(title) + 1);
                    JOptionPane.showMessageDialog(this, "Книга \"" + title + "\" возвращена в библиотеку.");
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI());
    }
}