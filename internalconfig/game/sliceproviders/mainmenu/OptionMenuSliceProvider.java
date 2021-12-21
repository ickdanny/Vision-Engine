package internalconfig.game.sliceproviders.mainmenu;

import ecs.AbstractECSInterface;
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
import static internalconfig.game.components.MenuCommands.*;
import static internalconfig.game.systems.SliceCodes.OPTION;
import static internalconfig.game.systems.Topics.SPECIAL_KEYBOARD_BACK;

public class OptionMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider{

    private static final int ENTITY_COUNT = 10;

    public OptionMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                   SystemContainer systemContainer){
        super(
                OPTION,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                true,
                new OptionMenuInitScript(MENU_COMPONENT_TYPES, systemContainer)
        );

    }

    private static class OptionMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        public OptionMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                   SystemContainer systemContainer){
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
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
                    makeBackgroundMessage("background_option", new DoublePoint(100, 0), 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages(){
            double x = WIDTH/2d;
            double y = 300;
            double spacing = 114;
            return (Message<AddEntityOrder>[])new Message[]{
                    makeAddFirstButtonMessage(
                            "toggle_sound", SOUND_TOGGLE, 0, new ButtonData(
                                    new DoublePoint(x, y - spacing),
                                    "button_sound")),
                    makeAddButtonMessage(
                            "toggle_fullscreen", FULLSCREEN_TOGGLE, 0, new ButtonData(
                                    new DoublePoint(x, y),
                                    "button_fullscreen")),
                    makeAddButtonMessage(
                            "back", BACK_AND_WRITE_PROPERTIES, 0, new ButtonData(
                                    new DoublePoint(x, y + spacing),
                                    "button_option_exit")),
            };
        }
    }
}
