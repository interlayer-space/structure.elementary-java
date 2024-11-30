package space.interlayer.structure.elementary.alpha.convention;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * KeyValueNode represents a dictionary, a key to value mapping. Despite
 * the common formats always have a string to something mapping, this
 * type considers key to be an arbitrary node rather than just a string.
 */
@SuppressWarnings("unused")
public interface KeyValueNode extends Node.Container<KeyValueNode.Entry, KeyValueNode> {
    /**
     * Ask for a value and get a null if it doesn't exist.
     *
     * @param key The key to query value for.
     * @return Either non-null value for an existing key or null for a
     * missing one.
     */
    Node requestValue(@NonNull Node key);

    /**
     * Optional-wrapped {@link #requestValue(Node)}.
     *
     * @param key The key to query value for.
     * @return A corresponding value for the key.
     */
    default Optional<Node> tryGetValue(@NonNull Node key) {
        return Optional.ofNullable(requestValue(key));
    }

    /**
     * Performs value retrieval and throws an exception if the key is
     * not present.
     *
     * @param key The key to query value for.
     * @return A non-null value for the key.
     * @throws NoSuchElementException In case of missing key.
     */
    default Node getValue(@NonNull Node key) {
        Node resolved = requestValue(key);
        if (resolved == null) {
            throw new NoSuchElementException("KeyValueNode " + this + " doesn't have requested a child with the requested key: " + key);
        }
        return resolved;
    }

    // todo
    default Node getValue(@NonNull Node key, @NonNull Node fallback) {
        Node resolved = requestValue(key);
        return resolved != null ? resolved : fallback;
    }

    /**
     * <p>
     *     Replaces the content with contents of the provided map.
     * </p>
     *
     * <p>
     *     <b>
     *         Expected to be highly suboptimal in case of immutable
     *         implementations as it just delegates to
     *         {@link #withEntry(Node, Node)} in a loop.
     *         Implementations, both mutable and immutable, are highly
     *         encouraged to override this method.
     *     </b>
     * </p>
     *
     * @param source New content.
     * @return Updated node.
     */
    default KeyValueNode withContent(@NonNull Map<Node, Node> source) {
        KeyValueNode updated = withoutContent();
        for (Map.Entry<Node, Node> entry : source.entrySet()) {
            updated = updated.withEntry(entry.getKey(), entry.getValue());
        }
        return updated;
    }

    // todo
    default KeyValueNode withElements(@NonNull Map<Node, Node> source) {
        KeyValueNode updated = this;
        for (Map.Entry<Node, Node> entry : source.entrySet()) {
            updated = updated.withEntry(entry.getKey(), entry.getValue());
        }
        return updated;
    }

    /**
     * Adds or replaces a single value by key.
     *
     * @param entry Element to be added to the node.
     * @return Updated node.
     */
    default KeyValueNode withElement(@NonNull Entry entry) {
        return withElement(entry.getKey(), entry.getValue());
    }

    /**
     * Adds or replaces a single value by key.
     *
     * @param entry Element to be added to the node.
     * @return Updated node.
     */
    default KeyValueNode withElement(@NonNull Map.Entry<Node, Node> entry) {
        return withElement(entry.getKey(), entry.getValue());
    }

    /**
     * <p>
     * Adds or replaces a single value by key.
     * </p>
     *
     * @param key
     * @param value
     * @return Updated node.
     */
    KeyValueNode withElement(@NonNull Node key, @NonNull Node value);

    KeyValueNode withoutKey(@NonNull Node key);

    default boolean hasKey(@NonNull Node key) {
        return requestValue(key) != null;
    }

    // todo does not replace
    KeyValueNode withKey(@NonNull Node key, @NonNull Node value);
    KeyValueNode withKey(@NonNull Node key, @NonNull Function<Node, Node> factory);
    default KeyValueNode withKey(@NonNull Entry entry) {
        return withKey(entry.getKey(), entry.getValue());
    }

    // todo replaces
    KeyValueNode withEntry(@NonNull Node key, @NonNull Node value);
    default KeyValueNode withEntry(@NonNull Entry entry) {
        return withEntry(entry.getKey(), entry.getValue());
    }

