package space.interlayer.structure.elementary.alpha.convention;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

public interface NumericNode extends Node.Scalar<Double> {
    double getPrimitiveValue();
    NumericNode withPrimitiveValue(double value);

    static NumericNode mutable(double value) {
        return Mutable.of(value);
    }

    static NumericNode mutable(double value, @NonNull KeyValueNode attributes) {
        return Mutable.of(value, attributes);
    }

    static NumericNode immutable(double value) {
        return Immutable.of(value);
    }

    static NumericNode immutable(double value, @NonNull KeyValueNode attributes) {
        return Immutable.of(value, attributes);
    }

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.NONE)
    class Mutable implements NumericNode, Node.Mutable {
        private double value;
        private KeyValueNode attributes;

        @Override
        public double getPrimitiveValue() {
            return value;
        }

        @Override
        public NumericNode.Mutable withPrimitiveValue(double value) {
            this.value = value;
            return this;
        }

        @Override
        public Double getValue() {
            return value;
        }

        @Override
        public NumericNode.Mutable withValue(Double value) {
            this.value = value;
            return this;
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            return of(value, this.attributes.withChildren(attributes));
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
        public Node withoutAttributes() {
            return of(value, attributes.withoutContent());
        }

        public static NumericNode of(double value, @NonNull KeyValueNode attributes) {
            return new Mutable(value, attributes);
        }

        public static NumericNode of(double value) {
            return new Mutable(value, KeyValueNode.mutable());
        }
    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class Immutable implements NumericNode, Node.Immutable {
        public static final NumericNode ZERO = of(0);
        public static final NumericNode ONE = of(1);
        public static final NumericNode NEGATIVE_ONE = of(-1);

        double value;
        KeyValueNode attributes;

        @Override
        public double getPrimitiveValue() {
            return value;
        }

        @Override
        public NumericNode withPrimitiveValue(double value) {
            return of(value, attributes);
        }

        @Override
        public Double getValue() {
            return value;
        }

        @Override
        public NumericNode withValue(Double value) {
            return of(value, attributes);
        }

        @Override
        public Node withAttributes(@NonNull KeyValueNode attributes) {
            return of(value, this.attributes.withChildren(attributes));
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
        public Node withoutAttributes() {
            return of(value, attributes.withoutContent());
        }

        public static NumericNode of(double value, @NonNull KeyValueNode attributes) {
            return new Immutable(value, attributes);
        }

        public static NumericNode of(double value) {
            return new Immutable(value, KeyValueNode.immutable());
        }

        public static NumericNode withDisabledAttributes(double value) {
            return of(value, KeyValueNode.ignorant());
        }
    }
}
