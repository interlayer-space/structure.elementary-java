package space.interlayer.structure.elementary.alpha.core;

import java.util.Objects;

/**
 * This enum allows to describe node kind in a more switch-friendly way
 * rather than checking which interface it implements.
 *
 * Nodes that are not supported by this library directly have to have
 * {@link NodeKind#SPECIAL} kind, for example common pattern of
 * MissingNode would have to carry this kind.
 *
 * Consuming implementations should be aware of {@link NodeKind#SPECIAL}
 * kind and pass such nodes through without processing where possible,
 * throwing compatibility exceptions otherwise.
 */
@SuppressWarnings("rawtypes")
public enum NodeKind {
    NULL(Category.NULL, NullNode.class),
    FLAG(Category.SCALAR, FlagNode.class),
    DECIMAL(Category.SCALAR, NumericNode.class),
    TEXT(Category.SCALAR, TextNode.class),
    GROUP(Category.CONTAINER, GroupNode.class),
    KEY_VALUE(Category.CONTAINER, KeyValueNode.class),
    SPECIAL(Category.SPECIAL, null);
    
    private final Category category;
    private final Class<? extends Node> type;

    NodeKind(Category category, Class<? extends Node> type) {
        this.category = category;
        this.type = type;
    }

    public boolean hasCategory(Category category) {
        return this.category.equals(Objects.requireNonNull(category));
    }
    
    public boolean isKindFor(Class<? extends Node> type) {
        Objects.requireNonNull(type);

        if (this.type != null) {
            return this.type.isAssignableFrom(type);
        }

        return equals(compute(type));
    }
    
    public static NodeKind compute(Class<? extends Node> type) {
        Objects.requireNonNull(type);

        for (NodeKind candidate : values()) {
            if (candidate.type == null) {
                continue;
            }
            
            if (candidate.type.isAssignableFrom(type)) {
                return candidate;
            }
        }
        
        return SPECIAL;
    }

    public enum Category {
        NULL,
        SCALAR,
        CONTAINER,
        SPECIAL
    }
}