    // todo
    default KeyValueNode withReplacements(@NonNull BiPredicate<Node, Node> predicate, @NonNull BiFunction<Node, Node, Node> transformer) {
        return withReplacements(
                entry -> predicate.test(entry.getKey(), entry.getValue()),
                entry -> Entry.of(entry.getKey(), transformer.apply(entry.getKey(), entry.getValue()))
        );
    }

    // TODO: MUST provide an instance that isn't modified in the future

    /**
     * <p>
     *     Returns node content in the format of a map.
     * </p>
     *
     * <p>
     *     <b>MUST</b> return a map that does not update regardless of
     *     current node state and can't be updated externally.
     * </p>
     *
     * <p>
     *     <b>
     *         Expected to be highly suboptimal. Implementations, both
     *         mutable and immutable, are highly encouraged to override
     *         this method.
     *     </b>
     * </p>
     *
     * @return An immutable snapshot of the node state.
     */
    default Map<Node, Node> toMap() {
        Map<Node, Node> result = new HashMap<>();
        for (Entry child : getContent()) {
            result.put(child.getKey(), child.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * @return A new node that is guaranteed to return itself on every
     * mutation and apply requested changes.
     */
    static KeyValueNode mutable() {
        return new Mutable(new HashMap<>(), ignorant());
    }

    /**
     * @return A new node that is guaranteed to return itself on every
     * mutation and apply requested changes.
     */
    static KeyValueNode mutable(@NonNull KeyValueNode attributes) {
        return new Mutable(new HashMap<>(), attributes);
    }

    /**
     * @return A new node that is guaranteed to return a new instance
     * of every requested change.
     */
    static KeyValueNode immutable() {
        return new Immutable(Collections.emptyMap(), ignorant());
    }

    /**
     * @return A new node that is guaranteed to return a new instance
     * of every requested change.
     */
    static KeyValueNode immutable(@NonNull KeyValueNode attributes) {
        return new Immutable(Collections.emptyMap(), attributes);
    }

    /**
     * @return A node that ignores all requested operations and returns
     * itself.
     */
    static KeyValueNode ignorant() {
        return Ignorant.INSTANCE;
    }

    /**
     * //todo
     */
    interface Entry {
        // todo
        Node getKey();
        // todo
        Node getValue();

        static Entry of(@NonNull Node key, @NonNull Node value) {
            return new Immutable(key, value);
        }

        @With
        @Value
        class Immutable implements Entry {
            Node key;
            Node value;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    class Mutable implements KeyValueNode {
        private Map<Node, Node> content = new HashMap<>();
        private KeyValueNode attributes;

        @Override
        public Iterable<Entry> getContent() {
            return () -> stream().iterator();
        }

        @Override
        public Stream<Entry> stream() {
            return content.entrySet().stream()
                    .map(entry -> new Entry.Immutable(entry.getKey(), entry.getValue()));
        }

        @Override
        public Node requestValue(@NonNull Node key) {
            return content.get(key);
        }

        @Override
        public KeyValueNode withKey(@NonNull Node key, @NonNull Node value) {
            content.put(key, value);
            return this;
        }

        @Override
        public KeyValueNode withContent(Iterable<? extends Entry> content) {
            int size = content instanceof Collection ? ((Collection<? extends Entry>) content).size() : 0;
            this.content.clear();
            Map<Node, Node> accumulator = new HashMap<>(size);
            for (Entry entry : content) {
                this.content.put(entry.getKey(), entry.getValue());
            }
            return withContent(accumulator);
        }

        @Override
        public KeyValueNode withContent(KeyValueNode source) {
            return withContent(source.getContent());
        }

        @Override
        public KeyValueNode withContent(@NonNull Map<Node, Node> content) {
            this.content.clear();
            this.content.putAll(content);
            return this;
        }

        @Override
        public KeyValueNode withoutContent() {
            content.clear();
            return this;
        }

        @Override
        public KeyValueNode withoutKey(@NonNull Node key) {
            content.remove(key);
            return this;
        }

        @Override
        public KeyValueNode withEntry(@NonNull Node key, @NonNull Node value) {
            //todo remove
            content.put(key, value);
            return this;
        }

        @Override
        public KeyValueNode withElement(@NonNull Node key, @NonNull Node value) {
            content.put(key, value);
            return this;
        }

        @Override
        public KeyValueNode withElements(@NonNull Map<@NonNull Node, @NonNull Node> source) {
            content.putAll(source);
            return this;
        }

        @Override
        public KeyValueNode withKey(@NonNull Node key, @NonNull Function<@NonNull Node, @NonNull Node> factory) {
            if (!hasKey(key)) {
                return withElement(key, factory.apply(key));
            }

            return this;
        }

        @Override
        public KeyValueNode withoutElement(Entry element) {
            if (!content.containsKey(element.getKey()) || !content.get(element.getKey()).equals(element.getValue())) {
                return this;
            }
            content.remove(element.getKey());
            return this;
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            this.attributes = attributes;
            return this;
        }

        @Override
        public KeyValueNode withoutElements(@NonNull Iterable<? extends Entry> elements) {
            KeyValueNode self = this;
            for (Entry element : elements) {
                self = self.withoutElement(element);
            }
            return self;
        }

        @Override
        public KeyValueNode withElements(@NonNull Iterable<? extends Entry> elements) {
            KeyValueNode self = this;
            for (Entry entry : elements) {
                self = self.withElement(entry);
            }
            return self;
        }

        @Override
        public KeyValueNode withoutFilteredElements(@NonNull Predicate<? super Entry> condition) {
            //todo
            return this;
        }

        @Override
        public KeyValueNode withSelectedElements(@NonNull Predicate<? super Entry> condition) {
            //todo
            return this;
        }

        @Override
        public KeyValueNode withReplacements(@NonNull Predicate<? super Entry> condition, @NonNull Function<? super Entry, ? extends Entry> transformer) {
            Map<Node, Node> updates = new HashMap<>();
            for (Map.Entry<Node, Node> entry : content.entrySet()) {
                Entry combined = Entry.of(entry.getKey(), entry.getValue());
                if (condition.test(combined)) {
                    updates.put(entry.getKey(), transformer.apply(combined).getValue());
                }
            }
            return withElements(updates);
        }

        @Override
        public KeyValueNode withReplacements(@NonNull BiPredicate<Node, Node> condition, @NonNull BiFunction<Node, Node, Node> transformer) {
            Map<Node, Node> updates = new HashMap<>();
            for (Map.Entry<Node, Node> entry : content.entrySet()) {
                if (condition.test(entry.getKey(), entry.getValue())) {
                    updates.put(entry.getKey(), transformer.apply(entry.getKey(), entry.getValue()));
                }
            }
            return withElements(updates);
        }
    }

    @RequiredArgsConstructor
    class Immutable implements KeyValueNode {
        private static final KeyValueNode.Immutable EMPTY = new KeyValueNode.Immutable(Collections.emptyMap(), KeyValueNode.ignorant());

        private final Map<Node, Node> children;
        private final KeyValueNode attributes;

        @Override
        public Node requestValue(@NonNull Node key) {
            return children.get(key);
        }

        @Override
        public KeyValueNode withContent(Iterable<? extends Entry> children) {
            Map<Node, Node> replacement = new HashMap<>();
            for (Entry child : children) {
                replacement.put(child.getKey(), child.getValue());
            }
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        @Override
        public KeyValueNode withContent(KeyValueNode source) {
            return of(source.toMap(), attributes);
        }

        @Override
        public KeyValueNode withoutContent() {
            return of(Collections.emptyMap(), attributes);
        }

        @Override
        public KeyValueNode withoutKey(Node key) {
            if (!children.containsKey(key)) {
                return this;
            }

            Map<Node, Node> replacement = new HashMap<>(children);
            replacement.remove(key);
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        @Override
        public KeyValueNode withKey(Node key, Node value) {
            if (Objects.equals(children.get(key), value)) {
                return this;
            }

            Map<Node, Node> replacement = new HashMap<>(children);
            replacement.put(key, value);
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        @Override
        public KeyValueNode withEntry(Node key, Node value) {
            if (Objects.equals(children.get(key), value)) {
                return this;
            }

            Map<Node, Node> replacement = new HashMap<>(children);
            replacement.put(key, value);
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        @Override
        public KeyValueNode withEntry(Node key, Function<Node, Node> transformer) {
            if (Objects.equals(children.get(key), value)) {
                return this;
            }

            Map<Node, Node> replacement = new HashMap<>(children);
            replacement.put(key, value);
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        @Override
        public KeyValueNode materialize(Node key, Function<Node, Node> operation) {
            Map<Node, Node> replacement = new HashMap<>(children);
            replacement.computeIfAbsent(key, operation);
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        @Override
        public Iterable<Entry> getContent() {
            return children.entrySet().stream()
                    .map(entry -> new Entry.Immutable(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }

        @Override
        public KeyValueNode withoutElement(Entry element) {
            if (!children.containsKey(element.getKey()) || !children.get(element.getKey()).equals(element.getValue())) {
                return this;
            }
            Map<Node, Node> replacement = new HashMap<>(children);
            replacement.remove(element.getKey());
            return new KeyValueNode.Immutable(replacement, attributes);
        }

        public static KeyValueNode empty() {
            return EMPTY;
        }

        public static KeyValueNode of(@NonNull Map<Node, Node> content, @NonNull KeyValueNode attributes) {
            return new Immutable(content, attributes);
        }

        public static KeyValueNode of(@NonNull Map<Node, Node> content) {
            return of(content, Ignorant.INSTANCE);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class Ignorant implements KeyValueNode {
        public static final KeyValueNode INSTANCE = new Ignorant();

        @Override
        public KeyValueNode withContent(@NonNull Iterable<? extends Entry> content) {
            return this;
        }

        @Override
        public Node requestValue(@NonNull Node key) {
            return null;
        }

        @Override
        public KeyValueNode withElement(@NonNull Node key, @NonNull Node value) {
            return this;
        }

        @Override
        public KeyValueNode withoutKey(@NonNull Node key) {
            return this;
        }

        @Override
        public KeyValueNode withoutContent() {
            return this;
        }

        @Override
        public KeyValueNode withEntry(@NonNull Node key, @NonNull Node value) {
            return this;
        }

        @Override
        public KeyValueNode materialize(@NonNull Node key, @NonNull Function<Node, Node> operation) {
            return this;
        }

        @Override
        public KeyValueNode getAttributes() {
            return this;
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            return this;
        }

        @Override
        public Node withAttribute(@NonNull Node key, @NonNull Node value) {
            return this;
        }

        @Override
        public Node withoutAttribute(@NonNull Node key) {
            return this;
        }

        @Override
        public Node withoutAttributes() {
            return this;
        }

        @Override
        public KeyValueNode withoutElement(@NonNull Entry child) {
            return this;
        }

        @Override
        public Iterable<Entry> getContent() {
            return Collections.emptyList();
        }

        @Override
        public Node withSelectedAttributes(@NonNull Predicate<Entry> predicate) {
            return this;
        }

        @Override
        public Node withoutFilteredAttributes(@NonNull Predicate<Entry> predicate) {
            return this;
        }

        @Override
        public KeyValueNode withoutElements(@NonNull Iterable<? extends Entry> elements) {
            return this;
        }

        @Override
        public KeyValueNode withElement(@NonNull Entry element) {
            return this;
        }

        @Override
        public KeyValueNode withElements(@NonNull Iterable<? extends Entry> elements) {
            return this;
        }

        @Override
        public KeyValueNode withoutFilteredElements(@NonNull Predicate<? super Entry> condition) {
            return this;
        }

        @Override
        public KeyValueNode withSelectedElements(@NonNull Predicate<? super Entry> condition) {
            return this;
        }

        @Override
        public KeyValueNode withReplacements(@NonNull Predicate<? super Entry> condition, @NonNull Function<? super Entry, ? extends Entry> transformer) {
            return this;
        }
    }
}
