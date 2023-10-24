package ua.zxc.cowbot.helper;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.UUID;

public class TelegramTestHelper {

    private static final Long CHAT_ID = 1111L;
    private static final Long USER_ID = 1111L;
    private static final String TITLE = "Test chat title";
    private static final String FIRST_NAME = "Test first name";
    private static final String USER_NAME = "testusername";
    private static final String CALLBACK_DATA = "callback_data";

    public static Chat createGroupChat() {
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        chat.setTitle(TITLE);
        return chat;
    }

    public static Chat createGroupChatWithId(Long id) {
        Chat chat = new Chat();
        chat.setId(id);
        chat.setTitle(TITLE);
        return chat;
    }

    public static Chat createUserChat() {
        Chat chat = new Chat();
        chat.setId(CHAT_ID);
        chat.setFirstName(FIRST_NAME);
        chat.setUserName(USER_NAME);
        return chat;
    }

    public static User createUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setUserName(USER_NAME);
        user.setFirstName(FIRST_NAME);
        return user;
    }
    public static User createUserWitId(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setUserName(USER_NAME);
        user.setFirstName(FIRST_NAME);
        return user;
    }


    public static Message createMessageFromGroupChatWithMessageText(String text) {
        Message message = new Message();
        message.setChat(createGroupChat());
        message.setText(text);
        message.setFrom(createUser());
        return message;
    }

    public static Message createMessageFromGroupChatWithMessageText(Long chatId, String text) {
        Message message = new Message();
        message.setChat(createGroupChatWithId(chatId));
        message.setText(text);
        message.setFrom(createUser());
        return message;
    }

    public static Message createMessageFromGroupChatWithChatId(Long chatId) {
        Message message = new Message();
        message.setChat(createGroupChatWithId(chatId));
        message.setFrom(createUser());
        return message;
    }

    public static Message createMessageFromUserChat(String text) {
        Message message = new Message();
        message.setChat(createUserChat());
        message.setText(text);
        message.setFrom(createUser());
        return message;
    }

    public static Message createMessageFromGroupChat() {
        Message message = new Message();
        message.setChat(createUserChat());
        message.setFrom(createUser());
        return message;
    }

    public static Update createMessageUpdateFromGroupChatWithMessageText(String messageText) {
        Update update = new Update();
        update.setMessage(createMessageFromGroupChatWithMessageText(messageText));
        return update;
    }

    public static Update createMessageUpdateFromGroupChatWithMessageText(Long chatId, String messageText) {
        Update update = new Update();
        update.setMessage(createMessageFromGroupChatWithMessageText(chatId, messageText));
        return update;
    }

    public static Update createMessageUpdateFromGroupChatWithMessageText(Long chatId) {
        Update update = new Update();
        update.setMessage(createMessageFromGroupChatWithChatId(chatId));
        return update;
    }

    public static Update createMessageUpdateFromUserChatWithMessageText(String messageText) {
        Update update = new Update();
        update.setMessage(createMessageFromUserChat(messageText));
        return update;
    }

    public static Update createMessageUpdateFromGroupNameWithChatId(Long chatId) {
        Update update = new Update();
        update.setMessage(createMessageFromGroupChatWithChatId(chatId));
        return update;
    }

    public static Update createMessageUpdateFromGroupNameWithChatIdAndUserId(Long chatId, Long userId) {
        Update update = new Update();
        update.setMessage(createMessageFromGroupChatWithChatId(chatId, userId));
        return update;
    }

    private static Message createMessageFromGroupChatWithChatId(Long chatId, Long userId) {
        Message message = new Message();
        message.setChat(createGroupChatWithId(chatId));
        message.setFrom(createUserWitId(userId));
        return message;
    }

    public static Update createMessageUpdateFromGroupChat() {
        Update update = new Update();
        update.setMessage(createMessageFromGroupChat());
        return update;
    }

    public static Update createCallbackUpdateFromGroupChat() {
        Update update = new Update();
        update.setCallbackQuery(createCallbackQueryFromGroupChat());
        return update;
    }

    private static CallbackQuery createCallbackQueryFromGroupChat() {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId(String.valueOf(UUID.randomUUID()));
        callbackQuery.setFrom(createUser());
        callbackQuery.setData(CALLBACK_DATA);
        callbackQuery.setMessage(createMessageFromGroupChat());
        return callbackQuery;
    }

    public static Update createCallbackUpdateFromGroupChatWithMessageText(String callbackData) {
        Update update = new Update();
        update.setCallbackQuery(createCallbackQueryFromGroupChat(callbackData));
        return update;
    }

    public static Update createCallbackUpdateFromGroupChatWithMessageText(Long chatId, String callbackData) {
        Update update = new Update();
        update.setCallbackQuery(createCallbackQueryFromGroupChat(chatId, callbackData));
        return update;
    }

    private static CallbackQuery createCallbackQueryFromGroupChat(Long chatId, String callbackData) {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId(String.valueOf(UUID.randomUUID()));
        callbackQuery.setFrom(createUser());
        callbackQuery.setData(callbackData);
        callbackQuery.setMessage(createMessageFromGroupChatWithChatId(chatId));
        return callbackQuery;
    }

    public static Update createCallbackUpdateFromGroupChat(Long chatId, Long userId, String callbackData) {
        Update update = new Update();
        update.setCallbackQuery(createCallbackQueryFromGroupChat(chatId, userId, callbackData));
        return update;
    }

    private static CallbackQuery createCallbackQueryFromGroupChat(Long chatId, Long userId, String callbackData) {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId(String.valueOf(UUID.randomUUID()));
        callbackQuery.setFrom(createUserWitId(userId));
        callbackQuery.setData(callbackData);
        callbackQuery.setMessage(createMessageFromGroupChatWithChatId(chatId));
        return callbackQuery;
    }


    private static CallbackQuery createCallbackQueryFromGroupChat(String callbackData) {
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setId(String.valueOf(UUID.randomUUID()));
        callbackQuery.setFrom(createUser());
        callbackQuery.setData(callbackData);
        callbackQuery.setMessage(createMessageFromGroupChat());
        return callbackQuery;
    }
}
