package internalconfig.game.systems.graphicssystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.component.componentstorage.AbstractGroup;
import ecs.component.componentstorage.ComponentIterator;
import ecs.datastorage.AbstractDataStorage;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.TrailComponent;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.components.Instructions.*;

public class TrailSystem implements AbstractSystem<Double> {

    private static final int REMOVE_PERIOD = 20;

    private static final InstructionNode<?, ?>[] TRAIL_PROGRAM = ProgramBuilder.linearLink(
            new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0d, REMOVE_PERIOD))
    ).linkInject(
            ProgramBuilder.linearLink(
                    new InstructionNode<>(SHIFT_SCALE_OVER_PERIOD, new Tuple2<>(.5d, REMOVE_PERIOD + 1)),
                    REMOVE_ENTITY
            )
    ).compile();

    private final AbstractComponentType<TrailComponent> trailComponentType;
    private final AbstractComponentType<TwoFramePosition> positionComponentType;

    private final AbstractComponentTypeContainer componentTypeContainer;

    public TrailSystem(AbstractComponentTypeContainer componentTypeContainer) {
        trailComponentType = componentTypeContainer.getTypeInstance(TrailComponentType.class);
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);

        this.componentTypeContainer = componentTypeContainer;
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

        private AbstractGroup group;
        private final SpawnBuilder spawnBuilder;

        private Instance() {
            group = null;
            spawnBuilder = new SpawnBuilder(componentTypeContainer);
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if (group == null) {
                getGroup(dataStorage);
            }

            ComponentIterator<TrailComponent> trailItr = group.getComponentIterator(trailComponentType);
            while (trailItr.hasNext()) {
                TrailComponent trailComponent = trailItr.next();

                int tick = trailComponent.stepAndGetTick();
                if (tick == 1) {
                    int entityID = trailItr.entityIDOfPreviousComponent();
                    DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
                    spawnTrail(sliceBoard, pos, trailComponent.getImage());
                }
            }

            sliceBoard.ageAndCullMessages();
        }

        private void spawnTrail(AbstractPublishSubscribeBoard sliceBoard,
                                DoublePoint pos,
                                String image){
            sliceBoard.publishMessage(
                    spawnBuilder.makeVisibleGameObject(pos)
                    .setDrawOrder(DrawPlane.MIDGROUND, 0)
                    .setSpriteInstruction(new SpriteInstruction(image))
                    .setProgram(TRAIL_PROGRAM)
                    .packageAsMessage()
            );
        }

        private void getGroup(AbstractDataStorage dataStorage) {
            group = dataStorage.createGroup(trailComponentType);
        }

    }
}
