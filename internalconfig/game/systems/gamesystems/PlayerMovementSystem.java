package internalconfig.game.systems.gamesystems;

import ecs.AbstractECSInterface;
import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.entity.EntityHandle;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.components.AbstractComponentTypeContainer;
import internalconfig.game.components.ComponentTypes;
import internalconfig.game.components.VelocityComponent;
import util.math.geometry.AbstractVector;
import util.math.geometry.CartesianVector;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.Message;

import java.util.List;
import java.util.Objects;

import static internalconfig.game.systems.Topics.*;
import static internalconfig.game.GameConfig.*;

public class PlayerMovementSystem implements AbstractSystem<Double> {

    private final AbstractComponentType<VelocityComponent> velocityComponentType;

    public PlayerMovementSystem(AbstractComponentTypeContainer componentTypeContainer){
        velocityComponentType = componentTypeContainer.getTypeInstance(ComponentTypes.VelocityComponentType.class);
    }

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private class Instance implements AbstractSystemInstance<Double>{
        private EntityHandle player;

        private boolean active;

        private final PlayerInputStates pastInputStates;
        private final PlayerInputStates inputStates;

        public Instance() {
            pastInputStates = new PlayerInputStates();
            inputStates = new PlayerInputStates();
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            AbstractPublishSubscribeBoard sliceBoard = ecsInterface.getSliceBoard();
            AbstractDataStorage dataStorage = ecsInterface.getSliceData();
            if(player == null){
                player = GameUtil.getPlayer(sliceBoard);
            }

            inputStates.reset();
            checkActive(sliceBoard);
            if(active) {
                for (Message<GameCommands> message : sliceBoard.getMessageList(GAME_COMMANDS)) {
                    parseGameCommand(message.getMessage());
                }
            }
            if(!inputStates.equals(pastInputStates)){
                AbstractVector velocity = calculateVelocity(inputStates);

                if(dataStorage.containsComponent(player, velocityComponentType)){
                    VelocityComponent velocityComponent = dataStorage.getComponent(player, velocityComponentType);
                    velocityComponent.setVelocity(velocity);
                }
                pastInputStates.setTo(inputStates);
            }

            sliceBoard.ageAndCullMessages();
        }

        private void checkActive(AbstractPublishSubscribeBoard sliceBoard){
            if(sliceBoard.hasTopicalMessages(PLAYER_STATE_ENTRY)) {
                List<Message<PlayerStateSystem.States>> list = sliceBoard.getMessageList(PLAYER_STATE_ENTRY);
                for(Message<PlayerStateSystem.States> message : list){
                    PlayerStateSystem.States state = message.getMessage();
                    switch(state){
                        case NORMAL:
                        case BOMBING:
                        case RESPAWN_INVULNERABLE:
                            active = true;
                            break;
                        case DEAD:
                        case CHECK_CONTINUE:
                        case RESPAWNING:
                        case GAME_OVER:
                            active = false;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + state);
                    }
                }
            }
        }

        private void parseGameCommand(GameCommands command){
            switch(command){
                case FOCUS:
                    inputStates.focused = true;
                    break;
                case UP:
                    inputStates.up = true;
                    break;
                case DOWN:
                    inputStates.down = true;
                    break;
                case LEFT:
                    inputStates.left = true;
                    break;
                case RIGHT:
                    inputStates.right = true;
                    break;
            }
        }

        private AbstractVector calculateVelocity(PlayerInputStates inputStates){
            CartesianVector toRet = new CartesianVector();
            if(inputStates.isZeroDirection()) {
                return toRet;
            }
            if(inputStates.up){
                toRet.setY(-1);
            }
            if(inputStates.down){
                toRet.addY(1);
            }
            if(inputStates.left){
                toRet.setX(-1);
            }
            if(inputStates.right){
                toRet.setX(1);
            }
            toRet.setMagnitude(inputStates.focused ? FOCUSED_SPEED : PLAYER_SPEED);
            return toRet;
        }

        private class PlayerInputStates{
            private boolean focused;
            private boolean up;
            private boolean down;
            private boolean left;
            private boolean right;

            private PlayerInputStates(){
                reset();
            }

            private void reset(){
                focused = false;
                up = false;
                down = false;
                left = false;
                right = false;
            }

            private void setTo(PlayerInputStates other){
                this.focused = other.focused;
                this.up = other.up;
                this.down = other.down;
                this.left = other.left;
                this.right = other.right;
            }

            private boolean isZeroDirection(){
                return (up && down && left && right) || (!up && !down && !left && !right);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof PlayerInputStates)) return false;
                PlayerInputStates that = (PlayerInputStates) o;
                return up == that.up &&
                       down == that.down &&
                       left == that.left &&
                       right == that.right &&
                       ((isZeroDirection() && that.isZeroDirection()) || (focused == that.focused));
            }

            @Override
            public int hashCode() {
                return Objects.hash(isZeroDirection() || focused, up, down, left, right);
            }

            @Override
            public String toString() {
                return "PlayerInputStates{" +
                        "focused=" + focused +
                        ", up=" + up +
                        ", down=" + down +
                        ", left=" + left +
                        ", right=" + right +
                        '}';
            }
        }
    }
}