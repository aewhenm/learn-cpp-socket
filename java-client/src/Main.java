import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

  public static void main(String[] args) throws IOException {
    System.out.println("Opening a socket...");
    try (Socket socket = new Socket("127.0.0.1", 20023)) {
      System.out.println("Configuring...");
      try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()))) {
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
      }
    }
  }

}
