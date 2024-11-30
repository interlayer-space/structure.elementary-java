/**
 * <p>
 *     This package contains regular definitions for the most classic
 *     type-free serialization formats, namely YAML, JSON and XML,
 *     trying to establish a common ground between all of them.
 *     It is based on the six basic types that are encountered in these
 *     formats:
 *
 *     <ul>
 *         <li>{@link space.interlayer.structure.elementary.alpha.convention.NullNode Null}</li>
 *         <li>{@link space.interlayer.structure.elementary.alpha.convention.FlagNode Boolean}</li>
 *         <li>{@link space.interlayer.structure.elementary.alpha.convention.NumericNode Double}</li>
 *         <li>{@link space.interlayer.structure.elementary.alpha.convention.TextNode String}</li>
 *         <li>{@link space.interlayer.structure.elementary.alpha.convention.GroupNode Collection}</li>
 *         <li>{@link space.interlayer.structure.elementary.alpha.convention.KeyValueNode Dictionary}</li>
 *     </ul>
 *
 *     There are some peculiarities around in the formats, such as set
 *     type in YAML and attributes in XML. To establish these common
 *     grounds and prevent repetition of nearly-identical class
 *     hierarchies, the basic definitions assume existence of node
 *     attributes, absence of order, indexing and count in collections,
 *     and absence of count in dictionaries.
 * </p>
 *
 * <p>
 *     Due to the absence of generic support for value types, all scalar
 *     nodes provide additional {@code #getPrimitiveValue()} and
 *     {@code withPrimitiveValue()} types.
 * </p>
 *
 * <p>
 *     The library distinguishes mutable and immutable nodes. Consumers
 *     should usually not rely on this at all and just consume output of
 *     the operations over nodes - for example, both mutable and
 *     immutable scalar nodes will respond with an instance of the same
 *     type when {@code #withPrimitiveValue()} is called, and the only
 *     difference is that the mutable node will return itself. This
 *     allows to completely decouple from the actual semantics and
 *     perform operations without distinguishing the two. However,
 * </p>
 *
 * <p>
 *     Despite many places where the hierarchy could rely on standard
 *     java types,
 * </p>
 */
package space.interlayer.structure.elementary.alpha.convention;
