package internalconfig.game.sliceproviders;

import ecs.AbstractECSInterface;
import ecs.SliceProvider;
import ecs.datastorage.AbstractDataStorage;
import ecs.datastorage.AbstractDataStorageConfig;
import ecs.datastorage.AbstractSliceInitScript;
import ecs.datastorage.DataStorageConfig;
import ecs.entity.NamedEntityHandle;
import ecs.system.SystemChainInfo;
import ecs.system.criticalorders.AddEntityOrder;
import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.DamageGiveCommands;
import internalconfig.game.components.DamageReceiveCommands;
import internalconfig.game.components.DeathCommands;
import internalconfig.game.components.InstructionNode;
import internalconfig.game.components.ProgramBuilder;
import internalconfig.game.components.ScrollingSubImageComponent;
import internalconfig.game.components.SpawnComponent;
import internalconfig.game.systems.PlayerData;
import internalconfig.game.systems.gamesystems.GameUtil;
import internalconfig.game.systems.gamesystems.PlayerUISystem;
import internalconfig.game.systems.gamesystems.spawnhandlers.SpawnBuilder;
import resource.AbstractResourceManager;
import util.math.geometry.AABB;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.DrawPlane;
import internalconfig.game.components.SpriteInstruction;
import util.math.geometry.CartesianVector;
import util.math.geometry.ConstCartesianVector;
import util.math.geometry.DoublePoint;
import internalconfig.game.systems.SystemContainer;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static internalconfig.game.SystemChainCalls.*;
import static internalconfig.game.components.ComponentTypes.GAME_COMPONENT_TYPES;
import static internalconfig.game.components.Instructions.*;
import static internalconfig.game.components.spawns.StageSpawns.STAGE_SPAWNER;
import static internalconfig.game.systems.SliceCodes.GAME;
import static internalconfig.game.GameConfig.*;
import static ecs.ECSTopics.*;
import static internalconfig.game.systems.Topics.*;

public class GameSliceProvider extends SliceProvider {

    public GameSliceProvider(SystemChainFactoryProvider systemChainFactoryProvider,
                             SystemContainer systemContainer,
                             AbstractResourceManager<BufferedImage> imageManager) {
        super(GAME, true, GAME_TOPICS,
                makeDataStorageConfig(systemChainFactoryProvider),
                makeSystemChainInfo(systemChainFactoryProvider),
                new GameInitScript(GAME_COMPONENT_TYPES, systemContainer, imageManager));
    }

    private static AbstractDataStorageConfig makeDataStorageConfig(
            SystemChainFactoryProvider systemChainFactoryProvider) {

        int numSystems = systemChainFactoryProvider.getGameMainSystemChainFactory().getNumSystems();
        return new DataStorageConfig(
                GAME_COMPONENT_TYPES.getArray(), false, 500, numSystems);
    }

    @SuppressWarnings("unchecked")
    private static SystemChainInfo<Double>[] makeSystemChainInfo(SystemChainFactoryProvider systemChainFactoryProvider) {
        SystemChainInfo<Double> mainInfo = new SystemChainInfo<>(
                MAIN, systemChainFactoryProvider.getGameMainSystemChainFactory(), false);
        SystemChainInfo<Double> graphicsInfo = new SystemChainInfo<>(
                GRAPHICS, systemChainFactoryProvider.getGameGraphicsSystemChainFactory(), false);
        return (SystemChainInfo<Double>[]) new SystemChainInfo[]{mainInfo, graphicsInfo};
    }

    private static class GameInitScript extends AbstractSliceInitScript {

        private static final String PLAYER = "player";
        private static final String SPAWNER = "spawner";

        private final InstructionNode<?, ?>[] BULLET_SLOW_BARRIER_PROGRAM =
                ProgramBuilder.circularLink(
                        WAIT_UNTIL_PLAYER_FOCUSED,
                        SET_COLLIDABLE,
                        WAIT_UNTIL_PLAYER_UNFOCUSED,
                        REMOVE_COLLIDABLE
                ).noLinkInject(
                        ProgramBuilder.linearLink(FOLLOW_PLAYER).linkInject(
                                ProgramBuilder.linearLink(
                                        WAIT_UNTIL_PLAYER_DEAD,
                                        REMOVE_COLLIDABLE,
                                        WAIT_UNTIL_PLAYER_RESPAWN_INVULNERABLE
                                )
                        )
                ).linkBackToFront().compile();

