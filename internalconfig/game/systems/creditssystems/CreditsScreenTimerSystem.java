package internalconfig.game.systems.creditssystems;

import ecs.AbstractECSInterface;
import ecs.system.AbstractSystem;
import ecs.system.AbstractSystemInstance;
import internalconfig.game.SliceUtil;

@SuppressWarnings("unused")
public class CreditsScreenTimerSystem implements AbstractSystem<Double> {

    @Override
    public AbstractSystemInstance<Double> makeInstance() {
        return new Instance();
    }

    private static class Instance implements AbstractSystemInstance<Double>{

        private static final int TIMER = 60 * 31;

        private int timer;

        private Instance(){
            timer = TIMER;
        }

        @Override
        public void run(AbstractECSInterface ecsInterface, Double data) {
            if(timer > 0){
                --timer;
            }
            if(timer <= 0){
                SliceUtil.returnToMain(ecsInterface);
            }
            ecsInterface.getSliceBoard().ageAndCullMessages();
        }
    }
}
