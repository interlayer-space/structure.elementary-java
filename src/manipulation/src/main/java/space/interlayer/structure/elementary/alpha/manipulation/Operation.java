package space.interlayer.structure.elementary.alpha.manipulation;

import space.interlayer.structure.elementary.alpha.core.Node;

import java.util.function.UnaryOperator;

/**
 * An abstract operation that can be applied to structure to create
 * another one based on it.
 *
 * @param <N> Specific node type.
 */
public interface Operation<N extends Node<N>> extends UnaryOperator<N> {}
