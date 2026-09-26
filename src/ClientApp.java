import java.net.*;
import java.io.*;

public class ClientApp {
    public static void main(String[] args) {
        DataInputStream dis = null;
        Socket s = null;

        try {
            s = new Socket("127.0.0.1", 3001);
            dis = new DataInputStream(s.getInputStream());

            String msg = dis.readLine();

            if(msg != null) {
                System.out.println("Ответ от сервера: " + msg);
            }

            dis.close();
            s.close();

        } catch(Exception e) {
            System.out.println("Ошибка: " + e);
        }
    }
}