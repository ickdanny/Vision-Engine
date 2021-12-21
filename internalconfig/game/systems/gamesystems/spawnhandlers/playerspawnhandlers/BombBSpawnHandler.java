package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.PlayerSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.stream.DoubleStream;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;

class BombBSpawnHandler extends AbstractBombSpawnHandler {

    private static final double MAIN_ANGLE_DEVIATION = 20;
    private static final double SPAWN_ANGLE_OFFSET = 5;
    private static final double FINAL_ANGLE_SPREAD = 10;
    private static final double FINAL_ANGLE_OFFSET = 5;
    private static final double INIT_VEL = 4;
    private static final double FINAL_VEL = 10;
    private static final int SLOW_TIME = 50;
    private static final int AIR_TIME = 10;
    private static final int TIME_TO_SPEED_UP = 60;
    private static final AABB HITBOX = new AABB(100);
    private static final double OUTBOUND = -500;
    private static final int DAMAGE = 30;

    public BombBSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if (tick != PlayerSpawns.BOMB.getDuration()) {
            return;
        }
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint pos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);
        Angle mainAngle = mainAngle(pos.getX());

        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        DoubleStream spawnAngleOffsetStream = random.doubles(3, -SPAWN_ANGLE_OFFSET, SPAWN_ANGLE_OFFSET);
        DoubleStream finalAngleOffsetStream = random.doubles(3, -FINAL_ANGLE_OFFSET, FINAL_ANGLE_OFFSET);
        PrimitiveIterator.OfDouble spawnAngleOffsetItr = spawnAngleOffsetStream.iterator();
        PrimitiveIterator.OfDouble finalAngleOffsetItr = finalAngleOffsetStream.iterator();

        for (int i = -1; i <= 1; ++i) {
            Angle spawnAngle = mainAngle.add((i * 360d / 3) + spawnAngleOffsetItr.next());
            Angle finalAngle = mainAngle.add((i * FINAL_ANGLE_SPREAD) + finalAngleOffsetItr.next());
            AbstractVector velocity = new PolarVector(INIT_VEL, spawnAngle);
            AbstractVector finalVelocity = new PolarVector(FINAL_VEL, finalAngle);
            spawn(sliceBoard, pos, velocity, finalVelocity, -Math.abs(i));
        }
    }

    private Angle mainAngle(double x) {
        return new Angle((90 - MAIN_ANGLE_DEVIATION) + ((x / WIDTH) * 2 * MAIN_ANGLE_DEVIATION));
    }

    private void spawn(AbstractPublishSubscribeBoard sliceBoard,
                       DoublePoint pos,
                       AbstractVector velocity,
                       AbstractVector finalVelocity,
                       int relativeDrawOrder) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, HITBOX, OUTBOUND)
                        .markAsBomb()
                        .setDamage(DAMAGE)
                        .setProgram(
                                ProgramBuilder.linearLink(
                                        new InstructionNode<>(SLOW_TO_HALT, SLOW_TIME),
                                        new InstructionNode<>(TIMER, AIR_TIME),
                                        new InstructionNode<>(SPEED_UP_TO_VELOCITY, new Tuple2<>(finalVelocity, TIME_TO_SPEED_UP))
                                ).compile()
                        )
                        .setDrawOrder(DrawPlane.MIDGROUND, 198 + relativeDrawOrder)
                        .setSpriteInstruction(new SpriteInstruction("bomb_b_1"))
                        .setAnimation(new AnimationComponent(
                                new Animation(true,
                                        "bomb_b_1",
                                        "bomb_b_2",
                                        "bomb_b_3",
                                        "bomb_b_4"
                                ),
                                3
                        ))
                        .setConstantSpriteRotation(2)
                        .packageAsMessage()
        );
    }
}
