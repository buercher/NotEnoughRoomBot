package telegramBots.commands;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import telegramBots.GetRoomAvailability;
import telegramBots.TelegramBotForOccupancy;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;

/**
 * This class implements the /search command.
 */
public class Search {

    private static final String EPFL = "EPFL";
    private static final String FLEP = "FLEP";

    /**
     * This class should not be instantiated.
     */
    private Search() {
    }

    /**
     * Handles the /search command in the Telegram bot.
     * Checks if the user has any rooms stored in their list.
     * If not, sends a message to the user to create a list.
     * If the list is empty, sends a message to the user to add rooms.
     * If the list is not empty, asks the user to choose a start hour for the room search.
     *
     * @param message The message received from the user
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
            bot.execute(request);
        } else if (rooms.get(message.from().id()).isEmpty()) {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(), "Votre liste est vide, ajoutez des " +
                        "salles avec les différentes commandes");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list is empty, add rooms with the different commands");
            }
            bot.execute(request);
        } else {
            int hour = Instant.now().atZone(ZoneId.of("Europe/Paris")).getHour();
            String messageText;
            if (message.from().languageCode().equals("fr")) {
                messageText = "Choisissez une heure de début: ";
            } else {
                messageText = "Choose a start hour: ";
            }
            request = new SendMessage(
                    message.chat().id(), messageText)
                    .replyMarkup(hourButton(hour, "SearchStart "));
            SendResponse response = bot.execute(request);
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "search",
                            List.of(String.valueOf(response.message().messageId()))));
        }
    }

    /**
     * Handles the selection of the end time for the room search in the Telegram bot.
     * This method is called after the user has selected a start hour for the room search.
     * Sends a message to the user asking them to choose an end hour for the room search.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param startHour     The start hour for the room search selected by the user.
     */
    public static void endTime(CallbackQuery callbackQuery, int startHour) {
        EditMessageText request;

        String messageText;
        if (callbackQuery.from().languageCode().equals("fr")) {
            messageText = "Heure de départ: " + startHour + "h\nChoisissez une heure de fin: ";
        } else {
            messageText = "Starting hours: " + startHour + "h\nChoose an end hour ";
        }
        request = new EditMessageText(
                callbackQuery.message().chat().id(),
                callbackQuery.message().messageId(), messageText)
                .replyMarkup(hourButton(startHour + 1, "SearchMid "));
        bot.execute(request);
        userOnWait.add(
                new TelegramBotForOccupancy.MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "search",
                        List.of(String.valueOf(callbackQuery.message().messageId()), String.valueOf(startHour))));

    }

    /**
     * Handles the final stage of the room search in the Telegram bot.
     * This method is called after the user has selected both a start hour and an end hour for the room search.
     * Uses the {@link GetRoomAvailability#search} method to find rooms that are available
     * during the selected time frame.
     * If no rooms are found, sends a message to the user indicating that no rooms were found.
     * If rooms are found, sends a message to the user listing the rooms that are available.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param startHour     The start hour for the room search selected by the user.
     * @param endHour       The end hour for the room search selected by the user.
     * @throws IOException If an input or output exception occurred
     */
    public static void searchResult(CallbackQuery callbackQuery, int startHour, int endHour) throws IOException {
        EditMessageText request;

        Map<String, List<String>> result = GetRoomAvailability.search(
                rooms.get(callbackQuery.from().id()), startHour, endHour);
        if (result.get(EPFL).isEmpty() && result.get(FLEP).isEmpty()) {
            String messageText;
            if (callbackQuery.from().languageCode().equals("fr")) {
                messageText = "Je n'ai pas pu trouver une seule salle dans votre liste qui satisfasse votre délai :(";
            } else {
                messageText = "I couldn't find a single room on your list that satisfied your time frame :(";
            }
            request = new EditMessageText(
                    callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), messageText);
            bot.execute(request);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String source;
            if (callbackQuery.from().languageCode().equals("fr")) {
                source = "Source";
                stringBuilder.append("Voici les pièces de votre liste qui correspondent à votre délai :\n");
            } else {
                source = "From";
                stringBuilder.append("Here are the rooms on your list that suit your timeframe:\n");
            }
            if (!result.get(EPFL).isEmpty()) {
                Map<String, Set<String>> map = buildingMap(result, EPFL);
                stringBuilder.append("<b>").append(source).append(" EPFL :</b>");
                for (String key : map.keySet()) {
                    stringBuilder.append("\n");
                    stringBuilder.append(String.join(", ", map.get(key)));
                }
            }
            if (!result.get(FLEP).isEmpty()) {
                Map<String, Set<String>> map = buildingMap(result, FLEP);
                stringBuilder.append("<b>\n").append(source).append(" FLEP :</b>");
                for (String key : map.keySet()) {
                    stringBuilder.append("\n");
                    stringBuilder.append(String.join(", ", map.get(key)));
                }
            }
            request = new EditMessageText(
                    callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), stringBuilder.toString())
                    .parseMode(ParseMode.HTML);
            bot.execute(request);
        }
    }

    /**
     * Builds a map of buildings and their corresponding rooms.
     * This method filters the valid room data based on the rooms
     * present in the result map and groups them by building.
     *
     * @param result A map of results. The key is the source (either EPFL or FLEP)
     *               and the value is a list of room names.
     * @param epfl   A string representing the source (either EPFL or FLEP).
     * @return A map where the key is the building name and the value is a set of room names.
     */
    private static Map<String, Set<String>> buildingMap(Map<String, List<String>> result, String epfl) {
        Map<String, Set<String>> map = new TreeMap<>();
        validRoomData
                .stream()
                .filter(l -> result.get(epfl).contains(l.getRooms()))
                .forEach(l -> {
                    if (!map.containsKey(l.getBuildings())) {
                        map.put(l.getBuildings(), new TreeSet<>());
                    }
                    map.get(l.getBuildings()).add(l.getPlanName());
                });
        return map;
    }

    /**
     * Creates an inline keyboard markup for the Telegram bot.
     * This keyboard is used to allow the user to select an hour.
     * The method creates an array of inline keyboard buttons, each representing an hour from the lower limit to 24.
     * Each button is associated with a callback data string that is a combination of the callback output and the hour.
     *
     * @param lowerHour      The lower limit for the hour selection.
     * @param callbackOutput A string representing the callback output.
     * @return An InlineKeyboardMarkup object representing the created keyboard.
     */
    private static InlineKeyboardMarkup hourButton(int lowerHour, String callbackOutput) {
        InlineKeyboardButton[][] inlineKeyboardButtonBig = new InlineKeyboardButton[(25 - lowerHour) / 4][4];
        InlineKeyboardButton[] inlineKeyboardButtonSmall =
                new InlineKeyboardButton[25 - lowerHour - inlineKeyboardButtonBig.length * 4];
        int count = 0;
        for (int i = lowerHour; i < 25; i++) {
            if (!((count >>> 2) == inlineKeyboardButtonBig.length)) {
                inlineKeyboardButtonBig[count >>> 2][count & 3] =
                        new InlineKeyboardButton(String.valueOf(i)).callbackData(callbackOutput + i);
            } else {
                inlineKeyboardButtonSmall[count & 3] =
                        new InlineKeyboardButton(String.valueOf(i)).callbackData(callbackOutput + i);
            }
            count++;
        }
        InlineKeyboardButton[][] inlineKeyboardButton;
        if (inlineKeyboardButtonSmall.length != 0) {
            inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 1][];
            inlineKeyboardButton[inlineKeyboardButtonBig.length] = inlineKeyboardButtonSmall;
        } else {
            inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length][];
        }
        System.arraycopy(
                inlineKeyboardButtonBig, 0,
                inlineKeyboardButton, 0, inlineKeyboardButtonBig.length);
        return new InlineKeyboardMarkup(inlineKeyboardButton);
    }
}
