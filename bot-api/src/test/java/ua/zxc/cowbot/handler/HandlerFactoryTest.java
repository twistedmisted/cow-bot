package ua.zxc.cowbot.handler;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import ua.zxc.cowbot.handler.types.HandlerStrategy;
import ua.zxc.cowbot.handler.types.callback.ChangeNumberPlacesHandler;
import ua.zxc.cowbot.handler.types.callback.CloseHandler;
import ua.zxc.cowbot.handler.types.callback.ConfirmPlaceChangingHandler;
import ua.zxc.cowbot.handler.types.callback.DeleteLessonHandler;
import ua.zxc.cowbot.handler.types.callback.DeleteQueueHandler;
import ua.zxc.cowbot.handler.types.callback.LeaveQueueHandler;
import ua.zxc.cowbot.handler.types.callback.RefusePlaceChangingHandler;
import ua.zxc.cowbot.handler.types.callback.ShowLessonHandler;
import ua.zxc.cowbot.handler.types.callback.ShowLessonPageHandler;
import ua.zxc.cowbot.handler.types.callback.ShowLessonSettingsHandler;
import ua.zxc.cowbot.handler.types.callback.ShowPlacesForQueueHandler;
import ua.zxc.cowbot.handler.types.callback.ShowQueueListHandler;
import ua.zxc.cowbot.handler.types.callback.ShowQueuePageHandler;
import ua.zxc.cowbot.handler.types.callback.ShowQueueSettingsHandler;
import ua.zxc.cowbot.handler.types.callback.ShowRespectPageHandler;
import ua.zxc.cowbot.handler.types.callback.TakePlaceHandler;
import ua.zxc.cowbot.handler.types.command.AddLessonHandler;
import ua.zxc.cowbot.handler.types.command.AllHandler;
import ua.zxc.cowbot.handler.types.command.CreateQueueHandler;
import ua.zxc.cowbot.handler.types.command.HelpHandler;
import ua.zxc.cowbot.handler.types.command.LessonsHandler;
import ua.zxc.cowbot.handler.types.command.NowPairsHandler;
import ua.zxc.cowbot.handler.types.command.QueuesHandler;
import ua.zxc.cowbot.handler.types.command.RegistrationHandler;
import ua.zxc.cowbot.handler.types.command.RespectsHandler;
import ua.zxc.cowbot.handler.types.command.SetGroupNameHandler;
import ua.zxc.cowbot.handler.types.command.TodayPairsHandler;
import ua.zxc.cowbot.handler.types.command.TomorrowPairsHandler;
import ua.zxc.cowbot.handler.types.other.WordsHandler;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Profile("test")
@ActiveProfiles("test")
@SpringBootTest
class HandlerFactoryTest {

    @Autowired
    private HandlerFactory handlerFactory;

    @Test
    public void getMessageHandlerWithAddLessonCommandShouldReturnAddLessonHandler() {
        handlerFactory.setRawCommand("/addlesson");

        HandlerStrategy addLessonHandler = handlerFactory.getMessageHandler();

        assertTrue(addLessonHandler instanceof AddLessonHandler);
    }

    @Test
    public void getMessageHandlerWithNotExistingCommandShouldWordsHandler() {
        handlerFactory.setRawCommand("not existing command");

        HandlerStrategy otherHandler = handlerFactory.getMessageHandler();

        assertTrue(otherHandler instanceof WordsHandler);
    }

    @Test
    public void getMessageHandlerWithAllCommandShouldReturnAllHandler() {
        handlerFactory.setRawCommand("/all");

        HandlerStrategy allHandler = handlerFactory.getMessageHandler();

        assertTrue(allHandler instanceof AllHandler);
    }

    @Test
    public void getMessageHandlerWithCreateCommandShouldReturnCreateHandler() {
        handlerFactory.setRawCommand("/create");

        HandlerStrategy createHandler = handlerFactory.getMessageHandler();

        assertTrue(createHandler instanceof CreateQueueHandler);
    }

    @Test
    public void getMessageHandlerWithHelpCommandShouldReturnHelpHandler() {
        handlerFactory.setRawCommand("/help");

        HandlerStrategy helpHandler = handlerFactory.getMessageHandler();

        assertTrue(helpHandler instanceof HelpHandler);
    }

