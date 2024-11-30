package space.interlayer.structure.elementary.alpha.manipulation;

import lombok.Value;

import java.util.List;

public interface Location<S> {
    boolean isAbsolute();
    List<S> getSegments();

    default boolean isEmpty() {
        return getSegments().isEmpty();
    }

    class Standard<S> implements Location<S> {
        private final boolean absolute;
        private final List<S> segments;

        public Standard(List<S> segments, boolean absolute) {
            this.absolute = absolute;
            this.segments = segments;
        }

        @Override
        public boolean isAbsolute() {
            return absolute;
        }

        @Override
        public List<S> getSegments() {
            return segments;
        }

        public static <S> Standard<S> absolute(List<S> segments) {
            return new Standard<>(segments, true);
        }

        public static <S> Standard<S> relative(List<S> segments) {
            return new Standard<>(segments, false);
        }
    }

    @Value
    class StandardSegment {
        long index;
        String key;
        boolean isIndex;

        public Kind getKind() {
            return isIndex ? Kind.INDEX : Kind.KEY;
        }

        public boolean isKey() {
            return !isIndex;
        }

        public static StandardSegment index(long value) {
            return new StandardSegment(value, null, true);
        }

        public static StandardSegment key(String value) {
            return new StandardSegment(0, value, false);
        }

        public enum Kind {
            INDEX,
            KEY
        }
    }

    interface Locator {}
}
