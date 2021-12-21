package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.spawns.GraphicSpawns;
import internalconfig.game.components.spawns.Spawns;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.spawnhandlers.AbstractPositionSpawnHandler;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.consumer.TriConsumer;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.ConstCartesianVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.math.interval.IntInterval;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.HashMap;
import java.util.Map;

import static internalconfig.game.components.spawns.PlayerSpawns.SHOT;
import static internalconfig.game.GameConfig.*;

@SuppressWarnings("SameParameterValue")
public abstract class AbstractShotSpawnHandler extends AbstractPositionSpawnHandler {

    public static final AbstractVector SPAWN_POSITION_OFFSET = new ConstCartesianVector(0, 25);
    public static final Angle UP_ANGLE = new Angle(90);
    public static final Angle OFF_ANGLE_1 = UP_ANGLE.add(BASIC_PLAYER_BULLET_ANGLE_ADD);
    public static final Angle OFF_ANGLE_2 = OFF_ANGLE_1.add(BASIC_PLAYER_BULLET_ANGLE_ADD);
    public static final AbstractVector BASIC_SHOT_SPAWN_OFFSET = new ConstCartesianVector(7, 3);
    public static final AbstractVector SPECIAL_SHOT_SPAWN_OFFSET_FAR = new ConstCartesianVector(21, 12);
    public static final AbstractVector SPECIAL_SHOT_SPAWN_OFFSET_CLOSE = new ConstCartesianVector(14, 8);

    private final IntInterval[] powerBuckets;
    private int bucketIndex;

    private final Map<IntInterval, FocusedUnfocusedSpawnerTuple> powerShotMap;
    private FocusedUnfocusedSpawnerTuple currentShotTuple;



    AbstractShotSpawnHandler(SpawnBuilder spawnBuilder,
                             AbstractComponentTypeContainer componentTypeContainer) {
        super(SHOT, spawnBuilder, componentTypeContainer);
        powerBuckets = makePowerBuckets();
        bucketIndex = 0;

        powerShotMap = makePowerShotMap();

        currentShotTuple = powerShotMap.get(powerBuckets[0]);
    }

    private IntInterval[] makePowerBuckets() {
        return new IntInterval[]{
                IntInterval.makeLowerInclusiveOnly(0, 8),
                IntInterval.makeLowerInclusiveOnly(8, 16),
                IntInterval.makeLowerInclusiveOnly(16, 32),
                IntInterval.makeLowerInclusiveOnly(32, 48),
                IntInterval.makeLowerInclusiveOnly(48, 64),
                IntInterval.makeLowerInclusiveOnly(64, 80),
                IntInterval.makeLowerInclusiveOnly(80, 100),
                IntInterval.makeLowerInclusiveOnly(100, MAX_POWER),
                IntInterval.makeInclusive(MAX_POWER, MAX_POWER)
        };
    }

