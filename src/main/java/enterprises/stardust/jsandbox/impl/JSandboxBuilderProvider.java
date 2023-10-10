package enterprises.stardust.jsandbox.impl;

import enterprises.stardust.jsandbox.api.APIService;
import enterprises.stardust.jsandbox.api.JSandbox;

public class JSandboxBuilderProvider implements APIService.Provider {
    @Override
    public JSandbox.Builder provideBuilder() {
        return new JSandboxImpl.Builder();
    }
}
