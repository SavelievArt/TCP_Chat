package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private String host;
    private int port;
    private String nickname;
    PrintWriter output;

    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("ip server: ");
        String host;
        int port;
        host = sc.nextLine();
        System.out.println("server port: ");
        port = sc.nextInt();
        new Client(host, port).run();
    }

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws UnknownHostException, IOException {
        // connect client to server
        Socket client = new Socket(host, port);
        System.out.println("Client successfully connected to server!");

        // create a new thread for server messages handling
        new Thread(new ReceivedMessagesHandler(client.getInputStream())).start();

        // ask for a nickname
        Scanner sc = new Scanner(System.in);
        PrintStream output = new PrintStream(client.getOutputStream());
        System.out.print("Enter a nickname: ");
        nickname = sc.nextLine();
        output.println(nickname);

        // read messages from keyboard and send to server
        System.out.println("Send messages: ");
        while (sc.hasNextLine()) {
            output.println(nickname + ": " + sc.nextLine());
        }

        output.close();
        sc.close();
        client.close();
    }
}

class ReceivedMessagesHandler implements Runnable {

    private InputStream server;

    public ReceivedMessagesHandler(InputStream server) {
        this.server = server;
    }

    @Override
    public void run() {
        // receive server messages and print out to screen
        Scanner s = new Scanner(server);
        while (s.hasNextLine()) {
            System.out.println(s.nextLine());
        }
        s.close();
    }
}
