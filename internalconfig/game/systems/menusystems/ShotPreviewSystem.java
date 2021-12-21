package internalconfig.game.systems.menusystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.component.TypeComponentTuple;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.entity.NamedEntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.AddEntityOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawOrder;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.SpawnUnit;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.components.VelocityComponent;
import internalconfig.game.components.spawns.PlayerSpawns;
import internalconfig.game.systems.ShotType;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnUtil;
import util.Ticker;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.DoublePoint;
import util.math.geometry.PolarVector;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.systems.Topics.*;
import static ecs.ECSTopics.NEW_NAMED_ENTITIES;
import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.playerspawnhandlers.AbstractShotSpawnHandler.*;
import static internalconfig.game.sliceproviders.mainmenu.ShotMenuSliceProvider.PLAYER_POS;
import static internalconfig.game.systems.graphicssystems.MakeOpaqueWhenPlayerFocusedAndAliveSystem.OPACITY_CHANGE_PER_TICK;

public class ShotPreviewSystem implements AbstractSystem<Double> {

    public static final String BULLET_SLOW_BARRIER = "slow_barrier";

    private final AbstractComponentType<TwoFramePosition> positionComponentType;
    private final AbstractComponentType<VelocityComponent> velocityComponentType;
    private final AbstractComponentType<Double> outboundComponentType;
    private final AbstractComponentType<Void> visibleMarker;
    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<DrawOrder> drawOrderComponentType;
    private final AbstractComponentType<Void> rotateSpriteForwardMarker;

    private final AbstractShotPreviewHandler shotAPreviewHandler;
    private final AbstractShotPreviewHandler shotBPreviewHandler;
    private final AbstractShotPreviewHandler shotCPreviewHandler;

