package internalconfig.game.sliceproviders.mainmenu;

import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.SystemContainer;
import util.math.geometry.DoublePoint;
import util.messaging.Message;

import static internalconfig.MainConfig.WIDTH;
import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.components.MenuCommands.ENTER;
import static internalconfig.game.systems.SliceCodes.*;

@SuppressWarnings("unused")
public class ExtraDifficultyMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider {
    private static final int ENTITY_COUNT = 10;

    public ExtraDifficultyMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                            SystemContainer systemContainer,
                                            String name) {
        super(
                name,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                false,
                new ExtraDifficultyMenuInitScript(MENU_COMPONENT_TYPES, systemContainer)
        );

    }

    private static class ExtraDifficultyMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        public ExtraDifficultyMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                             SystemContainer systemContainer) {
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeBackgroundMessage("background_difficulty", 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages() {
            double x = WIDTH / 2d;
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeAddFirstButtonMessage(
                            "extrad", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 358), "button_extrad"),
                            EXTRAD),
            };
        }
    }
}
