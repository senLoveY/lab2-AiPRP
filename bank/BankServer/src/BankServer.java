import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

// Сервер банковского счета с окном.
// Хранит один общий счет, с которого любой клиент может снимать и на который может класть деньги.
// Протокол: клиент отправляет одну строку-команду, сервер отвечает одной строкой и закрывает соединение.
//   BALANCE        -> OK <баланс>
//   DEPOSIT <n>    -> OK <баланс>
//   WITHDRAW <n>   -> OK <баланс> | NOMONEY <баланс>
//   неверная команда -> ERROR <баланс>
public class BankServer extends Frame {
    static final int PORT = 3001;
    static int amount = 200; // Общий счет

    Label balanceLabel = new Label();
    TextArea log = new TextArea();

    public BankServer()
    {
        super("Сервер банка");
        setLayout(null);
        Label title = new Label("Баланс счета:");
        title.setBounds(20, 40, 110, 25);
        balanceLabel.setBounds(130, 40, 200, 25);
        balanceLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        log.setBounds(20, 80, 460, 300);
        log.setEditable(false);
        add(title);
        add(balanceLabel);
        add(log);
        showBalance();
    }

    public boolean handleEvent(Event evt)
    {
        if (evt.id == Event.WINDOW_DESTROY)
        { System.exit(0); }
        return super.handleEvent(evt);
    }

    void showBalance()
    {
        balanceLabel.setText(amount + " руб.");
    }

    void print(String msg)
    {
        System.out.println(msg);
        log.append(msg + "\n");
    }

    // Выводит IPv4-адреса компьютера, чтобы сообщить их клиентам
    void printAddresses()
    {
        print("Адреса этого компьютера (введите один из них в клиенте):");
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!ni.isUp() || ni.isLoopback())
                    continue;
                for (InetAddress a : Collections.list(ni.getInetAddresses()))
                    if (a instanceof Inet4Address)
                        print("    " + a.getHostAddress() + "   (" + ni.getDisplayName() + ")");
            }
        } catch (Exception e) {
            print("Ошибка получения адресов: " + e);
        }
        print("    127.0.0.1   (этот же компьютер)");
    }

    // Выполняет команду клиента и возвращает строку ответа
    String execute(String request)
    {
        if (request == null)
            return "ERROR " + amount;
        String[] parts = request.trim().split(" ");
        try {
            if (parts[0].equals("BALANCE"))
                return "OK " + amount;
            int sum = Integer.parseInt(parts[1]);
            if (sum <= 0)
                return "ERROR " + amount;
            if (parts[0].equals("DEPOSIT")) {
                amount += sum;
                return "OK " + amount;
            }
            if (parts[0].equals("WITHDRAW")) {
                if (sum > amount)
                    return "NOMONEY " + amount; // Нельзя снять больше, чем есть на счете
                amount -= sum;
                return "OK " + amount;
            }
        } catch (Exception e) {
            // неверный формат команды
        }
        return "ERROR " + amount;
    }

    public static void main(String args[])
    {
        BankServer f = new BankServer();
        f.setSize(500, 400);
        f.setVisible(true);
        f.printAddresses();

        try {
            ServerSocket server = new ServerSocket(PORT);
            f.print("Сервер запущен на порту " + PORT + ". Начальный баланс: " + amount + " руб.");

            while (true) { // Клиенты обслуживаются по очереди в главном потоке
                Socket s = null;
                try {
                    s = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
                    PrintStream ps = new PrintStream(s.getOutputStream(), true, "UTF-8");

                    String request = in.readLine();       // команда от клиента
                    String response = f.execute(request);
                    ps.println(response);                 // ответ клиенту
                    ps.flush();

                    f.print(s.getInetAddress().getHostAddress() + ": " + request + " -> " + response);
                    f.showBalance();
                } catch (Exception e) {
                    f.print("Ошибка: " + e);
                } finally {
                    if (s != null)
                        try { s.close(); } catch (IOException e) { }
                }
            }
        } catch (Exception e) {
            f.print("Ошибка запуска сервера: " + e);
        }
    }
}
