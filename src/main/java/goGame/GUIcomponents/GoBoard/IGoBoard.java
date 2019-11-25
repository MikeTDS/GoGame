package goGame.GUIcomponents.GoBoard;

import java.awt.*;

public interface IGoBoard {
    void drawBoard();
    void drawFields(int size, Graphics g);
    void drawSpecialFields(int size, Graphics g);
    int setGaps(int size);
}
