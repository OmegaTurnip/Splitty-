package client.history;

import java.util.Objects;

/**
 * A class that stores history of actions, allowing for undo and a redo
 * functionality.
 */
public class ActionHistory {

    // Dummies to make the logic simpler
    private final ActionNode head = new ActionNode(null);
    private final ActionNode tail = new ActionNode(null);

    /**
     * This {@code ActionNode} will point to the first 'undo' node.
     */
    private ActionNode currentNode;

    private int totalSize;
    private int amountOfAvailableUndoActions;


    /**
     * Constructs an empty history.
     */
    public ActionHistory() {
        clear();
    }


    /**
     * Resets the history to be empty. Should be called after an irreversible
     * change.
     */
    public void clear() {
        // If the clear function doesn't actually free memory fast enough,
        // uncomment this code. This code would "unnecessarily" make the clear
        // function O(n) instead of O(1), but it helps a (the) generational GC
        // if the discarded nodes inhabit more than one generation.

        /*
         * ActionNode cursor = head.next;
         * while (cursor != tail) {
         *     ActionNode temp = cursor;
         *     cursor = cursor.next;
         *     temp.action = null;
         *     temp.previous = null;
         *     temp.next = null;
         * }
         */

        head.next = tail;
        tail.previous = head;
        currentNode = head;
        totalSize = 0;
        amountOfAvailableUndoActions = 0;
    }

    /**
     * Adds an action to the history. Overwrites any available redo actions.
     *
     * @param   action
     *          The action to be added.
     *
     * @throws  NullPointerException
     *          If {@code action} is {@code null}.
     */
    public void addAction(Action action) throws NullPointerException {
        if (action == null)
            throw new NullPointerException();

        addBetween(currentNode, tail, action);
        currentNode = currentNode.next;

        amountOfAvailableUndoActions++;
        totalSize = amountOfAvailableUndoActions;
    }

    /**
     * Gets the total size of the history. I.e. the amount of available undo and
     * redo actions.
     *
     * @return  The total size of the history.
     */
    public int totalSize() {
        return totalSize;
    }

    /**
     * Gets the amount of available undo actions.
     *
     * @return  The amount of available undo actions.
     */
    public int amountOfAvailableUndoActions() {
        return amountOfAvailableUndoActions;
    }

    /**
     * Gets the amount of available redo actions.
     *
     * @return  The amount of available redo actions.
     */
    public int amountOfAvailableRedoActions() {
        return totalSize - amountOfAvailableUndoActions;
    }

    /**
     * Returns whether this {@code ActionHistory} object contains no actions.
     *
     * @return  Whether this {@code ActionHistory} object contains no actions.
     */
    public boolean isEmpty() {
        return totalSize == 0;
    }

    /**
     * Return whether the history contains any undo actions, and thus if the
     * {@link ActionHistory#undo()} function can be called.
     *
     * @return  Whether the history contains any undo actions.
     */
    public boolean hasUndoActions() {
        return amountOfAvailableUndoActions != 0;
    }

    /**
     * Return whether the history contains any redo actions, and thus if the
     * {@link ActionHistory#redo()} function can be called.
     *
     * @return  Whether the history contains any redo actions.
     */
    public boolean hasRedoActions() {
        return amountOfAvailableRedoActions() != 0;
    }

    /**
     * Undoes the current action in the action history. Use
     * {@link ActionHistory#hasUndoActions()} to check if any undo actions are
     * left, and thus if this function can be called.
     *
     * @throws  NoUndoActionsLeftException
     *          If no undo action can be done.
     */
    public void undo() throws NoUndoActionsLeftException {
        if (!hasUndoActions())
            throw new NoUndoActionsLeftException();

        this.currentNode.action.undo();
        this.currentNode = this.currentNode.previous;
        this.amountOfAvailableUndoActions--;
    }

    /**
     * Redoes the current action in the action history. Use
     * {@link ActionHistory#hasRedoActions()} to check if any redo actions are
     * left, and thus if this function can be called.
     *
     * @throws  NoRedoActionsLeftException
     *          If no redo action can be done.
     */
    public void redo() throws NoRedoActionsLeftException {
        if (!hasRedoActions())
            throw new NoRedoActionsLeftException();

        this.currentNode = this.currentNode.next;
        this.currentNode.action.redo();
        this.amountOfAvailableUndoActions++;
    }

    /**
     * Checks whether {@code other} is equal to {@code this}.
     *
     * @param   other
     *          The {@code Object} to check.
     *
     * @return  Whether {@code other} is equal to {@code this}.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        ActionHistory that = (ActionHistory) other;

        // this if statement in combination with the equalsSequence() call also
        // implicitly checks if this.currentNode is equal to that.currentNode
        if (this.totalSize != that.totalSize
                || this.amountOfAvailableUndoActions !=
                that.amountOfAvailableUndoActions)
            return false;

        return equalsSequence(that);
    }

    /**
     * Generates a hash code value for this {@code ActionHistory}. Based upon
     * the {@link java.util.List#hashCode()} specification.
     *
     * @return  The hash code value for this {@code ActionHistory}.
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        ActionNode cursor = head;

        while (cursor.next != tail) {
            cursor = cursor.next;
            hashCode = 31 * hashCode +
                    (cursor.action == null ? 0 : cursor.action.hashCode());
        }
        return hashCode;
    }

    /**
     * Generates a string representation of the history, including the current
     * state and possible redo actions.
     *
     * @return  A string representation of the history.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ActionHistory[");
        ActionNode cursor = head;

        while (cursor != currentNode) {
            cursor = cursor.next;
            stringBuilder.append("Undo: ").append(cursor.action).append(", ");
        }

        stringBuilder.append("<CurrentState>");

        while (cursor.next != tail) {
            cursor = cursor.next;
            stringBuilder.append(", Redo: ").append(cursor.action);
        }
        return stringBuilder.append(']').toString();
    }


    /**
     * This is just here because the cyclomatic complexity in the
     * {@link ActionHistory#equals(Object)} method would otherwise be too high.
     *
     * @param   that
     *          The {@code ActionHistory} to check.
     *
     * @return  Whether the nodes in {@code this} and {@code that} match.
     */
    private boolean equalsSequence(ActionHistory that) {
        ActionNode thisNode = this.head.next;
        ActionNode thatNode = that.head.next;

        while (true) {
            if (thisNode == this.tail && thatNode == that.tail)
                return true;

            else if (thisNode == this.tail || thatNode == that.tail)
                return false;

            else if (!Objects.equals(thisNode.action, thatNode.action))
                return false;

            thisNode = thisNode.next;
            thatNode = thatNode.next;
        }
    }

    private void addBetween(ActionNode predecessor, ActionNode successor,
                            Action action) {
        addBetween(predecessor, successor, new ActionNode(action));
    }

    private void addBetween(ActionNode predecessor, ActionNode successor,
                            ActionNode newNode) {
        newNode.next = successor;
        newNode.previous = predecessor;
        predecessor.next = newNode;
        successor.previous = newNode;
    }


    private final static class ActionNode {
        private ActionNode previous;
        private ActionNode next;
        private final Action action;

        public ActionNode(Action action) {
            this.action = action;
        }
    }
}
