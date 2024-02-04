package telegramBots.commands;


import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import static telegramBots.TelegramBotForOccupancy.AllBuilding;
import static telegramBots.TelegramBotForOccupancy.bot;
import static telegramBots.commands.Method.removeKeyboard;

/**
 * this class implements the /allBuilding command.
 */
public class AllBuildings {

    /**
     * This constructor is private to avoid instantiation of the class.
     */
    private AllBuildings() {
    }

    /**
     * Semi-Useless tbf since /building does the same thing and more.
     * This method is used to send a message to the user with a list
     * of all buildings that offer rooms with public schedules.
     * The list is sorted in alphabetical order.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);
        String[] language = new String[3];
        if ("fr".equals(message.from().languageCode())) {
            language[0] = "Voici une liste de bâtiments qui proposent des salles avec des horaires publics: ";
            language[2] = "Utilisez /bâtiment pour obtenir des infos sur un bâtiment spécifique.";
        } else {
            language[0] = "Here is a list of buildings that offer rooms with public schedules: ";
            language[2] = "Use /building to retrieve details about a specific building.";
        }
        language[1] = String.join(" ", AllBuilding);
        String responseMessage =
                String.format("%s\n<strong>%s</strong>\n%s", (Object[]) language);

        SendMessage request = new SendMessage(message.chat().id(), responseMessage);
        request.parseMode(ParseMode.HTML).disableNotification(true)
                .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
        bot.execute(request);
    }
}
