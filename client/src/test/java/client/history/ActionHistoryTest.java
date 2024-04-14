package client.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionHistoryTest {

    private ActionHistory history1;
    private ActionHistory history2;
    private ActionHistory history3;
    private Action testAction1;

    private String lastCall;

    @BeforeEach
    void setUp() {
        lastCall = null;
        history1 = new ActionHistory();
        history2 = new ActionHistory();
        history3 = new ActionHistory();
        testAction1 = createAction(1);
        history3.addAction(testAction1);
    }

    private Action createAction(int id) {
        return ActionFactory.createAction(
                () -> lastCall = "undo",
                () -> lastCall = "redo",
                "Action(" + id + ")"
        );
    }

    @Test
    void clear() {
        history3.clear();
        assertEquals(history1, history3);
    }

    @Test
    void addAction() {
        assertThrows(NullPointerException.class, () -> history1.addAction(null));
        history1.addAction(testAction1);
        assertEquals(history1, history3);
        assertNotEquals(history1, history2);
    }

    @Test
    void totalSize() {
        assertEquals(0, history1.totalSize());
        assertEquals(1, history3.totalSize());
        history3.addAction(testAction1);
        assertEquals(2, history3.totalSize());
        history3.undo();
        assertEquals(2, history3.totalSize());
    }

    @Test
    void amountOfAvailableUndoActions() {
        assertEquals(0, history1.amountOfAvailableUndoActions());
        assertEquals(1, history3.amountOfAvailableUndoActions());
        history3.addAction(testAction1);
        assertEquals(2, history3.amountOfAvailableUndoActions());
        history3.undo();
        assertEquals(1, history3.amountOfAvailableUndoActions());
    }

    @Test
    void amountOfAvailableRedoActions() {
        assertEquals(0, history1.amountOfAvailableRedoActions());
        assertEquals(0, history3.amountOfAvailableRedoActions());
        history3.addAction(testAction1);
        assertEquals(0, history3.amountOfAvailableRedoActions());
        history3.undo();
        assertEquals(1, history3.amountOfAvailableRedoActions());
    }

    @Test
    void isEmpty() {
        assertTrue(history1.isEmpty());
        assertFalse(history3.isEmpty());
    }

    @Test
    void hasUndoActions() {
        assertFalse(history1.hasUndoActions());
        assertTrue(history3.hasUndoActions());
        history3.undo();
        assertFalse(history3.hasUndoActions());
    }

    @Test
    void hasRedoActions() {
        assertFalse(history1.hasRedoActions());
        assertFalse(history3.hasRedoActions());
        history3.undo();
        assertTrue(history3.hasRedoActions());
    }

    @Test
    void undo() {
        history3.undo();
        assertEquals("undo", lastCall);
        assertThrows(NoUndoActionsLeftException.class, () -> history3.undo());
    }

    @Test
    void redo() {
        history3.undo();
        history3.redo();
        assertEquals("redo", lastCall);
        assertThrows(NoRedoActionsLeftException.class, () -> history3.redo());
    }

    @Test
    void testEquals() {
        assertEquals(history1, history2);
        assertNotEquals(history1, history3);
        history2.addAction(testAction1);

        // As the Action created by the factory uses the object equals, make
        // sure they're actually the same (i.e. == returns true).
        Action testAction2 = createAction(2);
        history2.addAction(testAction2);
        history3.addAction(testAction2);
        assertEquals(history2, history3);

        history3.undo();
        assertNotEquals(history2, history3);
        history3.redo();
        assertEquals(history2, history3);

        history2.addAction(createAction(3));
        assertNotEquals(history2, history3);
        history3.addAction(createAction(4));
        assertNotEquals(history2, history3);
    }

    @Test
    void testHashCode() {
        assertEquals(history1, history2);
        assertEquals(history1.hashCode(), history2.hashCode());

        history2.addAction(testAction1);
        assertEquals(history2, history3);
        assertEquals(history2.hashCode(), history3.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("ActionHistory[<CurrentState>]", history1.toString());
        assertEquals("ActionHistory[Undo: Action(1), <CurrentState>]", history3.toString());

        history3.undo();
        assertEquals("ActionHistory[<CurrentState>, Redo: Action(1)]", history3.toString());

        history3.redo();
        assertEquals("ActionHistory[Undo: Action(1), <CurrentState>]", history3.toString());

        history3.addAction(createAction(2));
        assertEquals("ActionHistory[Undo: Action(1), Undo: Action(2), <CurrentState>]", history3.toString());

        history3.undo();
        assertEquals("ActionHistory[Undo: Action(1), <CurrentState>, Redo: Action(2)]", history3.toString());

        history3.undo();
        assertEquals("ActionHistory[<CurrentState>, Redo: Action(1), Redo: Action(2)]", history3.toString());

        history3.addAction(createAction(5));
        assertEquals("ActionHistory[Undo: Action(5), <CurrentState>]", history3.toString());
    }
}
