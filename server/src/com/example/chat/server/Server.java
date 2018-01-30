package com.example.chat.server;

import com.example.chat.network.TCPConnection;
import com.example.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements TCPConnectionListener {
    private List<TCPConnection> connections = new ArrayList<>();

    private Server() {
        System.out.println("Server is started...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("Exception occured: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAll("Client is connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveMessage(TCPConnection tcpConnection, String value) {
        sendToAll(value);
    }

    @Override
    public synchronized void onDisconnection(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAll("Client is disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Exception occured: " + e);
    }

    private void sendToAll(String value) {
        System.out.println(value);
        int size = connections.size();
        for (int i = 0; i < size; i++)
            connections.get(i).sendMessage(value);
    }
}
