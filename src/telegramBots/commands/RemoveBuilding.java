package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Building.buildingToListDeletion;
import static telegramBots.commands.Method.removeKeyboard;

/**
 * this class implements the /removebuilding command.
 */
public class RemoveBuilding {

    /**
     * the class isn't meant to be instantiated.
     */
    private RemoveBuilding() {
    }

    /**
     * This method is used to remove all the rooms of a building from a user's list of rooms.
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
                new MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(),
                        message.messageThreadId(),
                        "removebuilding"));
        SendMessage request;
        if ("fr".equals(message.from().languageCode())) {
            request = new SendMessage(
                    message.chat().id(), "Merci de donner le bâtiment que vous voulez supprimer");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give the building you want to remove");
        }
        request.disableNotification(true)
                .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
        bot.execute(request);
    }

    /**
     * This method is used to complete the deletion of al the rooms of a building from a user's list of rooms.
     * It is called when the user sends the building they want to remove.
     * The method checks if the building exists and sends a message notifying the user of the result.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     * The methods continue to add building until the user sends another command.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void complete(Message message) {
        String building = message.text().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String response;
        if (AllBuilding.contains(building)) {
            response = buildingToListDeletion(message.from().languageCode(), building, message.from().id());
        } else {
            if ("fr".equals(message.from().languageCode())) {
                response = "Ce bâtiment n'existe pas ou n'est pas dans ma base de données";
            } else {
                response = "This building doesn't exist or isn't in my database";
            }
        }
        SendMessage request = new SendMessage(message.chat().id(), response).disableNotification(true)
                .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
        bot.execute(request);
    }
}