    private Map<IntInterval, FocusedUnfocusedSpawnerTuple> makePowerShotMap() {
        Map<IntInterval, FocusedUnfocusedSpawnerTuple> toRet = new HashMap<>();
        toRet.put(powerBuckets[0], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_0, this::spawnUnfocused_0));
        toRet.put(powerBuckets[1], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_8, this::spawnUnfocused_8));
        toRet.put(powerBuckets[2], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_16, this::spawnUnfocused_16));
        toRet.put(powerBuckets[3], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_32, this::spawnUnfocused_32));
        toRet.put(powerBuckets[4], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_48, this::spawnUnfocused_48));
        toRet.put(powerBuckets[5], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_64, this::spawnUnfocused_64));
        toRet.put(powerBuckets[6], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_80, this::spawnUnfocused_80));
        toRet.put(powerBuckets[7], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_100, this::spawnUnfocused_100));
        toRet.put(powerBuckets[8], new FocusedUnfocusedSpawnerTuple(this::spawnFocused_MAX, this::spawnUnfocused_MAX));
        return toRet;
    }

    abstract void spawnUnfocused_0(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_8(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_16(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_32(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_48(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_64(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_80(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_100(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnUnfocused_MAX(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_0(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_8(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_16(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_32(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_48(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_64(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_80(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_100(AbstractECSInterface ecsInterface, int tick, int entityID);

    abstract void spawnFocused_MAX(AbstractECSInterface ecsInterface, int tick, int entityID);

    @Override
    public final void handleSpawn(AbstractECSInterface ecsInterface, int tick, int entityID) {
        AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

        boolean hasCurrentShotChanged = false;

        int currentPower = GameUtil.getPlayerData(sliceBoard).getPower();
        IntInterval currentPowerInterval = powerBuckets[bucketIndex];

        while (!currentPowerInterval.isInInterval(currentPower)) {
            if (currentPower < currentPowerInterval.getLowerInclusive()) {
                --bucketIndex;
            } else {
                ++bucketIndex;
            }
            currentPowerInterval = powerBuckets[bucketIndex];
            hasCurrentShotChanged = true;
        }

        if (hasCurrentShotChanged) {
            currentShotTuple = powerShotMap.get(currentPowerInterval);
        }

        boolean isFocused = GameUtil.isPlayerFocused(sliceBoard);
        if (isFocused) {
            currentShotTuple.getFocused().accept(ecsInterface, tick, entityID);
        } else {
            currentShotTuple.getUnfocused().accept(ecsInterface, tick, entityID);
        }
    }

    protected DoublePoint getSpawnPos(AbstractECSInterface ecsInterface,
                                      int entityID){
        return SPAWN_POSITION_OFFSET.add(GameUtil.getPos(ecsInterface.getSliceData(), entityID, positionComponentType));
    }

    private static class FocusedUnfocusedSpawnerTuple {

        private final TriConsumer<AbstractECSInterface, Integer, Integer> focused;
        private final TriConsumer<AbstractECSInterface, Integer, Integer> unfocused;

        public FocusedUnfocusedSpawnerTuple(TriConsumer<AbstractECSInterface, Integer, Integer> focused,
                                            TriConsumer<AbstractECSInterface, Integer, Integer> unfocused) {
            this.focused = focused;
            this.unfocused = unfocused;
        }

        public TriConsumer<AbstractECSInterface, Integer, Integer> getFocused() {
            return focused;
        }

        public TriConsumer<AbstractECSInterface, Integer, Integer> getUnfocused() {
            return unfocused;
        }
    }

    //spawners for bullets//

    protected void spawnBasicBullet(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
        AbstractVector velocity = new PolarVector(BASIC_PLAYER_BULLET_SPEED, angle);

        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPlayerBullet(pos, velocity, BASIC_PLAYER_BULLET_HITBOX, NORMAL_OUTBOUND, BASIC_PLAYER_BULLET_DAMAGE)
                        .setDrawOrder(DrawPlane.MIDGROUND, 90)
                        .setSpriteInstruction(new SpriteInstruction("basic"))
                        .markRotateSpriteForward()
                        .setDeathSpawnWithCommandAndSpawnComponent(GraphicSpawns.BASIC_EXPLODE)
                        .packageAsMessage()
        );
    }

    private void spawnSpecialBullet(DoublePoint pos,
                                    AbstractVector velocity,
                                    int damage,
                                    SpriteInstruction spriteInstruction,
                                    Spawns deathSpawn,
                                    AbstractPublishSubscribeBoard sliceBoard) {

        sliceBoard.publishMessage(
                spawnBuilder.makeStraightPlayerBullet(pos, velocity, SPECIAL_PLAYER_BULLET_HITBOX, NORMAL_OUTBOUND, damage)
                        .setDrawOrder(DrawPlane.MIDGROUND, 95)
                        .setSpriteInstruction(spriteInstruction)
                        .markRotateSpriteForward()
                        .setDeathSpawnWithCommandAndSpawnComponent(deathSpawn)
                        .packageAsMessage()
        );
    }

    private void spawnSpecialBulletNormal(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
        spawnSpecialBullet(
                pos,
                new PolarVector(SPECIAL_PLAYER_BULLET_SPEED_NORMAL, angle),
                SPECIAL_PLAYER_BULLET_DAMAGE_NORMAL,
                new SpriteInstruction("special_normal"),
                GraphicSpawns.SPECIAL_NORMAL_EXPLODE,
                sliceBoard
        );
    }

    private void spawnSpecialBulletMedium(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
        spawnSpecialBullet(
                pos,
                new PolarVector(SPECIAL_PLAYER_BULLET_SPEED_MEDIUM, angle),
                SPECIAL_PLAYER_BULLET_DAMAGE_MEDIUM,
                new SpriteInstruction("special_medium"),
                GraphicSpawns.SPECIAL_MEDIUM_EXPLODE,
                sliceBoard
        );
    }

    private void spawnSpecialBulletHigh(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
        spawnSpecialBullet(
                pos,
                new PolarVector(SPECIAL_PLAYER_BULLET_SPEED_HIGH, angle),
                SPECIAL_PLAYER_BULLET_DAMAGE_HIGH,
                new SpriteInstruction("special_high"),
                GraphicSpawns.SPECIAL_HIGH_EXPLODE,
                sliceBoard
        );
    }

    protected void spawnSpecialPairNormalClose(DoublePoint playerPos, AbstractPublishSubscribeBoard sliceBoard) {
        DoublePoint specialOffsetPos = SPECIAL_SHOT_SPAWN_OFFSET_CLOSE.add(playerPos);
        SpawnUtil.mirrorFormation(
                specialOffsetPos,
                playerPos.getX(),
                (p) -> spawnSpecialBulletNormal(p, UP_ANGLE, sliceBoard)
        );
    }

    protected void spawnSpecialPairNormalFar(DoublePoint playerPos, AbstractPublishSubscribeBoard sliceBoard) {
        DoublePoint specialOffsetPos = SPECIAL_SHOT_SPAWN_OFFSET_FAR.add(playerPos);
        SpawnUtil.mirrorFormation(
                specialOffsetPos,
                playerPos.getX(),
                (p) -> spawnSpecialBulletNormal(p, UP_ANGLE, sliceBoard)
        );
    }

    protected void spawnSpecialPairMedium(DoublePoint playerPos, AbstractPublishSubscribeBoard sliceBoard) {
        DoublePoint specialOffsetPos = SPECIAL_SHOT_SPAWN_OFFSET_CLOSE.add(playerPos);
        SpawnUtil.mirrorFormation(
                specialOffsetPos,
                playerPos.getX(),
                (p) -> spawnSpecialBulletMedium(p, UP_ANGLE, sliceBoard)
        );
    }

    protected void spawnSpecialPairHighClose(DoublePoint playerPos, AbstractPublishSubscribeBoard sliceBoard) {
        DoublePoint specialOffsetPos = SPECIAL_SHOT_SPAWN_OFFSET_CLOSE.add(playerPos);
        SpawnUtil.mirrorFormation(
                specialOffsetPos,
                playerPos.getX(),
                (p) -> spawnSpecialBulletHigh(p, UP_ANGLE, sliceBoard)
        );
    }

    protected void spawnSpecialPairHighFar(DoublePoint playerPos, AbstractPublishSubscribeBoard sliceBoard) {
        DoublePoint specialOffsetPos = SPECIAL_SHOT_SPAWN_OFFSET_FAR.add(playerPos);
        SpawnUtil.mirrorFormation(
                specialOffsetPos,
                playerPos.getX(),
                (p) -> spawnSpecialBulletHigh(p, UP_ANGLE, sliceBoard)
        );
    }
}