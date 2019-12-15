package goGame.Client;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerCommunicator implements IServerCommunicator{
    private String _serverAddress;
    private int _port;

    private JTextField _tfServerAddress,
                              _tfServerPort;

    private Socket _socket;
    private Scanner _in;
    private PrintWriter _out;
    private Object[] _newServerFields;
    private static ServerCommunicator serverCommunicator;

    private ServerCommunicator(String serverAddress, int port){
        setConnectionData(serverAddress, port);
        initializeFields();
    }

    public static ServerCommunicator getInstance(String serverAddress, int port){
        if(serverCommunicator ==null){
            serverCommunicator = new ServerCommunicator(serverAddress, port);
        }
        return serverCommunicator;
    }
    public static ServerCommunicator getInstance(){
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
    @Override
    public void connectToServer() throws IOException {
        int input = JOptionPane.showConfirmDialog(null, _newServerFields, "Łączenie z serwerem", JOptionPane.OK_CANCEL_OPTION);
        _serverAddress="";
        _port=-1;
        if(input == 0) {
            _serverAddress = _tfServerAddress.getText();
            _port = convertToInt(_tfServerPort.getText());
        }
        if(_serverAddress=="" || _port==-1 ){
            System.exit(-1);
        }

        _socket = new Socket(_serverAddress, _port);
        _in = new Scanner(_socket.getInputStream());
        _out = new PrintWriter(_socket.getOutputStream(), true);
    };

    public int convertToInt(String str) throws NumberFormatException{
        int num = 0;
        num = Integer.parseInt(str);
        return num;
    }

    public Scanner getScanner(){ return _in; }
    public PrintWriter getPrintWriter(){ return _out; }
    public Socket getSocket() { return _socket; }
    private void setConnectionData(String serverAddress, int port){
        _serverAddress = serverAddress;
        _port = port;
    }
}
