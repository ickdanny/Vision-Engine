package window.input;

import util.observer.AbstractPushObserver;

import javax.swing.*;

@SuppressWarnings("FieldCanBeLocal")
public class InputController {
    private final AbstractKeyOutputter outputter;
    private final AbstractInputParser parser;
    private final AbstractInputBinder binder;

    private InputController(JComponent jComponent, AbstractInputValue[] inputValueArray, int numTurns){
        outputter = new JComponentKeyOutputter(jComponent);
        parser = new FixedSizeInputParser(inputValueArray, numTurns);
        binder = new StaticInputBinder(outputter, parser, inputValueArray);
    }

    public static InputController makeJComponentInputController
            (JComponent jComponent, AbstractInputValue[] inputValueArray, int numTurns) {
        return new InputController(jComponent, inputValueArray, numTurns);
    }

    public AbstractPushObserver<AbstractInputConverter> getInputConverterReceiver(){
        return parser.getInputConverterReceiver();
    }
}