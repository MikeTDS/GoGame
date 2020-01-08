package goGame.DataBase;

public class DataBaseConnector {
    private static DataBaseConnector dataBaseConnector;

    private DataBaseConnector(){}

    public static synchronized DataBaseConnector getInstance(){
        if(dataBaseConnector==null)
            dataBaseConnector=new DataBaseConnector();
        return dataBaseConnector;
    }
    public static void sendData(String data){
        System.out.println(data);
    }
}
