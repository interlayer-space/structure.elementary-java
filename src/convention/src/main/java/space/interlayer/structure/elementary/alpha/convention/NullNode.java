package space.interlayer.structure.elementary.alpha.convention;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public interface NullNode extends Node.Immutable {
    static NullNode mutable(@NonNull KeyValueNode attributes) {
        return Mutable.of(attributes);
    }

    static NullNode mutable() {
        return Mutable.withNoAttributes();
    }

    static NullNode immutable(@NonNull KeyValueNode attributes) {
        return Immutable.of(attributes);
    }

    static NullNode immutable() {
        return Immutable.withNoAttributes();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class Mutable implements NullNode {
        private KeyValueNode attributes = KeyValueNode.mutable();

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            this.attributes = attributes.withChildren(attributes);
            return this;
        }

        @Override
        public Node withAttribute(@NonNull Node key, @NonNull Node value) {
            attributes = attributes.withChild(key, value);
            return this;
        }

        @Override
        public Node withoutAttribute(@NonNull Node key) {
            attributes = attributes.withoutKey(key);
            return this;
        }

        @Override
        public Node withoutAttributes() {
            attributes = attributes.withoutContent();
            return this;
        }

        static NullNode of(@NonNull KeyValueNode attributes) {
            return new Mutable(attributes);
        }

        static NullNode withNoAttributes() {
            return of(KeyValueNode.immutable());
        }

        static NullNode withDisabledAttributes() {
            return of(KeyValueNode.ignorant());
        }
    }

    @Value
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    class Immutable implements NullNode {
        KeyValueNode attributes;

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            return of(this.attributes.withChildren(attributes));
        }

        @Override
        public Node withAttribute(@NonNull Node key, @NonNull Node value) {
            return of(attributes.withChild(key, value));
        }

        @Override
        public Node withoutAttribute(@NonNull Node key) {
            return of(attributes.withoutKey(key));
        }

        @Override
        public Node withoutAttributes() {
            return of(attributes.withoutContent());
        }

        static NullNode of(@NonNull KeyValueNode attributes) {
            return new Immutable(attributes);
        }

        static NullNode withNoAttributes() {
            return of(KeyValueNode.immutable());
        }

        static NullNode withDisabledAttributes() {
            return of(KeyValueNode.ignorant());
        }
    }
}
