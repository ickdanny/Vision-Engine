package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.Random;

import static internalconfig.game.components.spawns.PlayerSpawns.PLAYER_DEATH_PICKUPS;
import static internalconfig.game.GameConfig.*;

public class PlayerDeathPickupSpawnHandler extends AbstractPositionSpawnHandler {

    private static final InstructionNode<?, ?>[] PLAYER_PICKUP_PROGRAM =
            ProgramUtil.makeTimerProgram(PLAYER_PICKUP_AIR_TIME)
                    .linkAppend(ProgramUtil.makeSetVelocityProgram(new PolarVector(PICKUP_FINAL_SPEED, new Angle(90))))
                    .compile();

    public PlayerDeathPickupSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(PLAYER_DEATH_PICKUPS, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        int lives = GameUtil.getPlayerData(sliceBoard).getLives();

        DoublePoint pos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);

        Random random = GameUtil.getRandom(globalBoard);

        SpawnUtil.randomPosition(
                PLAYER_PICKUP_INBOUND,
                WIDTH - PLAYER_PICKUP_INBOUND,
                PLAYER_PICKUP_INBOUND,
                PLAYER_PICKUP_Y_HIGH,
                random,
                (p) -> {
                    AbstractVector velocity = GeometryUtil.vectorFromAToB(pos, p);
                    velocity.scale(1d / PLAYER_PICKUP_AIR_TIME);
                    sliceBoard.publishMessage(
                            spawnBuilder.makeLargePowerPickup(pos, velocity)
                                    .setProgram(PLAYER_PICKUP_PROGRAM)
                                    .packageAsMessage()
                    );
                }
        );
        SpawnUtil.randomPositions(
                PLAYER_PICKUP_INBOUND,
                WIDTH - PLAYER_PICKUP_INBOUND,
                PLAYER_PICKUP_INBOUND,
                PLAYER_PICKUP_Y_HIGH,
                NUM_SMALL_PLAYER_PICKUPS,
                random,
                (p) -> {
                    AbstractVector velocity = GeometryUtil.vectorFromAToB(pos, p);
                    velocity.scale(1d / PLAYER_PICKUP_AIR_TIME);
                    sliceBoard.publishMessage(
                            spawnBuilder.makeSmallPowerPickup(pos, velocity)
                                    .setProgram(PLAYER_PICKUP_PROGRAM)
                                    .packageAsMessage()
                    );
                }
        );
    }
}
