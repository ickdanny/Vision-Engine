package internalconfig.game.systems;

import ecs.SliceStackCriticalSystem;
import ecs.datastorage.DataStorageCriticalSystem;
import internalconfig.game.FixedSizeActionTable;
import internalconfig.game.systems.creditssystems.CreditsScreenTimerSystem;
import internalconfig.game.systems.dialoguesystems.Dialogue;
import internalconfig.game.systems.dialoguesystems.DialogueInputParserSystem;
import internalconfig.game.systems.dialoguesystems.DialogueSystem;
import internalconfig.game.systems.gamesystems.DialogueScreenEntrySystem;
import internalconfig.game.systems.gamesystems.GameOverSystem;
import internalconfig.game.systems.gamesystems.GameWinSystem;
import internalconfig.game.systems.gamesystems.NextStageEntrySystem;
import internalconfig.game.systems.gamesystems.PlayerBombSystem;
import internalconfig.game.systems.gamesystems.CollisionSystem;
import internalconfig.game.systems.gamesystems.ContinueAndGameOverSystem;
import internalconfig.game.systems.gamesystems.DamageHandlingSystem;
import internalconfig.game.systems.gamesystems.DamageParserSystem;
import internalconfig.game.systems.gamesystems.DeathHandlingSystem;
import internalconfig.game.systems.gamesystems.GameInputParserSystem;
import internalconfig.game.systems.gamesystems.GameStateSystem;
import internalconfig.game.systems.gamesystems.InboundSystem;
import internalconfig.game.systems.gamesystems.MovePositionSystem;
import internalconfig.game.systems.gamesystems.OutboundSystem;
import internalconfig.game.systems.gamesystems.PauseMenuEntrySystem;
import internalconfig.game.systems.gamesystems.PlayerDeathParserSystem;
import internalconfig.game.systems.gamesystems.PlayerMovementSystem;
import internalconfig.game.systems.gamesystems.PlayerReactivateSystem;
import internalconfig.game.systems.gamesystems.PlayerShotSystem;
import internalconfig.game.systems.gamesystems.PlayerStateSystem;
import internalconfig.game.systems.gamesystems.PlayerRespawnSystem;
import internalconfig.game.systems.gamesystems.PlayerUISystem;
import internalconfig.game.systems.gamesystems.ProgramSystem;
import internalconfig.game.systems.gamesystems.SpawnSystem;
import internalconfig.game.systems.graphicssystems.AnimationSystem;
import internalconfig.game.systems.graphicssystems.ConstantSpriteRotationSystem;
import internalconfig.game.systems.graphicssystems.DrawCommandSystem;
import internalconfig.game.systems.graphicssystems.GameDrawCommandSystem;
import internalconfig.game.systems.graphicssystems.GameGraphicsSystem;
import internalconfig.game.systems.graphicssystems.GraphicsSystem;
import internalconfig.game.systems.graphicssystems.MakeOpaqueWhenPlayerFocusedAndAliveSystem;
import internalconfig.game.systems.graphicssystems.RotateSpriteForwardSystem;
import internalconfig.game.systems.graphicssystems.ScrollingSubImageSystem;
import internalconfig.game.systems.graphicssystems.SinusoidalSpriteVerticalOffsetSystem;
import internalconfig.game.systems.graphicssystems.SpriteInstructionSystem;
import internalconfig.game.systems.graphicssystems.SpriteRemovalSystem;
import internalconfig.game.systems.graphicssystems.SpriteSubImageFlagUpdateSystem;
import internalconfig.game.systems.graphicssystems.TrailSystem;
import internalconfig.game.systems.loadsystems.LoadScreenTimerSystem;
import internalconfig.game.systems.menusystems.ButtonSystem;
import internalconfig.game.systems.menusystems.GameBuilderSystem;
import internalconfig.game.systems.menusystems.LockConditionSystem;
import internalconfig.game.systems.menusystems.LockingSystem;
import internalconfig.game.systems.menusystems.MenuInputParserSystem;
import internalconfig.game.systems.menusystems.MenuNavigationSystem;
import internalconfig.game.systems.menusystems.MenuTrackStarterSystem;
import internalconfig.game.systems.menusystems.ShotPreviewSystem;
import internalconfig.game.systems.soundsystems.MusicSystem;
import resource.AbstractResourceManager;
import resource.Resource;
import sound.midi.MidiSequence;
import util.observer.AbstractPushSubject;
import util.observer.AbstractSubject;
import util.observer.ConfigurablePushSubject;
import util.observer.Subject;

import java.awt.image.BufferedImage;
import java.util.Properties;

import static internalconfig.game.components.ComponentTypes.*;

public class SystemContainer {

