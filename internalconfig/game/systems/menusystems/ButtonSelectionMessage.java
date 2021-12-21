package internalconfig.game.systems.menusystems;

import ecs.entity.EntityHandle;

import java.util.Objects;

public class ButtonSelectionMessage {
    private final EntityHandle button;
    private final ButtonSelectionState state;

    public ButtonSelectionMessage(EntityHandle button, ButtonSelectionState state) {
        this.button = button;
        this.state = state;
    }

    public EntityHandle getButton() {
        return button;
    }

    public ButtonSelectionState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ButtonSelectionMessage)) return false;
        ButtonSelectionMessage that = (ButtonSelectionMessage) o;
        return Objects.equals(button, that.button) &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(button, state);
    }

    @Override
    public String toString() {
        return state.name() + button;
    }
}
