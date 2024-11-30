package space.interlayer.structure.elementary.alpha.manipulation;

import lombok.NoArgsConstructor;
import space.interlayer.structure.elementary.alpha.core.TopTypeNode;

public interface Directive<N extends TopTypeNode<N>, S> {
    N resolve(Context<N, S> context);

    @NoArgsConstructor
    class CyclicDependencyException extends RuntimeException {
        public CyclicDependencyException(String message) {
            super(message);
        }

        public CyclicDependencyException(String message, Throwable cause) {
            super(message, cause);
        }

        public CyclicDependencyException(Throwable cause) {
            super(cause);
        }

        protected CyclicDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
