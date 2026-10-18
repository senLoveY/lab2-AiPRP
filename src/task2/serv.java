package task2;

import java.awt.*;
import java.net.*;
import java.io.*;

// Общее задание 2: два клиента работают с одним счетом, каждому клиенту соответствует свой поток.
// По щелчку мыши в окне сервера запускаются оба клиента.
public class serv extends Frame {
    public boolean handleEvent(Event evt)
    {
        if (evt.id == Event.WINDOW_DESTROY)
        { System.exit(0); }
        return super.handleEvent(evt);
    }

    public boolean mouseDown(Event evt, int x, int y)
    {
        new clientThread("Клиент 1").start(); // Каждому клиенту - свой поток
        new clientThread("Клиент 2").start();
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
    static int amount = 200; // Один общий счет для обоих клиентов

    public void run() {
        try
        {
            server = new ServerSocket(3001);
        }
        catch (Exception e)
        { System.out.println("Ошибка соединения+" + e);
        }
        while (true)
        { Socket s = null;
            try {
                s = server.accept();
            }
            catch (Exception e)
            { System.out.println("Ошибка" + e); }
            try
            { PrintStream ps = new PrintStream(s.getOutputStream());
                int amountcur = ((int) (Math.random() * 1000));
                if (Math.random() > 0.5)
                    amount -= amountcur;
                else
                    amount += amountcur;
                Integer x = new Integer(amount);
                amountstring = x.toString();
                ps.println("Account:" + amountstring);
                ps.flush();
                s.close();
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
    String name; // Имя клиента, чтобы различать вывод двух потоков

    public clientThread(String name)
    { this.name = name;
        try {
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
        { sleep(100);
        }
        catch (Exception er)
        { System.out.println("Ошибка  " + er);
        }
        try {
            String msg = dis.readLine();
            if (msg == null)
                break;
            System.out.println(name + " -> " + msg);
        }
        catch (Exception e)
        { System.out.println("ERRORR+" + e);
        }
    }
    }
}
