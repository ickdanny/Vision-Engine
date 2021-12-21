package window.input;

import util.observer.AbstractPushSubject;
import util.observer.ConfigurablePushSubject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.AbstractMap;
import java.util.EnumMap;

class JComponentKeyOutputter implements AbstractKeyOutputter {
    private AbstractMap<KeyValues, AbstractPushSubject<Boolean>> keyBroadcasterMap;

    private JComponentKeyOutputter(){}
    JComponentKeyOutputter(JComponent component){
        keyBroadcasterMap = makeKeyBroadcasterMap(component);
    }

    private AbstractMap<KeyValues, AbstractPushSubject<Boolean>> makeKeyBroadcasterMap(JComponent component){
        EnumMap<KeyValues, AbstractPushSubject<Boolean>> keyBroadcasterMap = new EnumMap<>(KeyValues.class);
        for(KeyValues key : KeyValues.values()){
            keyBroadcasterMap.put(key, makeKeyBroadcaster(component, key));
        }
        return keyBroadcasterMap;
    }

    private AbstractPushSubject<Boolean> makeKeyBroadcaster(JComponent component, KeyValues key){
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        addInputMappings(inputMap, key);
        ActionMap actionMap = component.getActionMap();
        return makeKeyBroadcasterHookedToActionMap(actionMap, key);
    }

    private void addInputMappings(InputMap inputMap, KeyValues key){
        int[] modifiers = new int[]{
                0,
                InputEvent.SHIFT_DOWN_MASK,
                InputEvent.CTRL_DOWN_MASK,
                InputEvent.ALT_DOWN_MASK,
                InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK,
                InputEvent.SHIFT_DOWN_MASK + InputEvent.ALT_DOWN_MASK,
                InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK,
                InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK
        };
        for(int modifier : modifiers) {
            inputMap.put(KeyStroke.getKeyStroke(key.ID, modifier, false), getActionMapKey(key, false));
            inputMap.put(KeyStroke.getKeyStroke(key.ID, modifier, true), getActionMapKey(key, true));
        }
    }

    private AbstractPushSubject<Boolean> makeKeyBroadcasterHookedToActionMap(ActionMap actionMap, KeyValues key){
        ConfigurablePushSubject<Boolean> broadcaster = new ConfigurablePushSubject<>();
        actionMap.put(getActionMapKey(key, false), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                broadcaster.setPushData(true);
                broadcaster.broadcast();
            }
        });
        actionMap.put(getActionMapKey(key, true), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                broadcaster.setPushData(false);
                broadcaster.broadcast();
            }
        });
        return broadcaster;
    }

    private Object getActionMapKey(KeyValues key, boolean onKeyRelease){
        return "" + onKeyRelease + key;
    }

    @Override
    public AbstractPushSubject<Boolean> getKeyBroadcaster(KeyValues key){
        return keyBroadcasterMap.get(key);
    }
}
