package TelegramBots;

import Utils.jsonObjects.JsonRoomArchitecture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.UpdatesListener;

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

    // Replace "YOUR_BOT_TOKEN" with your actual bot token
    public static TelegramBot bot = new TelegramBot("6944730251:AAFZSgdQfUYKU8r17WOBPzZSgLQ3ogH7rro");

    public static Set<String> AllBuilding = new TreeSet<>();
    public static List<String> AllBuildingList = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        File validRoomDataFile = new File("database/validRoomData.json");
        TypeReference<List<JsonRoomArchitecture>> typeRefValidRoom = new TypeReference<>() {
        };
        String validRoomJson = Files.readString(validRoomDataFile.toPath());
        validRoomData = new ObjectMapper().readValue(validRoomJson, typeRefValidRoom);

        validRoomData.forEach(rooms -> AllBuilding.add(rooms.getBuildings()));

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
                        System.out.println(userOnWait.size());
                        System.out.println(userOnWait);
                        userOnWait.remove(request.get());
                        switch (request.get().command) {
                            case "buildingStart" -> buildingMid(update.callbackQuery(),
                                    update.callbackQuery().data());
                            case "buildingMid" -> {

                                switch (update.callbackQuery().data()) {
                                    case "HaveListOfRoom" -> HaveListOfRoom(
                                            request.get()
                                                    .additionalProperties.get(0),
                                            update.callbackQuery());
                                    case "AddBuildingToList" -> AddBuildingToList(
                                            request.get()
                                                    .additionalProperties.get(0),
                                            update.callbackQuery().message());
                                    case "HaveListOfTypes" -> HaveListOfTypes(
                                            request.get().additionalProperties.get(0),
                                            update.callbackQuery().message());
                                    case "Go Back" -> buildingBackStart(update.callbackQuery());
                                    case "Go Back To buildingMid" ->buildingMid(update.callbackQuery(),
                                            request.get().additionalProperties.get(0));
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
                            default -> {
                                Optional<MessageData> request =
                                        userOnWait.stream().filter(l ->
                                                        Objects.equals(l.UserId(), message.from().id()) &&
                                                                Objects.equals(l.ChatId, message.chat().id()))
                                                .findFirst();
                                if (request.isPresent()) {
                                    //userOnWait.remove(request.get());
                                    String command = request.get().command;
                                    switch (command) {
                                        case "buildingStart" -> System.out.println("Pas Normal");
                                        case "buildingMid" -> System.out.println("Vraiment pas Normal");
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
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));
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
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));
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
            rooms.remove(message.from().id());
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Votre liste a été supprimée");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list has been deleted");
            }
        }
        bot.execute(request);
    }

    private static void reset(Message message) {
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));
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
            rooms.get(message.from().id()).clear();
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Votre liste a été réinitialisée");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Your list has been reset");
            }
        }
        bot.execute(request);
    }

    private static void allBuildings(Message message) {
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));

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
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));
        userOnWait.add(
                new MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "buildingStart"));
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
        bot.execute(request);
    }

    private static void buildingBackStart(CallbackQuery callbackQuery) {
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "buildingStart"));
        String messageText;
        if (Objects.equals(callbackQuery.message().from().languageCode(), "fr")) {
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
    }

    private static void buildingMid(CallbackQuery callbackQuery, String building) {
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "buildingMid", List.of(building)));
        String messageText;
        String back;
        if (Objects.equals(callbackQuery.message().from().languageCode(), "fr")) {
            back = "revenir en arrière";
            messageText = """
                    Veuillez sélectionner une option pour continuer :

                    🔍 Voir toutes les salles avec un horaire public dans ce bâtiment
                    ➕ Ajouter toutes les salles de ce bâtiment à votre liste
                    🗄️ Voir toutes les catégories de salles dans ce bâtiment.

                    N'hésitez pas à choisir une option ou à revenir en arrière.""";
        } else {
            back = "Go Back";
            messageText = """
                    Please select an option to proceed:

                    🔍 View all rooms with a public schedule in this building
                    ➕ Add all the rooms in this building to your list
                    🗄️ View all category of rooms in this building.

                    Feel free to choose an option or go back.""";
        }
        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        messageText).replyMarkup(
                                new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                                        {
                                                new InlineKeyboardButton("🔍").callbackData("HaveListOfRoom"),
                                                new InlineKeyboardButton("➕").callbackData("AddBuildingToList"),
                                                new InlineKeyboardButton("🗄️").callbackData("HaveListOfTypes")},
                                        {
                                                new InlineKeyboardButton(back).callbackData("Go Back")}
                                }))
                        .parseMode(ParseMode.Markdown);
        bot.execute(editMessageText);
    }

    private static void HaveListOfRoom(String building, CallbackQuery callbackQuery) {
        userOnWait.add(
                new MessageData(
                        callbackQuery.from().id(),
                        callbackQuery.message().date(),
                        callbackQuery.message().chat().id(), "buildingMid", List.of(building)));
        StringBuilder stringBuilder = new StringBuilder();
        String back;
        if (Objects.equals(callbackQuery.message().from().languageCode(), "fr")) {
            back = "revenir en arrière";
            stringBuilder.append("Voici une listes des salles disponible dans ce bâtiment: \n");
        } else {
            back = "Go Back";
            stringBuilder.append("Here is a list of the rooms available in this building: \n");
        }
        stringBuilder.append("\n");
        stringBuilder.append("<strong>");
        List<String> roomsOfThatBuilding = new ArrayList<>();
        validRoomData
                .stream()
                .filter(l -> l.getBuildings().equals(building))
                .forEach(l -> roomsOfThatBuilding.add(l.getPlanName()));
        Collections.sort(roomsOfThatBuilding);
        roomsOfThatBuilding.forEach(l -> stringBuilder.append(l).append("\n"));
        stringBuilder.append("</strong>");
        if (Objects.equals(callbackQuery.message().from().languageCode(), "fr")) {
            stringBuilder.append("/room to get information about a certain room");
        } else {
            stringBuilder.append("/room pour avoir des informations sur une certaine salle");
        }

        EditMessageText editMessageText =
                new EditMessageText(
                        callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(),
                        stringBuilder.toString())
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(back).callbackData("Go Back To buildingMid")));
        bot.execute(editMessageText);
    }

    private static void AddBuildingToList(String Room, Message message) {
        System.out.println(Room + message);
    }

    private static void HaveListOfTypes(String Room, Message message) {
        System.out.println(Room + message);
    }

}
