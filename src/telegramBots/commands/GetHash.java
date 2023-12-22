package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
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

    /**
     * This method is used to get the hash of a user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it sends a message with the hash of the user's list.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
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
                        "Voici votre hash, vous pouvez l'utiliser pour partager votre liste \n<code>" +
                                rooms.get(message.from().id()).hashCode() + " </code>");
            } else {
                request = new SendMessage(
                        message.chat().id(),
                        "Here's your hash, you can use it to share your list \n<code>" +
                                rooms.get(message.from().id()).hashCode() + " </code>");
            }
        }
        request.parseMode(ParseMode.HTML);
        bot.execute(request);
    }
}
