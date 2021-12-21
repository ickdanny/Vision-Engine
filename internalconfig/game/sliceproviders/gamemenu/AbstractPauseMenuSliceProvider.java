package internalconfig.game.sliceproviders.gamemenu;

import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.SystemContainer;
import internalconfig.game.components.MenuCommands;
import util.math.geometry.DoublePoint;
import util.messaging.Message;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.components.MenuCommands.*;

@SuppressWarnings("unused")
abstract class AbstractPauseMenuSliceProvider extends AbstractStaticEntityCountGameMenuSliceProvider {

    private static final int ENTITY_COUNT = 5;

    AbstractPauseMenuSliceProvider(String name,
                                   SystemChainFactoryProvider systemChainFactoryProvider,
                                   SystemContainer systemContainer,
                                   MenuCommands quitToMenuMenuCommand){
        super(name, ENTITY_COUNT, systemChainFactoryProvider,
                new PauseMenuInitScript(MENU_COMPONENT_TYPES, systemContainer, quitToMenuMenuCommand));
    }

    @SuppressWarnings("SameParameterValue")
    private static class PauseMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        private final MenuCommands quitToMenuMenuCommand;

        PauseMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                  SystemContainer systemContainer,
                                   MenuCommands quitToMenuMenuCommand){
            super(componentTypeContainer,
                    systemContainer.getPauseSpriteInstructionSystem(),
                    systemContainer.getPauseDrawCommandSystem());
            this.quitToMenuMenuCommand = quitToMenuMenuCommand;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[])new Message[]{
                    makeBackgroundMessage("background_pause", GRAPHICAL_OFFSET.add(new DoublePoint()), 0)
            };
        }

        @SuppressWarnings({"unchecked", "UnusedAssignment"})
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages(){
            int y = 200;
            final int spacing = 80;
            y -= spacing;
            return (Message<AddEntityOrder>[])new Message[]{
                    makeAddFirstButtonMessage(
                            "unpause", BACK, 0, new ButtonData(
                                    OFFSET.add(new DoublePoint(WIDTH/2d, y += spacing)),
                                    "button_resume")),
                    makeAddButtonMessage(
                            "retry", RESTART_GAME, 0, new ButtonData(
                                    OFFSET.add(new DoublePoint(WIDTH/2d, y += spacing)),
                                    "button_retry")),
                    makeAddButtonMessage(
                            "return_menu", quitToMenuMenuCommand, 0, new ButtonData(
                                    OFFSET.add(new DoublePoint(WIDTH/2d, y += spacing)),
                                    "button_retire")),
            };
        }
    }
}