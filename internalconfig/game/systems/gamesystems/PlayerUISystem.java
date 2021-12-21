package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.ECSUtil;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.entity.NamedEntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import ecs.system.criticalorders.AddComponentOrder;
import ecs.system.criticalorders.RemoveComponentOrder;
import ecs.system.criticalorders.SetComponentOrder;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.PlayerData;
import resource.AbstractResourceManager;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.awt.*;
import java.awt.image.BufferedImage;

import static internalconfig.game.components.ComponentTypes.*;
import static internalconfig.game.GameConfig.*;
import static ecs.ECSTopics.*;

public class PlayerUISystem implements AbstractSystem<Double> {

    private static final String POWER_IMAGE = "ui_power";
    private static final String POWER_MAX_IMAGE = "ui_power_max";

    private static final String LIFE_DISPLAY = "LIFE_DISPLAY_";
    private static final String BOMB_DISPLAY = "BOMB_DISPLAY_";

    public static final String POWER_DISPLAY = "POWER_DISPLAY";

    private final AbstractComponentType<Void> visibleMarker;
    private final AbstractComponentType<SpriteInstruction> spriteInstructionComponentType;
    private final AbstractComponentType<Rectangle> spriteSubImageComponentType;

    private final int powerBarWidth;
    private final int powerBarHeight;

    public PlayerUISystem(AbstractResourceManager<BufferedImage> imageManager,
                          AbstractComponentTypeContainer componentTypeContainer){
        visibleMarker = componentTypeContainer.getTypeInstance(VisibleMarker.class);
        spriteInstructionComponentType = componentTypeContainer.getTypeInstance(SpriteInstructionComponentType.class);
        spriteSubImageComponentType = componentTypeContainer.getTypeInstance(SpriteSubImageComponentType.class);

        BufferedImage powerBarImage = imageManager.getResource(POWER_IMAGE).getData();
        powerBarWidth = powerBarImage.getWidth();
        powerBarHeight = powerBarImage.getHeight();
    }

    public static String makeLifeDisplayName(int n){
        return LIFE_DISPLAY + n;
    }

    public static String makeBombDisplayName(int n){
        return BOMB_DISPLAY + n;
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{

        private static final int UNSET_FIELD = -3;

        private PlayerData playerData;

        private final EntityHandle[] lifeDisplay;
        private final EntityHandle[] bombDisplay;
        private EntityHandle powerDisplay;
        private int currentLifeIndex;
        private int currentBombIndex;

        private Instance(){
            lifeDisplay = new EntityHandle[MAX_LIVES];
            bombDisplay = new EntityHandle[MAX_BOMBS];
            powerDisplay = null;
            currentLifeIndex = UNSET_FIELD;
            currentBombIndex = UNSET_FIELD;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            if(playerData == null){
                init(sliceBoard);
            }

            updateUI(ecsInterface, sliceBoard);

            sliceBoard.ageAndCullMessages();
        }

        private void init(AbstractPublishSubscribeBoard sliceBoard){
            playerData = GameUtil.getPlayerData(sliceBoard);
            for(Message<NamedEntityHandle> message : sliceBoard.getMessageList(NEW_NAMED_ENTITIES)){
                NamedEntityHandle handle = message.getMessage();
                String name = handle.getName();
                if(name.contains(LIFE_DISPLAY)){
                    lifeDisplay[Integer.parseInt(name.substring(LIFE_DISPLAY.length()))] = handle;
                }
                else if(name.contains(BOMB_DISPLAY)){
                    bombDisplay[Integer.parseInt(name.substring(BOMB_DISPLAY.length()))] = handle;
                }
                else if(name.equals(POWER_DISPLAY)){
                    powerDisplay = handle;
                }
            }
            currentLifeIndex = -1;
            currentBombIndex = -1;
            throwIfAnyEntityNull();
        }

        private void throwIfAnyEntityNull(){
            for(int i = 0; i < lifeDisplay.length; ++i){
                EntityHandle handle = lifeDisplay[i];
                if(handle == null){
                    throw new RuntimeException("lifeDisplay entity " + i + " is null");
                }
            }
            for(int i = 0; i < bombDisplay.length; ++i){
                EntityHandle handle = bombDisplay[i];
                if(handle == null){
                    throw new RuntimeException("bombDisplay entity " + i + " is null");
                }
            }
            if(powerDisplay == null){
                throw new RuntimeException("powerDisplay entity is null");
            }
        }

        private void updateUI(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
            updateLives(sliceBoard);
            updateBombs(sliceBoard);
            updatePower(ecsInterface, sliceBoard);
        }

        private void updateLives(AbstractPublishSubscribeBoard sliceBoard){
            int playerLifeIndex = playerData.getLives() - 1;
            if(playerLifeIndex >= -1) {
                if (currentLifeIndex > playerLifeIndex) {
                    for (int i = Math.min(currentLifeIndex, MAX_LIVES - 1); i > playerLifeIndex; --i) {
                        RemoveComponentOrder order = new RemoveComponentOrder(lifeDisplay[i], visibleMarker);
                        sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(order));
                    }
                    currentLifeIndex = playerLifeIndex;
                } else if (currentLifeIndex < playerLifeIndex) {
                    for (int i = Math.max(currentLifeIndex + 1, 0); i <= playerLifeIndex; ++i) {
                        SetComponentOrder<?> order = new SetComponentOrder<>(
                                lifeDisplay[i], visibleMarker, null
                        );
                        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(order));
                    }
                    currentLifeIndex = playerLifeIndex;
                }
            }
        }

