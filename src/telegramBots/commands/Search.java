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
     * This method is used to find for available room in a user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds the user to the userOnWait set
     * The method is in two languages, English and French. The language is determined by the user's language preference.
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
