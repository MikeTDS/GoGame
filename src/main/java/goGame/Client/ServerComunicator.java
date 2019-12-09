package goGame.Client;

import goGame.GameLogic.Game;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerComunicator {
    private String _serverAddress;
    private int _port;

    private JTextField _tfServerAddress,
                              _tfServerPort;

    private Socket _socket;
    private Scanner _in;
    private PrintWriter _out;
    private Object[] _newServerFields;
    private static ServerComunicator serverCommunicator;

    private ServerComunicator(String serverAddress, int port){
        _serverAddress = serverAddress;
        _port = port;

        initializeFields();
    }

    public static ServerComunicator getInstance(String serverAddress, int port){
        if(serverCommunicator ==null){
            serverCommunicator = new ServerComunicator(serverAddress, port);
        }
        return serverCommunicator;
    }

    public static ServerComunicator getInstance(){
        if(serverCommunicator !=null){
            return serverCommunicator;
        }
        return null;
    }

    private void initializeFields(){
        _tfServerAddress = new JTextField(_serverAddress);
        _tfServerPort = new JTextField(String.valueOf(_port));

        _newServerFields = new Object[]{
                "IP:", _tfServerAddress,
                "Port:", _tfServerPort
        };
    }

    public void connectToServer() throws IOException {
        int input = JOptionPane.showConfirmDialog(null, _newServerFields, "Łączenie z serwerem", JOptionPane.OK_CANCEL_OPTION);

        if(input == 0) {
            _serverAddress = _tfServerAddress.getText();
            _port = convertToInt(_tfServerPort.getText());
        }

        _socket = new Socket(_serverAddress, _port);
        _in = new Scanner(_socket.getInputStream());
        _out = new PrintWriter(_socket.getOutputStream(), true);
    };

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
