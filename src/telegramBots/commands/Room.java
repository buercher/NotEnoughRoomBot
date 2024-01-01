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
import utils.jsonObjects.JsonRoomArchitecture;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Method.updateUserFile;

/**
 * This class implements the /room command.
 */
public class Room {
    /**
     * This constructor is private to avoid instantiation of the class.
     */
    private Room() {
    }

    /**
     * This method is used to initiate the room selection process for the user.
     * It sends a message to the user asking them to provide a room name.
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
                        message.chat().id(), "room"));
        SendMessage request;
        if (message.from().languageCode().equals("fr")) {
            request = new SendMessage(
                    message.chat().id(), "Merci de donner une salle");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give a room");
        }
        request.disableNotification(true);
        bot.execute(request);
    }

    /**
     * This method is used to handle the middle part of the room selection process.
     * It checks if the provided room name exists and if it does, it sends a message to the user with the room details.
     * If the room does not exist, it sends a message to the user notifying them of this.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param message     The message received from the user. It contains the user's ID and chat ID.
     * @param messageData The data related to the message. It contains the user's ID, chat ID,
     *                    command, and additional properties.
     */
    public static void mid(Message message, TelegramBotForOccupancy.MessageData messageData) {
        String room = message.text().replaceAll("[^A-Za-z0-9]", "").toUpperCase();

        SendMessage sendMessage;
        if (AllRooms.contains(room)) {
            userOnWait.remove(messageData);
            sendMessage = new SendMessage(message.chat().id(), midMessageText(message.from().languageCode(), room));
            sendMessage.replyMarkup(new InlineKeyboardMarkup(
                    new InlineKeyboardButton("📋")
                            .callbackData("ViewRoomInfo " + room),
                    new InlineKeyboardButton("➕")
                            .callbackData("addRoom " + room),
                    new InlineKeyboardButton("➖")
                            .callbackData("removeRoom " + room)
            )).parseMode(ParseMode.HTML).disableNotification(true);
            SendResponse response = bot.execute(sendMessage);
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "roomInlined",
                            List.of(response.message().messageId().toString())));
        } else {
            String messageText;
            if (message.from().languageCode().equals("fr")) {
                messageText = "Cette salle n'existe pas ou n'a pas d'horaire public\n" +
                        "/building pour plus d'info";
            } else {
                messageText = "This room does not exist or does not have a public schedule\n" +
                        "/building for more information";
            }
            sendMessage = new SendMessage(message.chat().id(), messageText).disableNotification(true);
            bot.execute(sendMessage);

        }
    }

    /**
     * This method is used to go back to the middle part of the room selection process.
     * It edits the previous message sent to the user and asks them to provide a room name again.
     * This method is specifically designed to handle CallbackQuery, which is different from Message.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param room          The room that the user has selected.
     */
    public static void backToMid(CallbackQuery callbackQuery, String room) {
        updateMessage(callbackQuery, midMessageText(callbackQuery.from().languageCode(), room),
                new InlineKeyboardMarkup(
                        new InlineKeyboardButton("📋")
                                .callbackData("ViewRoomInfo " + room),
                        new InlineKeyboardButton("➕")
                                .callbackData("addRoom " + room),
                        new InlineKeyboardButton("➖")
                                .callbackData("removeRoom " + room)
                ), "roomInlined");
    }

    /**
     * General method to generate the text for the middle part of the room selection process.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param languageCode The language code of the user (fr or else)
     * @param room         The room that the user has selected.
     * @return The message to be sent to the user.
     */
    private static String midMessageText(String languageCode, String room) {
        String validRoom = validRoomData.stream()
                .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
        if (languageCode.equals("fr")) {
            return String.format("""
                    Veuillez sélectionner une option pour continuer :

                    📋 Voir toutes les information sur la salle %s
                    ➕ Ajouter la salle %s à votre liste
                    ➖ Supprimer la salle %s de votre liste.

                    N'hésitez pas à choisir une option""", validRoom, validRoom, validRoom);
        } else {
            return String.format("""
                    Please select an option to proceed:

                    📋 View all information about the room %s
                    ➕ Add the room %s to your list
                    ➖ Remove the room %s from your list.

                    Feel free to choose an option or go back.""", validRoom, validRoom, validRoom);
        }
    }

    /**
     * This method is used to display detailed information about a selected room to the user.
     * It retrieves the room details from the validRoomData and dataJson data structures.
     * The room details include its
     * usage type, building, floor, plan link, PDF link, number of places, and availability.
     * It also provides options to the user to add or remove the room from their list.
     * These options are presented as inline keyboard buttons for the user to select.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param room          The room that the user has selected.
     * @param command       The command associated with the callback query.
     *                      This is used to determine the next steps based on the user's selection.
     * @param backTo        The command to go back to the previous state.
     *                      This is used to create the "Go Back" button in the inline keyboard.
     */
    public static void viewInfo(CallbackQuery callbackQuery, String room, String command, String backTo) {
        StringBuilder stringBuilder = new StringBuilder();
        String back;
        String add;
        String remove;
        Optional<JsonRoomArchitecture> roomData =
                validRoomData
                        .stream().filter(l -> l.getRooms().equals(room))
                        .findFirst();

        if (roomData.isPresent()) {
            if (callbackQuery.from().languageCode().equals("fr")) {
                add = "Ajouter la salle";
                remove = "Supprimer la salle";
                back = "Revenir en arrière";
                stringBuilder
                        .append("<b>Information sur la salle ").append(roomData.get().getPlanName()).append("</b> :\n")
                        .append(" <b>Utilisation</b> : ").append(roomData.get().getType()).append("\n")
                        .append(" <b>Batiment</b> : ").append(roomData.get().getBuildings()).append("\n");
                if (!roomData.get().getPlaces().isEmpty()) {
                    stringBuilder.append(" <b>Places</b> : ").append(roomData.get().getPlaces()).append("\n");
                }
                stringBuilder
                        .append(" <b>Étage</b> : ").append(roomData.get().getFloor()).append("\n")
                        .append(" <b>Plan</b> : <a href=\"")
                        .append(roomData.get().getPlanLink()).append("\">lien</a>\n")
                ;
                if (!roomData.get().getPdfLink().isEmpty()) {
                    stringBuilder.append(" <b>PDF</b> : <a href=\"")
                            .append(roomData.get().getPdfLink()).append("\">lien</a>\n");
                }
                stringBuilder.append("<b> Disponibilité de la Salle</b>");

                if (dataJson.get(room).getSource().equals("EPFL")) {
                    stringBuilder.append(" (Source <a href=\"https://occupancy.epfl.ch\">EPFL</a>) \n");
                } else if (dataJson.get(room).getSource().equals("FLEP")) {
                    stringBuilder.append(" (Source <a href=\"https://occupancy.FLEP.ch\">FLEP</a>) \n");
                }
                stringBuilder
                        .append("<code>|  Horaire  |⬜| Disponibilité |\n")
                        .append("|-----------|⬜|---------------|\n");
                for (int i = 7; i < 19; i++) {
                    stringBuilder.append(String.format("| %02dh - %02dh |", i, i + 1));
                    if (dataJson.get(room).getHoraire().contains(i)) {
                        stringBuilder.append("🟥| Occupé        |\n");
                    } else {
                        stringBuilder.append("🟩| Disponible    |\n");
                    }
                }
                stringBuilder.append("</code>");
            } else {
                add = "Add the room";
                remove = "Remove the room";
                back = "Go Back";
                stringBuilder
                        .append("<b>Information about the room ")
                        .append(roomData.get().getPlanName()).append("</b> :\n")
                        .append(" <b>Building</b> : ")
                        .append(roomData.get().getBuildings()).append("\n");
                if (!roomData.get().getPlaces().isEmpty()) {
                    stringBuilder.append(" <b>Places</b> : ").append(roomData.get().getPlaces()).append("\n");
                }
                stringBuilder
                        .append(" <b>Floor</b> : ").append(roomData.get().getFloor()).append("\n")
                        .append(" <b>Area</b> : ").append(roomData.get().getType()).append("\n")
                        .append(" <b>Plan</b> : <a href=\"")
                        .append(roomData.get().getPlanLink()).append("\">link</a>\n")
                ;
                if (!roomData.get().getPdfLink().isEmpty()) {
                    stringBuilder.append(" <b>PDF</b> : <a href=\"")
                            .append(roomData.get().getPdfLink()).append("\">link</a>\n");
                }
                stringBuilder.append("<b> Room availability</b>");

                if (dataJson.get(room).getSource().equals("EPFL")) {
                    stringBuilder.append(" (Source <a href=\"https://occupancy.epfl.ch\">EPFL</a>) \n");
                } else if (dataJson.get(room).getSource().equals("FLEP")) {
                    stringBuilder.append(" (Source <a href=\"https://occupancy.FLEP.ch\">FLEP</a>) \n");
                }
                stringBuilder
                        .append("<code>|  Schedule |⬜| Availability  |\n")
                        .append("|-----------|⬜|---------------|\n");
                for (int i = 7; i < 19; i++) {
                    stringBuilder.append(String.format("| %02dh - %02dh |", i, i + 1));
                    if (dataJson.get(room).getHoraire().contains(i)) {
                        stringBuilder.append("🟥| Occupied      |\n");
                    } else {
                        stringBuilder.append("🟩| Available     |\n");
                    }
                }
                stringBuilder.append("</code>");
            }

            String keyboardButtonText = switch (command) {
                case "building", "search" -> backTo + roomData.get().getBuildings();
                case "roomInlined" -> backTo + room;
                default -> throw new IllegalArgumentException(
                        command + " is not a valid command for ViewRoomInfo");
            };

            if (command.equals("search")) {// When you search for an available room, you don't want to add or remove it
                updateMessage(callbackQuery, stringBuilder.toString(),
                        new InlineKeyboardMarkup(
                                new InlineKeyboardButton(back).callbackData(keyboardButtonText)),
                        command);
            } else {
                updateMessage(callbackQuery, stringBuilder.toString(),
                        new InlineKeyboardMarkup(
                                new InlineKeyboardButton[][]{{
                                        new InlineKeyboardButton(add)
                                                .callbackData("addRoomFromViewRoomInfo " + room),
                                        new InlineKeyboardButton(remove)
                                                .callbackData("removeRoomFromViewRoomInfo " + room)}, {
                                        new InlineKeyboardButton(back).callbackData(keyboardButtonText)}}),
                        command);
            }
        } else {
            throw new NoSuchElementException("Room" + room + " exist but data not (Weird)");
        }
    }

    /**
     * This method is used to add a selected room to the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it adds the room to the user's list
     * and sends a message notifying the user of the successful addition.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param room          The room that the user has selected.
     * @param command       The command associated with the callback query.
     *                      This is used to determine the next steps based on the user's selection.
     * @param backTo        The command to go back to the previous state.
     *                      This is used to create the "Go Back" button in the inline keyboard.
     */
    public static void add(CallbackQuery callbackQuery, String room, String command, String backTo) {
        String back;
        if (callbackQuery.from().languageCode().equals("fr")) {
            back = "Revenir en arrière";
        } else {
            back = "Go Back";
        }
        updateMessage(callbackQuery,
                roomAddition(callbackQuery.from().languageCode(), room, callbackQuery.from().id()),
                new InlineKeyboardMarkup(
                        new InlineKeyboardButton(back).callbackData(backTo + room)),
                command);
    }

    /**
     * General method to add a rooms to the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * and sends a String notifying the successful addition.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param languageCode The language code of the user (fr or else)
     * @param room         The room that the user has selected.
     * @param UserId       The user's ID.
     * @return The message to be sent to the user.
     */
    public static String roomAddition(String languageCode, String room, Long UserId) {
        String success;
        String failure;
        String validRoom = validRoomData.stream()
                .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
        if (languageCode.equals("fr")) {
            success = "La salle " + validRoom + " a été ajoutée avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            success = "The room " + validRoom + " has been successfully added to your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(UserId)) {
            validRoomData.stream()
                    .filter(l -> l.getRooms().equals(room))
                    .forEachOrdered(l -> rooms.get(UserId).add(l.getRooms()));
            updateUserFile();
            return success;
        } else {
            return failure;
        }
    }

    /**
     * This method is used to remove a selected room from the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * Otherwise, it removes the room from the user's list
     * and sends a message notifying the user of the successful removal.
     *
     * @param callbackQuery The callback query received from the user. It contains the user's ID and chat ID.
     * @param room          The room that the user has selected.
     * @param command       The command associated with the callback query.
     *                      This is used to determine the next steps based on the user's selection.
     * @param backTo        The command to go back to the previous state.
     *                      This is used to create the "Go Back" button in the inline keyboard.
     */
    public static void remove(CallbackQuery callbackQuery, String room, String command, String backTo) {
        String back;
        if (callbackQuery.from().languageCode().equals("fr")) {
            back = "Revenir en arrière";
        } else {
            back = "Go Back";
        }
        updateMessage(callbackQuery,
                roomDeletion(callbackQuery.from().languageCode(), room, callbackQuery.from().id()),
                new InlineKeyboardMarkup(
                        new InlineKeyboardButton(back).callbackData(backTo + room)),
                command);
    }

    /**
     * General method to delete a rooms from the user's list.
     * If the user does not have a list, it sends a message notifying them of this.
     * and sends a String notifying the successful removal.
     * The method is in two languages, English and French.
     * The language is determined by the user's language preference.
     *
     * @param languageCode The language code of the user (fr or else)
     * @param room         The room that the user has selected.
     * @param UserId       The user's ID.
     * @return The message to be sent to the user.
     */
    public static String roomDeletion(String languageCode, String room, Long UserId) {
        String success;
        String failure;
        String validRoom = validRoomData.stream()
                .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
        if (languageCode.equals("fr")) {
            success = "La salle " + validRoom + " a été supprimée avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            success = "The rooms " + validRoom + " has been successfully removed to your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(UserId)) {
            validRoomData.stream()
                    .filter(l -> l.getRooms().equals(room))
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
                                      String messageText, InlineKeyboardMarkup inlineKeyboard, String command) {
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(inlineKeyboard);
        bot.execute(editMessageText);
        if (!command.equals("search")) { //search is a special case since we don't want to override the time
            userOnWait.add(
                    new MessageData(
                            callbackQuery.from().id(),
                            callbackQuery.message().date(),
                            callbackQuery.message().chat().id(), command,
                            List.of(callbackQuery.message().messageId().toString())));
        }
    }
}
