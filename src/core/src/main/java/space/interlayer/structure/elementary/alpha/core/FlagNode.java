package space.interlayer.structure.elementary.alpha.core;

/**
 * Node containing yes/no (boolean) value.
 *
 * @param <N> Specific node type.
 */
public interface FlagNode<N extends Node<N>> extends ScalarNode<N, Boolean> {
    interface Mutable<N extends Node<N>> extends FlagNode<N>, ScalarNode.Mutable<N, Boolean> {}
}
