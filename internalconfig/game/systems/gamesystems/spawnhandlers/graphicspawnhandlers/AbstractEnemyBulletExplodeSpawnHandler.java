package internalconfig.game.systems.gamesystems.spawnhandlers.graphicspawnhandlers;

import ecs.AbstractECSInterface;
import ecs.datastorage.AbstractDataStorage;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionVelocitySpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors;
import internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileTypes;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

class AbstractEnemyBulletExplodeSpawnHandler extends AbstractPositionVelocitySpawnHandler {

    private final String frame1;
    private final String frame2;
    private final int drawOrder;

    AbstractEnemyBulletExplodeSpawnHandler(Spawns spawn,
                                           EnemyProjectileTypes type,
                                           EnemyProjectileColors color,
                                           int drawOrder,
                                           SpawnBuilder spawnBuilder,
                                           AbstractComponentTypeContainer componentTypeContainer) {
        super(spawn, spawnBuilder, componentTypeContainer);
        frame1 = type.getName() + "_explode_1" + color.getSuffix();
        frame2 = type.getName() + "_explode_2" + color.getSuffix();
        this.drawOrder = drawOrder;
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        AbstractDataStorage dataStorage = ecsInterface.getSliceData();

        DoublePoint pos = GameUtil.getPos(dataStorage, entityID, positionComponentType);
        if (dataStorage.containsComponent(dataStorage.makeHandle(entityID), velocityComponentType)) {
            AbstractVector velocity = GameUtil.getVelocity(dataStorage, entityID, velocityComponentType);
            velocity = new PolarVector(velocity.getMagnitude() * .3, velocity.getAngle());

            sliceBoard.publishMessage(
                    spawnBuilder.makeVisibleGameObject(pos)
                            .setVelocity(velocity)
                            .setAnimation(new AnimationComponent(
                                    new Animation(false, frame1, frame2)
                            ))
                            .setSpriteInstruction(new SpriteInstruction(frame1))
                            .setDrawOrder(DrawPlane.MIDGROUND, drawOrder)
                            .setProgram(ProgramRepository.LIFETIME_10.getProgram())
                            .packageAsMessage()
            );
        } else {
            sliceBoard.publishMessage(
                    spawnBuilder.makeVisibleGameObject(pos)
                            .setAnimation(new AnimationComponent(
                                    new Animation(false, frame1, frame2)
                            ))
                            .setSpriteInstruction(new SpriteInstruction(frame1))
                            .setDrawOrder(DrawPlane.MIDGROUND, drawOrder)
                            .setProgram(ProgramRepository.LIFETIME_10.getProgram())
                            .packageAsMessage()
            );
        }
    }
}