    @Test
    public void getMessageHandlerWithLessonsCommandShouldReturnLessonsHandler() {
        handlerFactory.setRawCommand("/lessons");

        HandlerStrategy lessonsHandler = handlerFactory.getMessageHandler();

        assertTrue(lessonsHandler instanceof LessonsHandler);
    }

    @Test
    public void getMessageHandlerWithNowPairCommandShouldReturnNowPairHandler() {
        handlerFactory.setRawCommand("/now");

        HandlerStrategy nowPairHandler = handlerFactory.getMessageHandler();

        assertTrue(nowPairHandler instanceof NowPairsHandler);
    }

    @Test
    public void getMessageHandlerWithQueuesCommandShouldReturnQueuesHandler() {
        handlerFactory.setRawCommand("/queues");

        HandlerStrategy queuesHandler = handlerFactory.getMessageHandler();

        assertTrue(queuesHandler instanceof QueuesHandler);
    }

    @Test
    public void getMessageHandlerWithRegistrationCommandShouldReturnRegistrationHandler() {
        handlerFactory.setRawCommand("/registration");

        HandlerStrategy registrationHandler = handlerFactory.getMessageHandler();

        assertTrue(registrationHandler instanceof RegistrationHandler);
    }

    @Test
    public void getMessageHandlerWithReplistCommandShouldReturnReplistHandler() {
        handlerFactory.setRawCommand("/replist");

        HandlerStrategy replistHandler = handlerFactory.getMessageHandler();

        assertTrue(replistHandler instanceof RespectsHandler);
    }

    @Test
    public void getMessageHandlerWithGCommandShouldReturnSetGroupNameHandler() {
        handlerFactory.setRawCommand("/g XX-00");

        HandlerStrategy setGroupNameHandler = handlerFactory.getMessageHandler();

        assertTrue(setGroupNameHandler instanceof SetGroupNameHandler);
    }

    @Test
    public void getMessageHandlerWithTodayCommandShouldReturnTodayHandler() {
        handlerFactory.setRawCommand("/today");

        HandlerStrategy todayHandler = handlerFactory.getMessageHandler();

        assertTrue(todayHandler instanceof TodayPairsHandler);
    }

    @Test
    public void getMessageHandlerWithTomorrowCommandShouldReturnTomorrowHandler() {
        handlerFactory.setRawCommand("/tomorrow");

        HandlerStrategy tomorrowHandler = handlerFactory.getMessageHandler();

        assertTrue(tomorrowHandler instanceof TomorrowPairsHandler);
    }

    @Test
    public void getCallbackHandlerWithTakePlaceCallbackDataShouldReturnTakePlaceHandler() {
        handlerFactory.setRawCommand("1_take_place_1");

        HandlerStrategy takePlaceHandler = handlerFactory.getCallbackHandler();

        assertTrue(takePlaceHandler instanceof TakePlaceHandler);
    }

    @Test
    public void getCallbackHandlerWithChangeNumberPlacesCallbackDataShouldReturnChangeNumberPlacesHandler() {
        handlerFactory.setRawCommand("change_number_places_1");

        HandlerStrategy changeNumberPlacesHandler = handlerFactory.getCallbackHandler();

        assertTrue(changeNumberPlacesHandler instanceof ChangeNumberPlacesHandler);
    }

    @Test
    public void getCallbackHandlerWithCloseCallbackDataShouldReturnCloseHandler() {
        handlerFactory.setRawCommand("close");

        HandlerStrategy closeHandler = handlerFactory.getCallbackHandler();

        assertTrue(closeHandler instanceof CloseHandler);
    }

    @Test
    public void getCallbackHandlerWithConfirmPlaceChangingCallbackDataShouldReturnConfigPlaceChangingHandler() {
        handlerFactory.setRawCommand("confirm_place_changing_q_1_pf_1_pt_1");

        HandlerStrategy confirmPlaceChangingHandler = handlerFactory.getCallbackHandler();

        assertTrue(confirmPlaceChangingHandler instanceof ConfirmPlaceChangingHandler);
    }

    @Test
    public void getCallbackHandlerWithDeleteLessonCallbackDataShouldReturnDeleteLessonHandler() {
        handlerFactory.setRawCommand("delete_lesson_by_id_1");

        HandlerStrategy deleteLessonHandler = handlerFactory.getCallbackHandler();

        assertTrue(deleteLessonHandler instanceof DeleteLessonHandler);
    }

