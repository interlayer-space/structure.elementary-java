package space.interlayer.structure.elementary.alpha.core;

import lombok.NonNull;

import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>
 *     This is the top type, the base interface and the least
 *     restrictive common ground for all downstream implementations.
 *     It is expected that all processors, operations, providers,
 *     consumers and other dependent functionality will use this type or
 *     a derived interface as the common denominator.
 * </p>
 *
 * <p>
 *     This interface exposes generic parameter to allow setting a
 *     common ground for a (more) specific domain. One can create an
 *     interface N that would extend {@code Node<N>}, and then all
 *     container node operations would consume and produce N, not just
 *     Node. Without that, the type of the operations provided by base
 *     types would constantly reset to the very basic type.
 * </p>
 *
 * <p>
 *     This common denominator goes with two assumptions about derived
 *     nodes: there might be leaf and non-leaf nodes. Nodes are not
 *     forced to be leaf or non-leaf, so very sophisticated cases may
 *     completely abstain from this division.
 * </p>
 *
 * <p>
 *     Leaf nodes, called {@link TopTypeNode.Scalar}
 *     in the terminology of the library, contain a specific value of a
 *     Java type, resulting in a jump from an abstraction to the usual
 *     domain. However, in case of any specialty required by the end
 *     processors, the scalar type is generic, which means node
 *     implementations with peculiar types can use their own types
 *     inside the scalar nodes.
 * </p>
 *
 * <p>
 *     Non-leaf nodes, called {@link TopTypeNode.Container}
 *     in the terminology of the library, contain another number of
 *     nodes. While for special implementations that may mean only a
 *     single node (e.g. a decorator) inside a non-leaf one, an
 *     assumption is made that enclosed element(s) can be iterated, and
 *     these elements may be not nodes, but support structures (if such
 *     addition is necessary). Because of that there is a method that
 *     allows iterating enclosed nodes one way or another, but there are
 *     no assumptions on ordering, repetition, phantom records or
 *     anything else. More specific nodes may declare their own
 *     functionality to inspect the enclosed nodes.
 * </p>
 *
 * <p>
 *     The non-standard naming instead of just Node comes from the fact
 *     that the end libraries would become way less concise, having to
 *     use cringy names like ElementaryNode. To help the derivations
 *     keep end user happy, the name was set to a non-standard one.
 * </p>
 *
 * @param <N> Specific node type. This allows to retain particular child
 * type when performing operations, otherwise calling methods that
 * return other nodes would reset the actual type to this interface.
 */
@SuppressWarnings("unused")
public interface TopTypeNode<N extends TopTypeNode<N>> {
    /**
     * @return Whether node contains leaf value and can be cast to
     * {@link TopTypeNode.Scalar}.
     */
    default boolean isScalar() {
        return this instanceof Scalar;
    }

    /**
     * @return Whether node contains enclosed nodes and can be cast to
     * {@link TopTypeNode.Container}.
     */
    default boolean isContainer() {
        return this instanceof Container;
    }

    /**
     * Performs simple type checking. {@code instanceof}, but a bit
     * faster to type.
     *
     * @param type Checked node type reference.
     * @return Whether current node is an instance of target type.
     * @param <T> Checked node type.
     */
    default <T extends TopTypeNode<N>> boolean is(@NonNull Class<T> type) {
        return type.isInstance(this);
    }

    /**
     * Following C# convention, returns either item of the desired type
     * or null.
     *
     * @param type Desired node type reference.
     * @return Cast instance or null.
     * @param <T> Desired node type.
     */
    default <T extends TopTypeNode<N>> T as(@NonNull Class<T> type) {
        return type.isInstance(this) ? type.cast(this) : null;
    }

    /**
     * Forcefully casts to the target type, or, if the desired type is
     * not a supertype of the callee, throws a {@link ClassCastException}.
     *
     * @param type Desired node type reference.
     * @return An always non-null cast instance, in case of
     * incompatibility it halts beforehand by throwing a
     * {@link java.lang.ClassCastException}.
     * @param <T> Desired node type.
     */
    default <T extends TopTypeNode<N>> T to(@NonNull Class<T> type) {
        T casted = as(type);

        if (casted != null) {
            return casted;
        }

        throw new ClassCastException("Node " + this + " of type " + getClass() + " can not be casted to " + type);
    }

    /**
     * A marker interface that implies that node has additional methods
     * to alter it or produce new nodes by manipulating enclosed values.
     *
     * @param <N> Target node type.
     */
    interface Editable<N extends TopTypeNode<N>> extends TopTypeNode<N> {}

    /**
     * Scalar node represents a leaf node that contains value of particular
     * type.
     *
     * @param <N> Specific node type.
     * @param <T> Enclosed type.
     */
    interface Scalar<N extends TopTypeNode<N>, T> extends TopTypeNode<N> {
        /**
         * @return Enclosed value, which may <b>never</b> be null.
         */
        T getValue();

