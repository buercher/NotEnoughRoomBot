package telegramBots.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import telegramBots.TelegramBotForOccupancy;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static telegramBots.TelegramBotForOccupancy.*;

/**
 * This class contains methods that are used by multiple commands.
 * It is used to avoid code duplication.
 */
public class Method {

    /**
     * This constructor is private to avoid instantiation of the class.
     */
    private Method() {
    }

    /**
     * This method is used to remove the inline keyboard from the user's chat.
     * It iterates over the userOnWait set to find any messages from the user that have an inline keyboard.
     * If such messages are found, it removes them from the userOnWait set
     * and edits the messages to remove the inline keyboard.
     * It is used to avoid the user using multiple inline keyboards at the same time
     * and clean up the chat since some inline keyboards can be quite long.
     *
     * @param message The message received from the user. It contains the user's ID and chat ID.
     */
    public static void removeKeyboard(Message message) {
        List<TelegramBotForOccupancy.MessageData> replyMarkup = userOnWait.stream().filter(l ->
                Objects.equals(l.UserId(), message.from().id()) &&
                        Objects.equals(l.ChatId(), message.chat().id())).toList();
        if (!replyMarkup.isEmpty()) {
            replyMarkup.forEach(userOnWait::remove);
            for (TelegramBotForOccupancy.MessageData messageData : replyMarkup) {
                if (!messageData.additionalProperties().isEmpty()) {
                    EditMessageReplyMarkup editMessageReplyMarkup = new
                            EditMessageReplyMarkup(messageData.ChatId(),
                            Integer.parseInt(messageData.additionalProperties().get(0)));
                    bot.execute(editMessageReplyMarkup);
                }
            }
        }
    }

    public static void updateUserFile(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(UserDataJson, rooms);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
