package tinyhttpd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class TinyHttpdConnection extends Thread {

    Socket sock;

    TinyHttpdConnection(Socket s) {
        sock = s;
        setPriority(NORM_PRIORITY - 1);
        start();
    }

    @Override
    public void run() {
        System.out.println("=========");
        OutputStream out = null;
        try {
            out = sock.getOutputStream();
            
            BufferedReader d = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String req = d.readLine();
            System.out.println("Request: " + req);
            
            StringTokenizer st = new StringTokenizer(req);
            if ((st.countTokens() >= 2) && st.nextToken().equals("GET")) {
                if ((req = st.nextToken()).startsWith("/")) {
                    req = req.substring(1);
                }
                if (req.equals("")||req.equals("/")) {
                    req = "index.html";
                }
                if (req.startsWith("process/")) {
                    //the request is for a process
                    Writer.writeProcess(req, out);
                } else {
                    //the request is for a file
                    Writer.writeFile(req, out);
                }
            } else {
                new PrintStream(out).println("400 Bad Request");
                System.out.println("400 Bad Request: " + req);
                sock.close();
            }
        } catch (IOException e) {
            System.out.println("Generic I/O error " + e);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                System.out.println("I/O error on close" + ex);
            }
        }
    }
}