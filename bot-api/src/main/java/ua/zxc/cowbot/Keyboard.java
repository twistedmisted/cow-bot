package ua.zxc.cowbot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.zxc.cowbot.bo.PageBO;
import ua.zxc.cowbot.dto.LessonDTO;
import ua.zxc.cowbot.dto.PlaceDTO;
import ua.zxc.cowbot.dto.QueueDTO;
import ua.zxc.cowbot.dto.RespectDTO;
import ua.zxc.cowbot.utils.Emoji;

import java.util.ArrayList;
import java.util.List;


public class Keyboard {

    private Keyboard() {
    }

    public static InlineKeyboardMarkup createKeyboardForQueue(QueueDTO queue) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        addKeyboard(keyboardMarkup, queue);
        return keyboardMarkup;
    }

    private static void addKeyboard(InlineKeyboardMarkup inlineKeyboardMarkup, QueueDTO queue) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        addPlaces(queue, keyboard);
        addControlButtons(keyboard, queue.getId());
        inlineKeyboardMarkup.setKeyboard(keyboard);
    }

    private static void addPlaces(QueueDTO queue, List<List<InlineKeyboardButton>> keyboard) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (int i = 0; i < queue.getSize(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            int placeNumber = i + 1;
            if (!isPlaceFree(queue.getPlaces(), placeNumber)) {
                button.setText(Emoji.WHITE_CHECK_MARK.toString());
            } else {
                button.setText(String.valueOf(placeNumber));
            }
            button.setCallbackData(queue.getId() + "_take_place_" + placeNumber);
            row.add(button);
            if ((i + 1) % 5 == 0 || i + 1 == queue.getSize()) {
                keyboard.add(row);
                row = new ArrayList<>();
            }
        }
    }

    private static void addControlButtons(List<List<InlineKeyboardButton>> keyboard, long queueId) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonWithCallbackData("Вивести список", "show_queue_places_list_by_id_" + queueId));
        row.add(createButtonWithCallbackData("Покинути чергу", "leave_queue_by_id_" + queueId));
        row.add(createButtonWithCallbackData(Emoji.GEAR.toString(), "show_queue_settings_" + queueId));
        keyboard.add(row);
    }

    private static boolean isPlaceFree(List<PlaceDTO> places, int placeNumber) {
        for (PlaceDTO place : places) {
            if (place.getNumber() == placeNumber) {
                return false;
            }
        }
        return true;
    }

    public static InlineKeyboardMarkup createKeyboardForLessons(PageBO<LessonDTO> page) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row;
        for (LessonDTO lessonDTO : page.getContent()) {
            row = new ArrayList<>();
            row.add(createButtonWithCallbackData(lessonDTO.getName(), "show_lesson_by_id_" + lessonDTO.getId()));
            keyboard.add(row);
        }
        row = new ArrayList<>();
        int number = page.getCurrentPageNumber();
        if (number > 0) {
            row.add(createButtonWithCallbackData(Emoji.ARROW_LEFT.toString(), "show_lesson_page_" + (number - 1)));
        }
        row.add(createButtonWithCallbackData((number + 1) + "/" + page.getTotalPages(), "null"));
        if (number + 1 != page.getTotalPages()) {
            row.add(createButtonWithCallbackData(Emoji.ARROW_RIGHT.toString(), "show_lesson_page_" + (number + 1)));
        }
        keyboard.add(row);
        row = new ArrayList<>();
        row.add(getCloseButton());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForQueues(PageBO<QueueDTO> page) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row;
        for (QueueDTO queueDTO : page.getContent()) {
            row = new ArrayList<>();
            row.add(createButtonWithCallbackData(queueDTO.getName(), "show_places_for_queue_by_id_" + queueDTO.getId()));
            keyboard.add(row);
        }
        row = new ArrayList<>();
        int number = page.getCurrentPageNumber();
        if (number > 0) {
            row.add(createButtonWithCallbackData(Emoji.ARROW_LEFT.toString(), "show_queue_page_" + (number - 1)));
        }
        row.add(createButtonWithCallbackData((page.getCurrentPageNumber() + 1) + "/" + page.getTotalPages(), "null"));
        if (number + 1 != page.getTotalPages()) {
            row.add(createButtonWithCallbackData(Emoji.ARROW_RIGHT.toString(), "show_queue_page_" + (number + 1)));
        }
        keyboard.add(row);
        row = new ArrayList<>();
        row.add(getCloseButton());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForLesson(LessonDTO lesson) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonWithUrl("Посилання на пару", lesson.getUrl()));
        keyboard.add(row);

        row = new ArrayList<>();
        row.add(getCloseButton());
        row.add(createButtonWithCallbackData(Emoji.GEAR.toString(), "show_lesson_settings_by_id_" + lesson.getId()));
        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForLessonSettings(long lessonId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonWithCallbackData("Редагувати предмет", "edit_lesson_" + lessonId));
        row.add(createButtonWithCallbackData("Видалити предмет", "delete_lesson_by_id_" + lessonId));
        keyboard.add(row);

        row = new ArrayList<>();
        row.add(createButtonWithCallbackData("Назад", "show_lesson_by_id_" + lessonId));
        row.add(getCloseButton());
        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;

    }

    public static InlineKeyboardMarkup createKeyboardForRespectsList(PageBO<RespectDTO> respectsPage) {
        int number = respectsPage.getCurrentPageNumber();
        int totalPages = respectsPage.getTotalPages();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        if (number > 0) {
            row.add(createButtonWithCallbackData(Emoji.ARROW_LEFT.toString(), "show_respect_page_" + (number - 1)));
        }
        row.add(createButtonWithCallbackData((number + 1) + "/" + totalPages, "null"));
        if (number + 1 != totalPages) {
            row.add(createButtonWithCallbackData(Emoji.ARROW_RIGHT.toString(), "show_respect_page_" + (number + 1)));
        }
        keyboard.add(row);
        row = new ArrayList<>();
        row.add(getCloseButton());
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForChangePlace(PlaceDTO placeFrom, PlaceDTO placeTo, long queueId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        String data = "q_" + queueId + "_pf_" + placeFrom.getUser().getId() + "_pt_" + placeTo.getUser().getId();
        row.add(createButtonWithCallbackData("Підтвердити", "confirm_place_changing_" + data));
        row.add(createButtonWithCallbackData("Відхилити", "refuse_place_changing_" + data));
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForQueueSettings(long queueId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonWithCallbackData("Змінити кількість місць", "change_number_places_" + queueId));
        row.add(createButtonWithCallbackData("Видалити чергу", "delete_queue_by_id_" + queueId));
        keyboard.add(row);

        row = new ArrayList<>();
        row.add(createButtonWithCallbackData("Назад", "show_places_for_queue_by_id_" + queueId));
        row.add(getCloseButton());
        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForAddLessonMessage() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonWithUrl("Додати предмет", "https://mania-website.herokuapp.com"));
        keyboard.add(row);

        row = new ArrayList<>();
        row.add(getCloseButton());
        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup createKeyboardForHelpMessage() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButtonWithUrl("Відкрити гайд", "https://telegra.ph/Gajd-po-Manі-01-24"));
        keyboard.add(row);

        row = new ArrayList<>();
        row.add(getCloseButton());
        keyboard.add(row);

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private static InlineKeyboardButton getCloseButton() {
        return createButtonWithCallbackData("Закрити", "close");
    }

    private static InlineKeyboardButton createButtonWithCallbackData(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private static InlineKeyboardButton createButtonWithUrl(String text, String url) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        return button;
    }

}