    private final AbstractSubject cleanupBroadcaster;
    private final AbstractSubject fullscreenToggleBroadcaster;
    private final ConfigurablePushSubject<MidiSequence> trackStartBroadcaster;
    private final AbstractSubject sequencerResetBroadcaster;
    private final AbstractSubject muteToggleBroadcaster;

    //MENU SYSTEMS
    private final MovePositionSystem menuMovePositionSystem;
    private final LockConditionSystem menuLockConditionSystem;
    private final LockingSystem menuLockingSystem;
    private final MenuInputParserSystem menuInputParserSystem;
    private final MenuNavigationSystem menuNavigationSystem;
    private final ButtonSystem menuButtonSystem;
    private final GameBuilderSystem menuGameBuilderSystem;

    private final ShotPreviewSystem menuShotPreviewSystem;

    private final AnimationSystem menuAnimationSystem;
    private final RotateSpriteForwardSystem menuRotateSpriteForwardSystem;
    private final ConstantSpriteRotationSystem menuConstantSpriteRotationSystem;
    private final SpriteSubImageFlagUpdateSystem menuSpriteSubImageFlagUpdateSystem;
    private final SpriteInstructionSystem menuSpriteInstructionSystem;
    private final SpriteRemovalSystem menuSpriteRemovalSystem;

    private final OutboundSystem menuOutboundSystem;

    private final DataStorageCriticalSystem<Double> menuDataStorageCriticalSystem;
    private final DrawCommandSystem menuDrawCommandSystem;
    private final MenuTrackStarterSystem menuTrackStarterSystem;
    private final MusicSystem menuMusicSystem;
    private final SliceStackCriticalSystem<Double> menuSliceStackCriticalSystem;

    private final GraphicsSystem menuGraphicsSystem;

    //PAUSE SYSTEMS
    private final MovePositionSystem pauseMovePositionSystem;
    private final MenuInputParserSystem pauseInputParserSystem;
    private final MenuNavigationSystem pauseNavigationSystem;
    private final ButtonSystem pauseButtonSystem;

    private final SpriteSubImageFlagUpdateSystem pauseSpriteSubImageFlagUpdateSystem;
    private final SpriteInstructionSystem pauseSpriteInstructionSystem;
    private final SpriteRemovalSystem pauseSpriteRemovalSystem;


    private final DataStorageCriticalSystem<Double> pauseDataStorageCriticalSystem;
    private final DrawCommandSystem pauseDrawCommandSystem;
    private final MusicSystem pauseMusicSystem;
    private final SliceStackCriticalSystem<Double> pauseSliceStackCriticalSystem;

    private final GraphicsSystem pauseGraphicsSystem;

    //DIALOGUE SYSTEMS
    private final DialogueInputParserSystem dialogueInputParserSystem;
    private final DialogueSystem dialogueSystem;

    private final SpriteSubImageFlagUpdateSystem dialogueSpriteSubImageFlagUpdateSystem;
    private final SpriteInstructionSystem dialogueSpriteInstructionSystem;
    private final SpriteRemovalSystem dialogueSpriteRemovalSystem;

    private final DataStorageCriticalSystem<Double> dialogueDataStorageCriticalSystem;
    private final DrawCommandSystem dialogueDrawCommandSystem;
    private final MusicSystem dialogueMusicSystem;
    private final SliceStackCriticalSystem<Double> dialogueSliceStackCriticalSystem;

    private final GraphicsSystem dialogueGraphicsSystem;

    //LOAD SYSTEMS
    private final LoadScreenTimerSystem loadScreenTimerSystem;

    private final SpriteSubImageFlagUpdateSystem loadSpriteSubImageFlagUpdateSystem;
    private final SpriteInstructionSystem loadSpriteInstructionSystem;
    private final SpriteRemovalSystem loadSpriteRemovalSystem;

    private final DataStorageCriticalSystem<Double> loadDataStorageCriticalSystem;
    private final DrawCommandSystem loadDrawCommandSystem;
    private final SliceStackCriticalSystem<Double> loadSliceStackCriticalSystem;

    private final GraphicsSystem loadGraphicsSystem;

