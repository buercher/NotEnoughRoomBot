package telegramBots.commands;


import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Objects;

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
     * The method is in two languages, English and French. The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);

        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.equals(message.from().languageCode(), "fr")) {
            stringBuilder.append("Voici une liste de bâtiments qui proposent des salles avec des horaires publics: ");
        } else {
            stringBuilder.append("Here is a list of buildings that offer rooms with public schedules: ");
        }
        stringBuilder.append("\n<strong>");
        AllBuilding.forEach(l -> stringBuilder.append(l).append(" "));
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "))
                .append("\n</strong>");
        if (Objects.equals(message.from().languageCode(), "fr")) {
            stringBuilder.append("Use /building to retrieve details about a specific building.");
        } else {
            stringBuilder.append("Utilisez /bâtiment pour obtenir des infos sur un bâtiment spécifique.");
        }
        SendMessage request = new SendMessage(message.chat().id(), stringBuilder.toString());
        request.parseMode(ParseMode.HTML);
        bot.execute(request);
    }
}
