package task3;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

// Общее задание 3: клиент-сервер с визуальным интерфейсом.
// Клиент запускается по нажатию кнопки, состояние счета отображается в текстовом поле.
public class serv extends Frame implements ActionListener {
    Button startButton = new Button("Запустить клиента");
    TextField accountField = new TextField();

    public serv()
    {
        super("Банковский счет");
        setLayout(null);
        startButton.setBounds(120, 80, 160, 30);
        accountField.setBounds(100, 140, 200, 25);
        accountField.setEditable(false);
        add(startButton);
        add(accountField);
        startButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == startButton)
            new clientThread(accountField).start(); // Запуск потока клиента по нажатию кнопки
    }

    public boolean handleEvent(Event evt)
    {
        if (evt.id == Event.WINDOW_DESTROY)
        { System.exit(0); }
        return super.handleEvent(evt);
    }

    public static void main(String args[])
    {
        serv f = new serv();
        f.resize(400, 250);
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
    TextField field; // Текстовое поле, в котором отображается состояние счета

    public clientThread(TextField field)
    { this.field = field;
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
            System.out.println(msg);
            field.setText(msg); // Вывод состояния счета в текстовое поле
        }
        catch (Exception e)
        { System.out.println("ERRORR+" + e);
        }
    }
    }
}
