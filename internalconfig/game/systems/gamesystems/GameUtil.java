package internalconfig.game.systems.gamesystems;

import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import internalconfig.game.components.VelocityComponent;
import internalconfig.game.systems.Difficulty;
import internalconfig.game.systems.GameConfigObject;
import internalconfig.game.systems.GameMode;
import internalconfig.game.systems.PlayerData;
import util.math.geometry.AbstractVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.math.geometry.TwoFramePosition;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.NoSuchElementException;
import java.util.Random;

import static internalconfig.game.GameConfig.HEIGHT;
import static internalconfig.game.GameConfig.WIDTH;
import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.GlobalTopics.*;

public final class GameUtil {

    public static EntityHandle getPlayer(AbstractPublishSubscribeBoard sliceBoard){
        try {
            return sliceBoard.getMessageList(PLAYER_HANDLE).iterator().next().getMessage();
        } catch (NoSuchElementException nsee) {
            throw new RuntimeException("cannot find player!", nsee);
        }
    }

    public static PlayerData getPlayerData(AbstractPublishSubscribeBoard sliceBoard){
        try {
            return sliceBoard.getMessageList(PLAYER_DATA).iterator().next().getMessage();
        } catch (NoSuchElementException nsee) {
            throw new RuntimeException("cannot find player data!", nsee);
        }
    }

    public static boolean isPlayerFocused(AbstractPublishSubscribeBoard sliceBoard){
        for (Message<GameCommands> message : sliceBoard.getMessageList(GAME_COMMANDS)) {
            if(message.getMessage() == GameCommands.FOCUS){
                return true;
            }
        }
        return false;
    }

    public static EntityHandle getSpawner(AbstractPublishSubscribeBoard sliceBoard){
        try {
            return sliceBoard.getMessageList(SPAWNER_HANDLE).iterator().next().getMessage();
        } catch (NoSuchElementException nsee) {
            throw new RuntimeException("cannot find spawner!", nsee);
        }
    }

    public static GameConfigObject getGameConfigObject(AbstractPublishSubscribeBoard globalBoard){
        try{
            return globalBoard.getMessageList(GAME_CONFIG_OBJECT).iterator().next().getMessage();
        } catch(NoSuchElementException nsee){
            throw new RuntimeException("cannot find game config object!", nsee);
        }
    }

    public static GameMode getGameMode(AbstractPublishSubscribeBoard globalBoard){
        return getGameConfigObject(globalBoard).getGameMode();
    }

    public static Difficulty getDifficulty(AbstractPublishSubscribeBoard globalBoard){
        return getGameConfigObject(globalBoard).getDifficulty();
    }

    public static int getStage(AbstractPublishSubscribeBoard globalBoard){
        return getGameConfigObject(globalBoard).getStage();
    }

    public static Random getRandom(AbstractPublishSubscribeBoard globalBoard){
        return getGameConfigObject(globalBoard).getRandom();
    }

    public static long getRandomSeed(AbstractPublishSubscribeBoard globalBoard){
        return getGameConfigObject(globalBoard).getRandomSeed();
    }

    public static Random getPseudoRandomBasedOnEntity(AbstractPublishSubscribeBoard globalBoard,
                                                      AbstractDataStorage dataStorage,
                                                      int entityID){
        EntityHandle handle = dataStorage.makeHandle(entityID);
        Random random = new Random(getRandomSeed(globalBoard) + handle.getEntityID() + handle.getGeneration());
        random.nextDouble();
        random.nextDouble();
        return random;
    }

    public static Random getPseudoRandomBasedOnDouble(AbstractPublishSubscribeBoard globalBoard, double d){
        Random random = new Random(getRandomSeed(globalBoard) + ((long)d));
        random.nextDouble();
        random.nextDouble();
        return random;
    }

    public static Random getPseudoRandomBasedOnPosition(AbstractPublishSubscribeBoard globalBoard, DoublePoint pos){
        Random random = new Random(getRandomSeed(globalBoard) + ((long)(pos.hashCode())));
        random.nextDouble();
        random.nextDouble();
        return random;
    }

    public static DoublePoint getPos(AbstractDataStorage dataStorage,
                                     int entityID,
                                     AbstractComponentType<TwoFramePosition> positionComponentType){
        EntityHandle handle = dataStorage.makeHandle(entityID);
        return dataStorage.getComponent(handle, positionComponentType).getPos();
    }

    public static DoublePoint getPos(AbstractDataStorage dataStorage,
                                     EntityHandle handle,
                                     AbstractComponentType<TwoFramePosition> positionComponentType){
        return dataStorage.getComponent(handle, positionComponentType).getPos();
    }

    public static AbstractVector getVelocity(AbstractDataStorage dataStorage,
                                             int entityID,
                                             AbstractComponentType<VelocityComponent> velocityComponentType){
        EntityHandle handle = dataStorage.makeHandle(entityID);
        return new PolarVector(dataStorage.getComponent(handle, velocityComponentType).getVelocity());
    }

    public static double getAngleToPlayer(AbstractDataStorage dataStorage,
                                         AbstractPublishSubscribeBoard sliceBoard,
                                         AbstractComponentType<TwoFramePosition> positionComponentType,
                                         DoublePoint pos){
        DoublePoint playerPos = getPos(dataStorage, getPlayer(sliceBoard), positionComponentType);
        return GeometryUtil.angleFromAToB(pos, playerPos).getAngle();
    }

    public static boolean isOutOfBounds(DoublePoint pos, double bound){
        double x = pos.getX();
        double y = pos.getY();
        double highXBound = WIDTH - bound;
        double highYBound = HEIGHT - bound;
        return x < bound || x > highXBound || y < bound || y > highYBound;
    }

    public static DoublePoint inboundPosition(DoublePoint pos, double bound){
        DoublePoint copyOfPos = new DoublePoint(pos);
        double x = copyOfPos.getX();
        double y = copyOfPos.getY();
        double highXBound = WIDTH - bound;
        double highYBound = HEIGHT - bound;
        if(x < bound){
            copyOfPos.setX(bound);
        }
        if(x > highXBound){
            copyOfPos.setX(highXBound);
        }
        if(y < bound){
            copyOfPos.setY(bound);
        }
        if(y > highYBound){
            copyOfPos.setY(highYBound);
        }
        return copyOfPos;
    }
}
