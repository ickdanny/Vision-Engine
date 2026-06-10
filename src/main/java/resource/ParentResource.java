package resource;

import java.util.List;

public class ParentResource extends Resource<List<Resource<?>>> {
    public ParentResource(String id, AbstractResourceOrigin origin, List<Resource<?>> childList,
                          AbstractResourceType<List<Resource<?>>> type){
        super(id, origin, childList, type);
    }

    @Override
    public void unloadData(){
        for(Resource<?> child : data){
            child.unloadData();
        }
        loaded = false;
    }

    @Override
    public void reloadData(){ //will not reread the child list
        for(Resource<?> child : data){
            child.reloadData();
        }
        loaded = true;
    }
}