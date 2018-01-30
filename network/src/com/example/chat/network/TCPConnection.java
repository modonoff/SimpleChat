package com.example.chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener tcpConnectionListener;

    public TCPConnection(TCPConnectionListener tcpConnectionListener, String ipAddress, int port) throws IOException {
        this(tcpConnectionListener, new Socket(ipAddress, port));
    }

    public TCPConnection(TCPConnectionListener tcpConnectionListener, Socket socket) throws IOException {
        this.tcpConnectionListener = tcpConnectionListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpConnectionListener.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()) {
                        tcpConnectionListener.onReceiveMessage(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    tcpConnectionListener.onException(TCPConnection.this, e);
                } finally {
                    tcpConnectionListener.onDisconnection(TCPConnection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendMessage(String value) {
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            tcpConnectionListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpConnectionListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + "; " + socket.getPort();
    }
}
