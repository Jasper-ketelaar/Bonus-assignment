import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class IMClient {

    private Scanner sc;
    private final Socket socket;
    private final DataOutputStream output;
    private final DataInputStream input;
    private String message;
    private boolean verbonden;



    public IMClient() throws IOException {
        System.out.println("Wat is je gebruikersnaam:");

        this.sc = new Scanner(System.in);
        final String gebruiker = sc.nextLine();
        this.socket = new Socket("127.0.0.1", 4321);
        this.output = new DataOutputStream(socket.getOutputStream());
        this.input = new DataInputStream(socket.getInputStream());
        output.writeUTF("Gebruiker:" + gebruiker);

        final Thread read = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    try {
                        message = input.readUTF();
                        if(message.contains("Communicatie:")) {
                            try {
                                Thread.sleep(1500);
                                /*final Robot robot = new Robot();
                                robot.keyPress(KeyEvent.VK_0);
                                robot.keyRelease(KeyEvent.VK_0);

                                robot.keyPress(KeyEvent.VK_ENTER);
                                robot.keyRelease(KeyEvent.VK_ENTER);*/

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } /*catch (AWTException e) {
                                e.printStackTrace();
                            }*/
                            //sc = sc.reset();
                            System.out.println(message.replace("Communicatie:", "") + " is met je verbonden!\n\n");
                            System.out.println("Maak uw keuze: \n1) Por andere gebruiker!\n2) Stuur een bericht.\n3) Stop communicatie en ga terug naar basismenu.");
                            verbonden = true;

                        }

                        if(message.contains("Verbroken")) {
                            act(basisMenu());
                        }

                        if(message.contains("por")) {
                            System.out.println(message);
                        }

                        if(message.contains("Bericht:")) {
                            System.out.println(message.replace("Bericht:", ""));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        read.start();
    }

    public static void main(final String[] args) {
        try {
            final IMClient client = new IMClient();
            client.act(client.basisMenu());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int basisMenu() {
        System.out.println("Maak uw keuze: \n1) Verbind met een gebruiker. \n2) Geef overzicht van gebruikers.\n3) Verbreek communicatie en stop programma.");
        return sc.nextInt();
    }

    public int gebruikerMenu() {
        System.out.println("Maak uw keuze: \n1) Por andere gebruiker!\n2) Stuur een bericht.\n3) Stop communicatie en ga terug naar basismenu.");
        return sc.nextInt() + 3;
    }

    public void act(int actionCode) throws IOException {
        if(verbonden) {
            actionCode = actionCode + 1;
            verbonden = false;
        }
        switch (actionCode) {

            case 1:
                System.out.println("Geef de gebruikersnaam in:");
                final String gebruiker = sc.next();
                output.writeUTF("Verbind:" + gebruiker);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String answer = message;
                if (answer.equals("Verbonden")) {
                    act(this.gebruikerMenu());
                } else {
                    System.out.println("Gebruiker is niet online");
                    act(this.basisMenu());
                }
                break;

            case 2:
                output.writeUTF("Overzicht");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(message);
                act(this.basisMenu());
                break;

            case 3:
                output.writeUTF("Verbreek");
                this.disconnect();
                System.out.println("Het programma is gestopt");

                break;

            case 4:
                output.writeUTF("Por");
                act(this.gebruikerMenu());
                break;

            case 5:
                System.out.println("Typ je bericht:");
                final String bericht = sc.next();
                output.writeUTF("Bericht:" + bericht);
                act(this.gebruikerMenu());
                break;

            case 6:
                output.writeUTF("Verbreek-gebruiker");
                act(this.basisMenu());
                break;


            default:
                act(this.basisMenu());
                break;
        }
    }

    public void disconnect() throws IOException {
        sc.close();
        socket.close();
    }
}
