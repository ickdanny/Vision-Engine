package internalconfig.game.sliceproviders.mainmenu;

import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.SystemContainer;
import util.math.geometry.DoublePoint;
import util.messaging.Message;

import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.systems.SliceCodes.*;
import static internalconfig.game.components.MenuCommands.ENTER;
import static internalconfig.MainConfig.*;

@SuppressWarnings("unused")
public class DifficultyMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider{

    private static final int ENTITY_COUNT = 10;

    public DifficultyMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                       SystemContainer systemContainer,
                                       String name){
        super(
                name,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                false,
                new DifficultyMenuInitScript(MENU_COMPONENT_TYPES, systemContainer));

    }

    private static class DifficultyMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        public DifficultyMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                       SystemContainer systemContainer){
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[])new Message[]{
                    makeBackgroundMessage("background_difficulty", 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages(){
            double x = WIDTH/2d;
            return (Message<AddEntityOrder>[])new Message[]{
                    makeAddFirstButtonMessage(
                            "easy", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 220),
                                    new DoublePoint(x, 216),
                                    "button_easy"
                            ),
                            EASY),
                    makeAddButtonMessage(
                            "medium", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 312),
                                    new DoublePoint(x, 308),
                                    "button_normal"
                            ),
                            MEDIUM),
                    makeAddButtonMessage(
                            "hard", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 404),
                                    new DoublePoint(x, 400),
                                    "button_hard"
                            ),
                            HARD),
                    makeAddButtonMessage(
                            "lunatic", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, 496),
                                    new DoublePoint(x, 492),
                                    "button_lunatic"
                            ),
                            LUNATIC),
            };
        }
    }
}
