package internalconfig.game.sliceproviders;

import ecs.system.AbstractSystem;
import ecs.system.SystemChainFactory;
import internalconfig.game.systems.SystemContainer;

public class SystemChainFactoryProvider {

    private final SystemChainFactory<Double> menuMainSystemChainFactory;
    private final SystemChainFactory<Double> menuGraphicsSystemChainFactory;

    private final SystemChainFactory<Double> pauseMainSystemChainFactory;
    private final SystemChainFactory<Double> pauseGraphicsSystemChainFactory;

    private final SystemChainFactory<Double> dialogueMainSystemChainFactory;
    private final SystemChainFactory<Double> dialogueGraphicsSystemChainFactory;

    private final SystemChainFactory<Double> loadMainSystemChainFactory;
    private final SystemChainFactory<Double> loadGraphicsSystemChainFactory;

    private final SystemChainFactory<Double> gameMainSystemChainFactory;
    private final SystemChainFactory<Double> gameGraphicsSystemChainFactory;

    private final SystemChainFactory<Double> creditsMainSystemChainFactory;
    private final SystemChainFactory<Double> creditsGraphicsSystemChainFactory;

    public SystemChainFactoryProvider(SystemContainer systemContainer){
        menuMainSystemChainFactory = makeMenuMainSystemChainFactory(systemContainer);
        menuGraphicsSystemChainFactory = makeMenuGraphicsSystemChainFactory(systemContainer);

        pauseMainSystemChainFactory = makePauseMainSystemChainFactory(systemContainer);
        pauseGraphicsSystemChainFactory = makePauseGraphicsSystemChainFactory(systemContainer);

        dialogueMainSystemChainFactory = makeDialogueMainSystemChainFactory(systemContainer);
        dialogueGraphicsSystemChainFactory = makeDialogueGraphicsSystemChainFactory(systemContainer);

        loadMainSystemChainFactory = makeLoadMainSystemChainFactory(systemContainer);
        loadGraphicsSystemChainFactory = makeLoadGraphicsSystemChainFactory(systemContainer);

        gameMainSystemChainFactory = makeGameMainSystemChainFactory(systemContainer);
        gameGraphicsSystemChainFactory = makeGameGraphicsSystemChainFactory(systemContainer);

        creditsMainSystemChainFactory = makeCreditsMainSystemChainFactory(systemContainer);
        creditsGraphicsSystemChainFactory = makeCreditsGraphicsSystemChainFactory(systemContainer);
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeMenuMainSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getMenuMovePositionSystem(),
                systemContainer.getMenuLockConditionSystem(),
                systemContainer.getMenuLockingSystem(),
                systemContainer.getMenuInputParserSystem(),
                systemContainer.getMenuNavigationSystem(),
                systemContainer.getMenuButtonSystem(),
                systemContainer.getMenuGameBuilderSystem(),

                systemContainer.getMenuShotPreviewSystem(),

                systemContainer.getMenuAnimationSystem(),
                systemContainer.getMenuRotateSpriteForwardSystem(),
                systemContainer.getMenuConstantSpriteRotationSystem(),
                systemContainer.getMenuSpriteSubImageFlagUpdateSystem(),
                systemContainer.getMenuSpriteInstructionSystem(),
                systemContainer.getMenuSpriteRemovalSystem(),

                systemContainer.getMenuOutboundSystem(),

                systemContainer.getMenuDataStorageCriticalSystem(),
                systemContainer.getMenuDrawCommandSystem(),
                systemContainer.getMenuTrackStarterSystem(),
                systemContainer.getMenuMusicSystem(),
                systemContainer.getMenuSliceStackCriticalSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeMenuGraphicsSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getMenuGraphicsSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makePauseMainSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{

                systemContainer.getPauseMovePositionSystem(),
                systemContainer.getPauseInputParserSystem(),
                systemContainer.getPauseNavigationSystem(),
                systemContainer.getPauseButtonSystem(),

                systemContainer.getPauseSpriteSubImageFlagUpdateSystem(),
                systemContainer.getPauseSpriteInstructionSystem(),
                systemContainer.getPauseSpriteRemovalSystem(),


                systemContainer.getPauseDataStorageCriticalSystem(),
                systemContainer.getPauseDrawCommandSystem(),
                systemContainer.getPauseMusicSystem(),
                systemContainer.getPauseSliceStackCriticalSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makePauseGraphicsSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getPauseGraphicsSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeDialogueMainSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getDialogueInputParserSystem(),
                systemContainer.getDialogueSystem(),

                systemContainer.getDialogueSpriteSubImageFlagUpdateSystem(),
                systemContainer.getDialogueSpriteInstructionSystem(),
                systemContainer.getDialogueSpriteRemovalSystem(),

                systemContainer.getDialogueDataStorageCriticalSystem(),
                systemContainer.getDialogueDrawCommandSystem(),
                systemContainer.getDialogueMusicSystem(),
                systemContainer.getDialogueSliceStackCriticalSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeDialogueGraphicsSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getDialogueGraphicsSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeLoadMainSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getLoadScreenTimerSystem(),

                systemContainer.getLoadSpriteSubImageFlagUpdateSystem(),
                systemContainer.getLoadSpriteInstructionSystem(),
                systemContainer.getLoadSpriteRemovalSystem(),

                systemContainer.getLoadDataStorageCriticalSystem(),
                systemContainer.getLoadDrawCommandSystem(),
                systemContainer.getLoadSliceStackCriticalSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeLoadGraphicsSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getLoadGraphicsSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeGameMainSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getGameStateSystem(),
                systemContainer.getGameMovePositionSystem(),
                systemContainer.getGameProgramSystem(),
                systemContainer.getGameCollisionSystem(),
                systemContainer.getGameInputParserSystem(),
                systemContainer.getGamePlayerMovementSystem(),
                systemContainer.getGamePlayerShotSystem(),
                systemContainer.getGameDamageParserSystem(),
                systemContainer.getGameDamageHandlingSystem(),
                systemContainer.getGamePlayerStateSystem(),
                systemContainer.getGamePlayerBombSystem(),
                systemContainer.getGamePlayerDeathParserSystem(),
                systemContainer.getGameContinueAndGameOverSystem(),
                systemContainer.getGamePlayerRespawnSystem(),
                systemContainer.getGamePlayerReactivateSystem(),
                systemContainer.getGameDeathHandlingSystem(),
                systemContainer.getGameSpawnSystem(),

                systemContainer.getGamePlayerUISystem(),

                systemContainer.getGamePauseMenuEntrySystem(),
                systemContainer.getGameDialogueScreenEntrySystem(),
                systemContainer.getGameNextStageEntrySystem(),

                systemContainer.getGameAnimationSystem(),
                systemContainer.getGameRotateSpriteForwardSystem(),
                systemContainer.getGameConstantSpriteRotationSystem(),
                systemContainer.getGameSinusoidalSpriteVerticalOffsetSystem(),
                systemContainer.getGameScrollingSubImageSystem(),
                systemContainer.getGameMakeOpaqueWhenPlayerFocusedAndAliveSystem(),
                systemContainer.getGameSpriteSubImageFlagUpdateSystem(),
                systemContainer.getGameTrailSystem(),
                systemContainer.getGameSpriteInstructionSystem(),
                systemContainer.getGameSpriteRemovalSystem(),

                systemContainer.getGamePrimaryDataStorageCriticalSystem(),

                systemContainer.getGameInboundSystem(),
                systemContainer.getGameOutboundSystem(),

                systemContainer.getGameSecondaryDataStorageCriticalSystem(),

                systemContainer.getGameDrawCommandSystem(),

                systemContainer.getGameGameOverSystem(),
                systemContainer.getGameWinSystem(),
                systemContainer.getGameMusicSystem(),
                systemContainer.getGameSliceStackCriticalSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeGameGraphicsSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getGameGraphicsSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeCreditsMainSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getCreditsScreenTimerSystem(),

                systemContainer.getCreditsSpriteSubImageFlagUpdateSystem(),
                systemContainer.getCreditsSpriteInstructionSystem(),
                systemContainer.getCreditsSpriteRemovalSystem(),

                systemContainer.getCreditsDataStorageCriticalSystem(),
                systemContainer.getCreditsDrawCommandSystem(),
                systemContainer.getCreditsMusicSystem(),
                systemContainer.getCreditsSliceStackCriticalSystem(),
        });
    }

    @SuppressWarnings("unchecked")
    private static SystemChainFactory<Double> makeCreditsGraphicsSystemChainFactory(SystemContainer systemContainer){
        return new SystemChainFactory<Double>(new AbstractSystem[]{
                systemContainer.getCreditsGraphicsSystem(),
        });
    }

    public SystemChainFactory<Double> getMenuMainSystemChainFactory() {
        return menuMainSystemChainFactory;
    }
    public SystemChainFactory<Double> getMenuGraphicsSystemChainFactory() {
        return menuGraphicsSystemChainFactory;
    }

    public SystemChainFactory<Double> getPauseMainSystemChainFactory() {
        return pauseMainSystemChainFactory;
    }
    public SystemChainFactory<Double> getPauseGraphicsSystemChainFactory() {
        return pauseGraphicsSystemChainFactory;
    }

    public SystemChainFactory<Double> getDialogueMainSystemChainFactory() {
        return dialogueMainSystemChainFactory;
    }
    public SystemChainFactory<Double> getDialogueGraphicsSystemChainFactory() {
        return dialogueGraphicsSystemChainFactory;
    }

    public SystemChainFactory<Double> getLoadMainSystemChainFactory() {
        return loadMainSystemChainFactory;
    }
    public SystemChainFactory<Double> getLoadGraphicsSystemChainFactory() {
        return loadGraphicsSystemChainFactory;
    }

    public SystemChainFactory<Double> getGameMainSystemChainFactory() {
        return gameMainSystemChainFactory;
    }
    public SystemChainFactory<Double> getGameGraphicsSystemChainFactory() {
        return gameGraphicsSystemChainFactory;
    }

    public SystemChainFactory<Double> getCreditsMainSystemChainFactory() {
        return creditsMainSystemChainFactory;
    }
    public SystemChainFactory<Double> getCreditsGraphicsSystemChainFactory() {
        return creditsGraphicsSystemChainFactory;
    }
}