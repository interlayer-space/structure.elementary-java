package space.interlayer.structure.elementary.alpha.manipulation;

import lombok.Value;
import space.interlayer.structure.elementary.alpha.core.TopTypeNode;

import java.util.List;

/**
 * An abstract operation that can be applied to structure to create
 * another one based on it.
 *
 * @param <N> Specific node type.
 */
public interface Operation<N extends TopTypeNode<N>, S> {
    N apply(Context<N, S> context);

    class Identity<N extends TopTypeNode<N>, S> implements Operation<N, S> {
        @Override
        public N apply(Context<N, S> context) {
            return context.getNode();
        }
    }

    @Value
    class Immediate<N extends TopTypeNode<N>, S> implements Operation<N, S> {
        N node;

        @Override
        public N apply(Context<N, S> context) {
            return this.node;
        }
    }

    @Value
    class Conditional<N extends TopTypeNode<N>, S> implements Operation<N, S> {
        Operation<N, S> delegate;
        Condition<N, S> condition;

        @Override
        public N apply(Context<N, S> context) {
            if (!condition.test(context)) {
                return context.getNode();
            }

            return delegate.apply(context);
        }
    }

    @Value
    class Branching<N extends TopTypeNode<N>, S> implements Operation<N, S> {
        Condition<N, S> condition;
        Operation<N, S> passing;
        Operation<N, S> failing;

        @Override
        public N apply(Context<N, S> context) {
            return condition.test(context) ? passing.apply(context) : failing.apply(context);
        }
    }

    class Switch<N extends TopTypeNode<N>, S> implements Operation<N, S> {
        List<Entry<N, S>> branches;
        Operation<N, S> fallback;

        @Override
        public N apply(Context<N, S> context) {
            for (Entry<N, S> branch : branches) {
                if (branch.test(context)) {
                    return branch.apply(context);
                }
            }

            return fallback.apply(context);
        }

        @Value
        private static class Entry<N extends TopTypeNode<N>, S> {
            Condition<N, S> condition;
            Operation<N, S> operation;

            public boolean test(Context<N, S> context) {
                return condition.test(context);
            }

            public N apply(Context<N, S> context) {
                return operation.apply(context);
            }
        }
    }

    @Value
    class Sequence<N extends TopTypeNode<N>, S> implements Operation<N, S> {
        List<Operation<N, S>> delegates;

        @Override
        public N apply(Context<N, S> context) {
            Context<N, S> snapshot = context;

            for (Operation<N, S> delegate : delegates) {
                N node = delegate.apply(snapshot);
                snapshot = context.withNode(node);
            }

            return snapshot.getNode();
        }
    }
}