    @Test
    public void getCallbackHandlerWithDeleteQueueCallbackDataShouldReturnDeleteQueueHandler() {
        handlerFactory.setRawCommand("delete_queue_by_id_1");

        HandlerStrategy deleteQueueHandler = handlerFactory.getCallbackHandler();

        assertTrue(deleteQueueHandler instanceof DeleteQueueHandler);
    }

    @Test
    public void getCallbackHandlerWithLeaveQueueCallbackDataShouldReturnLeaveQueueHandler() {
        handlerFactory.setRawCommand("leave_queue_by_id_1");

        HandlerStrategy leaveQueueHandler = handlerFactory.getCallbackHandler();

        assertTrue(leaveQueueHandler instanceof LeaveQueueHandler);
    }

    @Test
    public void getCallbackHandlerWithRefusePlaceChangingCallbackDataShouldReturnRefusePlaceChangingHandler() {
        handlerFactory.setRawCommand("refuse_place_changing_q_1_pf_1_pt_1");

        HandlerStrategy refusePlaceChangingHandler = handlerFactory.getCallbackHandler();

        assertTrue(refusePlaceChangingHandler instanceof RefusePlaceChangingHandler);
    }

    @Test
    public void getCallbackHandlerWithShowLessonCallbackDataShouldReturnShowLessonHandler() {
        handlerFactory.setRawCommand("show_lesson_by_id_1");

        HandlerStrategy showLessonHandler = handlerFactory.getCallbackHandler();

        assertTrue(showLessonHandler instanceof ShowLessonHandler);
    }

    @Test
    public void getCallbackHandlerWithShowLessonPageCallbackDataShouldReturnShowLessonPageHandler() {
        handlerFactory.setRawCommand("show_lesson_page_1");

        HandlerStrategy showLessonPageHandler = handlerFactory.getCallbackHandler();

        assertTrue(showLessonPageHandler instanceof ShowLessonPageHandler);
    }

    @Test
    public void getCallbackHandlerWithShowLessonSettingsCallbackDataShouldReturnShowLessonSettingsHandler() {
        handlerFactory.setRawCommand("show_lesson_settings_by_id_1");

        HandlerStrategy showLessonSettings = handlerFactory.getCallbackHandler();

        assertTrue(showLessonSettings instanceof ShowLessonSettingsHandler);
    }

    @Test
    public void getCallbackHandlerWithShowPlacesForQueueCallbackDataShouldReturnShowPlacesForQueueHandler() {
        handlerFactory.setRawCommand("show_places_for_queue_by_id_1");

        HandlerStrategy showPlacesForQueue = handlerFactory.getCallbackHandler();

        assertTrue(showPlacesForQueue instanceof ShowPlacesForQueueHandler);
    }

    @Test
    public void getCallbackHandlerWithShowQueueListCallbackDataShouldReturnShowQueueHandler() {
        handlerFactory.setRawCommand("show_queue_places_list_by_id_");

        HandlerStrategy showQueueListHandler = handlerFactory.getCallbackHandler();

        assertTrue(showQueueListHandler instanceof ShowQueueListHandler);
    }

    @Test
    public void getCallbackHandlerWithShowQueuePageCallbackDataShouldReturnShowQueuePageHandler() {
        handlerFactory.setRawCommand("show_queue_page_1");

        HandlerStrategy showQueuePageHandler = handlerFactory.getCallbackHandler();

        assertTrue(showQueuePageHandler instanceof ShowQueuePageHandler);
    }

    @Test
    public void getCallbackHandlerWithShowQueueSettingsCallbackDataShouldReturnShowQueueSettingsHandler() {
        handlerFactory.setRawCommand("show_queue_settings_1");

        HandlerStrategy showQueueSettingsHandler = handlerFactory.getCallbackHandler();

        assertTrue(showQueueSettingsHandler instanceof ShowQueueSettingsHandler);
    }

    @Test
    public void getCallbackHandlerWithShowRespectPageCallbackDataShouldReturnShowRespectPageHandler() {
        handlerFactory.setRawCommand("show_respect_page_1");

        HandlerStrategy showRespectPageHandler = handlerFactory.getCallbackHandler();

        assertTrue(showRespectPageHandler instanceof ShowRespectPageHandler);
    }
}
