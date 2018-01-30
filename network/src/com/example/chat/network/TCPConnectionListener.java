package com.example.chat.network;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection tcpConnection);

    void onReceiveMessage(TCPConnection tcpConnection, String value);

    void onDisconnection(TCPConnection tcpConnection);

    void onException(TCPConnection tcpConnection, Exception e);
}
