package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import telegramBots.TelegramBotForOccupancy;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Room.roomAddition;

/**
 * this class implements the /addroom command.
 */
public class AddRoom {

    /**
     * the class isn't meant to be instantiated.
     */
    private AddRoom() {
    }

    /**
     * This method is used to add a room to a user's list of rooms.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds the user to the userOnWait set and sends a message asking them to confirm the addition.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);
        userOnWait.add(
                new TelegramBotForOccupancy.MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "addroom"));
        SendMessage request;
        if (message.from().languageCode().equals("fr")) {
            request = new SendMessage(
                    message.chat().id(), "Merci de donner la salle que vous voulez ajouter");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give the room you want to add");
        }
        bot.execute(request);
    }

    /**
     * This method is used to complete the addition of a room to a user's list of rooms.
     * It is called when the user sends the room they want to add.
     * The method checks if the room exists and sends a message notifying the user of the result.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     * The methods continue to add rooms until the user sends another command.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void complete(Message message) {
        String[] rooms = message.text().split("\n");
        SendMessage request;
        String response = "";
        for (String room : rooms) {
            room = room.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
            if (AllRooms.contains(room)) {
                response = roomAddition(message.from().languageCode(), room, message.from().id());
            } else {
                if (message.from().languageCode().equals("fr")) {
                    response = "Cette salle n'existe pas ou n'est pas dans ma base de données";
                } else {
                    response = "This room doesn't exist or isn't in my database";
                }
            }
        }
        request = new SendMessage(message.chat().id(), response);
        bot.execute(request);
    }
}