    public ShotPreviewSystem(AbstractComponentTypeContainer componentTypeContainer) {
        positionComponentType = componentTypeContainer.getTypeInstance(PositionComponentType.class);
        velocityComponentType = componentTypeContainer.getTypeInstance(VelocityComponentType.class);
        outboundComponentType = componentTypeContainer.getTypeInstance(OutboundComponentType.class);
        visibleMarker = componentTypeContainer.getTypeInstance(VisibleMarker.class);
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        drawOrderComponentType = componentTypeContainer.getTypeInstance(DrawOrderComponentType.class);
        rotateSpriteForwardMarker = componentTypeContainer.getTypeInstance(RotateSpriteForwardMarker.class);

        shotAPreviewHandler = makeShotAPreviewHandler();
        shotBPreviewHandler = makeShotBPreviewHandler();
        shotCPreviewHandler = makeShotCPreviewHandler();
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double> {

        private static final int FOCUS_TOGGLE_TIME = 90;

        private ShotType currentShotType;
        private EntityHandle bulletSlowBarrier;

        private boolean hasShotUpdated;
        private AbstractShotPreviewHandler currentShotPreviewHandler;
        private boolean focused;

        private final SpawnUnit shotSpawnUnit;
        private final Ticker focusToggleTicker;


        public Instance() {
            currentShotType = null;
            bulletSlowBarrier = null;
            hasShotUpdated = false;
            currentShotPreviewHandler = null;
            focused = false;

            shotSpawnUnit = new SpawnUnit(PlayerSpawns.SHOT, true);
            focusToggleTicker = new Ticker(FOCUS_TOGGLE_TIME, true);
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();

            checkForShotUpdateAndUpdateCurrentShotPreviewHandler(sliceBoard);

            if (currentShotType != null) {
                if (bulletSlowBarrier == null) {
                    getBulletSlowBarrier(sliceBoard);
                }
                spawnShot(ecsInterface);
                updateBulletSlowBarrier(ecsInterface, sliceBoard);
                hasShotUpdated = false;
            }

            sliceBoard.ageAndCullMessages();
        }

        private void checkForShotUpdateAndUpdateCurrentShotPreviewHandler(AbstractPublishSubscribeBoard sliceBoard) {
            if (sliceBoard.hasTopicalMessages(SHOT_TYPE_SELECTION_TOPIC)) {
                List<Message<ShotType>> messageList = sliceBoard.getMessageList(SHOT_TYPE_SELECTION_TOPIC);
                for (Message<ShotType> message : messageList) {
                    currentShotType = message.getMessage();
                    hasShotUpdated = true;
                }
                updateCurrentShotPreviewHandler();
            }
        }

        private void updateCurrentShotPreviewHandler() {
            switch (currentShotType) {
                case A:
                    currentShotPreviewHandler = shotAPreviewHandler;
                    break;
                case B:
                    currentShotPreviewHandler = shotBPreviewHandler;
                    break;
                case C:
                default:
                    currentShotPreviewHandler = shotCPreviewHandler;
                    break;
            }
        }

        private void spawnShot(AbstractECSInterface ecsInterface) {
            if (focusToggleTicker.stepAndGetTick() == 1) {
                focused = !focused;
            }
            currentShotPreviewHandler.handleShot(ecsInterface, shotSpawnUnit.stepAndGetTick(), focused);
        }

        private void updateBulletSlowBarrier(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard) {
            if (hasShotUpdated) {
                switch (currentShotType) {
                    case A://offensive
                        sliceBoard.publishMessage(
                                ECSUtil.makeRemoveComponentMessage(new RemoveComponentOrder(bulletSlowBarrier, visibleMarker))
                        );
                        break;
                    case C://defensive
                        sliceBoard.publishMessage(
                                ECSUtil.makeSetComponentMessage(new SetComponentOrder<>(bulletSlowBarrier, visibleMarker, null))
                        );
                        setBulletSlowBarrierSprite(ecsInterface, "large_barrier");
                        break;
                    case B://balanced
                    default:
                        sliceBoard.publishMessage(
                                ECSUtil.makeSetComponentMessage(new SetComponentOrder<>(bulletSlowBarrier, visibleMarker, null))
                        );
                        setBulletSlowBarrierSprite(ecsInterface, "small_barrier");
                        break;
                }
            }
            updateBulletSlowBarrierTransparency(ecsInterface);
        }

        private void updateBulletSlowBarrierTransparency(AbstractECSInterface ecsInterface) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            SpriteInstruction spriteInstruction = dataStorage.getComponent(bulletSlowBarrier, spriteInstructionComponentType);

            double originalTransparency = spriteInstruction.getTransparency();
            if (focused && originalTransparency < 1d) {
                spriteInstruction.setTransparency(Math.min(1d, originalTransparency + OPACITY_CHANGE_PER_TICK));
            } else if (!focused && originalTransparency > 0) {
                spriteInstruction.setTransparency(Math.max(0d, originalTransparency - OPACITY_CHANGE_PER_TICK));
            }
        }

        private void setBulletSlowBarrierSprite(AbstractECSInterface ecsInterface, String image) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            SpriteInstruction spriteInstruction = dataStorage.getComponent(bulletSlowBarrier, spriteInstructionComponentType);
            spriteInstruction.setImage(image);
        }

