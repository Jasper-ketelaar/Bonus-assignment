package nl.tudelft.instantmessenger.client.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Jasper on 10/16/2015.
 */
public class Gebruiker {

    private final Socket socket;
    private final String naam;

    public Gebruiker(final int poort, final InetAddress ip, final String naam) throws IOException {
        this.socket = new Socket(ip, poort);
        this.naam = naam;
    }

    public boolean isOnline() {
        return socket.isConnected() && !socket.isClosed();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getNaam() {
        return this.naam;
    }
}
