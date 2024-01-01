package telegramBots.commands;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import telegramBots.TelegramBotForOccupancy;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Method.updateUserFile;

/**
 * This class implements the /building command.
 */
public class Building {

    /**
     * This constructor is private to avoid instantiation of the class.
     */
    private Building() {
    }

    /**
     * This method is used to start the building selection process for the user.
     * It sends a message to the user asking them to choose a building.
     * The buildings are presented as inline keyboard buttons for the user to select.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void command(Message message) {
        removeKeyboard(message);
        SendMessage request = new SendMessage(message.chat().id(), messageStartText(message.from().languageCode()));
        request.replyMarkup(allBuildingInlineKeyboard()).disableNotification(true);
        SendResponse response = bot.execute(request);
        userOnWait.add(
                new TelegramBotForOccupancy.MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "building",
                        List.of(response.message().messageId().toString())));
    }

    /**
     * This method is used to go back to the start of the building selection process.
     * This method is specifically designed to handle CallbackQuery, which is different from Message.
     * It edits the previous message sent to the user and asks them to choose a building again.
     * The buildings are presented as inline keyboard buttons for the user to select.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     */
    public static void backToStart(CallbackQuery callbackQuery) {
        updateMessage(callbackQuery, messageStartText(callbackQuery.from().languageCode()),
                allBuildingInlineKeyboard());
    }

    /**
     * Gives the message start text in the correct language
     *
     * @param languageCode the language code of the user (fr or else)
     * @return the message start text in the correct language
     */
    private static String messageStartText(String languageCode) {
        if (languageCode.equals("fr")) {
            return "Veuillez choisir un bâtiment";
        } else {
            return "Please choose a building";
        }
    }

    /**
     * This method is used to create an inline keyboard with all buildings.
     * It is used to avoid code duplication.
     * The buildings are presented as inline keyboard buttons for the user to select.
     *
     * @return The inline keyboard with all buildings.
     */
    private static InlineKeyboardMarkup allBuildingInlineKeyboard() {
        InlineKeyboardButton[][] inlineKeyboardButton = new InlineKeyboardButton[AllBuildingList.size() / 4][4];
        int count = 0;
        for (String building : AllBuildingList) {
            inlineKeyboardButton[count >>> 2][count & 3] = new InlineKeyboardButton(building).callbackData(building);
            count++;
        }
        return new InlineKeyboardMarkup(inlineKeyboardButton);
    }

    /**
     * This method is used to handle the middle part of the building selection process.
     * It sends a message to the user asking them to choose an option for the selected building.
     * The options are presented as inline keyboard buttons for the user to select.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param building      The building that the user has selected.
     */
    public static void mid(CallbackQuery callbackQuery, String building) {
        String messageText;
        String back;
        if (callbackQuery.from().languageCode().equals("fr")) {
            back = "Revenir en arrière";
            messageText = String.format("""
                    Veuillez sélectionner une option pour continuer :

                    🔍 Voir toutes les salles avec un horaire public dans le bâtiment %s
                    ➕ Ajouter toutes les salles de du bâtiment %s à votre liste
                    ➖ Supprimer toutes les salles du bâtiment %s de votre liste.

                    N'hésitez pas à choisir une option ou à revenir en arrière.""", building, building, building);
        } else {
            back = "Go Back";
            messageText = String.format("""
                    Please select an option to proceed:

                    🔍 View all rooms with a public schedule in the building %s
                    ➕ Add all the rooms in the building %s to your list
                    ➖ Remove all the rooms in the building %s from your list.

                    Feel free to choose an option or go back.""", building, building, building);
        }
        updateMessage(callbackQuery, messageText, new InlineKeyboardMarkup(new InlineKeyboardButton[][]{{
                new InlineKeyboardButton("🔍").callbackData("HaveListOfRoom " + building),
                new InlineKeyboardButton("➕").callbackData("AddBuildingToList " + building),
                new InlineKeyboardButton("➖").callbackData("RemoveBuildingToList " + building)},
                {new InlineKeyboardButton(back).callbackData("Go Back")}}));
    }

