package goGame.GUI;
import goGame.Client.GoGameClient;
import goGame.Client.ServerComunicator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static javax.swing.JOptionPane.YES_OPTION;

public class GuiFrame extends JFrame {
    private static int _xSize, _ySize;

    public GuiFrame(int xSize, int ySize){
        _xSize = xSize;
        _ySize = ySize;

        initializeGUI();
    }
    private void initializeGUI(){
        setSize(new Dimension(_xSize, _ySize));
        setLayout(new GridBagLayout());
        setTitle("GoGame");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int returnValue=0;
                returnValue = JOptionPane.showConfirmDialog(GoGameClient.getGuiFrame(), "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION);
                if(returnValue==YES_OPTION) {
                    ServerComunicator.getInstance().getPrintWriter().println("EXIT");
                }
            }
        });
        setResizable(false);
    }
    public void chooseOpponent(){
        int returnValue=0;
        returnValue = JOptionPane.showConfirmDialog(this, "Do you want to play with bot?", "Exit", JOptionPane.YES_NO_OPTION);
        if(returnValue==0){
            ServerComunicator.getInstance().getPrintWriter().println("PLAY_WITH_BOT");
        }
        else if(returnValue==1){
            ServerComunicator.getInstance().getPrintWriter().println("DONT_PLAY_WITH_BOT");
        }
    }
}
