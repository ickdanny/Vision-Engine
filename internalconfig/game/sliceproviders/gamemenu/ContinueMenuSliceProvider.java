package internalconfig.game.sliceproviders.gamemenu;

import ecs.AbstractECSInterface;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.SystemContainer;
import util.math.geometry.DoublePoint;
import util.messaging.Message;

import static internalconfig.game.systems.SliceCodes.*;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.components.MenuCommands.*;
import static internalconfig.game.systems.Topics.SPECIAL_KEYBOARD_BACK;

@SuppressWarnings("unused")
public class ContinueMenuSliceProvider extends AbstractStaticEntityCountGameMenuSliceProvider {
    private static final int ENTITY_COUNT = 5;

    public ContinueMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                              SystemContainer systemContainer, int continues){
        super(CONTINUE + continues, ENTITY_COUNT, systemChainFactoryProvider,
                new ContinueMenuInitScript(MENU_COMPONENT_TYPES, systemContainer, continues));
    }

    @SuppressWarnings("SameParameterValue")
    private static class ContinueMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        private final int continues;

        ContinueMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                               SystemContainer systemContainer,
                               int continues){
            super(componentTypeContainer,
                    systemContainer.getPauseSpriteInstructionSystem(),
                    systemContainer.getPauseDrawCommandSystem());
            this.continues = continues;

        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            super.runOn(ecsInterface);

            ecsInterface.getSliceBoard().publishMessage(
                    new Message<>(SPECIAL_KEYBOARD_BACK, NAV_FAR_DOWN, Message.AGELESS)
            );
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[])new Message[]{
                    makeBackgroundMessage(
                            "background_continue_" + continues,
                            GRAPHICAL_OFFSET.add(new DoublePoint()),
                            0
                    )
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages(){
            int x = WIDTH/2;
            int y = (HEIGHT/2);
            int offset = 30;
            return (Message<AddEntityOrder>[])new Message[]{
                    makeAddFirstButtonMessage(
                            "accept", BACK, 0, new ButtonData(
                                    OFFSET.add(new DoublePoint(x, y - offset)),
                                    "button_accept")),
                    makeAddButtonMessage(
                            "decline", GAME_OVER, 0, new ButtonData(
                                    OFFSET.add(new DoublePoint(x, y + offset)),
                                    "button_decline")),
            };
        }
    }
}