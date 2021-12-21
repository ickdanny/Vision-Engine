package internalconfig.game;

import internalconfig.game.systems.GameConfigObject;
import util.messaging.Topic;

public final class GlobalTopics {

    public static final Topic<String> TOP_LEVEL_SLICES;
    public static final Topic<GameConfigObject> GAME_CONFIG_OBJECT;
    public static final Topic<String> DIALOGUE_CODE;
    public static final Topic<Void> DIALOGUE_OVER;
    public static final Topic<Void> CONTINUE_SCREEN_GAME_OVER;
    public static final Topic<Void> RETURN_TO_MENU;
    public static final Topic<Void> GAME_BEATEN;

    public static final Topic<?>[] GLOBAL_TOPICS = {
            TOP_LEVEL_SLICES =  new Topic<>(),
            GAME_CONFIG_OBJECT = new Topic<>(),
            DIALOGUE_CODE = new Topic<>(),
            DIALOGUE_OVER = new Topic<>(),
            CONTINUE_SCREEN_GAME_OVER = new Topic<>(),
            RETURN_TO_MENU = new Topic<>(),
            GAME_BEATEN = new Topic<>(),
    };
}