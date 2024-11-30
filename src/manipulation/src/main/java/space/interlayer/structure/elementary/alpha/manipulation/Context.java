package space.interlayer.structure.elementary.alpha.manipulation;

import lombok.NonNull;
import lombok.Value;
import lombok.With;
import space.interlayer.structure.elementary.alpha.core.TopTypeNode;
import space.interlayer.structure.elementary.alpha.core.api.Node;

@With
@Value
public class Context<N extends TopTypeNode<N>, S> {
    @NonNull
    N root;

    N node;
    @NonNull
    Location<S> location;
}
