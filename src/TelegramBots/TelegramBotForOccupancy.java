package TelegramBots;

import Utils.jsonObjects.JsonRoomArchitecture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
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
    public static TelegramBot bot = new TelegramBot("");

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

        File UserDatajson = new File("database/UserData/rooms.json");
        if (Files.exists(UserDatajson.toPath())) {
            String jsonString = Files.readString(UserDatajson.toPath());
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
                        userOnWait.remove(request.get());
                        switch (request.get().command) {
                            case "buildingStart"-> {}
                            case "buildingMid" -> {
                                switch (update.callbackQuery().data()) {
                                    case "HaveListOfRoom" -> HaveListOfRoom(
                                            request.get()
                                                    .additionalProperties.get(0),
                                            update.callbackQuery().message());
                                    case "AddBuildingToList" -> AddBuildingToList(
                                            request.get()
                                                    .additionalProperties.get(0),
                                            update.callbackQuery().message());
                                    case "HaveListOfTypes" -> HaveListOfTypes(
                                            request.get().additionalProperties.get(0),
                                            update.callbackQuery().message());
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
                            case "/batiment" -> batiment(message);
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
                                    userOnWait.remove(request.get());
                                    String command = request.get().command;
                                    switch (command) {
                                        case "buildingStart" -> buildingMid(message, receivedText);
                                        case "buildingMid" -> System.out.println("buildingMid");
                                        default -> System.out.println("default");
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

    private static void batiment(Message message) {

        long chatId = message.chat().id();
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));
        userOnWait.add(
                new MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "batiment"));
        SendMessage request = new SendMessage(chatId,
                "Please choose an option to proceed:\n\n" +
                        "1️⃣ View all the available rooms in this building\n" +
                        "2️⃣ Add all the rooms in this building to the list\n" +
                        "3️⃣ View a list of all the types of rooms in this building\n\n" +
                        "Feel free to choose an option by replying with the corresponding number " +
                        "or type /help for more information.");
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton("1️⃣").callbackData("HaveListOfRoom"),
                new InlineKeyboardButton("2️⃣").callbackData("AddBuildingToList"),
                new InlineKeyboardButton("3️⃣!").callbackData("HaveListOfTypes"));
        request.replyMarkup(inlineKeyboard);
        request.parseMode(ParseMode.Markdown);
        bot.execute(request);
    }

    private static void create(Message message) {
        userOnWait.removeIf(messageData ->
                Objects.equals(messageData.UserId(), message.from().id()) &&
                        Objects.equals(messageData.ChatId(), message.chat().id()));
        SendMessage request;
        if (rooms.containsKey(message.from().id())) {
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(), "Vous avez déjà créé votre liste de salles");
            } else {
                request = new SendMessage(
                        message.chat().id(), "You have already created your list of rooms");
            }

        } else {
            rooms.put(message.from().id(), new TreeSet<>());
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Liste de salles crées, vous pouvez maintenant ajouter les salles avec /add");
            } else {
                request = new SendMessage(
                        message.chat().id(), "List of rooms created, you can now add rooms with /add");
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
                        "Liste supprimée");
            } else {
                request = new SendMessage(
                        message.chat().id(), "List deleted");
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
                        "Liste reset");
            } else {
                request = new SendMessage(
                        message.chat().id(), "List rested");
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
            stringBuilder.append("Voici une listes des bâtiments qui ont au moins une salle avec un horaire publique: ");
        } else {
            stringBuilder.append("Here's a list of all buildings that has a room with a public schedule: ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("<strong>");
        AllBuilding.forEach(l -> stringBuilder.append(l).append(" "));
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "))
                .append("\n</strong>");
        if (Objects.equals(message.from().languageCode(), "fr")) {
            stringBuilder.append("/building to get information about a certain building");
        } else {
            stringBuilder.append("/building pour avoir des informations sur un certains batiment");
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
                    message.chat().id(), "Merci de donner le nom du batiment");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give the name of the building");
        }
        InlineKeyboardButton[][] inlineKeyboardButton = new InlineKeyboardButton[9][4];


        int count = 0;
        int count1 = 0;
        for (String building : AllBuildingList) {
            if (count == 4) {
                count = 0;
                count1++;
            }
            inlineKeyboardButton[count1][count] = new InlineKeyboardButton(building).callbackData(building);
            count++;
        }
        InlineKeyboardMarkup inlineKeyboard =
                new InlineKeyboardMarkup(inlineKeyboardButton);

        request.replyMarkup(inlineKeyboard);
        bot.execute(request);
    }

    private static void buildingMid(Message message, String receivedText) {
        SendMessage request;
        if (AllBuilding.contains(receivedText.toUpperCase())) {
            userOnWait.add(
                    new MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "buildingMid", List.of(receivedText.toUpperCase())));
            request = new SendMessage(message.chat().id(),
                    "Please choose an option to proceed:\n\n" +
                            "1️⃣ View all the available rooms in this building\n" +
                            "2️⃣ Add all the rooms in this building to the list\n" +
                            "3️⃣ View a list of all the types of rooms in this building\n\n" +
                            "Feel free to choose an option by replying with the corresponding number " +
                            "or type /help for more information.");
            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("1️⃣").callbackData("HaveListOfRoom"),
                    new InlineKeyboardButton("2️⃣").callbackData("AddBuildingToList"),
                    new InlineKeyboardButton("3️⃣").callbackData("HaveListOfTypes"));
            request.replyMarkup(inlineKeyboard);
            request.parseMode(ParseMode.Markdown);
        } else {
            userOnWait.add(
                    new MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(), "buildingStart"));
            if (Objects.equals(message.from().languageCode(), "fr")) {
                request = new SendMessage(
                        message.chat().id(), "Nom de bâtiment introuvable" +
                        "vous pouvez trouver tous les bâtiments disponibles avec /allbuildings");
            } else {
                request = new SendMessage(
                        message.chat().id(),
                        "Couldn't find any building by that name, " +
                                "you can find all the available buildings with /allbuildings");
            }
        }
        bot.execute(request);
    }

    private static void HaveListOfRoom(String Building, Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        if (Objects.equals(message.from().languageCode(), "fr")) {
            stringBuilder.append("Voici une listes des salles disponible dans ce bâtiment: ");
        } else {
            stringBuilder.append("Here is a list of the rooms available in this building: ");
        }
        stringBuilder.append("\n");
        stringBuilder.append("<strong>");
        List<String> roomsOfThatBuilding = new ArrayList<>();
        validRoomData
                .stream()
                .filter(l -> l.getBuildings().equals(Building))
                .forEach(l -> roomsOfThatBuilding.add(l.getPlanName()));
        Collections.sort(roomsOfThatBuilding);
        roomsOfThatBuilding.forEach(l -> stringBuilder.append(l).append("\n"));
        stringBuilder.append("</strong>");
        if (Objects.equals(message.from().languageCode(), "fr")) {
            stringBuilder.append("/room to get information about a certain room");
        } else {
            stringBuilder.append("/room pour avoir des informations sur une certaine salle");
        }

        EditMessageText editMessageText =
                new EditMessageText(
                        message.chat().id(),
                        message.messageId(),
                        stringBuilder.toString())
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true);
        bot.execute(editMessageText);
    }

    private static void AddBuildingToList(String Room, Message message) {
    }

    private static void HaveListOfTypes(String Room, Message message) {
    }

}
