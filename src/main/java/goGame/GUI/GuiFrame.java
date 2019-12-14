package goGame.GUI;
import goGame.Client.GoGameClient;
import goGame.Client.ServerCommunicator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static javax.swing.JOptionPane.YES_OPTION;

public class GuiFrame extends JFrame implements IGuiFrame{
    private static int _xSize, _ySize;

    public GuiFrame(int xSize, int ySize){
        _xSize = xSize;
        _ySize = ySize;

        initializeGUI();
    }
    @Override
    public void initializeGUI(){
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
                    ServerCommunicator.getInstance().getPrintWriter().println("EXIT");
                }
            }
        });
        setResizable(false);
    }
    @Override
    public void chooseOpponent(){
        int returnValue=0;
        returnValue = getOption();
        if(returnValue==0){
            ServerCommunicator.getInstance().getPrintWriter().println("PLAY_WITH_BOT");
        }
        else if(returnValue==1){
            ServerCommunicator.getInstance().getPrintWriter().println("DONT_PLAY_WITH_BOT");
        }
        else{
            System.exit(0);
        }
    }
    public int getOption(){
        return JOptionPane.showConfirmDialog(this, "Do you want to play with bot?", "Exit", JOptionPane.YES_NO_OPTION);
    }
}
