package com.example.chat.client;

import com.example.chat.network.TCPConnection;
import com.example.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Client extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDRESS = "192.168.1.101";
    private static final int PORT = 8189;

    private TCPConnection tcpConnection;

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private JTextArea textArea = new JTextArea();
    private JTextField nameField = new JTextField("client");
    private JTextField inputField = new JTextField();

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        inputField.addActionListener(this);
        add(textArea, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        add(nameField, BorderLayout.NORTH);

        setVisible(true);

        try {
            tcpConnection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage("Exception occured: " + e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = inputField.getText();
        if (msg.equals(""))
            return;
        inputField.setText(null);
        tcpConnection.sendMessage(nameField.getText() + ": " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection established...");
    }

    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnection(TCPConnection tcpConnection) {
        printMessage("Connection is closed...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Exception occured: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(message + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        });
    }
}
