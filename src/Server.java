import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Server {

    private int port;
    private boolean isLoggedIn;
    private long millisTotal;
    private String pathToFolder;


    public Server(int port) { //default port: 1000
        this.port = port;
        this.isLoggedIn = false;
        this.millisTotal = 0;
        this.pathToFolder = "C:\\Users\\Cedric\\Documents\\zeitenlogger\\";
        ServerSocket server;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat diffFormatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        diffFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            server = new ServerSocket(this.port);
            long lastTime = System.currentTimeMillis();
            String lastDate = formatter.format(lastTime).split(" ")[0];
            while(true) {
                lastTime = System.currentTimeMillis();
                String date = formatter.format(lastTime).split(" ")[0];
                String p = pathToFolder + date + ".txt";
                if(!lastDate.equals(date) && !isLoggedIn) this.millisTotal = 0;
                createFile(p);

                Socket client = server.accept();
                BufferedReader rein = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String s = rein.readLine();

                if(s.equalsIgnoreCase("70cff436") || s.equalsIgnoreCase("7d2d2d5b")) {
                    this.isLoggedIn = !isLoggedIn;
                    if(isLoggedIn) {
                        lastTime = System.currentTimeMillis();
                        System.out.println("Logged in: " + formatter.format(lastTime));
                        writeToFile(p, "Logged in: " + formatter.format(lastTime));
                    } else {
                        long time = System.currentTimeMillis();
                        this.millisTotal += (time - lastTime);
                        System.out.println("Logged out: " + formatter.format(time));
                        writeToFile(p, "Logged out: " + formatter.format(time));
                        System.out.println("Diff: " + diffFormatter.format(time - lastTime) + " | Total: " + diffFormatter.format(millisTotal));
                        writeToFile(p, "Diff: " + diffFormatter.format(time - lastTime) + " | Total: " + diffFormatter.format(millisTotal));
                        System.out.println();
                        writeToFile(p, "");

                    }
                } else {
                    System.out.println("Unauthorized user tried to login at " + formatter.format(System.currentTimeMillis()));
                }
                lastDate = date;
                Thread.sleep(100);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void createFile(String path) {
        File file = new File(path);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToFile(String path, String data) {
        FileWriter fw;
        try {
            fw = new FileWriter(path, true);
            fw.write(data + "\n");
            fw.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
