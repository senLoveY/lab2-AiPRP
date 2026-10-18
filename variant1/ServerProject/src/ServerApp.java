import java.net.*;
import java.io.*;

public class ServerApp {
    static int amount = 200;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(3001);
            System.out.println("Сервер запущен. Ожидание клиента на порту 3001...");

            while (true) {
                Socket s = null;
                try {
                    s = server.accept();
                    System.out.println("Клиент подключился!");

                    PrintStream ps = new PrintStream(s.getOutputStream());

                    int amountcur = ((int)(Math.random() * 1000));
                    if (Math.random() > 0.5)
                        amount -= amountcur;
                    else
                        amount += amountcur;

                    Integer x = new Integer(amount);
                    String amountstring = x.toString();

                    ps.println("Account:" + amountstring);
                    ps.flush();
                    s.close();

                } catch(Exception e) {
                    System.out.println("Ошибка: " + e);
                }
            }
        } catch(Exception e) {
            System.out.println("Ошибка соединения: " + e);
        }
    }
}