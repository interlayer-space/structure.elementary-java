package space.interlayer.structure.elementary.alpha.convention;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

public interface TextNode extends Node, Node.Scalar<String> {
    static TextNode mutable(@NonNull String value) {
        return Mutable.of(value);
    }
    static TextNode mutable(@NonNull String value, @NonNull KeyValueNode attributes) {
        return Mutable.of(value, attributes);
    }

    static TextNode immutable(@NonNull String value) {
        return Immutable.of(value);
    }

    static TextNode immutable(@NonNull String value, KeyValueNode attributes) {
        return Immutable.of(value, attributes);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.NONE)
    class Mutable implements TextNode, Node.Mutable {
        @NonNull
        private String value;
        @NonNull
        private KeyValueNode attributes;

        @Override
        public Node.Scalar<String> withValue(@NonNull String value) {
            this.value = value;
            return this;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            this.attributes = this.attributes.withChildren(attributes);
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

        public static TextNode of(@NonNull String value, @NonNull KeyValueNode attributes) {
            return new TextNode.Mutable(value, attributes);
        }

        public static TextNode of(@NonNull String value) {
            return of(value, KeyValueNode.mutable());
        }
    }

    @With
    @Value
    class Immutable implements TextNode, Node.Immutable {
        @NonNull
        String value;
        @NonNull
        KeyValueNode attributes;

        @Override
        public Node withAttribute(@NonNull Node key, @NonNull Node value) {
            return of(this.value, attributes.withChild(key, value));
        }

        @Override
        public Node withoutAttribute(@NonNull Node key) {
            return of(value, attributes.withoutKey(key));
        }

        @Override
        public Node withoutAttributes() {
            return of(value, attributes.withoutContent());
        }

        public static TextNode of(@NonNull String value, @NonNull KeyValueNode attributes) {
            return new TextNode.Immutable(value, attributes);
        }

        public static TextNode of(@NonNull String value) {
            return of(value, KeyValueNode.immutable());
        }

        public static TextNode withDisabledAttributes(@NonNull String value) {
            return of(value, KeyValueNode.ignorant());
        }
    }
}
