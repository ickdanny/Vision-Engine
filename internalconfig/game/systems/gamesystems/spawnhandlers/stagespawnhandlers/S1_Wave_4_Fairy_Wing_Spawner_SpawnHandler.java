package internalconfig.game.systems.gamesystems.spawnhandlers.stagespawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ProgramUtil;
import internalconfig.game.components.spawns.DanmakuSpawns;
import internalconfig.game.components.spawns.DeathSpawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractSpawnHandlerTemplate;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.tuple.Tuple2;

import java.util.Random;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.StageSpawns.S1_WAVE_4_FAIRY_WING_SPAWNER;
import static internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil.TOP_OUT;

public class S1_Wave_4_Fairy_Wing_Spawner_SpawnHandler extends AbstractSpawnHandlerTemplate {

    private static final int WAVE_TIME = 100;
    private static final int DELAY_TIME = 65;
    private static final int TOTAL_TIME = WAVE_TIME + DELAY_TIME;

    private static final int FAIRY_MOD = 15;

    private static final int FAIRY_HEALTH = 100;

    private static final double FAIRY_SPEED = 3.7;

    private static final double FAIRY_SPACING = 25;
    private static final double FAIRY_X_1 = WIDTH - 85;
    private static final double FAIRY_X_2 = FAIRY_X_1 + FAIRY_SPACING;

    private static final DoublePoint FAIRY_POS_1 = new DoublePoint(FAIRY_X_1, TOP_OUT);
    private static final DoublePoint FAIRY_POS_2 = new DoublePoint(FAIRY_X_2, TOP_OUT);

    private static final Angle FAIRY_HORIZONTAL_ANGLE = new Angle(-175);

    private static final double FAIRY_Y_HIGH_BOUNDARY = HEIGHT / 2d - 100;
    private static final int FAIRY_TURN_DURATION = 45;
    private static final int FAIRY_HORIZONTAL_TIMER = 50;
    private static final double FAIRY_Y_NO_SHOOT_BOUNDARY = HEIGHT - 20;

    private static final int FAIRY_DELAY_LOW = 1;
    private static final int FAIRY_DELAY_HIGH = 61;

    private static final int WING_HEALTH = 1745;

    private static final double WING_SPEED = 2.2;

    private static final double WING_X = 3 * WIDTH / 14d;

    private static final int WING_START_WAIT_TIME = 40;
    private static final int WING_SLOW_DURATION = 40;
    private static final int WING_WAIT_DURATION = 15;
    private static final int WING_WAIT_AFTER_DURATION = 50;
    private static final int WING_SPEED_DURATION = 70;

    private static final InstructionNode<?, ?>[] WING_PROGRAM = ProgramUtil.makeShootOnceAndLeaveEnemyProgram(
            WING_START_WAIT_TIME,
            WING_SLOW_DURATION,
            WING_WAIT_DURATION,
            DanmakuSpawns.S1_WAVE_4_WING_MIRROR_ARCS,
            WING_WAIT_AFTER_DURATION,
            new PolarVector(WING_SPEED, new Angle(-90)),
            WING_SPEED_DURATION
    ).compile();

    public S1_Wave_4_Fairy_Wing_Spawner_SpawnHandler(SpawnBuilder spawnBuilder) {
        super(S1_WAVE_4_FAIRY_WING_SPAWNER, spawnBuilder);
    }

    @Override
    public void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnFairy = tickMod(tick, FAIRY_MOD);
        boolean spawnWing = tickMod(S1_WAVE_4_FAIRY_WING_SPAWNER.getDuration() - tick, TOTAL_TIME);
        if (spawnFairy || spawnWing) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            boolean side = (tick / TOTAL_TIME) % 2 == 0;

            if (spawnFairy) {
                Random random = GameUtil.getRandom(globalBoard);

                if (side) {
                    SpawnUtil.singleSideMirror(FAIRY_POS_1, FAIRY_HORIZONTAL_ANGLE, WIDTH / 2d, (p, a) -> spawnFairy(sliceBoard, random, p, a));
                    SpawnUtil.singleSideMirror(FAIRY_POS_2, FAIRY_HORIZONTAL_ANGLE, WIDTH / 2d, (p, a) -> spawnFairy(sliceBoard, random, p, a));
                } else {
                    spawnFairy(sliceBoard, random, FAIRY_POS_1, FAIRY_HORIZONTAL_ANGLE);
                    spawnFairy(sliceBoard, random, FAIRY_POS_2, FAIRY_HORIZONTAL_ANGLE);
                }
            }
            if (spawnWing) {
                DoublePoint pos = side ? new DoublePoint(WIDTH - WING_X, TOP_OUT) : new DoublePoint(WING_X, TOP_OUT);
                AbstractVector velocity = new PolarVector(WING_SPEED, -90);

                spawnWing(sliceBoard, pos, velocity);
            }
        }
    }

    private void spawnFairy(AbstractPublishSubscribeBoard sliceBoard,
                            Random random,
                            DoublePoint pos,
                            Angle horizontalAngle) {
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, new PolarVector(FAIRY_SPEED, -90), SMALL_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(FAIRY_HEALTH)
                        .setAsFairyOrange()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_SMALL_POWER_FIFTH)
                        .setProgram(makeFairyProgram(random, horizontalAngle))
                        .packageAsMessage()
        );
    }

    private InstructionNode<?, ?>[] makeFairyProgram(Random random, Angle horizontalAngle) {
        return ProgramBuilder.linearLink(
                new InstructionNode<>(TIMER, RandomUtil.randIntInclusive(FAIRY_DELAY_LOW, FAIRY_DELAY_HIGH, random)),
                new InstructionNode<>(SET_SPAWN, DanmakuSpawns.S1_WAVE_4_FAIRY_SHOT),
                new InstructionNode<>(BOUNDARY_Y_HIGH, FAIRY_Y_HIGH_BOUNDARY),
                new InstructionNode<>(TURN_TO, new Tuple2<>(horizontalAngle, FAIRY_TURN_DURATION)),
                new InstructionNode<>(TIMER, FAIRY_HORIZONTAL_TIMER),
                new InstructionNode<>(TURN_TO, new Tuple2<>(new Angle(-90), FAIRY_TURN_DURATION)),
                new InstructionNode<>(BOUNDARY_Y_HIGH, FAIRY_Y_NO_SHOOT_BOUNDARY),
                CLEAR_SPAWN
        ).compile();
    }

    private void spawnWing(AbstractPublishSubscribeBoard sliceBoard, DoublePoint pos, AbstractVector velocity){
        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPathCollidable(pos, velocity, LARGE_ENEMY_HITBOX, ENEMY_OUTBOUND)
                        .markAsMob(WING_HEALTH)
                        .setAsWing()
                        .setDeathSpawnWithCommandAndSpawnComponent(DeathSpawns.DROP_LARGE_POWER)
                        .setProgram(WING_PROGRAM)
                        .packageAsMessage()
        );
    }
}
