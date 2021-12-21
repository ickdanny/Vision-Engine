package window.input;

import static java.awt.event.KeyEvent.*;

public enum KeyValues {
    K_UNDEFINED(VK_UNDEFINED),

    K_ESCAPE(VK_ESCAPE),

    K_Space(VK_SPACE),

    K_BACK_QUOTE(VK_BACK_QUOTE),
    K_1(VK_1),
    K_2(VK_2),
    K_3(VK_3),
    K_4(VK_4),
    K_5(VK_5),
    K_6(VK_6),
    K_7(VK_7),
    K_8(VK_8),
    K_9(VK_9),
    K_0(VK_0),
    K_MINUS(VK_MINUS),
    K_EQUALS(VK_EQUALS),
    K_BACK_SLASH(VK_BACK_SLASH),
    K_BACK_SPACE(VK_BACK_SPACE),

    K_A(VK_A),
    K_B(VK_B),
    K_C(VK_C),
    K_D(VK_D),
    K_E(VK_E),
    K_F(VK_F),
    K_G(VK_G),
    K_H(VK_H),
    K_I(VK_I),
    K_J(VK_J),
    K_K(VK_K),
    K_L(VK_L),
    K_M(VK_M),
    K_N(VK_N),
    K_O(VK_O),
    K_P(VK_P),
    K_Q(VK_Q),
    K_R(VK_R),
    K_S(VK_S),
    K_T(VK_T),
    K_U(VK_U),
    K_V(VK_V),
    K_W(VK_W),
    K_X(VK_X),
    K_Y(VK_Y),
    K_Z(VK_Z),
    K_ENTER(VK_ENTER),
    K_OPEN_BRACKET(VK_OPEN_BRACKET),
    K_CLOSE_BRACKET(VK_CLOSE_BRACKET),
    K_SEMICOLON(VK_SEMICOLON),
    K_QUOTE(VK_QUOTE),
    K_COMMA(VK_COMMA),
    K_PERIOD(VK_PERIOD),
    K_SLASH(VK_SLASH),

    K_TAB(VK_TAB),
    K_SHIFT(VK_SHIFT),
    K_CONTROL(VK_CONTROL),
    K_ALT(VK_ALT),

    K_NUMPAD1(VK_NUMPAD1),
    K_NUMPAD2(VK_NUMPAD2),
    K_NUMPAD3(VK_NUMPAD3),
    K_NUMPAD4(VK_NUMPAD4),
    K_NUMPAD5(VK_NUMPAD5),
    K_NUMPAD6(VK_NUMPAD6),
    K_NUMPAD7(VK_NUMPAD7),
    K_NUMPAD8(VK_NUMPAD8),
    K_NUMPAD9(VK_NUMPAD9),
    K_NUMPAD0(VK_NUMPAD0),

    K_LEFT(VK_LEFT),
    K_RIGHT(VK_RIGHT),
    K_UP(VK_UP),
    K_DOWN(VK_DOWN)
    ;
    public final int ID;
    KeyValues(int id){
        this.ID = id;
    }
}
