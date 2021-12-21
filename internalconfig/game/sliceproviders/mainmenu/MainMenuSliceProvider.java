package internalconfig.game.sliceproviders.mainmenu;

import ecs.AbstractECSInterface;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.MainConfig;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.components.LockConditions;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.SystemContainer;
import internalconfig.game.systems.Topics;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.components.MenuCommands.*;
import static internalconfig.game.systems.SliceCodes.*;

import static internalconfig.game.GlobalTopics.TOP_LEVEL_SLICES;
import static internalconfig.game.systems.Topics.SPECIAL_KEYBOARD_BACK;

public class MainMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider {

    private static final int ENTITY_COUNT = 10;

    public MainMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                 SystemContainer systemContainer,
                                 boolean isExtraUnlocked) {
        super(
                MAIN,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                false,
                new MainMenuInitScript(MENU_COMPONENT_TYPES, systemContainer, isExtraUnlocked)
        );
    }

    private static class MainMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        private final boolean isExtraUnlocked;

        public MainMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                  SystemContainer systemContainer,
                                  boolean isExtraUnlocked) {
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
            this.isExtraUnlocked = isExtraUnlocked;
        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            super.runOn(ecsInterface);

            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            globalBoard.publishMessage(new Message<>(TOP_LEVEL_SLICES, MAIN, Message.AGELESS));
            sliceBoard.publishMessage(new Message<>(SPECIAL_KEYBOARD_BACK, NAV_FAR_DOWN, Message.AGELESS));

            sliceBoard.publishMessage(new Message<>(Topics.MUSIC, "01", Message.AGELESS));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeBackgroundMessage("background_main", 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages() {
            double x = MainConfig.WIDTH/2d;
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeAddFirstButtonMessage(
                            "start", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 400),
                                    new DoublePoint(x, 398), "button_start"),
                            START),
                    isExtraUnlocked
                            ? makeAddButtonMessage(
                            "extra", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 432),
                                    new DoublePoint(x, 430), "button_extra"),
                            EXTRA)
                            : makeAddLockedButtonMessage("extra", ENTER, 0, new ButtonData(
                                    true,
                                    new DoublePoint(x, 432),
                                    new DoublePoint(x, 430), "button_extra"),
                            LockConditions.UNLOCK_WHEN_GAME_BEATEN,
                            EXTRA),
                    makeAddButtonMessage(
                            "practice", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 464),
                                    new DoublePoint(x, 462), "button_practice"),
                            PRACTICE),
                    makeAddButtonMessage(
                            "music", ENTER_AND_STOP_MUSIC, 0, new ButtonData(
                                    new DoublePoint(x, 496),
                                    new DoublePoint(x, 494), "button_music"),
                            MUSIC),
                    makeAddButtonMessage(
                            "option", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 528),
                                    new DoublePoint(x, 526), "button_option"),
                            OPTION),
                    makeAddButtonMessage(
                            "exit", EXIT, 0, new ButtonData(
                                    new DoublePoint(x, 560),
                                    new DoublePoint(x, 558), "button_quit"))
            };
        }
    }
}