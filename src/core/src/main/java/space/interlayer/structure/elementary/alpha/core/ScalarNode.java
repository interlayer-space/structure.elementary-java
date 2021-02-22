package space.interlayer.structure.elementary.alpha.core;

/**
 * Scalar node represents a leaf node that contains value of particular
 * type.
 *
 * @param <N> Specific node type.
 * @param <T> Enclosed type.
 */
public interface ScalarNode<N extends Node<N>, T> extends Node<N> {
    /**
     * @return Enclosed value, which may <b>never</b> be null.
     */
    T getValue();

    interface Mutable<N extends Node<N>, T> extends ScalarNode<N, T> {
        void setValue(T value);
    }
}
