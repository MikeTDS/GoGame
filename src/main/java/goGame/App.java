package goGame;

/**
 * Hello world!
 *
 */
public class App 
{
    private static ClientGUI clientGUI;
    public static void main( String[] args ) {
        initializeGame();
    }
    private static void initializeGame(){
        clientGUI = new ClientGUI(900, 900);
    }
}
