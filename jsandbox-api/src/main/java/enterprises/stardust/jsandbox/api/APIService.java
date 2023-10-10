package enterprises.stardust.jsandbox.api;

import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.ServiceLoader;

@ApiStatus.Internal
public class APIService {
    private static final ServiceLoader<Provider> LOADER =
            ServiceLoader.load(Provider.class);
    private static Provider PROVIDER;

    static Provider provider() {
        if (PROVIDER == null) {
            LOADER.reload();
            Iterator<Provider> iterator = LOADER.iterator();
            if (!iterator.hasNext()) {
                throw new RuntimeException("No JSandbox implementation found");
            }
            PROVIDER = iterator.next();
        }
        return PROVIDER;
    }

    @ApiStatus.Internal
    public interface Provider {
        JSandbox.Builder provideBuilder();
    }
}
