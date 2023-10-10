package enterprises.stardust.jsandbox.api.restrict;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@EqualsAndHashCode
public class Restriction {
    private final String id;
    private final Restriction[] parents;

    public Restriction(@NotNull String id, @NonNull Restriction... parents) {
        this.id = id;
        this.parents = parents == null ? new Restriction[0] : parents;
    }
}
