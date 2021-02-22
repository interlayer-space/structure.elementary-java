package space.interlayer.structure.elementary.alpha.core;

/**
 * A node for null value. It is not a {@link ScalarNode} since it is
 * treated as no value at all.
 *
 * @param <N> Specific node type.
 */
public interface NullNode<N extends Node<N>> extends Node<N> {}
