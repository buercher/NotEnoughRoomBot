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
public class AddAll {

    /**
     * This class should not be instantiated.
     */
    private AddAll() {
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
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste /create pour en créer une");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list /create to create one");
            }

        } else {
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "addall"));
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(message.chat().id(),
                        "Envoyez \"CONFIRM\" (en majuscule) pour valider l'ajout de TOUTES les salles");
            } else {
                request = new SendMessage(
                        message.chat().id(),
                        "Send \"CONFIRM\" (in all caps) to validate the addition of ALL rooms");
            }
        }
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
        if (message.text().equals("CONFIRM")) {
            rooms.get(message.from().id()).addAll(AllRooms);
            updateUserFile();
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Toutes les salles ont été ajouté à votre liste avec succès");
            } else {
                request = new SendMessage(
                        message.chat().id(), "All rooms have been added to your list successfully");
            }

        } else {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Erreur, refaites /addall pour réessayer");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Error, redo /addall to try again");
            }
        }
        bot.execute(request);
    }
}
