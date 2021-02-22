package space.interlayer.structure.elementary.alpha.core;

import java.util.Objects;
import java.util.Optional;

/**
 * This is the base interface and common ground for all processing. It
 * is expected that all possible processors, operations, providers,
 * consumers and other dependent functionality will use this or derived
 * interface as a basic unit.
 *
 * Interface exposes generic parameter to allow extending this interface
 * and provide additional functionality. One can create an interface X
 * that would extend {@code Node<X>}, and then all container node
 * operations would consume and produce X, not just Node.
 *
 * @param <N> Specific node type. This allows to retain particular child
 * type when performing operations, otherwise calling methods like
 * {@link #asKeyValueNode()} would reset type to this interface.
 */
public interface Node<N extends Node<N>> {
    /**
     * Describes which structure this node represents in a more friendly
     * form rather than just being an implementation specific interface.
     *
     * @return Current node kind. In case of any node kinds not
     * supported by this library {@link NodeKind#SPECIAL} has to be
     * returned.
     */
    default NodeKind getKind() {
        return NodeKind.compute(getClass());
    }

    /**
     * Attributes describing this node rather than its contents. Node
     * having attributes is a rare case, but some formats as XML provide
     * such feature. It has been decided to include attributes in base
     * interface instead of making separate chain of interfaces
     * (AttributeAwareNode, AttributeAwareGroupNode and so on) to reduce
     * clogging.
     *
     * There are no restrictions on attribute nesting (i.e. attribute
     * nodes may have their own attributes), but cyclic references are
     * forbidden.
     *
     * @return Attributes of this node.
     */
    default Optional<KeyValueNode<N>> getAttributes() {
        return Optional.empty();
    }

    default boolean hasKind(NodeKind kind) {
        return Objects.requireNonNull(kind).isKindFor(getClass());
    }

    default boolean isNullNode() {
        return this instanceof NullNode;
    }

    default boolean isScalarNode() {
        return this instanceof ScalarNode;
    }

    default boolean isFlagNode() {
        return this instanceof FlagNode;
    }

    default boolean isTextNode() {
        return this instanceof TextNode;
    }

    default boolean isContainerNode() {
        return this instanceof ContainerNode;
    }

    default boolean isGroupNode() {
        return this instanceof GroupNode;
    }

    default boolean isKeyValueNode() {
        return this instanceof KeyValueNode;
    }

    default boolean isSpecialNode() {
        return NodeKind.SPECIAL.isKindFor(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default <T extends Node<N>> T toNodeOfType(Class<T> type) {
        Objects.requireNonNull(type);

        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        }

        throw new ClassCastException("Node " + this + " of type " + getClass() + " can not be cast to " + type);
    }

    @SuppressWarnings({"unchecked"})
    default NullNode<? extends N> toNullNode() {
        return toNodeOfType(NullNode.class);
    }

    @SuppressWarnings({"unchecked"})
    default FlagNode<? extends N> toFlagNode() {
        return toNodeOfType(FlagNode.class);
    }

    @SuppressWarnings({"unchecked"})
    default NumericNode<? extends N> toDecimalNode() {
        return toNodeOfType(NumericNode.class);
    }

    @SuppressWarnings({"unchecked"})
    default TextNode<? extends N> toTextNode() {
        return (TextNode<N>) toNodeOfType(TextNode.class);
    }

    @SuppressWarnings({"unchecked"})
    default GroupNode<? extends N> toGroupNode() {
        return toNodeOfType(GroupNode.class);
    }

    @SuppressWarnings({"unchecked"})
    default KeyValueNode<? extends N> toKeyValueNode() {
        return toNodeOfType(KeyValueNode.class);
    }

    default <T extends Node<N>> Optional<T> asNodeOfType(Class<T> type) {
        Objects.requireNonNull(type);

        return type.isAssignableFrom(getClass()) ? Optional.of(type.cast(this)) : Optional.empty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends NullNode<? extends N>> asNullNode() {
        return asNodeOfType((Class) NullNode.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends FlagNode<? extends N>> asFlagNode() {
        return asNodeOfType((Class) FlagNode.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends NumericNode<? extends N>> asDecimalNode() {
        return asNodeOfType((Class) NumericNode.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends TextNode<? extends N>> asTextNode() {
        return asNodeOfType((Class) TextNode.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends GroupNode<? extends N>> asGroupNode() {
        return asNodeOfType((Class) GroupNode.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends KeyValueNode<? extends N>> asKeyValueNode() {
        return asNodeOfType((Class) KeyValueNode.class);
    }
}
