package space.interlayer.structure.elementary.alpha.core;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Key-value node represents map / dictionary / other-named structure
 * that consists of key-value pairs. Contrary to such structures in
 * standard class libraries coming with programming languages, this one
 * <b>might</b> have more than one pair with same key. However, most of
 * the time client implementations can just ignore this case and just
 * use first key occurrence / overwrite previous values on iteration.
 * All {@link KeyValueNode} implementations that allow key repetitions
 * <b>must</b> implement {@link RepetitionTolerant} interface, so
 * clients would be able to know whether they have to account for
 * repetitions.
 *
 * @param <N> Specific node type.
 */
public interface KeyValueNode<N extends Node<N>> extends ContainerNode<N, KeyValueNode.Entry<? extends N>> {
    /**
     * Retrieves value for provided key. In case of key repetitions
     * ({@link RepetitionTolerant}) should return a value consistent
     * across calls (preferably the first occurrence). If such key
     * doesn't exist in structure at all, must throw
     * {@link NoSuchElementException}.
     *
     * @param key Key under which desired value is stored.
     * @return Value stored under key.
     *
     * @throws NoSuchElementException Must throw this exception if there
     * are no elements for such key.
     */
    N get(N key);

    /**
     * @param key Key to check.
     * @return Whether key exists in structure.
     */
    boolean containsKey(N key);

    /**
     * Retrieves child node, if such is present. In case of key
     * repetitions ({@link RepetitionTolerant}) should return a value
     * consistent across calls (preferably the first occurrence).
     *
     * @param key Key under which desired value is stored.
     * @return Value for key, if such is present.
     */
    default Optional<? extends N> tryGet(N key) {
        return containsKey(key) ? Optional.of(get(key)) : Optional.empty();
    }

