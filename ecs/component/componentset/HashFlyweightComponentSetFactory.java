package ecs.component.componentset;

import ecs.component.AbstractComponentType;
import util.flyweight.AbstractFlyweightSet;
import util.flyweight.FlyweightHashSet;
import util.tuple.Tuple2;

public class HashFlyweightComponentSetFactory extends AbstractFlyweightComponentSetFactoryTemplate {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final AbstractFlyweightSet<AbstractComponentSet_PP> canonicalComponentSets;
    private final AbstractComponentType<?>[] validTypes;

    public HashFlyweightComponentSetFactory(AbstractComponentType<?>[] validTypes) {
        canonicalComponentSets = new FlyweightHashSet<>();
        this.validTypes = validTypes;
    }

    @Override
    public AbstractComponentSet makeSet() {
        return getCanonicalSetAndBroadcastIfNew(new ComponentSet(validTypes));
    }

    @Override
    public AbstractComponentSet makeSet(AbstractComponentType<?>... types) {
        return getCanonicalSetAndBroadcastIfNew(new ComponentSet(this.validTypes, types));
    }

    @Override
    public AbstractComponentSet addComponent(AbstractComponentSet base, AbstractComponentType<?> type) {
        return getCanonicalSetAndBroadcastIfNew(castToPackage(base).addComponent(type));
    }

    @Override
    public AbstractComponentSet addComponents(AbstractComponentSet base, AbstractComponentType<?>... types) {
        return getCanonicalSetAndBroadcastIfNew(castToPackage(base).addComponents(types));
    }

    @Override
    public AbstractComponentSet removeComponent(AbstractComponentSet base, AbstractComponentType<?> type) {
        return getCanonicalSetAndBroadcastIfNew(castToPackage(base).removeComponent(type));
    }

    @Override
    public AbstractComponentSet removeComponents(AbstractComponentSet base, AbstractComponentType<?>... types) {
        return getCanonicalSetAndBroadcastIfNew(castToPackage(base).removeComponents(types));
    }

    private AbstractComponentSet_PP getCanonicalSetAndBroadcastIfNew(AbstractComponentSet_PP set){
        Tuple2<AbstractComponentSet_PP, Boolean> setAndIsNew = canonicalComponentSets.getAndCheckIfNew(set);
        AbstractComponentSet_PP toRet = setAndIsNew.a;
        if(setAndIsNew.b){
            newComponentSetBroadcaster.setPushData(toRet);
            newComponentSetBroadcaster.broadcast();
        }
        return toRet;
    }

    private AbstractComponentSet_PP castToPackage(AbstractComponentSet toCast){
        return (AbstractComponentSet_PP)toCast;
    }
}
