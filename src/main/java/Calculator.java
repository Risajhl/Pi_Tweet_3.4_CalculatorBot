import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.db.Context;
import ir.pi.project.server.model.CurrentChat;
import ir.pi.project.shared.enums.Pages.MessagesPage;
import ir.pi.project.shared.enums.others.MessageStatus;
import ir.pi.project.shared.event.messages.NewMessageEvent;
import ir.pi.project.shared.model.GroupChat;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import java.util.List;

public class Calculator {
    ClientHandler clientHandler;
    BotSender botSender;
    Context context=new Context();

    public Calculator(ClientHandler clientHandler,BotSender botSender) {
        this.clientHandler=clientHandler;
        this.botSender=botSender;
    }

    public void check(){

        User bot=context.Users.get(Main.id);

        for (List<Integer> chat: bot.getChats()) {
            if(chat.size()==1) {
                Message message=context.Messages.get(chat.get(0));
                if(message.getStatus()!= MessageStatus.SEEN) {
                    message.setStatus(MessageStatus.SEEN);
                    context.Messages.update(message);

                    clientHandler.setCurrentUserId(Main.id);
                    clientHandler.setCurrentChat(new CurrentChat());
                    clientHandler.getCurrentChat().setTheOther(message.getSenderId());
                    String ans = "Hello, Thanks for choosing Calculator Bot. To use the bot follow this sample /calculateX+Y ";
                    botSender.addEvent(new NewMessageEvent(MessagesPage.DIRECT_CHATS, ans, null));
                }
            } else {
                for (Integer messageId : chat) {
                    Message message = context.Messages.get(messageId);
                    if (message.getSenderId() != Main.id) {
                        if (message.getStatus() != MessageStatus.SEEN) {

                            message.setStatus(MessageStatus.SEEN);
                            context.Messages.update(message);
                            clientHandler.setCurrentUserId(Main.id);
                            clientHandler.setCurrentChat(new CurrentChat());
                            clientHandler.getCurrentChat().setTheOther(message.getSenderId());


                            String ans = calculate(message.getText());
                            botSender.addEvent(new NewMessageEvent(MessagesPage.DIRECT_CHATS, ans, null));
                        }
                    }
                }
            }

        }

        for (Integer groupChatId: bot.getGroupChats()) {
            GroupChat groupChat=context.GroupChats.get(groupChatId);
            int q=0;
            for (Integer messageId: groupChat.getMessages()) {
                Message message=context.Messages.get(messageId);
                if(message.getSenderId()==Main.id) q++;
                if(!message.isBotSeen()){
                    String ans=calculate(message.getText());
                    if(!ans.equals("I can't understand, please try again!")){
                        message.setBotSeen(true);
                        context.Messages.update(message);
                        clientHandler.setCurrentUserId(Main.id);
                        clientHandler.getCurrentChat().setName(groupChat.getGroupName());
                        botSender.addEvent(new NewMessageEvent(MessagesPage.GROUP_CHATS, ans, null));
                    }
                }
            }
            if(q==0) {
                clientHandler.setCurrentUserId(Main.id);
                clientHandler.getCurrentChat().setName(groupChat.getGroupName());
                String ans = "Hello, Thanks for choosing Calculator Bot. To use the bot follow this sample /calculateX+Y ";
                botSender.addEvent(new NewMessageEvent(MessagesPage.GROUP_CHATS,ans,null));
            }

        }



    }

    public String calculate(String string){
        String ans = "I can't understand, please try again!";

        if (string.startsWith("/calculate")) {
            try {
                string=string.substring(10);
                for (int i = 0; i < string.length(); i++) {
                    String a = string.substring(0, i);
                    String b = string.substring(i + 1);
                    if (string.charAt(i) == '+')
                        ans = (get(a) + get(b)) + "";
                    else if (string.charAt(i) == '-')
                        ans = (get(a) - get(b)) + "";
                    else if (string.charAt(i) == '*')
                        ans = (get(a) * get(b)) + "";
                    else if (string.charAt(i) == '/')
                        ans = (get(a) / get(b)) + "";
                }

            } catch (Exception ignored) { }

        }
        return ans;
    }

    private double get(String s){ return Double.parseDouble(s); }


}
