package space.interlayer.structure.elementary.alpha.core;

import java.util.Optional;

/**
 * Container is an intermediate interface that expects implementations
 * to enclose other nodes rather than particular value.
 *
 * @param <N> Specific node type.
 * @param <T> Child unit type.
 */
public interface ContainerNode<N extends Node<N>, T> extends Node<N> {
    /**
     * @return Accessor for node children. Clients <b>must not</b>
     * assume that this method will return children in original order
     * unless additional evidence is provided by other means.
     */
    Iterable<? extends T> getChildren();

    default boolean isCountAware() {
        return this instanceof CountAware;
    }

    default Optional<? extends CountAware<? extends N, ? extends T>> asCountAware() {
        return isCountAware() ? Optional.of((CountAware<? extends N, ? extends T>) this) : Optional.empty();
    }

    default CountAware<? extends N, ? extends T> toCountAware() {
        if (isCountAware()) {
            return (CountAware<? extends N, ? extends T>) this;
        }

        throw new ClassCastException("Can't cast " + this + " to " + CountAware.class);
    }

    /**
     * Implementations are highly encouraged to reimplement this method.
     * In most of the cases there should be an easier, rational and more
     * performant way to check for emptiness.
     *
     * @return Whether or not this container isn't populated.
     */
    default boolean isEmpty() {
        return !getChildren().iterator().hasNext();
    }

    /**
     * Child interface for implementations that are aware of their
     * number of children.
     *
     * @param <N>
     * @param <T>
     */
    interface CountAware<N extends Node<N>, T> extends ContainerNode<N, T> {
        /**
         * @return Number of enclosed children units.
         */
        long getChildCount();

        @Override
        default boolean isEmpty() {
            return getChildCount() == 0;
        }
    }

    /**
     * Interface for implementations that allow to change their
     * contents.
     *
     * @param <N> Specific node type.
     * @param <T> Child unit type.
     */
    interface Mutable<N extends Node<N>, T> extends ContainerNode<N, T> {
        void setChildren(Iterable<? extends T> children);
    }
}
