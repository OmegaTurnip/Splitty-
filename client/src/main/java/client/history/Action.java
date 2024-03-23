package client.history;


/**
 * I recommend that, even tho I don't force it by making this an abstract class,
 * that implementations of this interface also properly override
 * {@link Object#equals(Object)}, {@link Object#hashCode()} and
 * {@link Object#toString()} to ensure those functions in the
 * {@link ActionHistory} class behave expectedly.
 */
public interface Action {

    /**
     * Undoes the action.
     */
    void undo();

    /**
     * Redoes the action after an undo.
     */
    void redo();
}
