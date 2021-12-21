package internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers;

import ecs.AbstractECSInterface;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.math.geometry.DoublePoint;
import util.messaging.AbstractPublishSubscribeBoard;

class ShotBSpawnHandler extends AbstractShotSpawnHandler {

    public ShotBSpawnHandler(SpawnBuilder spawnBuilder, AbstractComponentTypeContainer componentTypeContainer) {
        super(spawnBuilder, componentTypeContainer);
    }

    @Override
    void spawnUnfocused_0(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, 6)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
        }
    }

    @Override
    void spawnUnfocused_8(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, 6)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            spawnBasicBullet(pos, UP_ANGLE, sliceBoard);

            //mod 30
            if(tickMod(tick, 5)){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_16(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, 6)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = getSpawnPos(ecsInterface, entityID);

            SpawnUtil.mirrorFormation(
                    BASIC_SHOT_SPAWN_OFFSET.add(pos),
                    pos.getX(),
                    (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
            );
            //mod 30
            if(tickMod(tick, 5)){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_32(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 15);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_48(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 15);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        OFF_ANGLE_1,
                        pos.getX(),
                        (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_64(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 10);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        OFF_ANGLE_1,
                        pos.getX(),
                        (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_80(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick + 3, 6);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        OFF_ANGLE_1,
                        pos.getX(),
                        (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_100(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 5);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        OFF_ANGLE_1,
                        pos.getX(),
                        (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnUnfocused_MAX(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 5);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, pos),
                        OFF_ANGLE_1,
                        pos.getX(),
                        (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairNormalClose(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_0(AbstractECSInterface ecsInterface, int tick, int entityID) {
        spawnUnfocused_0(ecsInterface, tick, entityID);
    }

    @Override
    void spawnFocused_8(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, 6)) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            spawnBasicBullet(pos, UP_ANGLE, sliceBoard);

            //mod 30
            if(tickMod(tick, 5)){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_16(AbstractECSInterface ecsInterface, int tick, int entityID) {
        if(tickMod(tick, 6)){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            DoublePoint pos = getSpawnPos(ecsInterface, entityID);

            SpawnUtil.mirrorFormation(
                    BASIC_SHOT_SPAWN_OFFSET.add(pos),
                    pos.getX(),
                    (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
            );
            //mod 30
            if(tickMod(tick, 5)){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_32(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 15);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_48(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 15);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_64(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 10);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_80(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick + 3, 6);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_100(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 5);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                spawnBasicBullet(pos, UP_ANGLE, sliceBoard);
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }

    @Override
    void spawnFocused_MAX(AbstractECSInterface ecsInterface, int tick, int entityID) {
        boolean spawnBasic = tickMod(tick, 6);
        boolean spawnSpecial = tickMod(tick, 5);

        if(spawnBasic || spawnSpecial){
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            DoublePoint pos = getSpawnPos(ecsInterface, entityID);
            if(spawnBasic) {
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.add(pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
                SpawnUtil.mirrorFormation(
                        BASIC_SHOT_SPAWN_OFFSET.multiAdd(3, pos),
                        pos.getX(),
                        (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                );
            }
            if(spawnSpecial){
                spawnSpecialPairMedium(pos, sliceBoard);
            }
        }
    }
}
