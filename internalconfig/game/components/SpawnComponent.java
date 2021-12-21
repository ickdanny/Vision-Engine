package internalconfig.game.components;

import internalconfig.game.components.spawns.Spawns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpawnComponent {

    private final List<SpawnUnit> spawnUnits;

    public SpawnComponent(){
        spawnUnits = new ArrayList<>();
    }

    public SpawnComponent addSpawnUnit(Spawns spawn){
        spawnUnits.add(new SpawnUnit(spawn));
        return this;
    }

    public SpawnComponent addSpawnUnit(Spawns spawn, boolean loop){
        spawnUnits.add(new SpawnUnit(spawn, loop));
        return this;
    }

    public void clear(){
        spawnUnits.clear();
    }

    public boolean isEmpty(){
        return spawnUnits.isEmpty();
    }

    public boolean containsSpawn(Spawns spawn){
        for(SpawnUnit spawnUnit : spawnUnits){
            if(spawnUnit.getSpawn() == spawn){
                return true;
            }
        }
        return false;
    }

    public Iterator<SpawnUnit> iterator(){
        return spawnUnits.iterator();
    }
}
