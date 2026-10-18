package task1;

import java.awt.*;
import java.net.*;
import java.io.*;

// Общее задание 1: приложение из методички (поток-сервер Account и поток-клиент clientThread).
// Клиент запускается щелчком мыши в окне сервера.
public class serv extends Frame {
    public boolean handleEvent(Event evt) // Используется обработчик событий ранних версий Java
    {
        if (evt.id == Event.WINDOW_DESTROY) // Закрыть приложение
        { System.exit(0); }
        return super.handleEvent(evt);
    }

    public boolean mouseDown(Event evt, int x, int y) // Обработчик события от мыши
    {
        new clientThread().start(); // Запуск потока клиента
        return (true);
    }

    public static void main(String args[])
    {
        serv f = new serv();
        f.resize(400, 400);
        f.show();
        new Account().start();
    }
}

class Account extends Thread {
    ServerSocket server;
    String amountstring;
    static int amount = 200;

    public void run() {
        try
        {
            server = new ServerSocket(3001); // Номер сокета
        }
        catch (Exception e)
        { System.out.println("Ошибка соединения+" + e);
        }
        while (true)
        { Socket s = null;
            try {
                s = server.accept(); // ожидание соединения с клиентом
            }
            catch (Exception e)
            { System.out.println("Ошибка" + e); }
            try
            { PrintStream ps = new PrintStream(s.getOutputStream()); // PrintStream предназначен для текстового вывода
                int amountcur = ((int) (Math.random() * 1000)); // отрицательный вклад - снятие части денег со счета
                if (Math.random() > 0.5)
                    amount -= amountcur;
                else
                    amount += amountcur;
                Integer x = new Integer(amount);
                amountstring = x.toString();
                ps.println("Account:" + amountstring); // передача строки клиенту
                ps.flush();
                s.close(); // Сокетное соединение закрывается
            }
            catch (Exception e)
            { System.out.println("Ошибка  " + e);
            }
        }
    }
}

class clientThread extends Thread {
    DataInputStream dis = null;
    Socket s = null;

    public clientThread()
    { try {
        s = new Socket("127.0.0.1", 3001);
        dis = new DataInputStream(s.getInputStream());
    }
    catch (Exception e)
    { System.out.println("Ошибка:  " + e);
    } }

    public void run()
    { while (true)
    {
        try
        { sleep(100); // клиент выполняет попытку чтения из сокета каждые 100 миллисекунд
        }
        catch (Exception er)
        { System.out.println("Ошибка  " + er);
        }
        try {
            String msg = dis.readLine(); // Клиент пытается прочитать строку из сокета
            if (msg == null)
                break;
            System.out.println(msg); // прочитанная строка выводится на консоль
        }
        catch (Exception e)
        { System.out.println("ERRORR+" + e);
        }
    }
    }
}
