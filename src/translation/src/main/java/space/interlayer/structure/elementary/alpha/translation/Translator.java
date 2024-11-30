package space.interlayer.structure.elementary.alpha.translation;

import space.interlayer.structure.elementary.alpha.core.TopTypeNode;

/**
 * Basic interoperability unit that converts node representations
 * between library-specific intrinsic types and structure.elementary
 * compatible types. Since such a translator may accept and transform
 * into intrinsic representation less restricted interface than it
 * produces, different types {@link A} and {@link P} are used instead
 * of one.
 *
 * @param <I> Intrinsic library type.
 * @param <A> Accepted type that can be encoded into {@link I}.
 * @param <P> Produced type that is created from {@link I}.
 */
@SuppressWarnings("unused")
public interface Translator<I, A extends TopTypeNode<A>, P extends A> {
    /**
     * Converts structure.elementary compatible structure into intrinsic
     * representation.
     *
     * @param node Structure to convert.
     * @return Intrinsic representation of structure.
     */
    I encode(A node);

    /**
     * Restores interoperable structure from intrinsic representation.
     *
     * @param intrinsic Library-compatible structure representation.
     * @return Interoperable structure reflecting passed one.
     */
    P decode(I intrinsic);
}
