package ecs;

import ecs.component.AbstractComponentType;
import ecs.datastorage.AbstractDataStorage;
import ecs.datastorage.AbstractSliceInitScript;
import ecs.datastorage.DataStorage;
import ecs.datastorage.AbstractDataStorageConfig;
import ecs.system.SystemChainInfo;
import util.messaging.AbstractPublishSubscribeBoard;
import util.messaging.PublishSubscribeBoard;
import util.messaging.Topic;

import static ecs.ECSTopics.ECS_TOPICS;

public class SliceProvider implements AbstractSliceProvider {

    private PublishSubscribeBoard globalBoard;
    private AbstractECSInterfaceFactory ecsInterfaceFactory;

    private final String name;
    private final boolean refresh;
    private final Topic<?>[] totalSliceTopics;
    private final AbstractDataStorageConfig dataStorageConfig;
    private final SystemChainInfo<?>[] systemChainsInfo;
    private final AbstractSliceInitScript initScript;

    private AbstractSlice storedSlice;

    public SliceProvider(String name,
                         boolean refresh,
                         Topic<?>[] userTopics,
                         AbstractDataStorageConfig dataStorageConfig,
                         SystemChainInfo<?>[] systemChainsInfo,
                         AbstractSliceInitScript initScript) {

        this.name = name;
        this.refresh = refresh;
        totalSliceTopics = makeTotalTopics(userTopics, dataStorageConfig.getTypes());
        this.dataStorageConfig = dataStorageConfig;
        this.systemChainsInfo = systemChainsInfo;
        this.initScript = initScript;
    }

    private Topic<?>[] makeTotalTopics(Topic<?>[] userTopics, AbstractComponentType<?>[] types){
        Topic<?>[] toRet = new Topic[userTopics.length + ECS_TOPICS.length + (types.length * 2)];
        int index = 0;
        for(int i = 0; i < ECS_TOPICS.length; ++i, ++index){
            toRet[index] = ECS_TOPICS[i];
        }
        for(int i = 0; i < userTopics.length; ++i, ++index){
            toRet[index] = userTopics[i];
        }
        for(AbstractComponentType<?> type : types){
            toRet[index++] = type.getSetComponentTopic();
            toRet[index++] = type.getRemoveComponentTopic();
        }
        return toRet;
    }

    @Override
    public void init(PublishSubscribeBoard globalBoard, AbstractECSInterfaceFactory ecsInterfaceFactory) {
        this.globalBoard = globalBoard;
        this.ecsInterfaceFactory = ecsInterfaceFactory;
        if(!refresh){
            storedSlice = makeSlice(globalBoard, ecsInterfaceFactory);
        }
    }

    @Override
    public AbstractSlice getSlice(){
        if(refresh) {
            return makeSlice(globalBoard, ecsInterfaceFactory);
        }
        if(storedSlice == null){
            return storedSlice = makeSlice(globalBoard, ecsInterfaceFactory);
        }
        return storedSlice;
    }

    private AbstractSlice makeSlice(PublishSubscribeBoard globalBoard, AbstractECSInterfaceFactory ecsInterfaceFactory){
        AbstractPublishSubscribeBoard sliceBoard = new PublishSubscribeBoard(totalSliceTopics);
        AbstractDataStorage dataStorage = new DataStorage(dataStorageConfig, sliceBoard);
        AbstractECSInterface ecsInterface = ecsInterfaceFactory.makeECSInterface(globalBoard, sliceBoard, dataStorage);
        initScript.runOn(ecsInterface);

        return new Slice(name, systemChainsInfo, ecsInterface);
    }

    @Override
    public String getName() {
        return name;
    }
}