package space.interlayer.structure.elementary.alpha.convention;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public interface GroupNode extends Node.Container<Node> {
    interface Indexed extends GroupNode, Countable<Node> {
        Node get(long index);
        boolean hasIndex(long index);
        GroupNode drop(long index);
        Indexed set(long index, Node value);
    }

    interface Ordered extends GroupNode {
        Ordered sorted(@NonNull Comparator<Node> comparator);
    }

    interface Sequence extends Indexed, Ordered {}

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class Mutable implements Sequence {
        @NonNull
        private final List<Node> children;
        @NonNull
        private KeyValueNode attributes;

        private Mutable() {
            this(new ArrayList<>(), KeyValueNode.mutable());
        }

        private Mutable(List<Node> children) {
            this(children, KeyValueNode.mutable());
        }

        private Mutable(KeyValueNode attributes) {
            this(new ArrayList<>(), attributes);
        }

        @Override
        public Node get(long index) {
            // todo out of bounds index
            return children.get((int) index);
        }

        @Override
        public GroupNode drop(long index) {
            children.remove((int) index);
            return this;
        }

        @Override
        public Ordered sorted(@NonNull Comparator<Node> comparator) {
            children.sort(comparator);
            return this;
        }

        @Override
        public Iterable<? extends Node> getChildren() {
            return Collections.unmodifiableList(children);
        }

        @Override
        public boolean hasIndex(long index) {
            return children.size() > index;
        }

        @Override
        public long getChildCount() {
            return children.size();
        }

        @Override
        public GroupNode withContent(@NonNull Iterable<? extends Node> children) {
            int index = 0;
            int size = this.children.size();
            for (Node child : children) {
                if (size > index) {
                    this.children.set(index, child);
                } else {
                    this.children.add(index, child);
                }
                index++;
            }

            if (size > index) {
                this.children.subList(index, size).clear();
            }

            return this;
        }

        @Override
        public Node.Container<Node> withoutChild(Node child) {
            this.children.remove(child);
            return this;
        }

        @Override
        public Node.Container<Node> withoutContent() {
            this.children.clear();
            return this;
        }
    }

    @RequiredArgsConstructor
    class ImmutableSequence implements Sequence {
        public static final ImmutableSequence EMPTY = new ImmutableSequence(Collections.emptyList());

        private final List<Node> children;

        @Override
        public Node get(long index) {
            // todo long bounds checking
            return children.get((int) index);
        }

        @Override
        public boolean hasIndex(long index) {
            return children.size() > index;
        }

        @Override
        public GroupNode drop(long index) {
            List<Node> replacement = new ArrayList<>(children);
            // todo long bounds checking
            replacement.remove((int) index);
            return new ImmutableSequence(replacement);
        }

        @Override
        public Ordered sorted(@NonNull Comparator<Node> comparator) {
            List<Node> replacement = new ArrayList<>(children);
            replacement.sort(comparator);
            return new ImmutableSequence(replacement);
        }

        @Override
        public long getChildCount() {
            return children.size();
        }

        @Override
        public Iterable<? extends Node> getChildren() {
            return Collections.unmodifiableList(children);
        }

        @Override
        public Node.Container<Node> withChildren(@NonNull Iterable<? extends Node> children) {
            int size = children instanceof Collection ? ((Collection<? extends Node>) children).size() : 0;
            List<Node> replacement = new ArrayList<>(size);
            for (Node child : children) {
                replacement.add(child);
            }
            return new ImmutableSequence(replacement);
        }

        @Override
        public Node.Container<Node> withoutChild(Node child) {
            List<Node> replacement = new ArrayList<>(children);
            replacement.remove(child);
            return new ImmutableSequence(replacement);
        }

        @Override
        public Node.Container<Node> withoutContent() {
            return EMPTY;
        }
    }
}