    //GAME SYSTEMS
    private final GameStateSystem gameStateSystem;
    private final MovePositionSystem gameMovePositionSystem;
    private final ProgramSystem gameProgramSystem;
    private final CollisionSystem gameCollisionSystem;
    private final GameInputParserSystem gameInputParserSystem;
    private final PlayerMovementSystem gamePlayerMovementSystem;
    private final PlayerShotSystem gamePlayerShotSystem;
    private final DamageParserSystem gameDamageParserSystem;
    private final DamageHandlingSystem gameDamageHandlingSystem;
    private final PlayerStateSystem gamePlayerStateSystem;
    private final PlayerBombSystem gamePlayerBombSystem;
    private final PlayerDeathParserSystem gamePlayerDeathParserSystem;
    private final ContinueAndGameOverSystem gameContinueAndGameOverSystem;
    private final PlayerRespawnSystem gamePlayerRespawnSystem;
    private final PlayerReactivateSystem gamePlayerReactivateSystem;
    private final DeathHandlingSystem gameDeathHandlingSystem;
    private final SpawnSystem gameSpawnSystem;

    private final PlayerUISystem gamePlayerUISystem;

    private final PauseMenuEntrySystem gamePauseMenuEntrySystem;
    private final DialogueScreenEntrySystem gameDialogueScreenEntrySystem;
    private final NextStageEntrySystem gameNextStageEntrySystem;

    private final AnimationSystem gameAnimationSystem;
    private final RotateSpriteForwardSystem gameRotateSpriteForwardSystem;
    private final ConstantSpriteRotationSystem gameConstantSpriteRotationSystem;
    private final SinusoidalSpriteVerticalOffsetSystem gameSinusoidalSpriteVerticalOffsetSystem;
    private final ScrollingSubImageSystem gameScrollingSubImageSystem;
    private final MakeOpaqueWhenPlayerFocusedAndAliveSystem gameMakeOpaqueWhenPlayerFocusedAndAliveSystem;
    private final SpriteSubImageFlagUpdateSystem gameSpriteSubImageFlagUpdateSystem;
    private final TrailSystem gameTrailSystem;
    private final SpriteInstructionSystem gameSpriteInstructionSystem;
    private final SpriteRemovalSystem gameSpriteRemovalSystem;


    private final DataStorageCriticalSystem<Double> gamePrimaryDataStorageCriticalSystem;

    private final InboundSystem gameInboundSystem;
    private final OutboundSystem gameOutboundSystem;

    //inbound system requires changing the velocity before the move and drawCommand systems
    //but after the first dataStorageCriticalSystem
    private final DataStorageCriticalSystem<Double> gameSecondaryDataStorageCriticalSystem;

    private final GameDrawCommandSystem gameDrawCommandSystem;

    private final GameOverSystem gameGameOverSystem;
    private final GameWinSystem gameWinSystem;
    private final MusicSystem gameMusicSystem;
    private final SliceStackCriticalSystem<Double> gameSliceStackCriticalSystem;

    private final GameGraphicsSystem gameGraphicsSystem;

    //CREDITS SYSTEMS
    private final CreditsScreenTimerSystem creditsScreenTimerSystem;

    private final SpriteSubImageFlagUpdateSystem creditsSpriteSubImageFlagUpdateSystem;
    private final SpriteInstructionSystem creditsSpriteInstructionSystem;
    private final SpriteRemovalSystem creditsSpriteRemovalSystem;

    private final DataStorageCriticalSystem<Double> creditsDataStorageCriticalSystem;
    private final DrawCommandSystem creditsDrawCommandSystem;
    private final MusicSystem creditsMusicSystem;
    private final SliceStackCriticalSystem<Double> creditsSliceStackCriticalSystem;

    private final GraphicsSystem creditsGraphicsSystem;