    /**
     * @return Collection of keys, which can hold repeated values in
     * case of {@link RepetitionTolerant}.
     */
    default Iterable<? extends N> getKeys() {
        Spliterator<Entry<? extends N>> spliterator = Spliterators.spliteratorUnknownSize(getChildren().iterator(), 0);

        return StreamSupport.stream(spliterator, false)
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * @return Collection of values without their keys.
     */
    default Iterable<? extends N> getValues() {
        Spliterator<Entry<? extends N>> spliterator = Spliterators.spliteratorUnknownSize(getChildren().iterator(), 0);

        return StreamSupport.stream(spliterator, false)
                .map(Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Collects node contents into map. In case of repeatable keys (see
     * {@link RepetitionTolerant}) only first occurrence is stored.
     *
     * @return A standard map that contains contents of this node.
     */
    @SuppressWarnings("RedundantCast")
    default Map<? extends N, ? extends N> toMap() {
        Map<N, N> accumulator = new HashMap<>();

        for (Entry<? extends N> entry : getChildren()) {
            N key = (N) entry.getKey();
            if (!accumulator.containsKey(key)) {
                accumulator.put(key, (N) entry.getValue());
            }
        }

        return Collections.unmodifiableMap(accumulator);
    }

    default boolean isKeyCountAware() {
        return this instanceof KeyCountAware;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends KeyCountAware<? extends N>> asKeyCountAware() {
        return asNodeOfType((Class) KeyCountAware.class);
    }

    @SuppressWarnings("unchecked")
    default KeyCountAware<? extends N> toKeyCountAware() {
        return toNodeOfType(KeyCountAware.class);
    }

    default boolean isRepetitionTolerant() {
        return this instanceof RepetitionTolerant;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<? extends RepetitionTolerant<? extends N>> asRepetitionTolerant() {
        return asNodeOfType((Class) RepetitionTolerant.class);
    }

    @SuppressWarnings("unchecked")
    default RepetitionTolerant<? extends N> toRepetitionTolerant() {
        return toNodeOfType(RepetitionTolerant.class);
    }

    /**
     * Single child unit of key-value structure.
     *
     * @param <N> Specific node type.
     */
    interface Entry<N extends Node<N>> {
        N getKey();
        N getValue();
    }

    /**
     * Child interface that provides number of distinct keys within
     * structure. Please note that it may not be equal to children count
     * as keys may repeat (see {@link RepetitionTolerant}).
     *
     * @param <N> Specific node type.
     */
    interface KeyCountAware<N extends Node<N>> extends KeyValueNode<N> {
        /**
         * @return Number of distinct keys within structure.
         */
        long getKeyCount();
    }

    /**
     * Interface for implementations that support changing entry values,
     * but not necessarily can introduce new or drop existing keys.
     *
     * @param <N> Specific node type.
     */
    interface Replaceable<N extends Node<N>> extends KeyValueNode<N> {
        /**
         * Replaces existing child with provided value or throws
         * {@link NoSuchElementException} in case of key absence.
         *
         * @param key Key for entry to be replaced.
         * @param value New value.
         * @return Old value.
         *
         * @throws NoSuchElementException Thrown in case there is no
         * entry for provided key. One can use {@link #containsKey(Node)}
         * or to be sure that key is present or use {@link #tryReplaceChild(Node, Node)}
         * to avoid throw possibility.
         */
        N replaceChild(N key, N value);

        /**
         * Tries to replace existing child, returns previous value if
         * corresponding key exists.
         *
         * @param key Key for entry to be replaced.
         * @param value New value.
         * @return Optional-wrapped old value in case key was present
         * before call, empty optional otherwise.
         */
        @SuppressWarnings("UnusedReturnValue")
        default Optional<? extends N> tryReplaceChild(N key, N value) {
            return containsKey(key) ? Optional.of(replaceChild(key, value)) : Optional.empty();
        }

        /**
         * Same as {@link #replaceChild(Node, Node)}, but for many keys
         * at once.
         *
         * @param replacements Key-value map of entry replacements.
         *
         * @throws NoSuchElementException Thrown in case any of provided
         * replacement keys isn't present in structure.
         */
        default void replaceManyChildren(Map<? extends N, ? extends N> replacements) {
            replacements.forEach(this::replaceChild);
        }

        /**
         * Same as {@link #tryReplaceChild(Node, Node)}, but for many
         * keys at once. Doesn't throw if some keys are absent in
         * structure.
         *
         * @param replacements Map of entry replacements organized by
         * entry key.
         */
        default void tryReplaceManyChildren(Map<? extends N, ? extends N> replacements) {
            replacements.forEach(this::tryReplaceChild);
        }
    }

    /**
     * Child interface for implementations that allow to change their
     * content. This particular interface doesn't account for
     * possibility of repeated keys, so implementations (unless they are
     * also implementing {@link RepetitionTolerant.Mutable}) are free
     * to not to worry about that.
     *
     * @param <N> Specific node type.
     */
    interface Mutable<N extends Node<N>> extends Replaceable<N>, ContainerNode.Mutable<N, Entry<? extends N>> {
        /**
         * Removes child under specified key.
         *
         * @param key Provided key.
         * @return Previous value.
         *
         * @throws NoSuchElementException Thrown if provided key is
         * missing. Use {@link #containsKey(Node)} or {@link #tryRemoveChild(Node)}
         * to handle things correctly.
         */
        @SuppressWarnings("UnusedReturnValue")
        N removeChild(N key);

        /**
         * Removes child with specified key, if it exists. Doesn't throw
         * exception if such key is missing.
         *
         * @param key Provided key.
         * @return Optional-wrapped removed child or empty optional
         * depending on whether entry for provided key was present
         * before call.
         */
        default Optional<? extends N> tryRemoveChild(N key) {
            return containsKey(key) ? Optional.of(removeChild(key)) : Optional.empty();
        }

        /**
         * Performs removal action for all specified keys. All keys
         * <b>must</b> be present in structure, otherwise
         * {@link NoSuchElementException} <b>must</b> be thrown.
         *
         * @param keys Keys for removal.
         *
         * @throws NoSuchElementException Thrown if any of provided keys
         * is absent in structure.
         */
        default void removeManyChildren(Iterable<? extends N> keys) {
            keys.forEach(this::removeChild);
        }

        /**
         * Performs removal action for all provided keys that are
         * present in structure, ignoring non-existing ones.
         *
         * @param keys Keys for removal.
         */
        default void tryRemoveManyChildren(Iterable<? extends N> keys) {
            keys.forEach(this::tryRemoveChild);
        }

        /**
         * Sets new child for provided key.
         *
         * @param key Entry key.
         * @param value New entry value.
         *
         * @return Previous value (if any) wrapped in optional.
         */
        @SuppressWarnings("UnusedReturnValue")
        Optional<? extends N> setChild(N key, N value);

        /**
         * Performs {@link #setChild(Node, Node)} for many entries at
         * once.
         *
         * @param content Map of new values organized by entry key.
         */
        default void setManyChildren(Map<? extends N, ? extends N> content) {
            content.forEach(this::setChild);
        }
    }

    /**
     * This interface is created for explicit support of repeated keys.
     * In rare case multiple keys may be observed (for example, in
     * format that explicitly allows it or during streaming where
     * implementation has to deal with already read values), and if this
     * behavior has to be preserved for clients, it can be addressed
     * with implementation of this interface.
     *
     * All methods present in parent interface must retain their
     * behavior as if structure doesn't have any repeated keys.
     *
     * @param <N> Specific node type.
     */
    interface RepetitionTolerant<N extends Node<N>> extends KeyValueNode<N> {
        /**
         * Retrieves all values for a specific key or throws if provided
         * key is absent.
         *
         * @param key Inspected key.
         * @return All values for provided key, must be not empty.
         *
         * @throws NoSuchElementException Thrown if absent key is
         * inspected. One can use {@link #containsKey(Node)} to check
         * whether entries exist or use {@link #tryGetAllKeyChildren(Node)} to
         * avoid possible exception.
         */
        Iterable<? extends N> getAllKeyChildren(N key);

        /**
         * Retrieves all values for a specific key if it is present or
         * empty iterable otherwise.
         *
         * @param key Inspected key.
         * @return All values for provided key, empty if key is absent
         * in structure.
         */
        default Iterable<? extends N> tryGetAllKeyChildren(N key) {
            return containsKey(key) ? getAllKeyChildren(key) : Collections.emptyList();
        }

        /**
         * @param key Inspected key.
         * @return Amount of entries for provided key. <b>Must</b>
         * return 0 for absent entries and not throw, i.e. require
         * checking via {@link #containsKey(Node)} before call.
         */
        int getKeyChildrenCount(N key);

        /**
         * Interface for a mutable repetition-tolerant key-value
         * structure.
         *
         * @param <N> Specific node type.
         */
        interface Mutable<N extends Node<N>> extends RepetitionTolerant<N>, KeyValueNode.Mutable<N> {
            /**
             * Adds new value for specified key. This doesn't guarantee
             * that this particular value will be last in
             * {@link #getAllKeyChildren(Node)} returned value on
             * subsequent calls, unless otherwise stated by particular
             * implementation.
             *
             * @param key Modified key.
             * @param value Additional value.
             *
             * @return Number of values under specified key.
             */
            @SuppressWarnings("UnusedReturnValue")
            int addKeyChild(N key, N value);

            /**
             * Adds several children for provided key. Follows
             * {@link #addKeyChild(Node, Node)} contract.
             *
             * @param key Key to add children for.
             * @param values Values to be added.
             * @return Number of children under key or -1 if empty
             * iterable is passed. In the latter case client code may
             * call {@link #getKeyChildrenCount(Node)} to get actual
             * value.
             */
            default int addKeyChildren(N key, Iterable<? extends N> values) {
                int result = -1;
                for (N value : values) {
                    result = addKeyChild(key, value);
                }

                return result;
            }

            /**
             * @see #addKeyChildren(Node, Iterable)
             */
            @SuppressWarnings({"UnusedReturnValue", "unchecked"})
            default int addKeyChildren(N key, N... values) {
                return addKeyChildren(key, Arrays.asList(values));
            }

            /**
             * Adds child values for many keys at once
             *
             * @param content Map of additional values organized by key.
             *
             * @see #addKeyChildren(Node, Iterable)
             */
            default void addManyKeysChildren(Map<? extends N, Iterable<? extends N>> content) {
                content.forEach(this::addKeyChildren);
            }
        }
    }
}
