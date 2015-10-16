import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class IMServer implements Runnable {

    private final HashMap<String, ClientThread> users = new HashMap<>();
    private final ServerSocket ss;
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");

    public IMServer(final int port) throws IOException {
        this.ss = new ServerSocket(port);
    }

    public static void main(final String[] args) {
        try {
            final IMServer server = new IMServer(4321);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        final Thread printThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    final Calendar calendar = Calendar.getInstance();
                    System.out.println("Server rapport van " + dateFormat.format(calendar.getTime()));
                    System.out.printf("... %d client(s) is/zijn verbonden\n", users.size());
                    final String[] userStrings = users.keySet().toArray(new String[users.keySet().size()]);
                    for (int i = 0; i < userStrings.length; i++) {
                        System.out.printf("%d. Client \"%s\" is verbonden\n", (i + 1), userStrings[i]);
                    }
                    System.out.println();
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        printThread.start();

        while (true) {
            try {
                final Socket socket = ss.accept();
                final ClientThread t = new ClientThread(socket);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ClientThread extends Thread {

        private final Socket socket;
        private final DataInputStream input;
        private final DataOutputStream output;
        private String connected;
        private String gebruiker;

        public ClientThread(final Socket socket) throws IOException {
            this.socket = socket;
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());

        }

        public DataOutputStream getOutput() {
            return this.output;
        }

        public synchronized void disconnectWithUser() throws IOException {
            final ClientThread correspondent = users.get(connected);
            correspondent.connected = null;
            correspondent.getOutput().writeUTF("Verbroken:");
            this.connected = null;
        }

        public boolean isConnectedWithUser() {
            return this.connected != null;
        }

        public synchronized void setConnectedWithUser(final String connected) throws IOException {
            if (users.containsKey(connected)) {
                output.writeUTF("Verbonden");
                this.connected = connected;

                final ClientThread correspondent = users.get(connected);
                correspondent.connected = this.gebruiker;
                correspondent.getOutput().writeUTF("Communicatie:" + gebruiker);
            } else {
                output.writeUTF("Deze gebruiker is niet online!");
            }
        }

        public synchronized void disconnect() {
            users.remove(gebruiker);
            System.out.println("Verbinding met " + gebruiker + " verbroken");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void connect(final String gebruiker) {
            users.put((this.gebruiker = gebruiker), this);
        }

        public synchronized void sendBericht(final String bericht) throws IOException {
            if (this.isConnectedWithUser()) {
                users.get(connected).getOutput().writeUTF("Bericht:" + bericht);
            } else {
                output.writeUTF("Je bent niet verbonden met een gebruiker");
            }
        }

        public synchronized void showOverzicht() throws IOException {
            final StringBuilder sb = new StringBuilder("");
            for (final String string : users.keySet()) {
                if (string.equals(this.gebruiker)) continue;
                sb.append(string);
                sb.append("\n");
            }

            output.writeUTF(sb.toString());
        }

        public synchronized void por() throws IOException {
            users.get(connected).getOutput().writeUTF(this.gebruiker + " port je!");
        }

        @Override
        public void run() {
            while (socket.isConnected() && !socket.isClosed()) {
                try {
                    final String line = input.readUTF();
                    if (line.contains("Bericht:")) {
                        this.sendBericht(line.replace("Bericht:", ""));
                    } else if (line.contains("Verbind:")) {
                        this.setConnectedWithUser(line.replace("Verbind:", ""));
                    } else if (line.contains("Gebruiker:")) {
                        this.connect(line.replace("Gebruiker:", ""));
                    } else if (line.equals("Verbreek")) {
                        this.disconnect();
                    } else if (line.equals("Verbreek-gebruiker")) {
                        this.disconnectWithUser();
                    } else if (line.equals("Overzicht")) {
                        this.showOverzicht();
                    } else if (line.equals("Por")) {
                        this.por();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
