package telegramBots.commands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import telegramBots.TelegramBotForOccupancy;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static telegramBots.TelegramBotForOccupancy.*;
import static telegramBots.commands.Method.removeKeyboard;
import static telegramBots.commands.Method.updateUserFile;

/**
 * This class implements the /copyhash command.
 */
public class CopyHash {

    /**
     * This class should not be instantiated.
     */
    private CopyHash() {
    }

    public static void command(Message message) {
        removeKeyboard(message);
        userOnWait.add(
                new TelegramBotForOccupancy.MessageData(
                        message.from().id(),
                        message.date(),
                        message.chat().id(), "copyhash"));
        SendMessage request;
        if (message.from().languageCode().equals("fr")) {
            request = new SendMessage(
                    message.chat().id(), "Merci de donner le hash de la liste que vous voulez copier");
        } else {
            request = new SendMessage(
                    message.chat().id(), "Please give the hash of the list you want to copy");
        }
        bot.execute(request);
    }

    public static void mid(Message message) {
        removeKeyboard(message);
        int hash = Integer.parseInt(message.text());
        SendMessage request;
        if (rooms.values().stream().noneMatch(set -> set.hashCode() == hash)) {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(), "Je n'ai trouvé aucune liste avec ce hash");
            } else {
                request = new SendMessage(
                        message.chat().id(), "I couldn't find any list with this hash");
            }

        } else {
            userOnWait.add(
                    new TelegramBotForOccupancy.MessageData(
                            message.from().id(),
                            message.date(),
                            message.chat().id(),
                            "copyhashmid",
                            List.of(String.valueOf(hash))));
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(message.chat().id(),
                        "Envoyez \"CONFIRM\" (en majuscule) pour valider la copie"
                                + "\n ⚠️Attention⚠️, cela va totalement remplacer votre liste actuelle");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Send \"CONFIRM\" (in upper case) to validate the copy"
                        + "\n ⚠️Warning⚠️, this will completely replace your current list");
            }
        }
        bot.execute(request);
    }

    public static void confirm(Message message, int hash) {
        removeKeyboard(message);
        SendMessage request;
        if (message.text().equals("CONFIRM")) {
            Set<String> set = new TreeSet<>();
            rooms.values().stream()
                    .filter(s -> s.hashCode() == hash).forEach(set::addAll);
            rooms.put(message.from().id(), set);
            updateUserFile();
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "La liste a été copiée avec succès");
            } else {
                request = new SendMessage(
                        message.chat().id(), "the has been successfully copied");
            }

        } else {
            if (message.from().languageCode().equals("fr")) {
                request = new SendMessage(
                        message.chat().id(),
                        "Copie annulée, refaites /copyhash pour vous voulez réessayer");
            } else {
                request = new SendMessage(
                        message.chat().id(), "Copy cancelled, redo /copyhash if you want to try again");
            }
        }
        bot.execute(request);
    }
}
