package nl.tudelft.instantmessenger.client.model;

import nl.tudelft.instantmessenger.client.err.InvalidNameException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Jasper on 10/16/2015.
 */
public class Gebruiker {

    private final Socket socket;
    private final String naam;

    private final DataInputStream input;
    private final DataOutputStream output;

    public Gebruiker(final int poort, final InetAddress ip, final String naam) throws IOException, InvalidNameException {
        this.socket = new Socket(ip, poort);
        this.naam = naam;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(naam);

        final String result = input.readUTF();
        if(result.equals("Deze naam is al in gebruik")) {
            throw new InvalidNameException(result);
        }
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

    public void disconnect() throws IOException {
        this.write(3, false);
        socket.close();
    }

    public String write(final int command, final boolean result, final String... args) throws IOException {
        final StringBuilder writeBuilder = new StringBuilder(command);
        writeBuilder.append(":");
        for (final String string : args) {
            writeBuilder.append(string);
        }

        output.writeUTF(writeBuilder.toString());
        return result ? input.readUTF() : "";
    }
}