    /**
     * This method is used to display a list of rooms available in a selected building.
     * It sends a message to the user with the list of rooms and allows the user to select a room for more information.
     * The rooms are presented as inline keyboard buttons for the user to select.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param building      The building that the user has selected.
     */
    public static void haveListOfRoom(CallbackQuery callbackQuery, String building) {
        StringBuilder stringBuilder = new StringBuilder();
        String back;
        if (callbackQuery.from().languageCode().equals("fr")) {
            back = "Revenir en arrière";
            stringBuilder
                    .append("Voici une listes des salles disponible dans le bâtiment ")
                    .append(building)
                    .append(": \nVous pouvez sélectionner une salle pour avoir plus d'information\n");
        } else {
            back = "Go Back";
            stringBuilder
                    .append("Here is a list of the rooms available in the building ")
                    .append(building)
                    .append(": \nYou can select a room to get more information\n");
        }

        Map<String, String> roomSearchToRoomName = new TreeMap<>();
        validRoomData
                .stream()
                .filter(l -> l.getBuildings().equals(building))
                .forEach(l -> roomSearchToRoomName.put(l.getRooms(), l.getPlanName()));

        InlineKeyboardButton[][] inlineKeyboardButtonBig =
                new InlineKeyboardButton[roomSearchToRoomName.size() / 4][4];
        InlineKeyboardButton[] inlineKeyboardButtonSmall =
                new InlineKeyboardButton[roomSearchToRoomName.size() - inlineKeyboardButtonBig.length * 4];
        int count = 0;
        for (String room : roomSearchToRoomName.keySet()) {
            if (!((count >>> 2) == inlineKeyboardButtonBig.length)) {
                inlineKeyboardButtonBig[count >>> 2][count & 3] =
                        new InlineKeyboardButton(
                                roomSearchToRoomName.get(room)).callbackData("ViewRoomInfo " + room);
            } else {
                inlineKeyboardButtonSmall[count & 3] =
                        new InlineKeyboardButton(roomSearchToRoomName.get(room)).callbackData("ViewRoomInfo " + room);
            }
            count++;
        }
        InlineKeyboardButton[][] inlineKeyboardButton;
        if (inlineKeyboardButtonSmall.length != 0) {
            inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 2][];
            inlineKeyboardButton[inlineKeyboardButtonBig.length] = inlineKeyboardButtonSmall;
        } else {
            inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 1][];
        }
        System.arraycopy(
                inlineKeyboardButtonBig, 0,
                inlineKeyboardButton, 0, inlineKeyboardButtonBig.length);
        inlineKeyboardButton[inlineKeyboardButton.length - 1] =
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)};
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(inlineKeyboardButton);

        updateMessage(callbackQuery, stringBuilder.toString(), inlineKeyboard);
    }

    /**
     * This method is used to add all rooms in a selected building to the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds all rooms in the building to the user's list
     * and sends a message notifying the user of the successful addition.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param building      The building that the user has selected.
     */
    public static void addToList(CallbackQuery callbackQuery, String building) {
        String back;
        if (callbackQuery.from().languageCode().equals("fr")) {
            back = "Revenir en arrière";
        } else {
            back = "Go Back";
        }
        updateMessage(
                callbackQuery,
                buildingToListAddition(callbackQuery.from().languageCode(), building, callbackQuery.from().id()),
                new InlineKeyboardMarkup(
                        new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)));
    }

    /**
     * General method to add all rooms in a selected building to the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Then it sends a String notifying the successful addition.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param languageCode The language code of the user (fr or else)
     * @param building     The building that the user has selected.
     * @param UserId       The user's ID.
     * @return The message to be sent to the user.
     */
    public static String buildingToListAddition(String languageCode, String building, Long UserId) {
        String success;
        String failure;
        if (languageCode.equals("fr")) {
            success = "Les salles du bâtiment " + building + " ont été ajoutées avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            success = "The rooms in the building " + building + " have been successfully added to your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(UserId)) {
            validRoomData.stream()
                    .filter(l -> l.getBuildings().equals(building))
                    .forEachOrdered(l -> rooms.get(UserId).add(l.getRooms()));
            updateUserFile();
            return success;
        } else {
            return failure;
        }
    }

    /**
     * This method is used to remove all rooms in a selected building from the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Then it sends a message notifying the user of the successful removal.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param building      The building that the user has selected.
     */
    public static void removeFromList(CallbackQuery callbackQuery, String building) {
        String back;
        if (callbackQuery.from().languageCode().equals("fr")) {
            back = "Revenir en arrière";
        } else {
            back = "Go Back";
        }
        updateMessage(
                callbackQuery,
                buildingToListDeletion(callbackQuery.from().languageCode(), building, callbackQuery.from().id()),
                new InlineKeyboardMarkup(
                        new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)));
    }

    /**
     * General method to delete all rooms from a selected building from the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * and sends a String notifying the successful removal.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param languageCode The language code of the user (fr or else)
     * @param building     The building that the user has selected.
     * @param UserId       The user's ID.
     * @return The message to be sent to the user.
     */
    public static String buildingToListDeletion(String languageCode, String building, Long UserId) {
        String success;
        String failure;
        if (languageCode.equals("fr")) {
            success = "Les salles du bâtiment " + building + " ont été supprimés avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            success = "The rooms in the building " + building + " have been successfully removed from your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(UserId)) {
            validRoomData.stream()
                    .filter(l -> l.getBuildings().equals(building))
                    .forEachOrdered(l -> rooms.get(UserId).remove(l.getRooms()));
            updateUserFile();
            return success;
        } else {
            return failure;
        }
    }

    /**
     * Updates a message sent to the user with new text and an inline keyboard.
     * It creates an EditMessageText request with the provided parameters and executes it using the bot.
     * Finally, it adds a new MessageData to the userOnWait set.
     * The addition to the set is used to avoid the user using multiple inline keyboards at the same time
     *
     * @param callbackQuery  The callback query received from the user. It contains the user's ID and chat ID.
     * @param messageText    The new text to be displayed in the message.
     * @param inlineKeyboard The new inline keyboard to be displayed with the message.
     */
    private static void updateMessage(CallbackQuery callbackQuery,
                                      String messageText, InlineKeyboardMarkup inlineKeyboard) {
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(inlineKeyboard);
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "building",
                        List.of(callbackQuery.message().messageId().toString())));
    }
}
