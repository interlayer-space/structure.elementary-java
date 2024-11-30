package space.interlayer.structure.elementary.alpha.convention;

import lombok.NonNull;

/**
 * <p>
 *     This is a type that can be used for two purposes:
 *     <ul>
 *         <li>
 *             Providing a signal to any extra libraries that a node
 *             should be removed completely. Functions that take and
 *             return a node may struggle with returning a null value
 *             for the contracts they are bound to. They can then signal
 *             the caller that the node in scope should be completely
 *             removed by returning a missing node type.
 *         </li>
 *         <li>
 *             Providing a signal from an extra library to the end user
 *             that the node in question doesn't exist at all. For
 *             example, when user provides a path that simply doesn't
 *             exist in the node hierarchy, then the library may return
 *             a missing node if it is bound with a non-null contract.
 *             The {@link java.util.Optional} type exists for situations
 *             like this, though.
 *         </li>
 *     </ul>
 * </p>
 *
 * <p>
 *     However, returning an instance of this type is highly
 *     discouraged. When building a library on top of this convention,
 *     the end users may easily mistake missing node for a real one.
 *     Locating X then converts in returning a missing node, which may
 *     then go through a chain of operations and mutations, in/after
 *     which end user may not even know that the node in question is
 *     missing, resulting in bugs. It is better to explicitly
 *     communicate with {@link java.util.Optional Optionals} and nulls.
 * </p>
 */
public interface MissingNode extends Node.Immutable {
    @Override
    default Node withAttributes(@NonNull KeyValueNode attributes) {
        throw new UnsupportedOperationException("Missing node is ephemeral and thus doesn't support regular node operations");
    }

    @Override
    default Node withAttribute(@NonNull Node key, @NonNull Node value) {
        throw new UnsupportedOperationException("Missing node is ephemeral and thus doesn't support regular node operations");
    }

    @Override
    default Node withoutAttribute(@NonNull Node key) {
        throw new UnsupportedOperationException("Missing node is ephemeral and thus doesn't support regular node operations");
    }

    @Override
    default Node withoutAttributes() {
        throw new UnsupportedOperationException("Missing node is ephemeral and thus doesn't support regular node operations");
    }
}
