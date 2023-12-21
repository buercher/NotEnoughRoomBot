package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import static telegramBots.TelegramBotForOccupancy.bot;
import static telegramBots.TelegramBotForOccupancy.rooms;
import static telegramBots.commands.Method.removeKeyboard;

/**
 * This class implements the /create command.
 */
public class GetHash {

    /**
     * the class isn't meant to be instantiated.
     */
    private GetHash() {
    }

    public static void command(Message message) {
        removeKeyboard(message);
        SendMessage request;
        if (!rooms.containsKey(message.from().id())) {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(), "Vous n'avez pas de liste, merci d'en créer une avec /create");
            } else {
                request = new SendMessage(
                        message.chat().id(), "You don't have a list, please create one with /create");
            }

        } else {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Voici votre hash, vous pouvez l'utiliser pour partager votre liste \n" +
                        rooms.get(message.from().id()).hashCode());
            } else {
                request = new SendMessage(
                        message.chat().id(),
                        "Here's your hash, you can use it to share your list \n" +
                                rooms.get(message.from().id()).hashCode());
            }
        }
        bot.execute(request);
    }
}