    public SystemContainer(Resource<Properties> propertiesResource,
                           AbstractResourceManager<BufferedImage> imageManager,
                           AbstractResourceManager<MidiSequence> midiManager,
                           AbstractResourceManager<Dialogue> dialogueManager,
                           FixedSizeActionTable actionTable,
                           BufferedImage toDraw){

        cleanupBroadcaster = new Subject();
        fullscreenToggleBroadcaster = new Subject();
        trackStartBroadcaster = new ConfigurablePushSubject<>();
        sequencerResetBroadcaster = new Subject();
        muteToggleBroadcaster = new Subject();

        //MENU SYSTEMS
        menuMovePositionSystem = new MovePositionSystem(MENU_COMPONENT_TYPES);
        menuLockConditionSystem = new LockConditionSystem(MENU_COMPONENT_TYPES);
        menuLockingSystem = new LockingSystem(MENU_COMPONENT_TYPES);
        menuInputParserSystem = new MenuInputParserSystem(actionTable);
        menuNavigationSystem = new MenuNavigationSystem(
                propertiesResource,
                MENU_COMPONENT_TYPES,
                cleanupBroadcaster,
                fullscreenToggleBroadcaster,
                muteToggleBroadcaster
        );
        menuButtonSystem = new ButtonSystem(MENU_COMPONENT_TYPES);
        menuGameBuilderSystem = new GameBuilderSystem();

        menuShotPreviewSystem = new ShotPreviewSystem(MENU_COMPONENT_TYPES);

        menuAnimationSystem = new AnimationSystem(MENU_COMPONENT_TYPES);
        menuRotateSpriteForwardSystem = new RotateSpriteForwardSystem(MENU_COMPONENT_TYPES);
        menuConstantSpriteRotationSystem = new ConstantSpriteRotationSystem(MENU_COMPONENT_TYPES);
        menuSpriteSubImageFlagUpdateSystem = new SpriteSubImageFlagUpdateSystem(MENU_COMPONENT_TYPES);
        menuSpriteInstructionSystem = new SpriteInstructionSystem(imageManager, MENU_COMPONENT_TYPES);
        menuSpriteRemovalSystem = new SpriteRemovalSystem(MENU_COMPONENT_TYPES);

        menuOutboundSystem = new OutboundSystem(MENU_COMPONENT_TYPES);

        menuDataStorageCriticalSystem = new DataStorageCriticalSystem<>();
        menuDrawCommandSystem = new DrawCommandSystem(MENU_COMPONENT_TYPES);
        menuTrackStarterSystem = new MenuTrackStarterSystem();
        menuMusicSystem = new MusicSystem(midiManager, trackStartBroadcaster, sequencerResetBroadcaster);
        menuSliceStackCriticalSystem = new SliceStackCriticalSystem<>();

        menuGraphicsSystem = new GraphicsSystem(toDraw);

        //PAUSE SYSTEMS
        pauseMovePositionSystem = new MovePositionSystem(MENU_COMPONENT_TYPES);
        pauseInputParserSystem = new MenuInputParserSystem(actionTable);
        pauseNavigationSystem = new MenuNavigationSystem(
                propertiesResource,
                MENU_COMPONENT_TYPES,
                cleanupBroadcaster,
                fullscreenToggleBroadcaster,
                muteToggleBroadcaster
        );
        pauseButtonSystem = new ButtonSystem(MENU_COMPONENT_TYPES);

        pauseSpriteSubImageFlagUpdateSystem = new SpriteSubImageFlagUpdateSystem(MENU_COMPONENT_TYPES);
        pauseSpriteInstructionSystem = new SpriteInstructionSystem(imageManager, MENU_COMPONENT_TYPES);
        pauseSpriteRemovalSystem = new SpriteRemovalSystem(MENU_COMPONENT_TYPES);


        pauseDataStorageCriticalSystem = new DataStorageCriticalSystem<>();
        pauseDrawCommandSystem = new DrawCommandSystem(MENU_COMPONENT_TYPES);
        pauseMusicSystem = new MusicSystem(midiManager, trackStartBroadcaster, sequencerResetBroadcaster);
        pauseSliceStackCriticalSystem = new SliceStackCriticalSystem<>();

        pauseGraphicsSystem = new GraphicsSystem(toDraw);

        //DIALOGUE SYSTEMS
        dialogueInputParserSystem = new DialogueInputParserSystem(actionTable);
        dialogueSystem = new DialogueSystem(dialogueManager, DIALOGUE_COMPONENT_TYPES);

        dialogueSpriteSubImageFlagUpdateSystem = new SpriteSubImageFlagUpdateSystem(DIALOGUE_COMPONENT_TYPES);
        dialogueSpriteInstructionSystem = new SpriteInstructionSystem(imageManager, DIALOGUE_COMPONENT_TYPES);
        dialogueSpriteRemovalSystem = new SpriteRemovalSystem(DIALOGUE_COMPONENT_TYPES);

        dialogueDataStorageCriticalSystem = new DataStorageCriticalSystem<>();
        dialogueDrawCommandSystem = new DrawCommandSystem(DIALOGUE_COMPONENT_TYPES);
        dialogueMusicSystem = new MusicSystem(midiManager, trackStartBroadcaster, sequencerResetBroadcaster);
        dialogueSliceStackCriticalSystem = new SliceStackCriticalSystem<>();

        dialogueGraphicsSystem = new GraphicsSystem(toDraw);

        //LOAD SYSTEMS
        loadScreenTimerSystem = new LoadScreenTimerSystem();

        loadSpriteSubImageFlagUpdateSystem = new SpriteSubImageFlagUpdateSystem(LOAD_COMPONENT_TYPES);
        loadSpriteInstructionSystem = new SpriteInstructionSystem(imageManager, LOAD_COMPONENT_TYPES);
        loadSpriteRemovalSystem = new SpriteRemovalSystem(LOAD_COMPONENT_TYPES);

        loadDataStorageCriticalSystem = new DataStorageCriticalSystem<>();
        loadDrawCommandSystem = new DrawCommandSystem(LOAD_COMPONENT_TYPES);
        loadSliceStackCriticalSystem = new SliceStackCriticalSystem<>();

        loadGraphicsSystem = new GraphicsSystem(toDraw);

        //GAME SYSTEMS
        gameStateSystem = new GameStateSystem();
        gameMovePositionSystem = new MovePositionSystem(GAME_COMPONENT_TYPES);
        gameProgramSystem = new ProgramSystem(GAME_COMPONENT_TYPES, propertiesResource);
        gameCollisionSystem = new CollisionSystem(GAME_COMPONENT_TYPES);
        gameInputParserSystem = new GameInputParserSystem(actionTable);
        gamePlayerMovementSystem = new PlayerMovementSystem(GAME_COMPONENT_TYPES);
        gamePlayerShotSystem = new PlayerShotSystem(GAME_COMPONENT_TYPES);
        gameDamageParserSystem = new DamageParserSystem(GAME_COMPONENT_TYPES);
        gameDamageHandlingSystem = new DamageHandlingSystem(GAME_COMPONENT_TYPES);
        gamePlayerStateSystem = new PlayerStateSystem();
        gamePlayerBombSystem = new PlayerBombSystem(GAME_COMPONENT_TYPES);
        gamePlayerDeathParserSystem = new PlayerDeathParserSystem();
        gameContinueAndGameOverSystem = new ContinueAndGameOverSystem();
        gamePlayerRespawnSystem = new PlayerRespawnSystem(GAME_COMPONENT_TYPES);
        gamePlayerReactivateSystem = new PlayerReactivateSystem(GAME_COMPONENT_TYPES);
        gameDeathHandlingSystem = new DeathHandlingSystem(GAME_COMPONENT_TYPES);
        gameSpawnSystem = new SpawnSystem(GAME_COMPONENT_TYPES);

        gamePlayerUISystem = new PlayerUISystem(imageManager, GAME_COMPONENT_TYPES);

        gamePauseMenuEntrySystem = new PauseMenuEntrySystem();
        gameDialogueScreenEntrySystem = new DialogueScreenEntrySystem();
        gameNextStageEntrySystem = new NextStageEntrySystem();

        gameAnimationSystem = new AnimationSystem(GAME_COMPONENT_TYPES);
        gameRotateSpriteForwardSystem = new RotateSpriteForwardSystem(GAME_COMPONENT_TYPES);
        gameConstantSpriteRotationSystem = new ConstantSpriteRotationSystem(GAME_COMPONENT_TYPES);
        gameSinusoidalSpriteVerticalOffsetSystem = new SinusoidalSpriteVerticalOffsetSystem(GAME_COMPONENT_TYPES);
        gameScrollingSubImageSystem = new ScrollingSubImageSystem(GAME_COMPONENT_TYPES);
        gameMakeOpaqueWhenPlayerFocusedAndAliveSystem = new MakeOpaqueWhenPlayerFocusedAndAliveSystem(GAME_COMPONENT_TYPES);
        gameSpriteSubImageFlagUpdateSystem = new SpriteSubImageFlagUpdateSystem(GAME_COMPONENT_TYPES);
        gameTrailSystem = new TrailSystem(GAME_COMPONENT_TYPES);
        gameSpriteInstructionSystem = new SpriteInstructionSystem(imageManager, GAME_COMPONENT_TYPES);
        gameSpriteRemovalSystem = new SpriteRemovalSystem(GAME_COMPONENT_TYPES);

        gamePrimaryDataStorageCriticalSystem = new DataStorageCriticalSystem<>();

        gameInboundSystem = new InboundSystem(GAME_COMPONENT_TYPES);
        gameOutboundSystem = new OutboundSystem(GAME_COMPONENT_TYPES);

        gameSecondaryDataStorageCriticalSystem = new DataStorageCriticalSystem<>();

        gameDrawCommandSystem = new GameDrawCommandSystem(GAME_COMPONENT_TYPES);

        gameGameOverSystem = new GameOverSystem();
        gameWinSystem = new GameWinSystem();
        gameMusicSystem = new MusicSystem(midiManager, trackStartBroadcaster, sequencerResetBroadcaster);
        gameSliceStackCriticalSystem = new SliceStackCriticalSystem<>();

        gameGraphicsSystem = new GameGraphicsSystem(toDraw);

        //CREDITS SYSTEMS
        creditsScreenTimerSystem = new CreditsScreenTimerSystem();

        creditsSpriteSubImageFlagUpdateSystem = new SpriteSubImageFlagUpdateSystem(CREDITS_COMPONENT_TYPES);
        creditsSpriteInstructionSystem = new SpriteInstructionSystem(imageManager, CREDITS_COMPONENT_TYPES);
        creditsSpriteRemovalSystem = new SpriteRemovalSystem(CREDITS_COMPONENT_TYPES);

        creditsDataStorageCriticalSystem = new DataStorageCriticalSystem<>();
        creditsDrawCommandSystem = new DrawCommandSystem(CREDITS_COMPONENT_TYPES);
        creditsMusicSystem = new MusicSystem(midiManager, trackStartBroadcaster, sequencerResetBroadcaster);
        creditsSliceStackCriticalSystem = new SliceStackCriticalSystem<>();

        creditsGraphicsSystem = new GraphicsSystem(toDraw);
    }

