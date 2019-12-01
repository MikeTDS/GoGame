package goGame.Client;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerComunitator {
    private static String _serverAddress;
    private static int _port;

    private static JTextField _tfServerAddress,
                              _tfServerPort;

    private static Socket _socket;
    private static Scanner _in;
    private static PrintWriter _out;
    private static Object[] _newServerFields;

    ServerComunitator(String serverAddress, int port){
        _serverAddress = serverAddress;
        _port = port;

        initializeFields();
    }

    private static void initializeFields(){
        _tfServerAddress = new JTextField(_serverAddress);
        _tfServerPort = new JTextField(String.valueOf(_port));

        _newServerFields = new Object[]{
                "IP:", _tfServerAddress,
                "Port:", _tfServerPort
        };
    }

    static void connectToServer() throws IOException {
        int input = JOptionPane.showConfirmDialog(null, _newServerFields, "Łączenie z serwerem", JOptionPane.OK_CANCEL_OPTION);

        if(input == 0) {
            _serverAddress = _tfServerAddress.getText();
            _port = convertToInt(_tfServerPort.getText());
        }

        _socket = new Socket(_serverAddress, _port);
        _in = new Scanner(_socket.getInputStream());
        _out = new PrintWriter(_socket.getOutputStream(), true);
    };

    private static int convertToInt(String str){
        int num = 0;
        try{
            num = Integer.parseInt(str);
        }catch (Exception e){ System.out.println(e.getMessage()); }

        return num;
    }

    public static Scanner getScanner(){ return _in; }
    public static PrintWriter getPrintWriter(){ return _out; }
    public static Socket getSocket() { return _socket; }
}
