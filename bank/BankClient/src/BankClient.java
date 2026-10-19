import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

// Клиент банковского счета с окном.
// Сначала пользователь вводит IP-адрес сервера и подключается,
// затем может класть деньги на общий счет, снимать их и обновлять баланс.
// Каждая операция - отдельное короткое соединение: команда -> ответ -> закрытие.
public class BankClient extends Frame implements ActionListener {
    static final int PORT = 3001;
    static final int TIMEOUT = 3000; // мс на подключение и ожидание ответа

    TextField ipField = new TextField("127.0.0.1");
    Button connectButton = new Button("Подключиться");
    Label statusLabel = new Label("Введите IP-адрес сервера и нажмите «Подключиться»");
    Label balanceLabel = new Label("—");
    TextField amountField = new TextField("100");
    Button depositButton = new Button("Положить");
    Button withdrawButton = new Button("Снять");
    Button refreshButton = new Button("Обновить");
    TextArea log = new TextArea();

    String serverIp = null; // адрес сервера после успешного подключения

    public BankClient()
    {
        super("Клиент банка");
        setLayout(null);

        Label ipTitle = new Label("IP сервера:");
        ipTitle.setBounds(20, 40, 90, 25);
        ipField.setBounds(110, 40, 180, 25);
        connectButton.setBounds(300, 40, 120, 25);
        statusLabel.setBounds(20, 75, 400, 25);

        Label balanceTitle = new Label("Баланс:");
        balanceTitle.setBounds(20, 110, 80, 25);
        balanceLabel.setBounds(100, 110, 200, 25);
        balanceLabel.setFont(new Font("Dialog", Font.BOLD, 18));

        Label amountTitle = new Label("Сумма:");
        amountTitle.setBounds(20, 150, 80, 25);
        amountField.setBounds(100, 150, 120, 25);

        depositButton.setBounds(20, 185, 130, 30);
        withdrawButton.setBounds(160, 185, 130, 30);
        refreshButton.setBounds(300, 185, 120, 30);

        log.setBounds(20, 230, 400, 180);
        log.setEditable(false);

        add(ipTitle); add(ipField); add(connectButton); add(statusLabel);
        add(balanceTitle); add(balanceLabel);
        add(amountTitle); add(amountField);
        add(depositButton); add(withdrawButton); add(refreshButton);
        add(log);

        connectButton.addActionListener(this);
        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        refreshButton.addActionListener(this);
        ipField.addActionListener(this); // Enter в поле IP = «Подключиться»

        setMoneyEnabled(false); // операции со счетом доступны только после подключения
    }

    void setMoneyEnabled(boolean on)
    {
        amountField.setEnabled(on);
        depositButton.setEnabled(on);
        withdrawButton.setEnabled(on);
        refreshButton.setEnabled(on);
    }

    // Отправляет серверу одну команду и возвращает его ответ
    String send(String ip, String command) throws IOException
    {
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(ip, PORT), TIMEOUT);
            s.setSoTimeout(TIMEOUT);
            PrintStream ps = new PrintStream(s.getOutputStream(), true, "UTF-8");
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            ps.println(command);
            ps.flush();
            String response = in.readLine();
            if (response == null)
                throw new IOException("сервер закрыл соединение без ответа");
            return response;
        } finally {
            s.close();
        }
    }

    // Обновляет баланс по ответу сервера и возвращает код ответа (OK, NOMONEY, ERROR)
    String applyResponse(String response)
    {
        String[] parts = response.split(" ");
        if (parts.length > 1)
            balanceLabel.setText(parts[1] + " руб.");
        return parts[0];
    }

    void connect()
    {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            statusLabel.setText("Введите IP-адрес сервера");
            return;
        }
        statusLabel.setText("Подключение к " + ip + "...");
        try {
            applyResponse(send(ip, "BALANCE")); // проверка связи и получение баланса
            serverIp = ip;
            statusLabel.setText("Подключено к серверу " + ip + ":" + PORT);
            log.append("Подключено к " + ip + "\n");
            setMoneyEnabled(true);
        } catch (Exception e) {
            serverIp = null;
            setMoneyEnabled(false);
            balanceLabel.setText("—");
            statusLabel.setText("Не удалось подключиться к " + ip);
            log.append("Ошибка подключения: " + e + "\n");
        }
    }

    void operation(String command, String title)
    {
        int sum = 0;
        if (!command.equals("BALANCE")) {
            try {
                sum = Integer.parseInt(amountField.getText().trim());
            } catch (NumberFormatException e) {
                sum = 0;
            }
            if (sum <= 0) {
                log.append("Сумма должна быть целым положительным числом\n");
                return;
            }
            command = command + " " + sum;
        }
        try {
            String code = applyResponse(send(serverIp, command));
            if (code.equals("OK"))
                log.append(title + (sum > 0 ? " " + sum + " руб." : "") + " - выполнено\n");
            else if (code.equals("NOMONEY"))
                log.append("Снять " + sum + " руб. нельзя: недостаточно средств\n");
            else
                log.append("Сервер отклонил запрос\n");
        } catch (Exception e) {
            statusLabel.setText("Связь с сервером " + serverIp + " потеряна");
            log.append("Ошибка: " + e + "\n");
        }
    }

    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if (src == connectButton || src == ipField)
            connect();
        else if (src == depositButton)
            operation("DEPOSIT", "Положено");
        else if (src == withdrawButton)
            operation("WITHDRAW", "Снято");
        else if (src == refreshButton)
            operation("BALANCE", "Баланс обновлен");
    }

    public boolean handleEvent(Event evt)
    {
        if (evt.id == Event.WINDOW_DESTROY)
        { System.exit(0); }
        return super.handleEvent(evt);
    }

    public static void main(String args[])
    {
        BankClient f = new BankClient();
        f.setSize(440, 430);
        f.setVisible(true);
    }
}