        private void updateBombs(AbstractPublishSubscribeBoard sliceBoard){
            int playerBombIndex = playerData.getBombs() - 1;
            if(playerBombIndex >= -1) {
                if (currentBombIndex > playerBombIndex) {
                    for (int i = Math.min(currentBombIndex, MAX_BOMBS - 1); i > playerBombIndex; --i) {
                        RemoveComponentOrder order = new RemoveComponentOrder(bombDisplay[i], visibleMarker);
                        sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(order));
                    }
                    currentBombIndex = playerBombIndex;
                } else if (currentBombIndex < playerBombIndex) {
                    for (int i = Math.max(currentBombIndex + 1, 0); i <= playerBombIndex; ++i) {
                        SetComponentOrder<?> order = new SetComponentOrder<>(
                                bombDisplay[i], visibleMarker, null
                        );
                        sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(order));
                    }
                    currentBombIndex = playerBombIndex;
                }
            }
        }

        private void updatePower(AbstractECSInterface ecsInterface, AbstractPublishSubscribeBoard sliceBoard){
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            SpriteInstruction spriteInstruction = dataStorage.getComponent(powerDisplay, spriteInstructionComponentType);

            int currentPower = playerData.getPower();
            if(currentPower == 0){
                if(dataStorage.containsComponent(powerDisplay, visibleMarker)){
                    sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(
                            new RemoveComponentOrder(powerDisplay, visibleMarker)
                    ));
                }
            }
            else{
                if(!dataStorage.containsComponent(powerDisplay, visibleMarker)){
                    sliceBoard.publishMessage(ECSUtil.makeAddComponentMessage(
                            new AddComponentOrder<>(powerDisplay, visibleMarker, null)
                    ));
                }
            }
            if (currentPower != MAX_POWER){
                if(!spriteInstruction.getImage().equals(POWER_IMAGE)){
                    spriteInstruction.setImage(POWER_IMAGE);
                }

                double ratio = ((double)currentPower)/MAX_POWER;
                int width = (int)(ratio * powerBarWidth);


                if(dataStorage.containsComponent(powerDisplay, spriteSubImageComponentType)) {
                    Rectangle subImage = dataStorage.getComponent(powerDisplay, spriteSubImageComponentType);
                    if (subImage.getWidth() != width) {
                        updatePowerBarWidth(sliceBoard, width);
                    }
                }
                else{
                    updatePowerBarWidth(sliceBoard, width);
                }
            }
            else{
                if(dataStorage.containsComponent(powerDisplay, spriteSubImageComponentType)){
                    sliceBoard.publishMessage(ECSUtil.makeRemoveComponentMessage(new RemoveComponentOrder(
                            powerDisplay,
                            spriteSubImageComponentType)
                    ));
                }

                if(!spriteInstruction.getImage().equals(POWER_MAX_IMAGE)){
                    spriteInstruction.setImage(POWER_MAX_IMAGE);
                }
            }
        }

        private void updatePowerBarWidth(AbstractPublishSubscribeBoard sliceBoard, int width){
            sliceBoard.publishMessage(ECSUtil.makeSetComponentMessage(new SetComponentOrder<>(
                    powerDisplay,
                    spriteSubImageComponentType,
                    //new Rectangle(powerBarWidth - width, 0, width, powerBarHeight)
                    new Rectangle(0, 0, width, powerBarHeight)
            )));
        }
    }
}
