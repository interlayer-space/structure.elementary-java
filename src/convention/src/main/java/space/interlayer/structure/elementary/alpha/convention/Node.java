package space.interlayer.structure.elementary.alpha.convention;

import lombok.NonNull;
import space.interlayer.structure.elementary.alpha.core.TopTypeNode;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * This is the root type for conventional type hierarchy that matches
 * JSON, YAML and XML. Please see {@link space.interlayer.structure.elementary.alpha.convention package-info.java}
 * for detailed information over the goals.
 */
@SuppressWarnings("unused")
public interface Node extends TopTypeNode<Node> {
    /**
     * Attributes associated with a node. These come from XML only, but
     * as this project is exactly about setting a common ground between
     * representations, implementations <b>must not</b> assume anything 
     * about presence or absence of these.
     *
     * @return Attributes of the associated node. <b>Even though it's
     * a KeyValueNode, it is implied that there are no attributes for 
     * attributes.</b> Nested calls are expected to return no-op 
     * implementation that only does goggles.
     */
    default KeyValueNode getAttributes() {
        return KeyValueNode.Ignorant.INSTANCE;
    }

    /**
     * Tries to retrieve attribute and returns either the attribute 
     * or null. 
     * 
     * @param key Attribute key.
     * @return Attribute value or null.
     */
    default Node queryAttribute(@NonNull Node key) {
        return getAttributes().requestValue(key);
    }

    default Optional<Node> tryGetAttribute(Node key) {
        return Optional.ofNullable(queryAttribute(key));
    }

    default Node getAttribute(@NonNull Node key) {
        return getAttributes().getValue(key);
    }

    default boolean hasAttribute(@NonNull Node key) {
        return getAttributes().requestValue(key) != null;
    }

    Node withAttributes(@NonNull KeyValueNode attributes);
    default Node withAttribute(@NonNull Node key, @NonNull Node value) {
        return withAttributes(getAttributes().withElement(key, value));
    }
    default Node withoutAttribute(@NonNull Node key) {
        return withAttributes(getAttributes().withoutKey(key));
    }
    default Node withoutAttributes() {
        return withAttributes(getAttributes().withoutContent());
    }
    //todo
    default Node withSelectedAttributes(@NonNull Predicate<KeyValueNode.Entry> predicate) {
        KeyValueNode attributes = getAttributes();
        for (KeyValueNode.Entry entry : attributes.getContent()) {
            if (!predicate.test(entry)) {
                attributes = attributes.withoutKey(entry.getKey());
            }
        }
        return withAttributes(attributes);
    }
    //todo
    default Node withoutFilteredAttributes(@NonNull Predicate<KeyValueNode.Entry> predicate) {
        return withSelectedAttributes(attribute -> !predicate.test(attribute));
    }

    default boolean isMutable() {
        return is(Mutable.class);
    }

    default boolean isImmutable() {
        return is(Immutable.class);
    }

    default boolean isMissing() {
        return is(MissingNode.class);
    }

    default boolean isNull() {
        return is(NullNode.class);
    }

    default boolean isFlag() {
        return is(FlagNode.class);
    }

    default boolean isNumeric() {
        return is(NumericNode.class);
    }

    default boolean isText() {
        return is(TextNode.class);
    }

    default boolean isGroup() {
        return is(GroupNode.class);
    }

    default boolean isKeyValue() {
        return is(KeyValueNode.class);
    }

    default MissingNode asMissing() {
        return as(MissingNode.class);
    }

    default NullNode asNull() {
        return as(NullNode.class);
    }

    default FlagNode asFlag() {
        return as(FlagNode.class);
    }

    default NumericNode asNumeric() {
        return as(NumericNode.class);
    }

    default TextNode asText() {
        return as(TextNode.class);
    }

    default GroupNode asGroup() {
        return as(GroupNode.class);
    }

    default KeyValueNode asKeyValue() {
        return as(KeyValueNode.class);
    }

    default MissingNode toMissing() {
        return to(MissingNode.class);
    }

    default NullNode toNull() {
        return to(NullNode.class);
    }

    default FlagNode toFlag() {
        return to(FlagNode.class);
    }

    default NumericNode toNumeric() {
        return to(NumericNode.class);
    }

    default TextNode toText() {
        return to(TextNode.class);
    }

    default GroupNode toGroup() {
        return to(GroupNode.class);
    }

    default KeyValueNode toKeyValue() {
        return to(KeyValueNode.class);
    }

    /**
     * Marker interface for nodes that perform updates in-place.
     */
    interface Mutable extends Node {}

    /**
     * Marker interface for nodes that perform updates by creating a new
     * instance containing the requested updates.
     */
    interface Immutable extends Node {}

    /**
     * Interface for leaf nodes. Mainly exists to aggregate interfaces
     * and provide more specific return type.
     *
     * @param <T> Enclosed value type.
     */
    interface Scalar<T> extends Node, TopTypeNode.Scalar.Editable<Node, T, Node.Scalar<T>> {
        @Override
        Node.Scalar<T> withValue(T value);
    }

    /**
     * Interface for non-leaf nodes. Mainly exists to aggregate
     * interfaces and provide more specific return type.
     *
     * @param <E> Enclosed element type.
     * @param <C> Container type.
     */
    interface Container<E, C extends Container<E, C>> extends Node, TopTypeNode.Container.Editable<Node, E, C> {
        interface Countable<T> extends TopTypeNode.Container.Countable<Node, T> {}
    }
}