        private final SpawnBuilder spawnBuilder;

        private final SystemContainer systemContainer;

        private final AbstractResourceManager<BufferedImage> imageManager;

        public GameInitScript(AbstractComponentTypeContainer componentTypeContainer,
                              SystemContainer systemContainer,
                              AbstractResourceManager<BufferedImage> imageManager) {

            this.systemContainer = systemContainer;
            this.imageManager = imageManager;
            spawnBuilder = new SpawnBuilder(componentTypeContainer);
        }

        @Override
        public void runOn(AbstractECSInterface ecsInterface) {
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractPublishSubscribeBoard globalBoard = ecsInterface.getGlobalBoard();

            publishPlayerData(globalBoard, sliceBoard);
            publishMessages(globalBoard, sliceBoard);
            carryOutCriticalOrders(dataStorage);
            publishPlayerHandle(sliceBoard);
            publishSpawnerHandle(sliceBoard);

            systemContainer.getGameSpriteInstructionSystem().makeInstance().run(ecsInterface, 0d);
            systemContainer.getGamePlayerUISystem().makeInstance().run(ecsInterface, 0d);
            carryOutCriticalOrders(dataStorage);
            systemContainer.getGameSpriteInstructionSystem().makeInstance().run(ecsInterface, 0d);
            carryOutCriticalOrders(dataStorage);
            systemContainer.getGameDrawCommandSystem().makeInstance().run(ecsInterface, 0d);
        }

        private void publishPlayerData(AbstractPublishSubscribeBoard globalBoard,
                                       AbstractPublishSubscribeBoard sliceBoard) {

            PlayerData playerData = GameUtil.getGameConfigObject(globalBoard).getPlayerData();
            sliceBoard.publishMessage(new Message<>(PLAYER_DATA, playerData, Message.AGELESS));
        }

        private void publishMessages(AbstractPublishSubscribeBoard globalBoard,
                                     AbstractPublishSubscribeBoard sliceBoard) {
            sliceBoard.publishMessage(makeBackgroundMessage(GameUtil.getStage(globalBoard)));
            sliceBoard.publishMessage(makeOverlayMessage());
            sliceBoard.publishMessage(makePlayerMessage());
            publishBulletSlowBarrierMessage(sliceBoard);
            sliceBoard.publishMessage(makeSpawnerMessage());
            publishUIMessages(sliceBoard);
        }

        private Message<AddEntityOrder> makeBackgroundMessage(int stage) {

            BackgroundData backgroundData = BackgroundData.getBackgroundData(stage);

            String imageID = backgroundData.imageID;

            try {
                BufferedImage image = imageManager.getResource(imageID).getData();
                int totalHeight = image.getHeight();

                int startingY = totalHeight - BackgroundData.SCREEN_HEIGHT;

                double yVelocity = backgroundData.calculateYVelocity(totalHeight);

                return spawnBuilder.makeVisibleGameObject(new DoublePoint(10, -10))
                        .setSpriteInstruction(new SpriteInstruction(imageID))
                        .setSpriteSubImage(new Rectangle(0, startingY, 480, BackgroundData.SCREEN_HEIGHT))
                        .setScrollingSubImage(new ScrollingSubImageComponent(
                                0,
                                startingY,
                                480,
                                BackgroundData.SCREEN_HEIGHT,
                                0,
                                yVelocity,
                                0,
                                0
                        ))
                        .setDrawOrder(DrawPlane.BACKGROUND, 0)
                        .packageAsMessage();
            }catch(Exception e){
                return spawnBuilder.makeVisibleGameObject((new DoublePoint()))
                        .setSpriteInstruction(new SpriteInstruction("background_game_default"))
                        .setDrawOrder(DrawPlane.BACKGROUND, 0)
                        .packageAsMessage();
            }
        }

