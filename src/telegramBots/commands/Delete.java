package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import telegramBots.TelegramBotForOccupancy;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Method.updateUserFile;

/**
 * This class implements the /delete command.
 */
public class Delete {

    /**
     * This class should not be instantiated.
     */
    private Delete() {
    }

    /**
     * This method is used to delete a user's list of rooms.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds the user to the userOnWait set and sends a message asking them to confirm the deletion.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);

        SendMessage request;
        if (!rooms.containsKey(message.from().id())) {
            if ("fr".equals(message.from().languageCode())) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list");
            }

        } else {
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(),
                            message.messageThreadId(),
                            "delete"));
            if ("fr".equals(message.from().languageCode())) {
                request = new SendMessage(message.chat().id(),
                        "Envoyez \"CONFIRM\" (en majuscule) pour valider la suppression");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Send \"CONFIRM\" (in all caps) to validate the deletion");
            }
        }
        request.disableNotification(true)
                .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
        bot.execute(request);
    }

    /**
     * This method is a continuation from the delete method.
     * It is used to confirm the deletion of a user's list of rooms.
     * If the user confirms the deletion by sending "CONFIRM", it deletes the list and sends a message
     * notifying the user of the successful deletion.
     * Otherwise, it sends a message notifying the user of the failed deletion.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void confirm(Message message) {
        removeKeyboard(message);
        SendMessage request;
        if ("CONFIRM".equals(message.text())) {
            rooms.remove(message.from().id());
            updateUserFile();
            if ("fr".equals(message.from().languageCode())) {
                request = new SendMessage(
                        message.chat().id(),
                        "Votre liste a été supprimée avec succès");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list has been successfully deleted");
            }

        } else {
            if ("fr".equals(message.from().languageCode())) {
                request = new SendMessage(
                        message.chat().id(),
                        "Erreur, refaites /delete pour réessayer");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Error, redo /delete to try again");
            }
        }
        request.disableNotification(true)
                .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
        bot.execute(request);
    }
}
