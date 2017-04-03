package client;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lucas on 03/04/2017.
 */
public class Client {

    private int port;
    private String adresse;
    private String secret;
    private String username;
    private String timestamp;
    private SSLSocket socket;
    private PrintStream client_out;
    private BufferedReader client_in;

    private final String ANSI_RESET = "\u001B[0m";
    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";


    public Client(int port, String adresse, String nomServeur) throws IOException {

        this.port = port;
        this.adresse = adresse;

        SocketFactory fact = SSLSocketFactory.getDefault();
        this.socket = (SSLSocket) fact.createSocket(this.adresse, this.port);

        this.socket.setEnabledCipherSuites(this.socket.getSupportedCipherSuites());

        InputStreamReader inputStream = new InputStreamReader(this.socket.getInputStream());
        client_in = new BufferedReader(inputStream);
        client_out = new PrintStream(socket.getOutputStream());
        this.send("telnet " + nomServeur + " " + this.port);
        String msg = this.read();
        this.print(msg);
    }

    public void connect(String name) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        this.send("ehlo " + name);
        String msg = this.read();
        this.print(msg);
        msg = this.read();
        this.print(msg);
    }


    private void send(String string) {
        try {
            this.client_out.write((string + "\n").getBytes());
            this.client_out.flush();
        } catch (Exception e) {
            print(e.getMessage());
        }
    }

    private String read() {
        try {
            return this.client_in.readLine();
        } catch (Exception e) {
            return "";
        }
    }

    private void print(String msg) {
        System.out.println(ANSI_RED + msg + ANSI_RESET);
    }
}
