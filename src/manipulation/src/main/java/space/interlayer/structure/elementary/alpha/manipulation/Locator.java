package space.interlayer.structure.elementary.alpha.manipulation;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import space.interlayer.structure.elementary.alpha.core.TopTypeNode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public interface Locator<N extends TopTypeNode<N>, S> {
    Result<N, S> locate(N root, List<S> location);

    default Optional<N> find(N root, List<S> location) {
        return Optional.of(locate(root, location))
            .filter(Result::isSuccessful)
            .map(Result::getNode);
    }

    @Value
    class Result<N extends TopTypeNode<N>, S> {
        List<S> walked;
        List<S> remainder;
        N node;

        boolean isSuccessful() {
            return remainder.isEmpty();
        }
    }

    @RequiredArgsConstructor
    class Standard<N extends TopTypeNode<N>, S> implements Locator<N, S> {
        private final Probe<N, S> walker;

        @Override
        public Result<N, S> locate(N root, List<S> location) {
            List<S> walked = new ArrayList<>(location.size());
            Deque<S> remainder = new ArrayDeque<>(location);
            N current = root;
            for (S segment : location) {
                N candidate = walker.advance(current, segment);
                if (candidate == null) {
                    return new Result<>(
                            walked,
                            new ArrayList<>(remainder),
                            current
                    );
                }

                current = candidate;
                walked.add(segment);
                remainder.removeFirst();
            }

            return new Result<>(
                    walked,
                    Collections.emptyList(),
                    current
            );
        }

        public interface Probe<N extends TopTypeNode<N>, S> {
            N advance(N node, S segment);
        }
    }
}
