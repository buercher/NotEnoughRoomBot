package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Room.roomDeletion;

/**
 * this class implements the /removeroom command.
 */
public class RemoveRoom {

    /**
     * the class isn't meant to be instantiated.
     */
    private RemoveRoom() {
    }

    /**
     * This method is used to remove a room from a user's list of rooms.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds the user to the userOnWait set and sends a message asking them to confirm the deletion.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);
        userOnWait.add(
                new MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "removeroom"));
        SendMessage request;
        if ("fr".equals(message.from().languageCode())) {
            request = new SendMessage(
                    message.chat().id(), "Merci de donner la salle que vous voulez supprimer");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give the room you want to remove");
        }
        request.disableNotification(true);
        bot.execute(request);
    }

    /**
     * This method is used to complete the deletion of a room from a user's list of rooms.
     * It is called when the user sends the room they want to remove.
     * The method checks if the room exists and sends a message notifying the user of the result.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     * The methods continue to remove rooms until the user sends another command.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void complete(Message message) {
        String room = message.text().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String response;
        if (AllRooms.contains(room)) {
            response = roomDeletion(message.from().languageCode(), room, message.from().id());
        } else {
            if ("fr".equals(message.from().languageCode())) {
                response = "Cette salle n'existe pas ou n'est pas dans ma base de données";
            } else {
                response = "This room doesn't exist or isn't in my database";
            }
        }
        SendMessage request = new SendMessage(message.chat().id(), response).disableNotification(true);
        bot.execute(request);
    }
}
