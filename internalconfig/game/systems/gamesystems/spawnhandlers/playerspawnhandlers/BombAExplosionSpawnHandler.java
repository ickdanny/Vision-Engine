package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AABB;
import util.math.geometry.AbstractVector;
import util.math.geometry.CartesianVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.PlayerSpawns.BOMB_A_EXPLOSION;

public class BombAExplosionSpawnHandler extends AbstractPositionSpawnHandler {

    private static final int EXPLOSION_RADIUS = 160;
    private static final AABB EXPLOSION_HITBOX = new AABB(-EXPLOSION_RADIUS, EXPLOSION_RADIUS, -10, EXPLOSION_RADIUS);
    private static final int EXPLOSION_DAMAGE = 3000;

    private static final InstructionNode<?, ?>[] EXPLOSION_PROGRAM =
            ProgramBuilder.linearLink(
                    new InstructionNode<>(SET_DAMAGE, 1),
                    new InstructionNode<>(SHIFT_TRANSPARENCY_OVER_PERIOD, new Tuple2<>(0.0, 120)),
                    DIE
            ).compile();

    private static final int NUM_PROJECTILES = 100;
    private static final AABB PROJECTILE_HITBOX = new AABB(20);
    private static final double PROJECTILE_INIT_SPEED_LOW = 3;
    private static final double PROJECTILE_INIT_SPEED_HIGH = 6.5;
    private static final double PROJECTILE_SLOW_SPEED = .7;
    private static final double PROJECTILE_DECELERATION = .3;
    private static final double PROJECTILE_FAST_SPEED = 20d;
    private static final double PROJECTILE_ACCELERATION_LOW = .025;
    private static final double PROJECTILE_ACCELERATION_HIGH = .05;
    private static final double OUTBOUND = -50;
    private static final int PROJECTILE_DAMAGE = 1;

    private static final int PROJECTILE_TIMER_LOW = 70;
    private static final int PROJECTILE_TIMER_HIGH = 110;

    public BombAExplosionSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(BOMB_A_EXPLOSION, spawnBuilder, componentTypeContainer);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
        DoublePoint spawnerPos = GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType);
        DoublePoint pos = new DoublePoint(spawnerPos.getX(), 0);

        sliceBoard.publishMessage(
                spawnBuilder.makeStationaryCollidable(pos, EXPLOSION_HITBOX)
                        .markAsBomb()
                        .setDamage(EXPLOSION_DAMAGE)
                        .setProgram(EXPLOSION_PROGRAM)
                        .setDrawOrder(DrawPlane.MIDGROUND, 198)
                        .setSpriteInstruction(new SpriteInstruction("bomb_a_explosion", new CartesianVector(0, EXPLOSION_RADIUS - 11)))
                        .packageAsMessage()
        );

        Random random = GameUtil.getRandom(ecsInterface.getGlobalBoard());
        SpawnUtil.randomAngles(180 - 15, 360 + 15, NUM_PROJECTILES, random, (angle) -> {
            double speed = RandomUtil.randDoubleInclusive(PROJECTILE_INIT_SPEED_LOW, PROJECTILE_INIT_SPEED_HIGH, random);
            AbstractVector velocity = new PolarVector(speed, angle);
            DoublePoint p = velocity.multiAdd(12, pos);
            sliceBoard.publishMessage(
                    spawnBuilder.makeStraightPathCollidable(p, velocity, PROJECTILE_HITBOX, OUTBOUND)
                            .markAsBomb()
                            .setDamage(PROJECTILE_DAMAGE)
                            .setProgram(makeProjectileProgram(random))
                            .setDrawOrder(DrawPlane.MIDGROUND, 199)
                            .setSpriteInstruction(new SpriteInstruction("bomb_a_shrapnel"))
                            .markRotateSpriteForward()
                            .packageAsMessage()
            );
        });
    }

    private InstructionNode<?, ?>[] makeProjectileProgram(Random random){
        int timer = RandomUtil.randIntInclusive(PROJECTILE_TIMER_LOW, PROJECTILE_TIMER_HIGH, random);
        double acceleration = RandomUtil.randDoubleInclusive(PROJECTILE_ACCELERATION_LOW, PROJECTILE_ACCELERATION_HIGH, random);
        return ProgramBuilder.linearLink(
                new InstructionNode<>(DECELERATE_TO_SPEED, new Tuple2<>(PROJECTILE_SLOW_SPEED, PROJECTILE_DECELERATION))
        ).linkInject(
                ProgramBuilder.linearLink(
                        new InstructionNode<>(TIMER, timer),
                        new InstructionNode<>(ACCELERATE_TO_SPEED, new Tuple2<>(PROJECTILE_FAST_SPEED, acceleration))
                )
        ).compile();
    }
}
