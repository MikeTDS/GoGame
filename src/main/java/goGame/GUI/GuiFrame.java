package goGame.GUI;
import goGame.GUI.GUIcomponents.GoBoard.GoBoard;

import javax.swing.JFrame;
import java.awt.*;

public class GuiFrame extends JFrame{
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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
}
