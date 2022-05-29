import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Opening a socket...");
    try (Socket socket = new Socket("127.0.0.1", 20023)) {
      System.out.println("Configuring...");
      try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()))) {

          final Thread userThread = new Thread(() -> {
            // wait to socket initialize
            try {
              Thread.sleep(500);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            Scanner sc = new Scanner(System.in);
            while (true) {
              String msg = sc.nextLine();
              if ("exit".equals(msg)) {
                sendExit(out);
                break;
              }
              sendMsg(out, msg);
            }
          });
          userThread.start();

          readMsg(in);
        }
      }
    }
  }

  private static void readMsg(BufferedReader in) throws IOException {
    char[] buff = new char[4096];

    in.read(buff, 0, buff.length);

    char type = buff[0];
    char length = buff[1];

    if (type == 's') {
      String msg = new String(buff, 2, length);
      System.out.println("Message from server: " + msg);
      return;
    }

    System.out.println("Unknown msg type: " + type);
  }

  private static void sendMsg(PrintWriter out, String msg) {
    char[] buff = new char[4096];

    buff[0] = 's';
    buff[1] = (char) msg.length();

    System.out.println("Sending message: " + msg);
    out.print(buff);
    out.flush();
  }

  private static void sendExit(PrintWriter out) {
    char[] buff = new char[4096];

    buff[0] = '0';

    out.print(buff);
  }


}
