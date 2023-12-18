package TelegramBots;

import Utils.jsonObjects.Datajson;
import Utils.jsonObjects.JsonRoomArchitecture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TelegramBotForOccupancy {

    private static final Set<MessageData> userOnWait = new HashSet<>();

    public record MessageData(
            Long UserId, Integer date, Long ChatId, String command, List<String> additionalProperties) {
        public MessageData(Long UserId, Integer date, Long ChatId, String command) {
            this(UserId, date, ChatId, command, new ArrayList<>());
        }
    }

    public static List<JsonRoomArchitecture> validRoomData;
    public static Map<Long, Set<String>> rooms;
    public static Map<String, Datajson> dataJson;

    // Replace "YOUR_BOT_TOKEN" with your actual bot token
    public static TelegramBot bot = new TelegramBot("6944730251:AAFZSgdQfUYKU8r17WOBPzZSgLQ3ogH7rro");

    public static Set<String> AllBuilding = new TreeSet<>();

    public static Set<String> AllRooms = new TreeSet<>();
    public static List<String> AllBuildingList = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        File validRoomDataFile = new File("database/validRoomData.json");

        TypeReference<List<JsonRoomArchitecture>> typeRefValidRoom = new TypeReference<>() {
        };
        String validRoomJson = Files.readString(validRoomDataFile.toPath());
        validRoomData = new ObjectMapper().readValue(validRoomJson, typeRefValidRoom);

        File DataJsonFile = new File("database/data.json");
        TypeReference<Map<String, Datajson>> typeRefDataJson = new TypeReference<>() {
        };
        String dataJsonString = Files.readString(DataJsonFile.toPath());
        dataJson = new ObjectMapper().readValue(dataJsonString, typeRefDataJson);


        validRoomData.forEach(validRooms -> {
            AllBuilding.add(validRooms.getBuildings());
            AllRooms.add(validRooms.getRooms());
        });

        AllBuildingList.addAll(AllBuilding);
        Collections.sort(AllBuildingList);

        File directory = new File("database/UserData");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create folder '" + directory.getPath() + "'");
        }

        File UserDataJson = new File("database/UserData/rooms.json");
        if (Files.exists(UserDataJson.toPath())) {
            String jsonString = Files.readString(UserDataJson.toPath());
            TypeReference<HashMap<Long, Set<String>>> typeRef = new TypeReference<>() {
            };
            rooms = new ObjectMapper().readValue(jsonString, typeRef);
        } else {
            rooms = new HashMap<>();
        }

        // Set up a listener for updates
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.callbackQuery() != null) {

                    Optional<MessageData> request = userOnWait.stream().filter(l ->
                            Objects.equals(l.UserId(), update.callbackQuery().from().id()) &&
                                    Objects.equals(l.ChatId, update.callbackQuery().message().chat().id())).findFirst();
                    if (request.isPresent()) {
                        switch (request.get().command) {
                            case "building" -> {
                                userOnWait.remove(request.get());
                                if (AllBuilding.contains(update.callbackQuery().data())) {
                                    buildingMid(update.callbackQuery(),
                                            update.callbackQuery().data());
                                } else if (Objects.equals(update.callbackQuery().data(), "Go Back")) {
                                    buildingBackStart(update.callbackQuery());

                                } else if (update.callbackQuery().data().startsWith("Go Back To buildingMid ")) {
                                    buildingMid(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("Go Back To buildingMid ", ""));

                                } else if (update.callbackQuery().data().startsWith("HaveListOfRoom ")) {
                                    HaveListOfRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("HaveListOfRoom ", ""));

                                } else if (update.callbackQuery().data().startsWith("AddBuildingToList ")) {
                                    AddBuildingToList(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("AddBuildingToList ", ""));

                                } else if (update.callbackQuery().data().startsWith("RemoveBuildingToList ")) {
                                    RemoveBuildingToList(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("RemoveBuildingToList ", ""));

                                } else if (update.callbackQuery().data().startsWith("ViewRoomInfo ")) {
                                    ViewRoomInfo(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("ViewRoomInfo ", ""),
                                            "building", "HaveListOfRoom ");

                                } else if (update.callbackQuery().data().startsWith("addRoomFromViewRoomInfo ")) {
                                    addRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("addRoomFromViewRoomInfo ", ""),
                                            "building", "ViewRoomInfo ");
                                } else if (update.callbackQuery().data().startsWith("removeRoomFromViewRoomInfo ")) {
                                    removeRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("removeRoomFromViewRoomInfo ", ""),
                                            "building", "ViewRoomInfo ");
                                } else {
                                    throw new IllegalArgumentException(
                                            update.callbackQuery().data() + " is not a valid parameter for building");
                                }
                            }
                            case "roomInlined" -> {
                                userOnWait.remove(request.get());
                                if (update.callbackQuery().data().startsWith("ViewRoomInfo ")) {
                                    ViewRoomInfo(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("ViewRoomInfo ", ""),
                                            "roomInlined", "Go Back To roomMid ");

                                } else if (update.callbackQuery().data().startsWith("Go Back To roomMid ")) {
                                    roomMidBack(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("Go Back To roomMid ", ""));
                                } else if (update.callbackQuery().data().startsWith("addRoom ")) {
                                    addRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("addRoom ", ""),
                                            "roomInlined", "Go Back To roomMid ");
                                } else if (update.callbackQuery().data().startsWith("removeRoom ")) {
                                    removeRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("removeRoom ", ""),
                                            "roomInlined", "Go Back To roomMid ");
                                } else if (update.callbackQuery().data().startsWith("addRoomFromViewRoomInfo ")) {
                                    addRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("addRoomFromViewRoomInfo ", ""),
                                            "roomInlined", "ViewRoomInfo ");
                                } else if (update.callbackQuery().data().startsWith("removeRoomFromViewRoomInfo ")) {
                                    removeRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("removeRoomFromViewRoomInfo ", ""),
                                            "roomInlined", "ViewRoomInfo ");
                                } else {
                                    throw new IllegalArgumentException(
                                            update.callbackQuery().data() + " is not a valid parameter for room");
                                }
                            }
                            default -> System.out.println("default");
                        }
                    }
                }

                // Check if the update has a message
                if (update.message() != null) {
                    Message message = update.message();

                    if (Objects.nonNull(message.text())) {
                        // Get the text of the received message
                        String receivedText = message.text()
                                .replaceAll("@NotEnoughRoomBot", "")
                                .toLowerCase();
                        switch (receivedText) {
                            case "/create" -> create(message);
                            case "/delete" -> delete(message);
                            case "/reset" -> reset(message);
                            case "/allbuildings" -> allBuildings(message);
                            case "/building" -> buildingStart(message);
                            case "/room" -> room(message);
                            default -> {
                                Optional<MessageData> request =
                                        userOnWait.stream().filter(l ->
                                                        Objects.equals(l.UserId(), message.from().id()) &&
                                                                Objects.equals(l.ChatId, message.chat().id()))
                                                .findFirst();
                                if (request.isPresent()) {
                                    String command = request.get().command;
                                    switch (command) {
                                        case "room" -> roomMid(message, request.get());
                                        case "reset" -> resetConfirm(message);
                                        case "delete" -> deleteConfirm(message);
                                        default -> System.out.println("Salut");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });

    }

    private static void create(Message message) {
        removeKeyboard(message);

        SendMessage request;
        if (rooms.containsKey(message.from().id())) {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(), "Vous avez déjà une liste de salles");
            } else {
                request = new SendMessage(
                        message.chat().id(), "You already have a list of rooms");
            }

        } else {
            rooms.put(message.from().id(), new TreeSet<>());
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Liste de salles créée. " +
                                "Vous pouvez maintenant ajouter des salles en utilisant /room ou /building");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Room list created. You can now add rooms using /room or /building.");
            }
        }
        bot.execute(request);
    }

    private static void delete(Message message) {
        removeKeyboard(message);

        SendMessage request;
        if (!rooms.containsKey(message.from().id())) {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list");
            }

        } else {
            userOnWait.add(
                    new MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "delete"));
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(message.chat().id(),
                        "Envoyez \"CONFIRM\" (en majuscule) pour valider la suppression");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Send \"CONFIRM\" (in all caps) to validate the deletion");
            }
        }
        bot.execute(request);
    }

    private static void deleteConfirm(Message message) {
        removeKeyboard(message);
        SendMessage request;
        if (message.text().equals("CONFIRM")) {
            rooms.remove(message.from().id());
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Votre liste a été supprimée avec succès");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list has been successfully deleted");
            }

        } else {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Erreur, refaites /delete pour réessayer");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Error, redo /delete to try again");
            }
        }
        bot.execute(request);
    }

    private static void reset(Message message) {
        removeKeyboard(message);

        SendMessage request;
        if (!rooms.containsKey(message.from().id())) {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list");
            }

        } else {
            userOnWait.add(
                    new MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "reset"));
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(message.chat().id(),
                        "Envoyez \"CONFIRM\" (en majuscule) pour valider la réinitialisation");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Send \"CONFIRM\" (in all caps) to validate the reset");
            }
        }
        bot.execute(request);
    }

    private static void resetConfirm(Message message) {
        removeKeyboard(message);
        SendMessage request;
        if (message.text().equals("CONFIRM")) {
            rooms.get(message.from().id()).clear();
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Votre liste a été réinitialisée avec succès");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list has been successfully reset");
            }

        } else {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Erreur, refaites /reset pour réessayer");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Error, redo /reset to try again");
            }
        }
        bot.execute(request);
    }

    private static void allBuildings(Message message) {
        removeKeyboard(message);


        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.equals(message.from().languageCode(), "fr")) {
            stringBuilder.append("Voici une liste de bâtiments qui proposent des salles avec des horaires publics: ");
        } else {
            stringBuilder.append("Here is a list of buildings that offer rooms with public schedules: ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("<strong>");
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

    private static void buildingStart(Message message) {
        removeKeyboard(message);

        SendMessage request;
        if (Objects.equals(message.from().languageCode(), "fr")) {
            request = new SendMessage(
                    message.chat().id(), "Veuillez choisir un bâtiment");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please choose a building");
        }
        InlineKeyboardButton[][] inlineKeyboardButton = new InlineKeyboardButton[AllBuildingList.size() / 4][4];
        int count = 0;
        for (String building : AllBuildingList) {
            inlineKeyboardButton[count >>> 2][count & 3] = new InlineKeyboardButton(building).callbackData(building);
            count++;
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(inlineKeyboardButton);

        request.replyMarkup(inlineKeyboard);

        SendResponse response = bot.execute(request);

        userOnWait.add(
                new MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "building",
                        List.of(response.message().messageId().toString())));
    }

    private static void buildingBackStart(CallbackQuery callbackQuery) {
        String messageText;
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            messageText = "Veuillez choisir un bâtiment";
        } else {
            messageText = "Please choose a building";
        }
        InlineKeyboardButton[][] inlineKeyboardButton = new InlineKeyboardButton[AllBuildingList.size() / 4][4];
        int count = 0;
        for (String building : AllBuildingList) {
            inlineKeyboardButton[count >>> 2][count & 3] = new InlineKeyboardButton(building).callbackData(building);
            count++;
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(inlineKeyboardButton);

        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .replyMarkup(inlineKeyboard)
                        .parseMode(ParseMode.Markdown);
        bot.execute(editMessageText);

        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "building",
                        List.of(callbackQuery.message().messageId().toString())));
    }

    private static void buildingMid(CallbackQuery callbackQuery, String building) {
        String messageText;
        String back;
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
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
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText).replyMarkup(
                                new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                                        {
                                                new InlineKeyboardButton("🔍")
                                                        .callbackData("HaveListOfRoom " + building),
                                                new InlineKeyboardButton("➕")
                                                        .callbackData("AddBuildingToList " + building),
                                                new InlineKeyboardButton("➖")
                                                        .callbackData("RemoveBuildingToList " + building)},
                                        {
                                                new InlineKeyboardButton(back).callbackData("Go Back")}
                                }))
                        .parseMode(ParseMode.Markdown);
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "building",
                        List.of(callbackQuery.message().messageId().toString())));
    }

    private static void HaveListOfRoom(CallbackQuery callbackQuery, String building) {
        StringBuilder stringBuilder = new StringBuilder();
        String back;
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            back = "Revenir en arrière";
            stringBuilder
                    .append("Voici une listes des salles disponible dans le bâtiment ")
                    .append(building)
                    .append(": \n")
                    .append("Vous pouvez sélectionner une salle pour avoir plus d'information");
        } else {
            back = "Go Back";
            stringBuilder
                    .append("Here is a list of the rooms available in the building ")
                    .append(building)
                    .append(": \n")
                    .append("You can select a room to get more information");
        }
        stringBuilder.append("\n");

        Map<String, String> roomSearchToRoomName = new TreeMap<>();
        validRoomData
                .stream()
                .filter(l -> l.getBuildings().equals(building))
                .forEach(l -> roomSearchToRoomName.put(l.getRooms(), l.getPlanName()));

        InlineKeyboardButton[][] inlineKeyboardButtonBig = new InlineKeyboardButton[roomSearchToRoomName.size() / 4][4];
        int count = 0;
        InlineKeyboardButton[] inlineKeyboardButtonSmall =
                new InlineKeyboardButton[roomSearchToRoomName.size() - inlineKeyboardButtonBig.length * 4];
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
            System.arraycopy(
                    inlineKeyboardButtonBig, 0,
                    inlineKeyboardButton, 0, inlineKeyboardButtonBig.length);
            inlineKeyboardButton[inlineKeyboardButtonBig.length] = inlineKeyboardButtonSmall;
            inlineKeyboardButton[inlineKeyboardButtonBig.length + 1] =
                    new InlineKeyboardButton[]{
                            new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)};
        } else {
            inlineKeyboardButton = new InlineKeyboardButton[inlineKeyboardButtonBig.length + 1][];
            System.arraycopy(
                    inlineKeyboardButtonBig, 0,
                    inlineKeyboardButton, 0, inlineKeyboardButtonBig.length);
            inlineKeyboardButton[inlineKeyboardButtonBig.length] =
                    new InlineKeyboardButton[]{
                            new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)};
        }
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(inlineKeyboardButton);

        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        stringBuilder.toString())
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

    private static void AddBuildingToList(CallbackQuery callbackQuery, String building) {
        String back;
        String success;
        String failure;
        String messageText;
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            back = "Revenir en arrière";
            success = "Les salles du bâtiment " + building + " ont été ajoutées avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            back = "Go Back";
            success = "The rooms in the building " + building + " have been successfully added to your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(callbackQuery.from().id())) {
            validRoomData.stream()
                    .filter(l -> l.getBuildings().equals(building))
                    .forEachOrdered(l -> rooms.get(callbackQuery.from().id()).add(l.getRooms()));
            messageText = success;
        } else {
            messageText = failure;
        }
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)));
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "building",
                        List.of(callbackQuery.message().messageId().toString())));
    }

    private static void RemoveBuildingToList(CallbackQuery callbackQuery, String building) {
        String back;
        String success;
        String failure;
        String messageText;
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            back = "Revenir en arrière";
            success = "Les salles du bâtiment " + building + " ont été supprimés avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            back = "Go Back";
            success = "The rooms in the building " + building + " have been successfully removed from your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(callbackQuery.from().id())) {
            validRoomData.stream()
                    .filter(l -> l.getBuildings().equals(building))
                    .forEachOrdered(l -> rooms.get(callbackQuery.from().id()).remove(l.getRooms()));
            messageText = success;
        } else {
            messageText = failure;
        }
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(back).callbackData("Go Back To buildingMid " + building)));
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "building",
                        List.of(callbackQuery.message().messageId().toString())));
    }

    private static void room(Message message) {
        removeKeyboard(message);
        userOnWait.add(
                new MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "room"));
        SendMessage request;
        if (Objects.equals(message.from().languageCode(), "fr")) {
            request = new SendMessage(
                    message.chat().id(), "Merci de donner une salle");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give a room");
        }
        bot.execute(request);
    }

    private static void roomMid(Message message, MessageData messageData) {
        String room = message.text().replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        String messageText;
        SendMessage sendMessage;
        if (AllRooms.contains(room)) {
            String validRoom = validRoomData.stream()
                    .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
            userOnWait.remove(messageData);
            if (Objects.equals(message.from().languageCode(), "fr")) {
                messageText = String.format("""
                        Veuillez sélectionner une option pour continuer :

                        📋 Voir toutes les information sur la salle %s
                        ➕ Ajouter la salle %s à votre liste
                        ➖ Supprimer la salle %s de votre liste.

                        N'hésitez pas à choisir une option""", validRoom, validRoom, validRoom);
            } else {
                messageText = String.format("""
                        Please select an option to proceed:

                        📋 View all information about the room %s
                        ➕ Add the room %s to your list
                        ➖ Remove the room %s from your list.

                        Feel free to choose an option or go back.""", validRoom, validRoom, validRoom);
            }
            sendMessage = new SendMessage(message.chat().id(), messageText);
            sendMessage.replyMarkup(new InlineKeyboardMarkup(

                    new InlineKeyboardButton("📋")
                            .callbackData("ViewRoomInfo " + room),
                    new InlineKeyboardButton("➕")
                            .callbackData("addRoom " + room),
                    new InlineKeyboardButton("➖")
                            .callbackData("removeRoom " + room)
            )).parseMode(ParseMode.Markdown);
            SendResponse response = bot.execute(sendMessage);
            userOnWait.add(
                    new MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "roomInlined",
                            List.of(response.message().messageId().toString())));
        } else {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                messageText = "Cette salle n'existe pas ou n'a pas d'horaire public\n" +
                        "/building pour plus d'info";
            } else {
                messageText = "This room does not exist or does not have a public schedule\n" +
                        "/building for more information";
            }
            sendMessage = new SendMessage(message.chat().id(), messageText);
            bot.execute(sendMessage);

        }
    }

    private static void roomMidBack(CallbackQuery callbackQuery, String room) {
        String messageText;
        EditMessageText editMessageText;
        String validRoom = validRoomData.stream()
                .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            messageText = String.format("""
                    Veuillez sélectionner une option pour continuer :

                    📋 Voir toutes les information sur la salle %s
                    ➕ Ajouter la salle %s à votre liste
                    ➖ Supprimer la salle %s de votre liste.

                    N'hésitez pas à choisir une option""", validRoom, validRoom, validRoom);
        } else {
            messageText = String.format("""
                    Please select an option to proceed:

                    📋 View all information about the room %s
                    ➕ Add the room %s to your list
                    ➖ Remove the room %s from your list.

                    Feel free to choose an option or go back.""", validRoom, validRoom, validRoom);
        }

        editMessageText = new EditMessageText(callbackQuery.message().chat().id(),
                callbackQuery.message().messageId(), messageText);
        editMessageText.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton("📋")
                        .callbackData("ViewRoomInfo " + room),
                new InlineKeyboardButton("➕")
                        .callbackData("addRoom " + room),
                new InlineKeyboardButton("➖")
                        .callbackData("removeRoom " + room)
        )).parseMode(ParseMode.Markdown);
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "roomInlined",
                        List.of(callbackQuery.message().messageId().toString())));
    }

    private static void ViewRoomInfo(CallbackQuery callbackQuery, String room, String command, String backTo) {
        StringBuilder stringBuilder = new StringBuilder();
        String back;
        String add;
        String remove;
        Optional<JsonRoomArchitecture> roomData =
                validRoomData
                        .stream().filter(l -> l.getRooms().equals(room))
                        .findFirst();

        if (roomData.isPresent()) {
            if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
                add = "Ajouter la salle";
                remove = "Supprimer la salle";
                back = "Revenir en arrière";
                stringBuilder
                        .append("<b>Information sur la salle ").append(roomData.get().getPlanName()).append("</b> :\n")
                        .append(" <b>Utilisation</b> : ").append(roomData.get().getType()).append("\n")
                        .append(" <b>Batiment</b> : ").append(roomData.get().getBuildings()).append("\n");
                if (!Objects.equals(roomData.get().getPlaces(), "")) {
                    stringBuilder.append(" <b>Places</b> : ").append(roomData.get().getPlaces()).append("\n");
                }
                stringBuilder
                        .append(" <b>Étage</b> : ").append(roomData.get().getFloor()).append("\n")
                        .append(" <b>Plan</b> : <a href=\"")
                        .append(roomData.get().getPlanLink()).append("\">lien</a>\n")
                ;
                if (!Objects.equals(roomData.get().getPdfLink(), "")) {
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
                    stringBuilder.append("| ");
                    if (i < 10) {
                        stringBuilder.append("0");
                    }
                    stringBuilder
                            .append(i)
                            .append("h - ");
                    if (i < 9) {
                        stringBuilder.append("0");
                    }
                    stringBuilder.append(i + 1)
                            .append("h |");
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
                if (!Objects.equals(roomData.get().getPlaces(), "")) {
                    stringBuilder.append(" <b>Places</b> : ").append(roomData.get().getPlaces()).append("\n");
                }
                stringBuilder
                        .append(" <b>Floor</b> : ").append(roomData.get().getFloor()).append("\n")
                        .append(" <b>Area</b> : ").append(roomData.get().getType()).append("\n")
                        .append(" <b>Plan</b> : <a href=\"")
                        .append(roomData.get().getPlanLink()).append("\">link</a>\n")
                ;
                if (!Objects.equals(roomData.get().getPdfLink(), "")) {
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
                    stringBuilder.append("| ");
                    if (i < 10) {
                        stringBuilder.append("0");
                    }
                    stringBuilder
                            .append(i)
                            .append("h - ");
                    if (i < 9) {
                        stringBuilder.append("0");
                    }
                    stringBuilder.append(i + 1)
                            .append("h |");
                    if (dataJson.get(room).getHoraire().contains(i)) {
                        stringBuilder.append("🟥| Occupied      |\n");
                    } else {
                        stringBuilder.append("🟩| Available     |\n");
                    }
                }
                stringBuilder.append("</code>");
            }

            String keyboardButtonText;

            if (command.equals("building")) {
                keyboardButtonText = backTo + roomData.get().getBuildings();
            } else if (command.equals("roomInlined")) {
                keyboardButtonText = backTo + room;
            } else {
                throw new IllegalArgumentException(
                        command + " is not a valid command for ViewRoomInfo");
            }

            EditMessageText editMessageText =
                    new EditMessageText(
                            callbackQuery.message().chat().id(),
                            callbackQuery.message().messageId(),
                            stringBuilder.toString())
                            .parseMode(ParseMode.HTML)
                            .disableWebPagePreview(true)
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton[][]{
                                            {
                                            new InlineKeyboardButton(add)
                                                    .callbackData("addRoomFromViewRoomInfo " + room),
                                            new InlineKeyboardButton(remove)
                                                    .callbackData("removeRoomFromViewRoomInfo " + room)
                                    },
                                            {
                                                    new InlineKeyboardButton(back).callbackData(keyboardButtonText)
                                            }
                                    }));
            bot.execute(editMessageText);
            userOnWait.add(
                    new MessageData(
                            callbackQuery.from().id(),
                            callbackQuery.message().date(),
                            callbackQuery.message().chat().id(), command,
                            List.of(callbackQuery.message().messageId().toString())));
        } else {
            throw new NoSuchElementException("Room" + room + " exist but data not (Weird)");
        }
    }

    private static void addRoom(CallbackQuery callbackQuery, String room, String command, String backTo) {
        String back;
        String success;
        String failure;
        String messageText;
        String validRoom = validRoomData.stream()
                .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            back = "Revenir en arrière";
            success = "La salle " + validRoom + " a été ajoutée avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            back = "Go Back";
            success = "The room " + validRoom + " has been successfully added to your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(callbackQuery.from().id())) {
            validRoomData.stream()
                    .filter(l -> l.getRooms().equals(room))
                    .forEachOrdered(l -> rooms.get(callbackQuery.from().id()).add(l.getRooms()));
            messageText = success;
        } else {
            messageText = failure;
        }
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(back).callbackData(backTo + room)));
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), command,
                        List.of(callbackQuery.message().messageId().toString())));

    }

    private static void removeRoom(CallbackQuery callbackQuery, String room, String command, String backTo) {
        String back;
        String success;
        String failure;
        String messageText;
        String validRoom = validRoomData.stream()
                .filter(l -> l.getRooms().equals(room)).toList().get(0).getPlanName();
        if (Objects.equals(callbackQuery.from().languageCode(), "fr")) {
            back = "Revenir en arrière";
            success = "La salle " + validRoom + " a été supprimée avec succès à votre liste";
            failure = "Vous n'avez pas de liste, merci d'en créer une avec /create";
        } else {
            back = "Go Back";
            success = "The rooms " + validRoom + " has been successfully removed to your list";
            failure = "You don't have a list, please create one with /create";
        }
        if (rooms.containsKey(callbackQuery.from().id())) {
            validRoomData.stream()
                    .filter(l -> l.getRooms().equals(room))
                    .forEachOrdered(l -> rooms.get(callbackQuery.from().id()).remove(l.getRooms()));
            messageText = success;
        } else {
            messageText = failure;
        }
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText)
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(back).callbackData(backTo + room)));
        bot.execute(editMessageText);
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), command,
                        List.of(callbackQuery.message().messageId().toString())));

    }

    private static void removeKeyboard(Message message) {
        List<MessageData> replyMarkup = userOnWait.stream().filter(l ->
                Objects.equals(l.UserId(), message.from().id()) &&
                        Objects.equals(l.ChatId, message.chat().id())).toList();
        if (!replyMarkup.isEmpty()) {
            replyMarkup.forEach(userOnWait::remove);
            for (MessageData messageData : replyMarkup) {
                if (!messageData.additionalProperties().isEmpty()) {
                    EditMessageReplyMarkup editMessageReplyMarkup = new
                            EditMessageReplyMarkup(messageData.ChatId(),
                            Integer.parseInt(messageData.additionalProperties().get(0)));
                    bot.execute(editMessageReplyMarkup);
                }
            }
        }
    }
}