    //MENU SYSTEMS
    public MovePositionSystem getMenuMovePositionSystem() {
        return menuMovePositionSystem;
    }
    public LockConditionSystem getMenuLockConditionSystem(){
        return menuLockConditionSystem;
    }
    public LockingSystem getMenuLockingSystem() {
        return menuLockingSystem;
    }
    public MenuInputParserSystem getMenuInputParserSystem() {
        return menuInputParserSystem;
    }
    public MenuNavigationSystem getMenuNavigationSystem() {
        return menuNavigationSystem;
    }
    public ButtonSystem getMenuButtonSystem() {
        return menuButtonSystem;
    }
    public GameBuilderSystem getMenuGameBuilderSystem() {
        return menuGameBuilderSystem;
    }

    public ShotPreviewSystem getMenuShotPreviewSystem() {
        return menuShotPreviewSystem;
    }

    public AnimationSystem getMenuAnimationSystem() {
        return menuAnimationSystem;
    }
    public RotateSpriteForwardSystem getMenuRotateSpriteForwardSystem() {
        return menuRotateSpriteForwardSystem;
    }
    public ConstantSpriteRotationSystem getMenuConstantSpriteRotationSystem() {
        return menuConstantSpriteRotationSystem;
    }
    public SpriteSubImageFlagUpdateSystem getMenuSpriteSubImageFlagUpdateSystem() {
        return menuSpriteSubImageFlagUpdateSystem;
    }
    public SpriteInstructionSystem getMenuSpriteInstructionSystem() {
        return menuSpriteInstructionSystem;
    }
    public SpriteRemovalSystem getMenuSpriteRemovalSystem() {
        return menuSpriteRemovalSystem;
    }

