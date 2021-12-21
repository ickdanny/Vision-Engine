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
import static internalconfig.game.systems.SliceCodes.STAGE;

public class StageMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider {

    private static final int ENTITY_COUNT = 10;

    public StageMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                  SystemContainer systemContainer,
                                  String name) {
        super(
                name,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                false,
                new StageMenuInitScript(MENU_COMPONENT_TYPES, systemContainer)
        );
    }

    private static class StageMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        public StageMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                   SystemContainer systemContainer) {
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeBackgroundMessage("background_stage", 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages() {
            int baseY = 182;
            int spacing = 70;
            double selOffset = 4;
            double x = WIDTH / 2d;
            return (Message<AddEntityOrder>[]) new Message[]{
                    makeAddFirstButtonMessage(
                            "1", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, baseY),
                                    new DoublePoint(x, baseY - selOffset)
                                    , "button_stage1"),
                            STAGE + "1"),
                    makeAddButtonMessage(
                            "2", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, baseY + spacing),
                                    new DoublePoint(x, baseY + spacing - selOffset),
                                    "button_stage2"),
                            STAGE + "2"),
                    makeAddButtonMessage(
                            "3", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, baseY + (spacing * 2)),
                                    new DoublePoint(x, baseY + (spacing * 2) - selOffset),
                                    "button_stage3"),
                            STAGE + "3"),
                    makeAddButtonMessage(
                            "4", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, baseY + (spacing * 3)),
                                    new DoublePoint(x, baseY + (spacing * 3) - selOffset),
                                    "button_stage4"),
                            STAGE + "4"),
                    makeAddButtonMessage(
                            "5", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, baseY + (spacing * 4)),
                                    new DoublePoint(x, baseY + (spacing * 4) - selOffset),
                                    "button_stage5"),
                            STAGE + "5"),
                    makeAddButtonMessage(
                            "6", ENTER, 0, new ButtonData(
                                    new DoublePoint(x, baseY + (spacing * 5)),
                                    new DoublePoint(x, baseY + (spacing * 5) - selOffset),
                                    "button_stage6"),
                            STAGE + "6"),
            };
        }
    }
}
