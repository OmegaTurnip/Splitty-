package client.history;

import java.util.Objects;

public final class ActionFactory {

    /**
     * Creates and returns a simple object implementing the {@link Action}
     * interface to be used in the {@link ActionHistory} class. It takes two
     * lambda functions, the first one will be called for every undo, the second
     * one for every redo. The {@link ActionImpl#toString() toString()} method
     * will return the {@link Object#toString()} value. <em><strong>THIS
     * FUNCTION SHOULD ONLY BE USED FOR SIMPLE UNDO/REDO FUNCTIONS.</strong>
     * </em> In any other case making a proper implementation of the {@code
     * Action} interface would make more sense.
     *
     * @param   undo
     *          The function that should be executed when undoing.
     * @param   redo
     *          The function that should be executed when redoing.
     *
     * @return  A simple object to be used in the {@code ActionHistory} class.
     */
    public static Action createAction(Runnable undo, Runnable redo) {
        return createAction(undo, redo, null);
    }

    /**
     * Creates and returns a simple object implementing the {@link Action}
     * interface to be used in the {@link ActionHistory} class. It takes two
     * lambda functions, the first one will be called for every undo, the second
     * one for every redo. It also takes a description which will be returned by
     * the {@link ActionImpl#toString() toString()} method. If {@code null}, the
     * {@code toString()} method will return the {@link Object#toString()}
     * value. <em><strong>THIS FUNCTION SHOULD ONLY BE USED FOR SIMPLE UNDO/REDO
     * FUNCTIONS.</strong></em> In any other case making a proper implementation
     * of the {@code Action} interface would make more sense.
     *
     * @param   undo
     *          The function that should be executed when undoing.
     * @param   redo
     *          The function that should be executed when redoing.
     * @param   description
     *          A {@code String} describing the action.
     *
     * @return  A simple object to be used in the {@code ActionHistory} class.
     */
    public static Action createAction(Runnable undo, Runnable redo,
                                          String description) {
        return new ActionImpl(undo, redo, description);
    }


    /**
     * A simple {@link Action} implementation that can be used if the action
     * isn't too complex. An action should have an actual full class
     * implementation if the action is more complex than be comfortably
     * expressed using this class.
     */
    private final static class ActionImpl implements Action {
        private final Runnable undo;
        private final Runnable redo;
        private final String description;

        private ActionImpl(Runnable undo, Runnable redo, String description) {
            Objects.requireNonNull(undo, "undo is null");
            Objects.requireNonNull(redo, "redo is null");

            this.undo = undo;
            this.redo = redo;
            this.description = description;
        }

        /**
         * Undoes the action.
         */
        @Override
        public void undo() {
            undo.run();
        }

        /**
         * Redoes the action after an undo.
         */
        @Override
        public void redo() {
            redo.run();
        }

        /**
         * Gets a string representing this object. If no string was given it
         * will return the default {@link Object#toString()} value.
         *
         * @return  A string representing this object.
         */
        @Override
        public String toString() {
            return description != null ? description : super.toString();
        }
    }
}
