package client.history;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionFactoryTest {

    private String state;

    @Test
    void createAction() {
        assertThrows(NullPointerException.class, () -> ActionFactory.createAction(() -> {}, null));
        assertThrows(NullPointerException.class, () -> ActionFactory.createAction(null, () -> {}));
        assertDoesNotThrow(() -> ActionFactory.createAction(() -> {}, () -> {}));
    }

    @Test
    void testCreateAction() {
        assertEquals("test", ActionFactory.createAction(() -> {}, () -> {}, "test").toString());
    }

    @Test
    void proofThatItWorks() {
        ActionHistory actionHistory = new ActionHistory();
        actionHistory.addAction(
                ActionFactory.createAction(() -> {}, () -> {}, "Text inputted")
        );
        assertEquals("ActionHistory[Undo: Text inputted, <CurrentState>]", actionHistory.toString());
    }

    @Test
    void testExternalState() {
        state = null;
        ActionHistory actionHistory = new ActionHistory();
        Runnable undo = () -> state = "undo";
        Runnable redo = () -> state = "redo";

        actionHistory.addAction(
                ActionFactory.createAction(undo, redo, "Text inputted")
        );
        assertNull(state);
        actionHistory.undo();
        assertEquals("undo", state);
        actionHistory.redo();
        assertEquals("redo", state);
    }
}
