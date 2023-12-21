package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.Set;
import java.util.TreeSet;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;

/**
 * This class implements the /mylist command.
 */
public class Mylist {

    /**
     * This class should not be instantiated.
     */
    private Mylist() {

    }

    /**
     * This method is used to get the list of rooms of a user.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it sends a message with the list of rooms of the user.
     * The method is in two languages, English and French. The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        String responseMessage;
        if (rooms.containsKey(message.from().id())) {
            Set<String> list = new TreeSet<>();
            validRoomData
                    .stream()
                    .filter(l -> rooms.get(message.from().id()).contains(l.getRooms()))
                    .forEach(l -> list.add(l.getPlanName()));
            removeKeyboard(message);
            String[] language = new String[3];
            if (message.from().languageCode().equals("fr")) {
                language[0] = "Voici une liste de vos salles: ";
                language[2] = "Utilisez /room ou /batiment pour obtenir des infos spécifique.";
            } else {
                language[0] = "Here is a list of your rooms: ";
                language[2] = "Use /room or /building to get specific infos.";
            }
            language[1] = String.join(", ", list);
            responseMessage =
                    String.format("%s\n<strong>%s</strong>\n%s", (Object[]) language);
        } else {
            if (message.from().languageCode().equals("fr")) {
                responseMessage = "Je n'ai trouvé aucune liste /create pour en créer une";
            } else {
                responseMessage = "I couldn't find any list /create to create one";
            }
        }
        SendMessage request = new SendMessage(message.chat().id(), responseMessage);
        request.parseMode(ParseMode.HTML);
        bot.execute(request);
    }
}