    public OutboundSystem getMenuOutboundSystem() {
        return menuOutboundSystem;
    }

    public DataStorageCriticalSystem<Double> getMenuDataStorageCriticalSystem() {
        return menuDataStorageCriticalSystem;
    }
    public DrawCommandSystem getMenuDrawCommandSystem() {
        return menuDrawCommandSystem;
    }
    public MenuTrackStarterSystem getMenuTrackStarterSystem() {
        return menuTrackStarterSystem;
    }
    public MusicSystem getMenuMusicSystem() {
        return menuMusicSystem;
    }
    public SliceStackCriticalSystem<Double> getMenuSliceStackCriticalSystem() {
        return menuSliceStackCriticalSystem;
    }
    public GraphicsSystem getMenuGraphicsSystem() {
        return menuGraphicsSystem;
    }

    //PAUSE SYSTEMS
    public MovePositionSystem getPauseMovePositionSystem() {
        return pauseMovePositionSystem;
    }
    public MenuInputParserSystem getPauseInputParserSystem() {
        return pauseInputParserSystem;
    }
    public MenuNavigationSystem getPauseNavigationSystem() {
        return pauseNavigationSystem;
    }
    public ButtonSystem getPauseButtonSystem() {
        return pauseButtonSystem;
    }

    public SpriteSubImageFlagUpdateSystem getPauseSpriteSubImageFlagUpdateSystem() {
        return pauseSpriteSubImageFlagUpdateSystem;
    }
    public SpriteInstructionSystem getPauseSpriteInstructionSystem() {
        return pauseSpriteInstructionSystem;
    }
    public SpriteRemovalSystem getPauseSpriteRemovalSystem() {
        return pauseSpriteRemovalSystem;
    }

    public DataStorageCriticalSystem<Double> getPauseDataStorageCriticalSystem() {
        return pauseDataStorageCriticalSystem;
    }
    public DrawCommandSystem getPauseDrawCommandSystem() {
        return pauseDrawCommandSystem;
    }
    public MusicSystem getPauseMusicSystem() {
        return pauseMusicSystem;
    }
    public SliceStackCriticalSystem<Double> getPauseSliceStackCriticalSystem() {
        return pauseSliceStackCriticalSystem;
    }
    public GraphicsSystem getPauseGraphicsSystem() {
        return pauseGraphicsSystem;
    }

