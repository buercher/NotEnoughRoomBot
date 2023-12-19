package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Objects;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;

/**
 * This class implements the /reset command.
 */
public class Reset {

    /**
     * This class should not be instantiated.
     */
    private Reset() {
    }

    /**
     * This method is used to reset a user's list of rooms.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds the user to the userOnWait set and sends a message asking them to confirm the reset.
     * The method is in two languages, English and French. The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);

        SendMessage request;
        if (!rooms.containsKey(message.from().id())) {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list");
            }

        } else {
            userOnWait.add(
                    new MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "reset"));
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(message.chat().id(),
                        "Envoyez \"CONFIRM\" (en majuscule) pour valider la réinitialisation");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Send \"CONFIRM\" (in all caps) to validate the reset");
            }
        }
        bot.execute(request);
    }

    /**
     * This method is a continuation from the reset method. It is used to confirm the reset of a user's list of rooms.
     * If the user confirms the reset by sending "CONFIRM",
     * it resets the list and sends a message notifying the user of the successful reset.
     * Otherwise, it sends a message notifying the user of the failed reset.
     * The method is in two languages, English and French. The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void confirm(Message message) {
        removeKeyboard(message);
        SendMessage request;
        if (message.text().equals("CONFIRM")) {
            rooms.get(message.from().id()).clear();
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Votre liste a été réinitialisée avec succès");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list has been successfully reset");
            }

        } else {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Erreur, refaites /reset pour réessayer");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Error, redo /reset to try again");
            }
        }
        bot.execute(request);
    }
}