        @Override
        default boolean isContainer() {
            return false;
        }

        @Override
        default boolean isScalar() {
            return true;
        }

        /**
         * An interface for the nodes that allow manipulation. It is not
         * expected to be actively inspected by libraries.
         *
         * @param <N> Derived node type.
         * @param <T> Enclosed value type.
         * @param <S> Derived scalar node type, used to allow type
         * system to infer more specific scalar type in derived class
         * hierarchy.
         */
        interface Editable<N extends TopTypeNode<N>, T, S extends Scalar<N, T>> extends Scalar<N, T>, TopTypeNode.Editable<N> {
            S withValue(@NonNull T value);
        }
    }

    /**
     * An interface for non-leaf nodes enclosing other nodes. Implies
     * that there is a way to iterate references to child nodes, be
     * these the enclosed nodes directly or support structures
     * referencing the enclosed nodes indirectly, even if there is only
     * enclosed node by design.
     *
     * @param <N> Derived node type.
     * @param <E> Type of the enclosed nodes / structures.
     */
    interface Container<N extends TopTypeNode<N>, E> extends TopTypeNode<N> {
        /**
         * <p>
         *     Basic access method. It relies on the {@link Iterable}
         *     interface as the most basic denominator to allow
         *     non-standard cases such as streaming / on-demand
         *     retrieval.
         * </p>
         *
         * @return Content of the container node.
         */
        Iterable<E> getContent();

        /**
         * Returns elements in a form of stream. Implementations are
         * highly encouraged to override it.
         *
         * @return Stream of enclosed elements.
         */
        default Stream<E> stream() {
            return StreamSupport.stream(getContent().spliterator(), false);
        }

        @Override
        default boolean isScalar() {
            return false;
        }

        @Override
        default boolean isContainer() {
            return true;
        }

        /**
         * @return Whether content size is already known.
         */
        default boolean isCountable() {
            return this instanceof Countable;
        }

        /**
         * @return Countable instance or null, depending on whether the
         * type implements corresponding interface.
         */
        @SuppressWarnings("unchecked")
        default Countable<N, E> asCountable() {
            return as(Countable.class);
        }

        /**
         * Aggressive version of {@link #asCountable()} that throws an
         * exception if the type doesn't implement {@link Countable}.
         *
         * @throws ClassCastException in case of type mismatch.
         * @return Countable instance.
         */
        @SuppressWarnings("unchecked")
        default Countable<N, E> toCountable() {
            return to(Countable.class);
        }

