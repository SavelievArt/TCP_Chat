package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private int port;
    private List<PrintStream> clients;
    private Map<PrintStream, String> Clients;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {

        new Server(55566).run();
    }

    public Server(int port) {
        this.port = port;
        this.Clients = new HashMap<>();
    }

    public void run() throws IOException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };
        System.out.println(this.port + " port is open!");

        while (true) {
            // accepts a new client
            Socket client = server.accept();
            System.out.println("Connection established with client: " + client.getInetAddress().getHostAddress());

            // add client message to list
            Scanner sc = new Scanner(client.getInputStream());
            String nickName = sc.nextLine();
            this.Clients.put(new PrintStream(client.getOutputStream()), nickName);
            System.out.println(nickName);

            // create a new thread for client handling
            new Thread(new ClientHandler(this, client.getInputStream())).start();
        }
    }

    void broadcastMessages(String msg) {
        if(msg.contains("@sendUser")){
            String[] str = msg.split(" ");
            String msgNew="";
            for(int i = 3; i < str.length;i++){
                msgNew += str[i];
                msgNew += " ";
            }
            System.out.println(str[2]);
            privateMessage(msgNew, str[2]);
        }
        else {
            for (PrintStream clients : this.Clients.keySet()) {
                clients.println(msg);
            }
        }
    }

    void privateMessage(String msg, String nickName){
            for(Map.Entry<PrintStream, String> m : this.Clients.entrySet()){
                if(m.getValue().equals(nickName)){
                    PrintStream ps = m.getKey();
                    ps.println(msg);
                }
            }
        }
}

class ClientHandler implements Runnable {

    private Server server;
    private InputStream client;

    public ClientHandler(Server server, InputStream client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        String message;

        // when there is a new message, broadcast to all
        Scanner sc = new Scanner(this.client);
        while (sc.hasNextLine()) {
            message = sc.nextLine();
            server.broadcastMessages(message);
        }
        sc.close();
    }
}
