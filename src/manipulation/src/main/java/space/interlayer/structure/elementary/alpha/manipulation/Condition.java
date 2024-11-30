package space.interlayer.structure.elementary.alpha.manipulation;

import lombok.NonNull;
import lombok.Value;
import space.interlayer.structure.elementary.alpha.core.TopTypeNode;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * todo
 * @param <N>
 * @param <S>
 */
@SuppressWarnings("unsued")
public interface Condition<N extends TopTypeNode<N>, S> {
    boolean test(@NonNull Context<N, S> context);

    default Condition<N, S> and(@NonNull Collection<Condition<N, S>> conditions) {
        List<Condition<N, S>> merged = Stream.concat(Stream.of(this), conditions.stream())
                .collect(Collectors.toList());
        return new Conjunction<>(merged);
    }

    default Condition<N, S> or(@NonNull Collection<Condition<N, S>> conditions) {
        List<Condition<N, S>> merged = Stream.concat(Stream.of(this), conditions.stream())
                .collect(Collectors.toList());
        return new Disjunction<>(merged);
    }

    @Value
    class Conjunction<N extends TopTypeNode<N>, S> implements Condition<N, S> {
        @NonNull
        List<@NonNull Condition<N, S>> conditions;

        @Override
        public boolean test(@NonNull Context<N, S> context) {
            for (Condition<N, S> condition : conditions) {
                if (!condition.test(context)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public Condition<N, S> and(@NonNull Collection<Condition<N, S>> conditions) {
            List<Condition<N, S>> combined = Stream.concat(this.conditions.stream(), conditions.stream())
                    .collect(Collectors.toList());
            return new Conjunction<>(combined);
        }
    }

    @Value
    class Disjunction<N extends TopTypeNode<N>, S> implements Condition<N, S> {
        @NonNull
        List<@NonNull Condition<N, S>> conditions;

        @Override
        public boolean test(@NonNull Context<N, S> context) {
            for (Condition<N, S> condition : conditions) {
                if (condition.test(context)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public Condition<N, S> or(@NonNull Collection<Condition<N, S>> conditions) {
            List<Condition<N, S>> combined = Stream.concat(this.conditions.stream(), conditions.stream())
                    .collect(Collectors.toList());
            return new Disjunction<>(combined);
        }
    }
}