        private void getBulletSlowBarrier(AbstractPublishSubscribeBoard sliceBoard) {
            for (Message<NamedEntityHandle> message : sliceBoard.getMessageList(NEW_NAMED_ENTITIES)) {
                NamedEntityHandle handle = message.getMessage();
                String name = handle.getName();
                if (name.equals(BULLET_SLOW_BARRIER)) {
                    bulletSlowBarrier = handle;
                    return;
                }
            }
        }
    }

    private interface AbstractShotPreviewHandler {
        default void handleShot(AbstractECSInterface ecsInterface, int tick, boolean focused) {
            if (focused) {
                handleFocusedShot(ecsInterface, tick);
            } else {
                handleUnfocusedShot(ecsInterface, tick);
            }
        }

        void handleUnfocusedShot(AbstractECSInterface ecsInterface, int tick);

        void handleFocusedShot(AbstractECSInterface ecsInterface, int tick);
    }

    @SuppressWarnings("SameParameterValue")
    private abstract class AbstractShotPreviewHandlerTemplate implements AbstractShotPreviewHandler {

        protected boolean tickMod(int tick, int mod) {
            return tick % mod == 0;
        }

        protected void spawnBasicBullet(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
            AbstractVector velocity = new PolarVector(BASIC_PLAYER_BULLET_SPEED, angle);

            sliceBoard.publishMessage(
                    ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple<?>[]{
                            new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)),
                            new TypeComponentTuple<>(velocityComponentType, new VelocityComponent(velocity)),
                            new TypeComponentTuple<>(outboundComponentType, NORMAL_OUTBOUND),
                            new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, 90)),
                            new TypeComponentTuple<>(spriteInstructionComponentType, new SpriteInstruction("basic")),
                            new TypeComponentTuple<>(visibleMarker, null),
                            new TypeComponentTuple<>(rotateSpriteForwardMarker, null),
                    }))
            );
        }

        private void spawnSpecialBullet(DoublePoint pos,
                                        AbstractVector velocity,
                                        SpriteInstruction spriteInstruction,
                                        AbstractPublishSubscribeBoard sliceBoard) {

            sliceBoard.publishMessage(
                    ECSUtil.makeAddEntityMessage(new AddEntityOrder(new TypeComponentTuple<?>[]{
                            new TypeComponentTuple<>(positionComponentType, new TwoFramePosition(pos)),
                            new TypeComponentTuple<>(velocityComponentType, new VelocityComponent(velocity)),
                            new TypeComponentTuple<>(outboundComponentType, NORMAL_OUTBOUND),
                            new TypeComponentTuple<>(drawOrderComponentType, new DrawOrder(DrawPlane.MIDGROUND, 95)),
                            new TypeComponentTuple<>(spriteInstructionComponentType, spriteInstruction),
                            new TypeComponentTuple<>(visibleMarker, null),
                            new TypeComponentTuple<>(rotateSpriteForwardMarker, null),
                    }))
            );
        }

        private void spawnSpecialBulletNormal(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
            spawnSpecialBullet(
                    pos,
                    new PolarVector(SPECIAL_PLAYER_BULLET_SPEED_NORMAL, angle),
                    new SpriteInstruction("special_normal"),
                    sliceBoard
            );
        }

        private void spawnSpecialBulletMedium(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
            spawnSpecialBullet(
                    pos,
                    new PolarVector(SPECIAL_PLAYER_BULLET_SPEED_MEDIUM, angle),
                    new SpriteInstruction("special_medium"),
                    sliceBoard
            );
        }

        private void spawnSpecialBulletHigh(DoublePoint pos, Angle angle, AbstractPublishSubscribeBoard sliceBoard) {
            spawnSpecialBullet(
                    pos,
                    new PolarVector(SPECIAL_PLAYER_BULLET_SPEED_HIGH, angle),
                    new SpriteInstruction("special_high"),
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

        protected void spawnSpecialPairHigh(DoublePoint playerPos, AbstractPublishSubscribeBoard sliceBoard) {
            DoublePoint specialOffsetPos = SPECIAL_SHOT_SPAWN_OFFSET_FAR.add(playerPos);
            SpawnUtil.mirrorFormation(
                    specialOffsetPos,
                    playerPos.getX(),
                    (p) -> spawnSpecialBulletHigh(p, UP_ANGLE, sliceBoard)
            );
        }
    }

    private AbstractShotPreviewHandler makeShotAPreviewHandler() {
        return new AbstractShotPreviewHandlerTemplate() {
            @Override
            public void handleUnfocusedShot(AbstractECSInterface ecsInterface, int tick) {
                boolean spawnBasic = tickMod(tick, 6);
                boolean spawnSpecial = tickMod(tick, 5);

                if (spawnBasic || spawnSpecial) {
                    AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                    if (spawnBasic) {
                        spawnBasicBullet(PLAYER_POS, UP_ANGLE, sliceBoard);
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.add(PLAYER_POS),
                                OFF_ANGLE_1,
                                PLAYER_POS.getX(),
                                (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                        );
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, PLAYER_POS),
                                OFF_ANGLE_2,
                                PLAYER_POS.getX(),
                                (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                        );
                    }
                    if (spawnSpecial) {
                        spawnSpecialPairNormalFar(PLAYER_POS, sliceBoard);
                    }
                }
            }

            @Override
            public void handleFocusedShot(AbstractECSInterface ecsInterface, int tick) {
                boolean spawnBasic = tickMod(tick, 6);
                boolean spawnSpecial = tickMod(tick, 5);

                if (spawnBasic || spawnSpecial) {
                    AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                    if (spawnBasic) {
                        spawnBasicBullet(PLAYER_POS, UP_ANGLE, sliceBoard);
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(4, PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                    }
                    if (spawnSpecial) {
                        spawnSpecialPairHigh(PLAYER_POS, sliceBoard);
                    }
                }
            }
        };
    }

    private AbstractShotPreviewHandler makeShotBPreviewHandler() {
        return new AbstractShotPreviewHandlerTemplate() {
            @Override
            public void handleUnfocusedShot(AbstractECSInterface ecsInterface, int tick) {
                boolean spawnBasic = tickMod(tick, 6);
                boolean spawnSpecial = tickMod(tick, 5);

                if (spawnBasic || spawnSpecial) {
                    AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                    if (spawnBasic) {
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.add(PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, PLAYER_POS),
                                OFF_ANGLE_1,
                                PLAYER_POS.getX(),
                                (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                        );
                    }
                    if (spawnSpecial) {
                        spawnSpecialPairNormalClose(PLAYER_POS, sliceBoard);
                    }
                }
            }

            @Override
            public void handleFocusedShot(AbstractECSInterface ecsInterface, int tick) {
                boolean spawnBasic = tickMod(tick, 6);
                boolean spawnSpecial = tickMod(tick, 5);

                if (spawnBasic || spawnSpecial) {
                    AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                    if (spawnBasic) {
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.add(PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(3, PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                    }
                    if (spawnSpecial) {
                        spawnSpecialPairMedium(PLAYER_POS, sliceBoard);
                    }
                }
            }
        };
    }

    private AbstractShotPreviewHandler makeShotCPreviewHandler() {
        return new AbstractShotPreviewHandlerTemplate() {
            @Override
            public void handleUnfocusedShot(AbstractECSInterface ecsInterface, int tick) {
                boolean spawnBasic = tickMod(tick, 6);
                boolean spawnSpecial = tickMod(tick, 5);

                if (spawnBasic || spawnSpecial) {
                    AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                    if (spawnBasic) {
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.add(PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(2, PLAYER_POS),
                                OFF_ANGLE_1,
                                PLAYER_POS.getX(),
                                (p, a) -> spawnBasicBullet(p, a, sliceBoard)
                        );
                    }
                    if (spawnSpecial) {
                        spawnSpecialPairNormalClose(PLAYER_POS, sliceBoard);
                    }
                }
            }

            @Override
            public void handleFocusedShot(AbstractECSInterface ecsInterface, int tick) {
                boolean spawnBasic = tickMod(tick, 6);
                boolean spawnSpecial = tickMod(tick, 5);

                if (spawnBasic || spawnSpecial) {
                    AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
                    if (spawnBasic) {
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.add(PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                        SpawnUtil.mirrorFormation(
                                BASIC_SHOT_SPAWN_OFFSET.multiAdd(3, PLAYER_POS),
                                PLAYER_POS.getX(),
                                (p) -> spawnBasicBullet(p, UP_ANGLE, sliceBoard)
                        );
                    }
                    if (spawnSpecial) {
                        spawnSpecialPairNormalClose(PLAYER_POS, sliceBoard);
                    }
                }
            }
        };
    }
}