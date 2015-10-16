package nl.tudelft.instantmessenger.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Jasper on 10/16/2015.
 */
public class Applicatie {

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.err.println("Geef de poort en het ip in de genoemde volgorde mee aan het programma!");
            System.exit(1);
        }

        try {
            final int port = Integer.parseInt(args[0]);
            final InetAddress ip = InetAddress.getByName(args[1]);

        } catch (final NumberFormatException nfe) {
            System.err.println("Het eerste argument van dit programma moet een poort (integer) zijn!");
        } catch (final UnknownHostException uhe) {
            System.err.println("Deze host is niet bekend!");
        }
    }
}
