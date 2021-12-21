package internalconfig.game.components.spawns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class SpawnRepository {
    static final List<Spawns> SPAWNS;
    static{
        SPAWNS = new ArrayList<>();
        SPAWNS.addAll(Arrays.asList(StageSpawns.values()));
        SPAWNS.addAll(Arrays.asList(PlayerSpawns.values()));
        SPAWNS.addAll(Arrays.asList(DanmakuSpawns.values()));
        SPAWNS.addAll(Arrays.asList(DeathSpawns.values()));
        SPAWNS.addAll(Arrays.asList(PickupSpawns.values()));
        SPAWNS.addAll(Arrays.asList(OtherGameSpawns.values()));
        SPAWNS.addAll(Arrays.asList(MiscSpawns.values()));
        SPAWNS.addAll(Arrays.asList(GraphicSpawns.values()));
        setIndices();
    }

    private static void setIndices(){
        int counter = 0;
        for(Spawns spawn : SPAWNS){
            ((Spawns_PP)spawn).setIndex(counter++);
        }
    }
}