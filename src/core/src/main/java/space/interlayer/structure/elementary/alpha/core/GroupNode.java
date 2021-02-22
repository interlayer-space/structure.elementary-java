package space.interlayer.structure.elementary.alpha.core;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Group node represents set of values which can be represented as
 * collection, but no guarantees are made about things like length,
 * ordering and so on. For example, it is allowed for several
 * {@link #getChildren()} calls on non-{@link Ordered} implementation to
 * return children in different order.
 *
 * @param <N> Specific node type.
 */
public interface GroupNode<N extends Node<N>> extends ContainerNode<N, N> {
    /**
     * @return Whether or not this group has consistent order.
     */
    default boolean isOrdered() {
        return this instanceof Ordered;
    }

    /**
     * @return Whether or not index-based (random) access is possible.
     */
    default boolean isIndexed() {
        return this instanceof Indexed;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends Ordered<? extends N>> asOrdered() {
        return asNodeOfType((Class) Ordered.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends Indexed<? extends N>> asIndexed() {
        return asNodeOfType((Class) Indexed.class);
    }

    @SuppressWarnings("unchecked")
    default Ordered<? extends N> toOrdered() {
        return toNodeOfType(Ordered.class);
    }

    @SuppressWarnings("unchecked")
    default Indexed<? extends N> toIndexed() {
        return toNodeOfType(Indexed.class);
    }

    /**
     * Interface for implementations that allow replacement of their
     * children.
     *
     * @param <N> Specific node type.
     */
    interface Mutable<N extends Node<N>> extends GroupNode<N>, ContainerNode.Mutable<N, N> {}

    /**
     * Marker interface that tells clients it's contents are returned in
     * same order as specified.
     *
     * @param <N> Specific node type.
     */
    interface Ordered<N extends Node<N>> extends GroupNode<N> {
        /**
         * Ordered group node that allows to change its contents using
         * head or tail.
         *
         * @param <N> Specific node type.
         */
        interface Mutable<N extends Node<N>> extends Ordered<N>, GroupNode.Mutable<N> {
            /**
             * Prepends new node to the head.
             *
             * @param node Node to prepend.
             */
            void addFirstChild(N node);

            /**
             * Appends new node to the tail.
             *
             * @param node Node to append.
             */
            void addLastChild(N node);

            /**
             * Removes and returns first node.
             *
             * @return Removed node.
             *
             * @throws NoSuchElementException Thrown if children list is
             * empty. Check {@link #isEmpty()} output to discover such
             * condition.
             */
            N removeFirstChild();

            /**
             * Removes and returns last node.
             *
             * @return Removed node.
             *
             * @throws NoSuchElementException Thrown if children list is
             * empty. Check {@link #isEmpty()} output to discover such
             * condition.
             */
            N removeLastChild();
        }
    }

    /**
     * This interface is designed for groups that allow index-based
     * access. This implies additional group constraints like preserving
     * original order.
     *
     * @param <N> Specific node type.
     */
    interface Indexed<N extends Node<N>> extends CountAware<N, N>, Ordered<N> {
        /**
         * Retrieves specific node by its index. Index <b>must</b> be
         * within {@code 0 .. length - 1} range.
         *
         * @param index Child index.
         * @return Node Node at specific index.
         *
         * @throws IndexOutOfBoundsException Thrown on illegal index.
         */
        N getChild(long index);

        /**
         * Interface for implementations that allow replacement of
         * specific child node.
         *
         * @param <N> Specific node type.
         */
        interface Mutable<N extends Node<N>> extends Indexed<N>, Ordered.Mutable<N> {
            /**
             * Replaces child at specific index with provided value.
             *
             * @param index Child index.
             * @param value Value to set.
             *
             * @return Previous value.
             *
             * @throws IndexOutOfBoundsException If index is not within
             * group length.
             */
            N setChild(long index, N value);
        }
    }
}