        private enum BackgroundData {
            STAGE_1("background_s1", 60 * 108),
            STAGE_2("background_s2", 60 * 85),
            STAGE_3("background_s3", 60 * 98),
            STAGE_4("background_s4", 60 * 117 - 75),
            STAGE_5("background_s5", 60 * 95 + 75),
            STAGE_6("background_s6", 60 * 68),
            STAGE_EX("background_sEX", 60 * 150 + 10),
            ;

            private static final int SCREEN_HEIGHT = 580;

            private final String imageID;
            private final int ticks;

            BackgroundData(String imageID, int ticks){
                this.imageID = imageID;
                this.ticks = ticks;
            }

            private double calculateYVelocity(int totalHeight){
                if(totalHeight < SCREEN_HEIGHT){
                    throw new RuntimeException("totalHeight too low: " + totalHeight);
                }
                if(totalHeight == SCREEN_HEIGHT){
                    return 0;
                }
                int startingY = totalHeight - SCREEN_HEIGHT;

                return -((double)startingY)/ticks;
            }

            static BackgroundData getBackgroundData(int stage){
                switch(stage){
                    case 1:
                        return STAGE_1;
                    case 2:
                        return STAGE_2;
                    case 3:
                        return STAGE_3;
                    case 4:
                        return STAGE_4;
                    case 5:
                        return STAGE_5;
                    case 6:
                        return STAGE_6;
                    case 7:
                        return STAGE_EX;
                    default:
                        throw new RuntimeException("unrecognized stage: " + stage);
                }
            }
        }

        private Message<AddEntityOrder> makeOverlayMessage() {
            String image = "overlay";
            return spawnBuilder.makeVisible(new DoublePoint())
                    .setSpriteInstruction(new SpriteInstruction(image))
                    .setDrawOrder(DrawPlane.FOREGROUND, 0)
                    .packageAsMessage();
        }

        private Message<AddEntityOrder> makePlayerMessage() {
            return spawnBuilder.makeStationaryCollidable(PLAYER_SPAWN, PLAYER_HITBOX)
                    .setVelocity(new CartesianVector())
                    .setDrawOrder(DrawPlane.MIDGROUND, 100)
                    .setSpriteInstruction(new SpriteInstruction("p_idle_1", new ConstCartesianVector(0, 10)))
                    .setInbound(PLAYER_INBOUND)
                    .setSpawnComponent(new SpawnComponent())
                    .markAsPlayer()
                    .setDamageReceiveCommand(DamageReceiveCommands.PLAYER_DAMAGE)
                    .setDeathCommand(DeathCommands.PLAYER_DEATH)
                    .setAnimation(new AnimationComponent(
                            new Animation[]{
                                    new Animation(true, "p_left_1", "p_left_2", "p_left_3", "p_left_4"),
                                    new Animation(true, "p_left_turn_3"),
                                    new Animation(true, "p_left_turn_2"),
                                    new Animation(true, "p_left_turn_1"),
                                    new Animation(true, "p_idle_1", "p_idle_2", "p_idle_3", "p_idle_4"),
                                    new Animation(true, "p_right_turn_1"),
                                    new Animation(true, "p_right_turn_2"),
                                    new Animation(true, "p_right_turn_3"),
                                    new Animation(true, "p_right_1", "p_right_2", "p_right_3", "p_right_4")
                            },
                            4
                    ))
                    .packageAsNamedMessage(PLAYER);
        }

        //not factory method since sometimes we publish nothing
        private void publishBulletSlowBarrierMessage(AbstractPublishSubscribeBoard sliceBoard) {
            AABB hitbox;
            String image;
            switch (GameUtil.getPlayerData(sliceBoard).getShotType()) {
                case A:
                    return;
                case B:
                    hitbox = BULLET_SLOW_HITBOX_SMALL;
                    image = "small_barrier";
                    break;
                case C:
                default:
                    hitbox = BULLET_SLOW_HITBOX_LARGE;
                    image = "large_barrier";
                    break;
            }
            sliceBoard.publishMessage(spawnBuilder.makeVisibleGameObject(PLAYER_SPAWN)
                    .setHitbox(hitbox)
                    .setConstantSpriteRotation(-.5)
                    .setSpriteInstruction(SpriteInstruction.makeTransparent(image, 0d))
                    .setDrawOrder(DrawPlane.MIDGROUND, -100)
                    .markAsBulletSlower()
                    .setDamageGiveCommand(DamageGiveCommands.BULLET_SLOW)
                    .setProgram(BULLET_SLOW_BARRIER_PROGRAM)
                    .markMakeOpaqueWhenPlayerFocusedAndAlive()
                    .packageAsMessage()
            );
        }

