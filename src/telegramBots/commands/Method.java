package telegramBots.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;

import java.io.IOException;
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
        userOnWait.removeIf(messageData -> {
            boolean shouldRemove =
                    Objects.equals(messageData.UserId(), message.from().id()) &&
                            Objects.equals(messageData.ChatId(), message.chat().id());
            if (shouldRemove && !messageData.additionalProperties().isEmpty()) {
                if (!Objects.equals(messageData.additionalProperties().get(0), "")) {
                    EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(
                            messageData.ChatId(),
                            Integer.parseInt(messageData.additionalProperties().get(0))
                    );
                    bot.execute(editMessageReplyMarkup);
                }
            }
            return shouldRemove;
        });
    }

    /**
     * This method is used to update the user's file.
     * It is used to avoid code duplication.
     */
    public static void updateUserFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(UserDataJson, rooms);
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
