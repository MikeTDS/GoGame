package goGame.Client;

import goGame.GameLogic.Game;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class LobbyComunicator {
    private String _serverAddress;
    private int _port;

    private JTextField _tfServerAddress,
            _tfServerPort;

    private Socket _socket;
    private Scanner _in;
    private PrintWriter _out;
    private Object[] _newServerFields;
    private static LobbyComunicator lobbyCommunicator;

    private LobbyComunicator(){
        _serverAddress = "localhost";
        _port = 50000;
    }

    public static LobbyComunicator getInstance(){
        if(lobbyCommunicator !=null){
            return lobbyCommunicator;
        }
        return null;
    }

    public void connectToServer() throws IOException {
        _socket = new Socket(_serverAddress, _port);
        _in = new Scanner(_socket.getInputStream());
        _out = new PrintWriter(_socket.getOutputStream(), true);
    }

    private int convertToInt(String str){
        int num = 0;
        try{
            num = Integer.parseInt(str);
        }catch (Exception e){ System.out.println(e.getMessage()); }

        return num;
    }

    public Scanner getScanner(){ return _in; }
    public PrintWriter getPrintWriter(){ return _out; }
    public Socket getSocket() { return _socket; }
}