        /**
         * <p>
         *     Checks presence of an element.
         * </p>
         *
         * <p>
         *     <b>
         *         Highly suboptimal, performs linear search.
         *         Implementations are highly encouraged to override it.
         *     </b>
         * </p>
         *
         * @param element Element to seek for.
         * @return Answer whether the element belongs to the node.
         */
        default boolean hasElement(@NonNull E element) {
            for (E candidate : getContent()) {
                if (candidate.equals(element)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * This informational method is encouraged to be overridden in
         * the downstream implementations. It is using suboptimal
         * iterator checking, which can lead to lost data in case of
         * consume-once streams.
         *
         * @return Whether container doesn't have any elements.
         */
        default boolean isEmpty() {
            return getContent().iterator().hasNext();
        }

        /**
         * Simple informational method. Relies on {@link #isEmpty()}, so
         * implementations don't need to override this method.
         *
         * @return Whether container has at least one element.
         */
        default boolean isNotEmpty() {
            return !isEmpty();
        }

        /**
         * Exists for the container nodes that are aware of their size.
         * Some implementations, like streaming ones, wouldn't be.
         *
         * @param <N> Derived node type.
         * @param <E> Enclosed element type.
         */
        interface Countable<N extends TopTypeNode<N>, E> extends TopTypeNode.Container<N, E> {
            /**
             * @return Number of enclosed elements.
             */
            long count();

            @Override
            default boolean isEmpty() {
                return count() == 0;
            }
        }

        /**
         * <p>
         *     An interface marking that the node allows manipulation to
         *     update itself or to produce new nodes capturing the
         *     requested changes.
         * </p>
         *
         * <p>
         *     <b>
         *         This interface contains many suboptimal default
         *         methods. Implementations are highly encouraged to
         *         override them.
         *     </b>
         * </p>
         *
         * @param <N> Derived node type.
         * @param <E> Type of the enclosed element.
         * @param <C> Derived container node type, used to allow type
         * system to infer more specific scalar type in derived class
         * hierarchy.
         */
        interface Editable<N extends TopTypeNode<N>, E, C extends Container.Editable<N, E, C>> extends TopTypeNode.Container<N, E>, TopTypeNode.Editable<N> {
            /**
             * <p>
             *     Completely replaces node content.
             * </p>
             *
             * <p>
             *     <b>
             *         Consumers are asked to abstain from usage when
             *         possible, relying on {@link #withContent(space.interlayer.structure.elementary.alpha.core.TopTypeNode.Container.Editable)}
             *         instead. It is expected that this method would be
             *         highly suboptimal compared to the other one.
             *     </b>
             * </p>
             * @param elements Elements to fill the node with.
             * @return A node that would be filled with requested data.
             */
            default C withContent(@NonNull Iterable<? extends E> elements) {
                return withoutContent().withContent(elements);
            }

            /**
             * <p>
             *     Fills the node from the content of another similar
             *     container.
             * </p>
             *
             * <p>
             *     <b>
             *         Expected to be highly suboptimal as it just
             *         delegates to {@link #withContent(Iterable)}.
             *         Implementations are highly encouraged to override
             *         this method and inverse the delegation
             *         relationship.
             *     </b>
             * </p>
             *
             * @param source Node that would be used as a source of the
             * content.
             * @return Node with completely replaced content.
             */
            default C withContent(@NonNull C source) {
                return withContent(source.getContent());
            }

            /**
             * <p>
             *     Completely clears the node, making it empty.
             * </p>
             *
             * <p>
             *     <b>
             *         Expected to be highly suboptimal as it just
             *         delegates to {@link #withContent(Iterable)}.
             *         Implementations are highly encouraged to override
             *         this method and inverse the delegation
             *         relationship.
             *     </b>
             * </p>
             *
             * @return Empty node with no content.
             */
            default C withoutContent() {
                return withContent(Collections.emptyList());
            }

            /**
             * Removes <b>all</b> occurrences of the element in
             * question. Particular implementations supporting multiple
             * occurrences may provide means to remove only specific
             * occurrences.
             *
             * @param element Element to be removed.
             * @return Node with one occurrence of the element removed.
             */
            C withoutElement(@NonNull E element);

            /**
             * Removes <b>all</b> occurrences of the provided elements
             * from the node.
             *
             * @param elements Elements that must not be present in the
             * resulting node.
             * @return Node without the element in question.
             */
            C withoutElements(@NonNull Iterable<? extends E> elements);

            /**
             * <p>
             *     Removes all occurrences of the elements present in
             *     the other node.
             * </p>
             *
             * <p>
             *     <b>
             *         Expected to be highly suboptimal as it just
             *         delegates to {@link #withoutElements(Iterable)}.
             *         Implementations are highly encouraged to override
             *         this method and inverse the delegation
             *         relationship.
             *     </b>
             * </p>
             *
             * @param source Node that contains elements for removal.
             * @return Node without selected elements.
             */
            default C withoutElements(@NonNull C source) {
                return withoutElements(source.getContent());
            }

            /**
             * Ensures that the provided element is added to the node.
             * No guarantees are required / provided besides the fact
             * that the element will be present in the resulting node.
             * Absent guarantees include position of the element, change
             * in number of occurrences, and so on.
             *
             * @param element Element to be added to the node.
             * @return Node that contains all existing elements and
             * the provided element.
             */
            C withElement(@NonNull E element);

            /**
             * Ensures that the provided elements are added to the node.
             * No guarantees are required / provided besides the fact
             * that the elements will be present in the resulting node.
             * Absent guarantees include position of the elements,
             * change in number of occurrences, and so on. Semantically
             * equivalent to subsequent invocations of
             * {@link #withElement(Object)}, but doesn't have a default
             * implementation due to the expected severe performance
             * implications.
             *
             * @param elements Elements to be added to the node.
             * @return Node that contains all existing elements and
             * the provided element.
             */
            C withElements(@NonNull Iterable<? extends E> elements);

            /**
             * Ensures that the content of the provided node is added to
             * the current node. No guarantees are required / provided
             * besides the fact that the elements will be present in the
             * resulting node. Absent guarantees include position of the
             * elements, change in number of occurrences, and so on.
             * Semantically equivalent to invocation of
             * {@link #withElements(Iterable)} using node content, but
             * doesn't have a default implementation due to the expected
             * severe performance implications.
             *
             * @param source Node that contains elements for removal.
             * @return Node without selected elements.
             */
            default C withElements(@NonNull C source) {
                return withElements(source.getContent());
            }

            /**
             * Removes all elements that match the provided condition.
             *
             * @param condition Condition for removal.
             * @return Node without the selected elements.
             */
            C withoutFilteredElements(@NonNull Predicate<? super E> condition);

            /**
             * Removes all elements that match the provided condition.
             *
             * @param condition Condition for preservation.
             * @return Node populated only with the elements passed the
             * selection.
             */
            C withSelectedElements(@NonNull Predicate<? super E> condition);

            /**
             * Replaces every matching element with an element provided
             * by the transformer function. Non-matched elements stay
             * intact.
             *
             * @param condition Matcher for the elements to be replaced.
             * @param transformer Function to produce replacements.
             * @return Node with all matching elements being replaced.
             */
            C withReplacements(@NonNull Predicate<? super E> condition, @NonNull Function<? super E, ? extends E> transformer);
        }
    }
}