    //DIALOGUE SYSTEMS
    public DialogueInputParserSystem getDialogueInputParserSystem() {
        return dialogueInputParserSystem;
    }
    public DialogueSystem getDialogueSystem() {
        return dialogueSystem;
    }

    public SpriteSubImageFlagUpdateSystem getDialogueSpriteSubImageFlagUpdateSystem() {
        return dialogueSpriteSubImageFlagUpdateSystem;
    }
    public SpriteInstructionSystem getDialogueSpriteInstructionSystem() {
        return dialogueSpriteInstructionSystem;
    }
    public SpriteRemovalSystem getDialogueSpriteRemovalSystem() {
        return dialogueSpriteRemovalSystem;
    }

    public DataStorageCriticalSystem<Double> getDialogueDataStorageCriticalSystem() {
        return dialogueDataStorageCriticalSystem;
    }
    public DrawCommandSystem getDialogueDrawCommandSystem() {
        return dialogueDrawCommandSystem;
    }
    public MusicSystem getDialogueMusicSystem() {
        return dialogueMusicSystem;
    }
    public SliceStackCriticalSystem<Double> getDialogueSliceStackCriticalSystem() {
        return dialogueSliceStackCriticalSystem;
    }

    public GraphicsSystem getDialogueGraphicsSystem(){
        return dialogueGraphicsSystem;
    }

    //LOAD SYSTEMS
    public LoadScreenTimerSystem getLoadScreenTimerSystem() {
        return loadScreenTimerSystem;
    }

    public SpriteSubImageFlagUpdateSystem getLoadSpriteSubImageFlagUpdateSystem() {
        return loadSpriteSubImageFlagUpdateSystem;
    }
    public SpriteInstructionSystem getLoadSpriteInstructionSystem() {
        return loadSpriteInstructionSystem;
    }
    public SpriteRemovalSystem getLoadSpriteRemovalSystem() {
        return loadSpriteRemovalSystem;
    }

    public DataStorageCriticalSystem<Double> getLoadDataStorageCriticalSystem() {
        return loadDataStorageCriticalSystem;
    }
    public DrawCommandSystem getLoadDrawCommandSystem() {
        return loadDrawCommandSystem;
    }
    public SliceStackCriticalSystem<Double> getLoadSliceStackCriticalSystem() {
        return loadSliceStackCriticalSystem;
    }

    public GraphicsSystem getLoadGraphicsSystem() {
        return loadGraphicsSystem;
    }

    //GAME SYSTEMS
    public GameStateSystem getGameStateSystem(){
        return gameStateSystem;
    }
    public MovePositionSystem getGameMovePositionSystem() {
        return gameMovePositionSystem;
    }
    public ProgramSystem getGameProgramSystem() {
        return gameProgramSystem;
    }
    public CollisionSystem getGameCollisionSystem() {
        return gameCollisionSystem;
    }
    public GameInputParserSystem getGameInputParserSystem() {
        return gameInputParserSystem;
    }
    public PlayerMovementSystem getGamePlayerMovementSystem() {
        return gamePlayerMovementSystem;
    }
    public PlayerShotSystem getGamePlayerShotSystem(){
        return gamePlayerShotSystem;
    }
    public DamageParserSystem getGameDamageParserSystem(){
        return gameDamageParserSystem;
    }
    public DamageHandlingSystem getGameDamageHandlingSystem(){
        return gameDamageHandlingSystem;
    }
    public PlayerStateSystem getGamePlayerStateSystem() {
        return gamePlayerStateSystem;
    }
    public PlayerBombSystem getGamePlayerBombSystem(){
        return gamePlayerBombSystem;
    }
    public PlayerDeathParserSystem getGamePlayerDeathParserSystem(){
        return gamePlayerDeathParserSystem;
    }
    public ContinueAndGameOverSystem getGameContinueAndGameOverSystem() {
        return gameContinueAndGameOverSystem;
    }
    public PlayerRespawnSystem getGamePlayerRespawnSystem() {
        return gamePlayerRespawnSystem;
    }
    public PlayerReactivateSystem getGamePlayerReactivateSystem(){
        return gamePlayerReactivateSystem;
    }
    public DeathHandlingSystem getGameDeathHandlingSystem() {
        return gameDeathHandlingSystem;
    }
    public SpawnSystem getGameSpawnSystem(){
        return gameSpawnSystem;
    }

    public PlayerUISystem getGamePlayerUISystem() {
        return gamePlayerUISystem;
    }

