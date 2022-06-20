import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.shared.util.Loop;

public class Main  {

    static int id;
    String username="Calculator_Bot";
    Calculator calculator;


    public Main() {
        BotSender botSender=new BotSender();
        ClientHandler clientHandler = new ClientHandler(botSender);
        clientHandler.start();
        this.calculator= new Calculator(clientHandler,botSender);
    }

    public void start(){
        calculator.check();
    }

    public static void setId(int id) {
        Main.id = id;
    }

    public String getUsername() {
        return username;
    }


}
