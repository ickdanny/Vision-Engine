package internalconfig.game.components.spawns;

public interface Spawns {
    int getDuration();
    boolean loop();
    int getIndex();
    static Spawns[] values(){
        return SpawnRepository.SPAWNS.toArray(new Spawns[0]);
    }
}