package internalconfig.game.sliceproviders.mainmenu;

import ecs.AbstractECSInterface;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ButtonData;
import internalconfig.game.sliceproviders.SystemChainFactoryProvider;
import internalconfig.game.sliceproviders.sliceinitscripts.AbstractVerticalButtonAxisMenuInitScript;
import internalconfig.game.systems.SystemContainer;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.ArrayList;

import static internalconfig.MainConfig.WIDTH;
import static internalconfig.game.components.ComponentTypes.MENU_COMPONENT_TYPES;
import static internalconfig.game.components.MenuCommands.*;
import static internalconfig.game.systems.SliceCodes.MUSIC;
import static internalconfig.game.systems.Topics.SPECIAL_KEYBOARD_BACK;

public class MusicMenuSliceProvider extends AbstractStaticEntityCountMenuSliceProvider{

    private static final int ENTITY_COUNT = 20;

    public MusicMenuSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                                  SystemContainer systemContainer){
        super(
                MUSIC,
                ENTITY_COUNT,
                systemChainFactoryProvider,
                true,
                new MusicMenuInitScript(MENU_COMPONENT_TYPES, systemContainer)
        );
    }

    private static class MusicMenuInitScript extends AbstractVerticalButtonAxisMenuInitScript {

        public MusicMenuInitScript(AbstractComponentTypeContainer componentTypeContainer,
                                        SystemContainer systemContainer){
            super(componentTypeContainer,
                    systemContainer.getMenuSpriteInstructionSystem(),
                    systemContainer.getMenuDrawCommandSystem());
        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            super.runOn(ecsInterface);

            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            sliceBoard.publishMessage(new Message<>(SPECIAL_KEYBOARD_BACK, NAV_FAR_DOWN, Message.AGELESS));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddBackgroundMessages() {
            return (Message<AddEntityOrder>[])new Message[]{
                    makeBackgroundMessage("background_music", new DoublePoint(100, 0), 0)
            };
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Message<AddEntityOrder>[] makeAddButtonMessages(){
            double x = WIDTH / 2d;
            double y = 76;
            double yDist = 31;

            y -= yDist;

            ArrayList<Message<AddEntityOrder>> arrayList = new ArrayList<>();
            arrayList.add(makeAddFirstButtonMessage(
                    "01", START_TRACK, 0, new ButtonData(
                            new DoublePoint(x, y += yDist),
                            "button_01"
                    ),
                    "01"
            ));

            for(int i = 2; i <= 15; ++i){
                String code = i < 10 ? "0" + i : "" + i;
                arrayList.add(makeAddButtonMessage(
                        code, START_TRACK, 0, new ButtonData(
                                new DoublePoint(x, y += yDist),
                                "button_" + code
                        ),
                        code
                ));
            }

            arrayList.add(makeAddButtonMessage(
                    "16", START_TRACK, 0, new ButtonData(
                            new DoublePoint(x, y += yDist),
                            "button_16"
                    ),
                    "16b"
            ));

            //noinspection UnusedAssignment
            arrayList.add(makeAddButtonMessage(
                    "exit", BACK_AND_SET_TRACK_TO_MENU, 0, new ButtonData(
                            new DoublePoint(x, y += yDist), "button_music_exit")));

            return arrayList.toArray((Message<AddEntityOrder>[])new Message[0]);
        }
    }
}
