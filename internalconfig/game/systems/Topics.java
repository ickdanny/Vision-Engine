package internalconfig.game.systems;

import ecs.entity.EntityHandle;
import internalconfig.game.components.MenuCommands;
import internalconfig.game.systems.gamesystems.GameCommands;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;
import internalconfig.game.systems.graphicssystems.DrawCommand;
import internalconfig.game.systems.menusystems.ButtonSelectionMessage;
import internalconfig.game.systems.menusystems.MenuNavigationCommands;
import util.datastructure.ArrayUtil;
import util.messaging.Topic;
import util.tuple.Tuple2;

public class Topics {

    //SHARED TOPICS
    public static final Topic<DrawCommand> DRAW_COMMANDS;
    public static final Topic<String> MUSIC;

    public static final Topic<?>[] SHARED_TOPICS = new Topic[]{
            DRAW_COMMANDS = new Topic<>(),
            MUSIC = new Topic<>(),
    };

    //MENU TOPICS
    public static final Topic<MenuNavigationCommands> MENU_NAVIGATION_COMMANDS;
    public static final Topic<ButtonSelectionMessage> BUTTON_SELECTION;
    public static final Topic<Tuple2<EntityHandle, Boolean>> NEW_LOCK_STATES;
    public static final Topic<String> SLICE_ENTRY;
    public static final Topic<MenuCommands> SPECIAL_KEYBOARD_BACK;
    public static final Topic<ShotType> SHOT_TYPE_SELECTION_TOPIC;

    public static final Topic<?>[] MENU_TOPICS = ArrayUtil.addArrays(SHARED_TOPICS, new Topic[]{
            MENU_NAVIGATION_COMMANDS = new Topic<>(),
            BUTTON_SELECTION = new Topic<>(),
            NEW_LOCK_STATES = new Topic<>(),
            SLICE_ENTRY = new Topic<>(),
            SPECIAL_KEYBOARD_BACK = new Topic<>(),
            SHOT_TYPE_SELECTION_TOPIC = new Topic<>(),
    });

    //DIALOGUE TOPICS
    public static final Topic<EntityHandle> LOWER_IMAGE_HANDLE;
    public static final Topic<EntityHandle> UPPER_IMAGE_HANDLE;
    public static final Topic<EntityHandle> LOWER_TEXT_HANDLE;
    public static final Topic<EntityHandle> UPPER_TEXT_HANDLE;

    public static final Topic<Void> READ_DIALOGUE_COMMAND;

    public static final Topic<?>[] DIALOGUE_TOPICS = ArrayUtil.addArrays(SHARED_TOPICS, new Topic[]{
            LOWER_IMAGE_HANDLE = new Topic<>(),
            UPPER_IMAGE_HANDLE = new Topic<>(),
            LOWER_TEXT_HANDLE = new Topic<>(),
            UPPER_TEXT_HANDLE = new Topic<>(),
            READ_DIALOGUE_COMMAND = new Topic<>(),
    });

    //GAME TOPICS
    public static final Topic<Void> PAUSE_STATE;
    public static final Topic<GameCommands> GAME_COMMANDS;
    public static final Topic<String> DIALOGUE_ENTRY;
    public static final Topic<Void> NEXT_STAGE_ENTRY;
    public static final Topic<EntityHandle> PLAYER_HANDLE;
    public static final Topic<EntityHandle> SPAWNER_HANDLE;
    public static final Topic<PlayerData> PLAYER_DATA;
    public static final Topic<Tuple2<EntityHandle, EntityHandle>> COLLISIONS;
    public static final Topic<Tuple2<EntityHandle, EntityHandle>> DAMAGES;
    public static final Topic<EntityHandle> DEATHS;
    public static final Topic<Void> BOSS_DEATH;
    public static final Topic<Void> PLAYER_HITS;
    public static final Topic<PlayerStateSystem.States> PLAYER_STATE_ENTRY;
    public static final Topic<Void> CONTINUE_USED;
    public static final Topic<Void> GAME_OVER;
    public static final Topic<Void> GAME_WIN;

    public static final Topic<?>[] GAME_TOPICS = ArrayUtil.addArrays(SHARED_TOPICS, new Topic[]{
            PAUSE_STATE = new Topic<>(),
            GAME_COMMANDS = new Topic<>(),
            DIALOGUE_ENTRY = new Topic<>(),
            NEXT_STAGE_ENTRY = new Topic<>(),
            PLAYER_HANDLE = new Topic<>(),
            SPAWNER_HANDLE = new Topic<>(),
            PLAYER_DATA = new Topic<>(),
            COLLISIONS = new Topic<>(),
            DAMAGES = new Topic<>(),
            DEATHS = new Topic<>(),
            BOSS_DEATH = new Topic<>(),
            PLAYER_HITS = new Topic<>(),
            PLAYER_STATE_ENTRY = new Topic<>(),
            CONTINUE_USED = new Topic<>(),
            GAME_OVER = new Topic<>(),
            GAME_WIN = new Topic<>(),
    });
}