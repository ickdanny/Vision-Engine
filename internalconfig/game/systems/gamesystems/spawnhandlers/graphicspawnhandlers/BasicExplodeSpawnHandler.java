package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionVelocitySpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

public class BasicExplodeSpawnHandler extends AbstractPositionVelocitySpawnHandler {

    public BasicExplodeSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(GraphicSpawns.BASIC_EXPLODE, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
        AbstractVector velocity = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType);
        velocity = new PolarVector(.5, velocity.getAngle());

        sliceBoard.publishMessage(
                spawnBuilder.makeVisibleGameObject(pos)
                        .setVelocity(velocity)
                        .setAnimation(new AnimationComponent(
                                new Animation(false,
                                        "basic_explode_1",
                                        "basic_explode_2"
                                )
                        ))
                        .setSpriteInstruction(new SpriteInstruction("basic_explode_1"))
                        .setDrawOrder(DrawPlane.MIDGROUND, 89)
                        .setProgram(ProgramRepository.LIFETIME_10.getProgram())
                        .packageAsMessage()
        );
    }
}
