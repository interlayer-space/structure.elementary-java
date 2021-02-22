package space.interlayer.structure.elementary.alpha.core;

import java.math.BigDecimal;

/**
 * Node that contains numbers. {@link BigDecimal} was chosen to preserve
 * precision.
 *
 * @param <N> Specific node type.
 */
public interface NumericNode<N extends Node<N>> extends ScalarNode<N, BigDecimal> {
    interface Mutable<N extends Node<N>> extends NumericNode<N>, ScalarNode.Mutable<N, BigDecimal> {}
}