    public PauseMenuEntrySystem getGamePauseMenuEntrySystem() {
        return gamePauseMenuEntrySystem;
    }
    public DialogueScreenEntrySystem getGameDialogueScreenEntrySystem(){
        return gameDialogueScreenEntrySystem;
    }
    public NextStageEntrySystem getGameNextStageEntrySystem() {
        return gameNextStageEntrySystem;
    }

    public AnimationSystem getGameAnimationSystem() {
        return gameAnimationSystem;
    }
    public RotateSpriteForwardSystem getGameRotateSpriteForwardSystem() {
        return gameRotateSpriteForwardSystem;
    }
    public ConstantSpriteRotationSystem getGameConstantSpriteRotationSystem() {
        return gameConstantSpriteRotationSystem;
    }
    public SinusoidalSpriteVerticalOffsetSystem getGameSinusoidalSpriteVerticalOffsetSystem() {
        return gameSinusoidalSpriteVerticalOffsetSystem;
    }
    public ScrollingSubImageSystem getGameScrollingSubImageSystem() {
        return gameScrollingSubImageSystem;
    }
    public MakeOpaqueWhenPlayerFocusedAndAliveSystem getGameMakeOpaqueWhenPlayerFocusedAndAliveSystem() {
        return gameMakeOpaqueWhenPlayerFocusedAndAliveSystem;
    }
    public SpriteSubImageFlagUpdateSystem getGameSpriteSubImageFlagUpdateSystem() {
        return gameSpriteSubImageFlagUpdateSystem;
    }
    public TrailSystem getGameTrailSystem() {
        return gameTrailSystem;
    }
    public SpriteInstructionSystem getGameSpriteInstructionSystem() {
        return gameSpriteInstructionSystem;
    }
    public SpriteRemovalSystem getGameSpriteRemovalSystem() {
        return gameSpriteRemovalSystem;
    }

    public DataStorageCriticalSystem<Double> getGamePrimaryDataStorageCriticalSystem() {
        return gamePrimaryDataStorageCriticalSystem;
    }

    public InboundSystem getGameInboundSystem() {
        return gameInboundSystem;
    }

    public OutboundSystem getGameOutboundSystem() {
        return gameOutboundSystem;
    }
    public DataStorageCriticalSystem<Double> getGameSecondaryDataStorageCriticalSystem() {
        return gameSecondaryDataStorageCriticalSystem;
    }

    public GameDrawCommandSystem getGameDrawCommandSystem() {
        return gameDrawCommandSystem;
    }

    public GameOverSystem getGameGameOverSystem() {
        return gameGameOverSystem;
    }
    public GameWinSystem getGameWinSystem() {
        return gameWinSystem;
    }

    public MusicSystem getGameMusicSystem() {
        return gameMusicSystem;
    }
    public SliceStackCriticalSystem<Double> getGameSliceStackCriticalSystem() {
        return gameSliceStackCriticalSystem;
    }

    public GraphicsSystem getGameGraphicsSystem() {
        return gameGraphicsSystem;
    }

    //CREDITS SYSTEMS
    public CreditsScreenTimerSystem getCreditsScreenTimerSystem() {
        return creditsScreenTimerSystem;
    }

    public SpriteSubImageFlagUpdateSystem getCreditsSpriteSubImageFlagUpdateSystem() {
        return creditsSpriteSubImageFlagUpdateSystem;
    }
    public SpriteInstructionSystem getCreditsSpriteInstructionSystem() {
        return creditsSpriteInstructionSystem;
    }
    public SpriteRemovalSystem getCreditsSpriteRemovalSystem() {
        return creditsSpriteRemovalSystem;
    }

    public DataStorageCriticalSystem<Double> getCreditsDataStorageCriticalSystem() {
        return creditsDataStorageCriticalSystem;
    }
    public DrawCommandSystem getCreditsDrawCommandSystem() {
        return creditsDrawCommandSystem;
    }
    public MusicSystem getCreditsMusicSystem(){
        return creditsMusicSystem;
    }
    public SliceStackCriticalSystem<Double> getCreditsSliceStackCriticalSystem() {
        return creditsSliceStackCriticalSystem;
    }

    public GraphicsSystem getCreditsGraphicsSystem() {
        return creditsGraphicsSystem;
    }


    public AbstractSubject getCleanupBroadcaster() {
        return cleanupBroadcaster;
    }
    public AbstractSubject getFullscreenToggleBroadcaster() {
        return fullscreenToggleBroadcaster;
    }
    public AbstractPushSubject<MidiSequence> getTrackStartBroadcaster() {
        return trackStartBroadcaster;
    }
    public AbstractSubject getSequencerResetBroadcaster() {
        return sequencerResetBroadcaster;
    }
    public AbstractSubject getMuteToggleBroadcaster(){
        return muteToggleBroadcaster;
    }
}