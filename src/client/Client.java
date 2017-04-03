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
    private PrintStream client_out;
    private BufferedReader client_in;


    public Client(int port, String adresse, String nomServeur) throws IOException {
        SocketFactory fact = SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) fact.createSocket(adresse, port);

        socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
        client_in = new BufferedReader(inputStream);
        client_out = new PrintStream(socket.getOutputStream());
        this.send("telnet " + nomServeur + " " + port);
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
        System.out.println("\u001B[31m" + msg + "\u001B[0m");
    }
}
