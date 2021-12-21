package internalconfig.game.systems.gamesystems.spawnhandlers.deathspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

class AbstractEnemyDeathSpawnHandler extends AbstractPositionSpawnHandler {

    private static final int ANIMATION_TICK = 2;

    private static final InstructionNode<?, ?>[] LIFETIME_PROGRAM = ProgramUtil.makeLifetimeProgram(ANIMATION_TICK * 7).compile();

    AbstractEnemyDeathSpawnHandler(Spawns spawn, SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawn, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        sliceBoard.publishMessage(
                spawnBuilder.makeVisibleGameObject(pos)
                        .setSpriteInstruction(new SpriteInstruction("death_animation_1"))
                        .setAnimation(new AnimationComponent(
                                        new Animation(false,
                                                "death_animation_1",
                                                "death_animation_2",
                                                "death_animation_3",
                                                "death_animation_4",
                                                "death_animation_5",
                                                "death_animation_6",
                                                "death_animation_7"
                                        ),
                                        ANIMATION_TICK
                                )

                        )
                        .setDrawOrder(DrawPlane.MIDGROUND, -100000)
                        .setProgram(LIFETIME_PROGRAM)
                        .packageAsMessage()
        );
    }
}
