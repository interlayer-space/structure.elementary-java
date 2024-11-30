package space.interlayer.structure.elementary.alpha.convention;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

public interface FlagNode extends Node.Scalar<Boolean> {
    boolean getPrimitiveValue();
    FlagNode withPrimitiveValue(boolean value);
    default FlagNode invert() {
        return withPrimitiveValue(!getPrimitiveValue());
    }

    static FlagNode mutable(boolean value) {
        return Mutable.of(value);
    }

    static FlagNode mutable(boolean value, @NonNull KeyValueNode attributes) {
        return Mutable.of(value, attributes);
    }

    static FlagNode immutable(boolean value) {
        return Immutable.of(value);
    }

    static FlagNode immutable(boolean value, @NonNull KeyValueNode attributes) {
        return Immutable.of(value, attributes);
    }

    class Mutable implements FlagNode, Node.Mutable {
        @NonNull
        private KeyValueNode attributes;
        private boolean value;

        private Mutable(boolean value, @NonNull KeyValueNode attributes) {
            this.attributes = attributes;
            this.value = value;
        }

        private Mutable(boolean value) {
            this(value, new KeyValueNode.Mutable());
        }

        @Override
        public boolean getPrimitiveValue() {
            return value;
        }

        @Override
        public FlagNode withPrimitiveValue(boolean value) {
            this.value = value;
            return this;
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public Scalar<Boolean> withValue(@NonNull Boolean value) {
            this.value = value;
            return this;
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            this.attributes = attributes.withContent(attributes);
            return this;
        }

        @Override
        public Node withAttribute(@NonNull Node key, @NonNull Node value) {
            this.attributes = attributes.withChild(key, value);
            return this;
        }

        @Override
        public Node withoutAttribute(@NonNull Node key) {
            this.attributes = attributes.withoutKey(key);
            return this;
        }

        @Override
        public Node withoutAttributes() {
            this.attributes = attributes.withoutContent();
            return this;
        }

        @Override
        public Node withSelectedAttributes(@NonNull Predicate<KeyValueNode.Entry> predicate) {
            attributes = attributes.withSelectedElements(predicate);
            return this;
        }

        @Override
        public Node withoutFilteredAttributes(@NonNull Predicate<KeyValueNode.Entry> predicate) {
            return null;
        }

        public static FlagNode of(boolean value) {
            return new FlagNode.Mutable(value);
        }

        public static FlagNode of(boolean value, KeyValueNode attributes) {
            return new FlagNode.Mutable(value, attributes);
        }

        public static FlagNode withDisabledAttributes(boolean value) {
            return new FlagNode.Mutable(value, KeyValueNode.Ignorant.INSTANCE);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Immutable implements FlagNode, Node.Immutable {
        public static final FlagNode TRUE = new FlagNode.Immutable(true, KeyValueNode.Immutable.empty());
        public static final FlagNode FALSE = new FlagNode.Immutable(false, KeyValueNode.Immutable.empty());

        private final boolean value;
        private final KeyValueNode attributes;

        public Immutable(boolean value) {
            this(value, KeyValueNode.Immutable.empty());
        }

        @Override
        public boolean getPrimitiveValue() {
            return value;
        }

        @Override
        public FlagNode withPrimitiveValue(boolean value) {
            return of(value, attributes);
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public FlagNode withValue(Boolean value) {
            return new FlagNode.Immutable(value, attributes);
        }

        @Override
        public FlagNode invert() {
            return new FlagNode.Immutable(!value, attributes);
        }

        @Override
        public KeyValueNode getAttributes() {
            return attributes;
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            return of(value, attributes.withContent(attributes));
        }

        @Override
        public Node withAttribute(@NonNull Node key, @NonNull Node value) {
            return of(this.value, attributes.withChild(key, value));
        }

        @Override
        public Node withoutAttribute(@NonNull Node key) {
            return of(value, attributes.withoutKey(key));
        }

        @Override
        public FlagNode withoutAttributes() {
            return of(value, attributes.withoutContent());
        }

        public static FlagNode of(boolean value, @NonNull KeyValueNode attributes) {
            return new FlagNode.Immutable(value, attributes);
        }

        public static FlagNode of(boolean value) {
            return of(value, KeyValueNode.Immutable.empty());
        }

        public static FlagNode withDisabledAttributes(boolean value) {
            return of(value, KeyValueNode.Ignorant.INSTANCE);
        }
    }
}
