package telegramBots;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import telegramBots.commands.*;
import utils.jsonObjects.Datajson;
import utils.jsonObjects.JsonRoomArchitecture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main class for the Telegram Bot.
 * This class sets up the bot, loads the necessary data from JSON files, and sets up a listener for updates
 * from the Telegram Bot API.
 * It contains a set of static variables that are used throughout the bot operation, such as the bot token,
 * a list of all buildings and rooms, and a map of user data.
 */

public class TelegramBotForOccupancy {

    public static Set<MessageData> userOnWait;
    public static final File userOnWaitJson = new File("database/UserData/UserOnWait.json");

    public static Map<Long, Set<String>> rooms;
    public static List<JsonRoomArchitecture> validRoomData;
    public static Map<String, Datajson> dataJson;

    public static TelegramBot bot;
    public static final Set<String> AllBuilding = new TreeSet<>();
    public static final Set<String> AllRooms = new TreeSet<>();
    public static final List<String> AllBuildingList = new ArrayList<>();
    public static final File UserDataJson = new File("database/UserData/UserData.json");

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TelegramBotForOccupancy() {
    }

    /**
     * Main method for the Telegram Bot.
     */
    public static void main(String[] args) throws IOException {
        bot= new TelegramBot(args[0]);
        File validRoomDataFile = new File("database/validRoomData.json");

        TypeReference<List<JsonRoomArchitecture>> typeRefValidRoom = new TypeReference<>() {
        };
        String validRoomJson = Files.readString(validRoomDataFile.toPath());
        validRoomData = new ObjectMapper().readValue(validRoomJson, typeRefValidRoom);

        File DataJsonFile = new File("database/data.json");
        TypeReference<Map<String, Datajson>> typeRefDataJson = new TypeReference<>() {
        };
        AtomicReference<String> dataJsonString = new AtomicReference<>(Files.readString(DataJsonFile.toPath()));
        dataJson = new ObjectMapper().readValue(dataJsonString.get(), typeRefDataJson);


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

        if (Files.exists(UserDataJson.toPath())) {
            String jsonString = Files.readString(UserDataJson.toPath());
            TypeReference<HashMap<Long, Set<String>>> typeRef = new TypeReference<>() {
            };
            rooms = new ObjectMapper().readValue(jsonString, typeRef);
        } else {
            rooms = new HashMap<>();
        }

        if (Files.exists(userOnWaitJson.toPath())) {
            String jsonString = Files.readString(userOnWaitJson.toPath());
            TypeReference<Set<MessageData>> typeRef = new TypeReference<>() {
            };
            userOnWait = new ObjectMapper().readValue(jsonString, typeRef);
        } else {
            userOnWait = new HashSet<>();
        }

        // Set up a listener for updates from the Telegram Bot API.
        // Processes each update and performs actions based on the type of update.
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.callbackQuery() != null) {

                    Optional<MessageData> request = userOnWait.stream()
                            .filter(l ->
                                    Objects.equals(
                                            l.UserId(), update.callbackQuery().from().id()) &&
                                            Objects.equals(
                                                    l.ChatId(),
                                                    update.callbackQuery().maybeInaccessibleMessage().chat().id()) &&
                                            Objects.equals(
                                                    l.ThreadId(),
                                                    ((Message) update.callbackQuery()
                                                            .maybeInaccessibleMessage()).messageThreadId()) &&
                                            Objects.equals(
                                                    l.additionalProperties().get(0),
                                                    update.callbackQuery().maybeInaccessibleMessage()
                                                            .messageId().toString()))
                            .findFirst();
                    if (request.isPresent()) {
                        switch (request.get().command) {
                            case "building" -> {
                                userOnWait.remove(request.get());
                                if (AllBuilding.contains(update.callbackQuery().data())) {
                                    Building.mid(update.callbackQuery(),
                                            update.callbackQuery().data());
                                } else if ("Go Back".equals(update.callbackQuery().data())) {
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
                                    try {
                                        dataJsonString.set(Files.readString(DataJsonFile.toPath()));
                                        dataJson = new ObjectMapper().readValue(dataJsonString.get(), typeRefDataJson);
                                        Room.viewInfo(update.callbackQuery(),
                                                update.callbackQuery().data()
                                                        .replaceAll("ViewRoomInfo ", ""),
                                                "building", "HaveListOfRoom ");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

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
                                    try {
                                        dataJsonString.set(Files.readString(DataJsonFile.toPath()));
                                        dataJson = new ObjectMapper().readValue(dataJsonString.get(), typeRefDataJson);
                                        Room.viewInfo(update.callbackQuery(),
                                                update.callbackQuery().data()
                                                        .replaceAll("ViewRoomInfo ", ""),
                                                "roomInlined", "Go Back To roomMid ");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

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
                            case "search" -> {
                                userOnWait.remove(request.get());
                                if (update.callbackQuery().data().startsWith("SearchStart ")) {
                                    Search.endTime(update.callbackQuery(),
                                            Integer.parseInt(update.callbackQuery().data()
                                                    .replaceAll("SearchStart ", "")));
                                } else if (update.callbackQuery().data().startsWith("SearchMid ")) {
                                    try {
                                        dataJsonString.set(Files.readString(DataJsonFile.toPath()));
                                        dataJson = new ObjectMapper().readValue(dataJsonString.get(), typeRefDataJson);
                                        Search.searchResult(update.callbackQuery(),
                                                Integer.parseInt(request.get().additionalProperties.get(1)),
                                                Integer.parseInt(update.callbackQuery().data()
                                                        .replaceAll("SearchMid ", "")));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else if (update.callbackQuery().data().startsWith("SearchEnd ")) {
                                    try {
                                        dataJsonString.set(Files.readString(DataJsonFile.toPath()));
                                        dataJson = new ObjectMapper().readValue(dataJsonString.get(), typeRefDataJson);
                                        Search.searchResultOfBuilding(update.callbackQuery(),
                                                Integer.parseInt(request.get().additionalProperties.get(1)),
                                                Integer.parseInt(request.get().additionalProperties.get(2)),
                                                update.callbackQuery().data()
                                                        .replaceAll("SearchEnd ", ""));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                } else if (update.callbackQuery().data().startsWith("SearchResult ")) {
                                    try {
                                        dataJsonString.set(Files.readString(DataJsonFile.toPath()));
                                        dataJson = new ObjectMapper().readValue(dataJsonString.get(), typeRefDataJson);
                                        Room.viewInfo(update.callbackQuery(),
                                                update.callbackQuery().data()
                                                        .replaceAll("SearchResult ", ""),
                                                "search", "SearchEnd ");
                                        userOnWait.add(request.get());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    throw new IllegalArgumentException(
                                            update.callbackQuery().data() + " is not a valid parameter for search");
                                }
                            }
                        }
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            objectMapper.writeValue(userOnWaitJson, userOnWait);
                        } catch (IOException e) {
                            e.fillInStackTrace();
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
                            case "/addroom" -> AddRoom.command(message);
                            case "/addbuilding" -> AddBuilding.command(message);
                            case "/removeroom", "/deleteroom" -> RemoveRoom.command(message);
                            case "/removebuilding", "/deletebuilding" -> RemoveBuilding.command(message);
                            case "/gethash" -> GetHash.command(message);
                            case "/copyhash" -> CopyHash.command(message);
                            case "/mylist" -> Mylist.command(message);
                            case "/search" -> Search.command(message);
                            case "/addall" -> AddAll.command(message);
                            default -> {
                                Optional<MessageData> request =
                                        userOnWait.stream().filter(l ->
                                                        Objects.equals(l.UserId(), message.from().id()) &&
                                                                Objects.equals(l.ChatId(), message.chat().id()) &&
                                                                Objects.equals(l.ThreadId(), message.messageThreadId()))
                                                .findFirst();
                                if (request.isPresent()) {
                                    String command = request.get().command;
                                    switch (command) {
                                        case "room" -> Room.mid(message, request.get());
                                        case "reset" -> Reset.confirm(message);
                                        case "delete" -> Delete.confirm(message);
                                        case "addroom" -> AddRoom.complete(message);
                                        case "addbuilding" -> AddBuilding.complete(message);
                                        case "removeroom" -> RemoveRoom.complete(message);
                                        case "removebuilding" -> RemoveBuilding.complete(message);
                                        case "copyhash" -> CopyHash.mid(message);
                                        case "addall" -> AddAll.confirm(message);
                                        case "copyhashmid" -> CopyHash.confirm(message,
                                                Integer.parseInt(request.get().additionalProperties.get(1)));
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
            Long UserId, Integer date, Long ChatId, Integer ThreadId, String command,
            List<String> additionalProperties) {
        /**
         * Constructs of MessageData when there are no Additional Properties.
         *
         * @param UserId   The unique identifier of the user who sent the message.
         * @param date     The date when the message was sent.
         * @param ChatId   The unique identifier of the chat where the message was sent.
         * @param ThreadId The unique identifier of the Thread where the message was sent.
         * @param command  The command associated with the message.
         */
        public MessageData(Long UserId, Integer date, Long ChatId, Integer ThreadId, String command) {
            this(UserId, date, ChatId, ThreadId, command, new ArrayList<>());
        }
    }
}