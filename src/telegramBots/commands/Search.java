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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
            if ("fr".equals(message.from().languageCode())) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste /create pour en créer une");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list /create to create one");
            }
            request.disableNotification(true)
                    .disableWebPagePreview(true)
                    .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
            bot.execute(request);
        } else if (rooms.get(message.from().id()).isEmpty()) {
            if ("fr".equals(message.from().languageCode())) {
                request = new SendMessage(
                        message.chat().id(), "Votre liste est vide, ajoutez des " +
                        "salles avec les différentes commandes");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list is empty, add rooms with the different commands");
            }
            request.disableNotification(true).disableWebPagePreview(true)
                    .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
            bot.execute(request);
        } else {
            int hour = Instant.now().atZone(ZoneId.of("Europe/Paris")).getHour();
            String messageText;
            if ("fr".equals(message.from().languageCode())) {
                messageText = "Choisissez une heure de début: ";
            } else {
                messageText = "Choose a start hour: ";
            }
            request = new SendMessage(
                    message.chat().id(), messageText)
                    .replyMarkup(hourButton(hour, "SearchStart "))
                    .disableNotification(true).disableWebPagePreview(true)
                    .messageThreadId(message.messageThreadId() == null ? 0 : message.messageThreadId());
            SendResponse response = bot.execute(request);
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(),
                            message.messageThreadId(),
                            "search",
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
        if ("fr".equals(callbackQuery.from().languageCode())) {
            messageText = "Heure de départ: " + startHour + "h\nChoisissez une heure de fin: ";
        } else {
            messageText = "Starting hours: " + startHour + "h\nChoose an end hour ";
        }
        request = new EditMessageText(
                callbackQuery.message().chat().id(),
                callbackQuery.message().messageId(), messageText)
                .replyMarkup(hourButton(startHour + 1, "SearchMid ")).disableWebPagePreview(true);
        bot.execute(request);
        userOnWait.add(
                new TelegramBotForOccupancy.MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageThreadId(),
                        "search",
                        List.of(String.valueOf(callbackQuery.message().messageId()), String.valueOf(startHour))));

    }

    /**
     * Handles the search results in the Telegram bot.
     * This method is called after the user has selected a start hour and an end hour for the room search.
     * It performs a building count operation and sends a message to the user with the results.
     * If no buildings are found, it sends a message indicating that no rooms were found
     * that satisfy the user's time frame.
     * If buildings are found, it sends a message containing the buildings on the user's list that contain at least
     * one room corresponding to the user's time frame.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param startHour     The start hour for the room search selected by the user.
     * @param endHour       The end hour for the room search selected by the user.
     * @throws IOException If an I/O error occurs during the building count operation.
     */
    public static void searchResult(CallbackQuery callbackQuery, int startHour, int endHour) throws IOException {
        EditMessageText request;

        Map<String, Integer> buildingCount = GetRoomAvailability.buildingCount(
                rooms.get(callbackQuery.from().id()), startHour, endHour);
        if (buildingCount.isEmpty()) {
            String messageText;
            if ("fr".equals(callbackQuery.from().languageCode())) {
                messageText = "Je n'ai pas pu trouver une seule salle dans votre liste qui satisfasse votre délai :(";
            } else {
                messageText = "I couldn't find a single room on your list that satisfied your time frame :(";
            }
            request = new EditMessageText(
                    callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), messageText).disableWebPagePreview(true);
            bot.execute(request);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            if ("fr".equals(callbackQuery.from().languageCode())) {
                stringBuilder.append("Voici les bâtiments de votre liste qui contiennent au moins une salle ")
                        .append("correspondant à votre délai :\n");
            } else {
                stringBuilder.append("Here are the buildings on your list that contain at least one room ")
                        .append("corresponding to your timeframe:\n");
            }
            InlineKeyboardButton[][] inlineKeyboardButtonBig = new InlineKeyboardButton[buildingCount.size() / 4][4];
            InlineKeyboardButton[] inlineKeyboardButtonSmall =
                    new InlineKeyboardButton[buildingCount.size() - inlineKeyboardButtonBig.length * 4];
            int count = 0;
            for (String key : buildingCount.keySet()) {
                if (!((count >>> 2) == inlineKeyboardButtonBig.length)) {
                    inlineKeyboardButtonBig[count >>> 2][count & 3] =
                            new InlineKeyboardButton(key + " (" + buildingCount.get(key) + ")")
                                    .callbackData("SearchEnd " + key);
                } else {
                    inlineKeyboardButtonSmall[count & 3] =
                            new InlineKeyboardButton(key + " (" + buildingCount.get(key) + ")")
                                    .callbackData("SearchEnd " + key);
                }
                count++;
            }
            InlineKeyboardButton[][] inlineKeyboardButton;
            if ((buildingCount.size() & 3) != 0) {
                inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 1][];
                inlineKeyboardButton[inlineKeyboardButtonBig.length] = inlineKeyboardButtonSmall;
            } else {
                inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length][];
            }
            System.arraycopy(
                    inlineKeyboardButtonBig, 0,
                    inlineKeyboardButton, 0, inlineKeyboardButtonBig.length);

            request = new EditMessageText(
                    callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), stringBuilder.toString())
                    .replyMarkup(new InlineKeyboardMarkup(inlineKeyboardButton))
                    .parseMode(ParseMode.HTML).disableWebPagePreview(true);
            bot.execute(request);
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            callbackQuery.from().id(),
                            callbackQuery.message().date(),
                            callbackQuery.message().chat().id(),
                            callbackQuery.message().messageThreadId(),
                            "search",
                            List.of(String.valueOf(callbackQuery.message().messageId()), String.valueOf(startHour),
                                    String.valueOf(endHour))));
        }
    }

    /**
     * Handles the search results for a specific building in the Telegram bot.
     * This method is called after the user has selected a start hour, an end hour, and a building for the room search.
     * It performs a room availability search operation and sends a message to the user with the results.
     * If no rooms are found, it sends a message indicating that no rooms were found in the specified building
     * that satisfy the user's time frame.
     * If rooms are found, it sends a message containing the rooms in the specified building
     * that are available during the user's time frame.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param startHour     The start hour for the room search selected by the user.
     * @param endHour       The end hour for the room search selected by the user.
     * @param building      The building for the room search selected by the user.
     * @throws IOException If an I/O error occurs during the room availability search operation.
     */
    public static void searchResultOfBuilding(CallbackQuery callbackQuery,
                                              int startHour,
                                              int endHour,
                                              String building) throws IOException {
        EditMessageText request;
        Set<String> roomOfBuilding = new TreeSet<>();
        validRoomData.stream().filter(r ->
                        r.getBuildings().equals(building) &&
                                rooms.get(callbackQuery.from().id()).contains(r.getRooms()))
                .forEach(r -> roomOfBuilding.add(r.getRooms()));
        Map<String, List<String>> result = GetRoomAvailability.search(roomOfBuilding, startHour, endHour);
        if (result.isEmpty()) {
            String messageText;
            if ("fr".equals(callbackQuery.from().languageCode())) {
                messageText = "Je n'ai pas pu trouver une seule salle dans votre liste qui satisfasse votre délai :( "
                        + "\n Vous ne devriez pas voir ce message merci de signaler ce bug";
            } else {
                messageText = "I couldn't find a single room on your list that satisfied your time frame :( " +
                        "\n You shouldn't see this message please report this bug";
            }
            request = new EditMessageText(
                    callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), messageText).disableWebPagePreview(true);
            bot.execute(request);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String source;
            String back;
            Set<String> roomsOfBuilding = new TreeSet<>();
            if ("fr".equals(callbackQuery.from().languageCode())) {
                source = "Source";
                back = "Retour";
                stringBuilder.append("Voici les salles du bâtiments <b>")
                        .append(building)
                        .append("</b> qui sont disponibles : \n");
            } else {
                source = "From";
                back = "Back";
                stringBuilder
                        .append("Here are the available rooms of the building <b>")
                        .append(building)
                        .append("</b> :\n");
            }
            if (!result.get(EPFL).isEmpty()) {
                stringBuilder.append("<b>").append(source).append(" EPFL: </b>");
                roomsOfBuilding.addAll(result.get(EPFL));
                stringBuilder.append(String.join(", ", result.get(EPFL)));
            }
            if (!result.get(FLEP).isEmpty()) {
                stringBuilder.append("<b>\n").append(source).append(" FLEP: </b>");
                roomsOfBuilding.addAll(result.get(FLEP));
                stringBuilder.append(String.join(", ", result.get(FLEP)));
            }
            InlineKeyboardButton[][] inlineKeyboardButtonBig = new InlineKeyboardButton[roomsOfBuilding.size() / 4][4];
            InlineKeyboardButton[] inlineKeyboardButtonSmall =
                    new InlineKeyboardButton[roomsOfBuilding.size() - inlineKeyboardButtonBig.length * 4];
            int count = 0;
            for (String key : roomsOfBuilding) {
                if (!((count >>> 2) == inlineKeyboardButtonBig.length)) {
                    inlineKeyboardButtonBig[count >>> 2][count & 3] =
                            new InlineKeyboardButton(key)
                                    .callbackData("SearchResult " + key);
                } else {
                    inlineKeyboardButtonSmall[count & 3] =
                            new InlineKeyboardButton(key)
                                    .callbackData("SearchResult " + key);
                }
                count++;
            }
            InlineKeyboardButton[][] inlineKeyboardButton;
            if ((roomsOfBuilding.size() & 3) != 0) {
                inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 2][];
                inlineKeyboardButton[inlineKeyboardButtonBig.length] = inlineKeyboardButtonSmall;
            } else {
                inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 1][];
            }
            inlineKeyboardButton[inlineKeyboardButton.length - 1] =
                    new InlineKeyboardButton[]{
                            new InlineKeyboardButton(back).callbackData("SearchMid " + endHour)};
            System.arraycopy(
                    inlineKeyboardButtonBig, 0,
                    inlineKeyboardButton, 0, inlineKeyboardButtonBig.length);
            request = new EditMessageText(
                    callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), stringBuilder.toString())
                    .replyMarkup(new InlineKeyboardMarkup(inlineKeyboardButton))
                    .parseMode(ParseMode.HTML).disableWebPagePreview(true);
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            callbackQuery.from().id(),
                            callbackQuery.message().date(),
                            callbackQuery.message().chat().id(),
                            callbackQuery.message().messageThreadId(),
                            "search",
                            List.of(String.valueOf(callbackQuery.message().messageId()), String.valueOf(startHour),
                                    String.valueOf(endHour), building)));
            bot.execute(request);
        }
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
