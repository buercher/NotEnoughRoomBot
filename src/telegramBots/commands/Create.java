package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.TreeSet;

import static telegramBots.TelegramBotForOccupancy.bot;
import static telegramBots.TelegramBotForOccupancy.rooms;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Method.updateUserFile;

/**
 * This class implements the /create command.
 */
public class Create {

    /**
     * This class should not be instantiated.
     */
    private Create() {
    }

    /**
     * This method is used to create a new list of rooms for a user.
     * If the user already has a list, it sends a message notifying them of this.
     * Otherwise, it creates a new list and sends a message notifying the user of the successful creation.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);
        SendMessage request;
        if (rooms.containsKey(message.from().id())) {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(), "Vous avez déjà une liste de salles");
            } else {
                request = new SendMessage(
                        message.chat().id(), "You already have a list of rooms");
            }

        } else {
            rooms.put(message.from().id(), new TreeSet<>());
            updateUserFile();
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Liste de salles créée. \n" +
                                "Vous pouvez maintenant ajouter des salles en utilisant /room ou /building");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Room list created. \n" +
                        "You can now add rooms using /room or /building.");
            }
        }
        request.disableNotification(true);
        bot.execute(request);
    }
}
