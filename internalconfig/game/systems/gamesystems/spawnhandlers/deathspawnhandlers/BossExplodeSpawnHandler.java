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
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

import static internalconfig.game.components.spawns.DeathSpawns.BOSS_EXPLODE;

public class BossExplodeSpawnHandler extends AbstractPositionSpawnHandler {

    private static final int ANIMATION_TICK = 10;

    private static final InstructionNode<?, ?>[] LIFETIME_PROGRAM = ProgramUtil.makeLifetimeProgram(ANIMATION_TICK * 7).compile();

    public BossExplodeSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(BOSS_EXPLODE, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);

        sliceBoard.publishMessage(
                spawnBuilder.makeVisibleGameObject(pos)
                        .setSpriteInstruction(SpriteInstruction.makeScaled("death_animation_1", 1.5))
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
