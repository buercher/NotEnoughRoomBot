package telegramBots;

import telegramBots.commands.*;
import utils.jsonObjects.Datajson;
import utils.jsonObjects.JsonRoomArchitecture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.UpdatesListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TelegramBotForOccupancy {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TelegramBotForOccupancy() {
    }

    public static final Set<MessageData> userOnWait = new HashSet<>();

    /**
     * A record that encapsulates the data related to a message.
     *
     * @param UserId               The unique identifier of the user who sent the message.
     * @param date                 The date when the message was sent.
     * @param ChatId               The unique identifier of the chat where the message was sent.
     * @param command              The command associated with the message.
     * @param additionalProperties A list of additional properties related to the message and/or command.
     */
    public record MessageData(
            Long UserId, Integer date, Long ChatId, String command, List<String> additionalProperties) {
        /**
         * Constructs of MessageData when there are no Additional Properties.
         *
         * @param UserId  The unique identifier of the user who sent the message.
         * @param date    The date when the message was sent.
         * @param ChatId  The unique identifier of the chat where the message was sent.
         * @param command The command associated with the message.
         */
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


    /**
     * Main method for the Telegram Bot.
     */
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

        // Set up a listener for updates from the Telegram Bot API.
        // Processes each update and performs actions based on the type of update.
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.callbackQuery() != null) {

                    Optional<MessageData> request = userOnWait.stream()
                            .filter(l ->
                                    Objects.equals(l.UserId(), update.callbackQuery().from().id()) &&
                                            Objects.equals(l.ChatId, update.callbackQuery().message().chat().id()) &&
                                            l.additionalProperties.get(0).equals(
                                                    update.callbackQuery().message().messageId().toString()))
                            .findFirst();
                    if (request.isPresent()) {
                        switch (request.get().command) {
                            case "building" -> {
                                userOnWait.remove(request.get());
                                if (AllBuilding.contains(update.callbackQuery().data())) {
                                    Building.mid(update.callbackQuery(),
                                            update.callbackQuery().data());
                                } else if (Objects.equals(update.callbackQuery().data(), "Go Back")) {
                                    Building.backToStart(update.callbackQuery());

                                } else if (update.callbackQuery().data().startsWith("Go Back To buildingMid ")) {
                                    Building.mid(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("Go Back To buildingMid ", ""));

                                } else if (update.callbackQuery().data().startsWith("HaveListOfRoom ")) {
                                    Building.haveListOfRoom(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("HaveListOfRoom ", ""));

                                } else if (update.callbackQuery().data().startsWith("AddBuildingToList ")) {
                                    Building.addToList(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("AddBuildingToList ", ""));

                                } else if (update.callbackQuery().data().startsWith("RemoveBuildingToList ")) {
                                    Building.removeFromList(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("RemoveBuildingToList ", ""));

                                } else if (update.callbackQuery().data().startsWith("ViewRoomInfo ")) {
                                    Room.viewInfo(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("ViewRoomInfo ", ""),
                                            "building", "HaveListOfRoom ");

                                } else if (update.callbackQuery().data().startsWith("addRoomFromViewRoomInfo ")) {
                                    Room.add(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("addRoomFromViewRoomInfo ", ""),
                                            "building", "ViewRoomInfo ");
                                } else if (update.callbackQuery().data().startsWith("removeRoomFromViewRoomInfo ")) {
                                    Room.remove(update.callbackQuery(),
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
                                    Room.viewInfo(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("ViewRoomInfo ", ""),
                                            "roomInlined", "Go Back To roomMid ");

                                } else if (update.callbackQuery().data().startsWith("Go Back To roomMid ")) {
                                    Room.backToMid(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("Go Back To roomMid ", ""));
                                } else if (update.callbackQuery().data().startsWith("addRoom ")) {
                                    Room.add(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("addRoom ", ""),
                                            "roomInlined", "Go Back To roomMid ");
                                } else if (update.callbackQuery().data().startsWith("removeRoom ")) {
                                    Room.remove(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("removeRoom ", ""),
                                            "roomInlined", "Go Back To roomMid ");
                                } else if (update.callbackQuery().data().startsWith("addRoomFromViewRoomInfo ")) {
                                    Room.add(update.callbackQuery(),
                                            update.callbackQuery().data()
                                                    .replaceAll("addRoomFromViewRoomInfo ", ""),
                                            "roomInlined", "ViewRoomInfo ");
                                } else if (update.callbackQuery().data().startsWith("removeRoomFromViewRoomInfo ")) {
                                    Room.remove(update.callbackQuery(),
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
                            case "/create" -> Create.command(message);
                            case "/delete" -> Delete.command(message);
                            case "/reset" -> Reset.command(message);
                            case "/allbuildings" -> AllBuildings.command(message);
                            case "/building" -> Building.command(message);
                            case "/room" -> Room.command(message);
                            default -> {
                                Optional<MessageData> request =
                                        userOnWait.stream().filter(l ->
                                                        Objects.equals(l.UserId(), message.from().id()) &&
                                                                Objects.equals(l.ChatId, message.chat().id()))
                                                .findFirst();
                                if (request.isPresent()) {
                                    String command = request.get().command;
                                    switch (command) {
                                        case "room" -> Room.mid(message, request.get());
                                        case "reset" -> Reset.confirm(message);
                                        case "delete" -> Delete.confirm(message);
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
}