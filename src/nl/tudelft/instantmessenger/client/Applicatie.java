package nl.tudelft.instantmessenger.client;

import nl.tudelft.instantmessenger.client.err.InvalidNameException;
import nl.tudelft.instantmessenger.client.model.Gebruiker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Jasper on 10/16/2015.
 */
public class Applicatie {

    private final Gebruiker gebruiker;
    private final Scanner scanner;

    private int state = 1;

    public Applicatie(final Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
        this.scanner = new Scanner(System.in);
    }

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.err.println("Geef de poort en het ip in de genoemde volgorde mee aan het programma!");
            System.exit(1);
        }

        try {
            final int port = Integer.parseInt(args[0]);
            final InetAddress ip = InetAddress.getByName(args[1]);
            final Scanner scanner = new Scanner(System.in);
            System.out.println("Voer je gebruikersnaam in:");

            final Gebruiker gebruiker = new Gebruiker(port, ip, scanner.nextLine());
            final Applicatie applicatie = new Applicatie(gebruiker);
            applicatie.run();
            scanner.close();

        } catch (final NumberFormatException nfe) {
            System.err.println("Het eerste argument van dit programma moet een poort (integer) zijn!");
        } catch (final UnknownHostException uhe) {
            System.err.println("Deze host is niet bekend!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidNameException e) {
            System.err.println("Deze naam is al in gebruik!");
        }
    }

    public int basisMenu() {
        System.out.println("Maak uw keuze: \n1) Verbind met een gebruiker. \n2) Geef overzicht van gebruikers.\n3) Verbreek communicatie en stop programma.");
        return scanner.nextInt();
    }

    public int versturenMenu() {
        System.out.println("Maak uw keuze: \n1) Por andere gebruiker!\n2) Stuur een bericht.\n3) Stop communicatie en ga terug naar basismenu.");
        return scanner.nextInt() + 3;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public void run() throws IOException {
        setState(basisMenu());
        int state;
        while ((state = getState()) != 3) {
            switch (state) {
                case 1:
                    System.out.println("Wat is de naam van de gebruiker?");
                    final String gebruikerNaam = scanner.next();
                    final String result = gebruiker.write(state, true, gebruikerNaam);
                    if (result.contains("Verbonden")) {
                        System.out.println("Je bent verbonden met: " + gebruikerNaam);
                        setState(versturenMenu());
                    } else {
                        System.out.println("De gebruiker " + gebruikerNaam + " is niet online.");
                        setState(basisMenu());
                    }
                    break;

                case 2:
                    break;

                case 4:
                    break;

                case 5:
                    break;

                case 6:
                    break;

                default:
                    break;
            }
        }
        stop();
    }

    public void stop() {
        try {
            scanner.close();
            gebruiker.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