        private Message<AddEntityOrder> makeSpawnerMessage() {
            return spawnBuilder.makeEntity()
                    .setSpawnComponent(new SpawnComponent().addSpawnUnit(STAGE_SPAWNER))
                    .packageAsNamedMessage(SPAWNER);
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        private void publishUIMessages(AbstractPublishSubscribeBoard sliceBoard) {
            int initX = 612;
            int ySpacing = 52;
            int lifeY = 60;
            int bombY = lifeY + ySpacing;
            int powerY = bombY + ySpacing;
            int lifeXSpacing = 22;
            int bombXSpacing = lifeXSpacing;

            for (int i = 0; i < MAX_LIVES; ++i) {
                DoublePoint pos = new DoublePoint(initX + (i * lifeXSpacing), lifeY);
                sliceBoard.publishMessage(makeLifeDisplayMessage(pos, i));
            }
            for (int i = 0; i < MAX_BOMBS; ++i) {
                DoublePoint pos = new DoublePoint(initX + (i * bombXSpacing), bombY);
                sliceBoard.publishMessage(makeBombDisplayMessage(pos, i));
            }
            sliceBoard.publishMessage(makePowerDisplayMessage(new DoublePoint(initX, powerY)));
        }

        private Message<AddEntityOrder> makeLifeDisplayMessage(DoublePoint pos, int n) {
            return spawnBuilder.makePosition(pos)
                    .setDrawOrder(DrawPlane.FOREGROUND, 1)
                    .setSpriteInstruction(new SpriteInstruction("ui_player"))
                    .packageAsNamedMessage(PlayerUISystem.makeLifeDisplayName(n));
        }

        private Message<AddEntityOrder> makeBombDisplayMessage(DoublePoint pos, int n) {
            return spawnBuilder.makePosition(pos)
                    .setDrawOrder(DrawPlane.FOREGROUND, 1)
                    .setSpriteInstruction(new SpriteInstruction("ui_spell"))
                    .packageAsNamedMessage(PlayerUISystem.makeBombDisplayName(n));
        }

        private Message<AddEntityOrder> makePowerDisplayMessage(DoublePoint pos){
            return spawnBuilder.makePosition(pos)
                    .setDrawOrder(DrawPlane.FOREGROUND, 1)
                    .setSpriteInstruction(new SpriteInstruction("ui_power"))
                    .packageAsNamedMessage(PlayerUISystem.POWER_DISPLAY);
        }

        private void publishPlayerHandle(AbstractPublishSubscribeBoard sliceBoard) {
            if (!sliceBoard.hasTopicalMessages(NEW_NAMED_ENTITIES)) {
                throw new RuntimeException("cannot find player");
            }
            List<Message<NamedEntityHandle>> newNamedEntities = sliceBoard.getMessageList(NEW_NAMED_ENTITIES);
            for (Message<NamedEntityHandle> message : newNamedEntities) {
                if (message.getMessage().getName().equals(PLAYER)) {
                    sliceBoard.publishMessage(new Message<>(PLAYER_HANDLE, message.getMessage(), Message.AGELESS));
                    return;
                }
            }
            throw new RuntimeException("cannot find player");
        }

        private void publishSpawnerHandle(AbstractPublishSubscribeBoard sliceBoard) {
            if (!sliceBoard.hasTopicalMessages(NEW_NAMED_ENTITIES)) {
                throw new RuntimeException("cannot find spawner");
            }
            List<Message<NamedEntityHandle>> newNamedEntities = sliceBoard.getMessageList(NEW_NAMED_ENTITIES);
            for (Message<NamedEntityHandle> message : newNamedEntities) {
                if (message.getMessage().getName().equals(SPAWNER)) {
                    sliceBoard.publishMessage(new Message<>(SPAWNER_HANDLE, message.getMessage(), Message.AGELESS));
                    return;
                }
            }
            throw new RuntimeException("cannot find spawner");
        }
    }
}