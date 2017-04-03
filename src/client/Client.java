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


    public Client(int port, String adresse) throws IOException {

        this.port = port;
        this.adresse = adresse;

        SocketFactory fact = SSLSocketFactory.getDefault();
        this.socket = (SSLSocket) fact.createSocket(this.adresse, this.port);

        this.socket.setEnabledCipherSuites(this.socket.getSupportedCipherSuites());

        InputStreamReader inputStream = new InputStreamReader(socket.getInputStream());
        client_in = new BufferedReader(inputStream);
        client_out = new PrintStream(socket.getOutputStream());
        try {
            String mess = read();
            String[] data = mess.split(" ");
            this.timestamp = data[4];
        } catch (Exception e) {
            this.print(e.getMessage());
        }
    }

    public boolean connect(String username, String secret) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        this.secret = secret;
        this.username = username;
        String pop = this.timestamp + this.secret;
        MessageDigest md;
        md = MessageDigest.getInstance("md5");
        byte[] digest = md.digest((this.timestamp + secret).getBytes("UTF-8"));
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        digest = hashtext.getBytes();

        byte[] response = ("APOP " + username + " ").getBytes();
        byte[] end_line = ("\n").getBytes();
        byte[] combined = new byte[digest.length + response.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < response.length ? response[i] : digest[i - response.length];
        }
        byte[] apop = new byte[combined.length + end_line.length];
        for (int i = 0; i < apop.length; ++i) {
            apop[i] = i < combined.length ? combined[i] : end_line[i - combined.length];
        }
        this.client_out.write(apop);
        this.client_out.flush();
        String mess = this.read();
        return (("+OK").equals(mess)) ? true : false;
    }

    private String read() {
        try {
            String msg = this.client_in.readLine();
            return msg;
        } catch (Exception e) {
            return "";
        }
    }

    private void print(String msg) {
        System.out.println(ANSI_RED + msg + ANSI_RESET);
    }
}
