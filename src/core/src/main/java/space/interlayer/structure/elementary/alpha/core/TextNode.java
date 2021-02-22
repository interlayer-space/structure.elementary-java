package space.interlayer.structure.elementary.alpha.core;

/**
 * Node enclosing string value.
 *
 * @param <N> Specific node type.
 */
public interface TextNode<N extends Node<N>> extends ScalarNode<N, String> {
    interface Mutable<N extends Node<N>> extends TextNode<N>, ScalarNode.Mutable<N, String> {}
}
