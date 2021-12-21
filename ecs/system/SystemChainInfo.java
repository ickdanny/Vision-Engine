package ecs.system;

public class SystemChainInfo<T> {

    private final AbstractSystemChainCall<T> call;
    private final AbstractSystemChainFactory<T> factory;
    private final boolean transparent;

    public SystemChainInfo(AbstractSystemChainCall<T> call, AbstractSystemChainFactory<T> factory, boolean transparent) {
        this.call = call;
        this.factory = factory;
        this.transparent = transparent;
    }

    public AbstractSystemChainCall<T> getCall() {
        return call;
    }

    public AbstractSystemChainFactory<T> getFactory() {
        return factory;
    }

    public boolean isTransparent() {
        return transparent;
    }
}
