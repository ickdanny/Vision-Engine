package internalconfig.game.sliceproviders;

import ecs.AbstractSliceProvider;
import internalconfig.InternalProperties;
import internalconfig.PropertiesUtil;
import internalconfig.game.sliceproviders.gamemenu.ContinueMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.DifficultyMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.ExtraDifficultyMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.MainMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.MusicMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.OptionMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.ShotMenuSliceProvider;
import internalconfig.game.sliceproviders.mainmenu.StageMenuSliceProvider;
import internalconfig.game.sliceproviders.gamemenu.PauseMenuSliceProvider;
import internalconfig.game.sliceproviders.gamemenu.PracticePauseMenuSliceProvider;
import internalconfig.game.systems.SliceCodes;
import internalconfig.game.systems.SystemContainer;
import resource.AbstractResourceManager;
import util.datastructure.ArrayUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static internalconfig.game.systems.SliceCodes.*;

public class SliceProviderContainer {

    public static final String BASE_SLICE = SliceCodes.MAIN;

    private final AbstractSliceProvider[] sliceProviders;

    public SliceProviderContainer(Properties properties,
                                  SystemChainFactoryProvider systemChainFactoryProvider,
                                  SystemContainer systemContainer,
                                  AbstractResourceManager<BufferedImage> imageManager) {
        boolean isExtraUnlocked = PropertiesUtil.getBooleanProperty(properties, InternalProperties.EXTRA.getPropertyName());
        sliceProviders = ArrayUtil.addArrays(
                new AbstractSliceProvider[]{
                        new MainMenuSliceProvider(systemChainFactoryProvider, systemContainer, isExtraUnlocked),
                        new DifficultyMenuSliceProvider(systemChainFactoryProvider, systemContainer,
                                START + DIFFICULTY),
                        new ExtraDifficultyMenuSliceProvider(systemChainFactoryProvider, systemContainer, EXTRA + DIFFICULTY),
                        new DifficultyMenuSliceProvider(systemChainFactoryProvider, systemContainer,
                                PRACTICE + DIFFICULTY),
                        new MusicMenuSliceProvider(systemChainFactoryProvider, systemContainer),
                        new OptionMenuSliceProvider(systemChainFactoryProvider, systemContainer),
                        new PauseMenuSliceProvider(systemChainFactoryProvider, systemContainer),
                        new PracticePauseMenuSliceProvider(systemChainFactoryProvider, systemContainer),
                        new GameSliceProvider(systemChainFactoryProvider, systemContainer, imageManager),
                        new DialogueSliceProvider(systemChainFactoryProvider),
                        new LoadSliceProvider(systemChainFactoryProvider, systemContainer),
                        new CreditsSliceProvider(systemChainFactoryProvider, systemContainer),
                },
                makeShotMenus(systemChainFactoryProvider, systemContainer),
                makeStageMenus(systemChainFactoryProvider, systemContainer),
                makeContinueMenus(systemChainFactoryProvider, systemContainer)
        );
    }

    private static StageMenuSliceProvider[] makeStageMenus(SystemChainFactoryProvider systemChainFactoryProvider,
                                                           SystemContainer systemContainer) {
        List<StageMenuSliceProvider> stageMenuSliceProviderList = new ArrayList<>();
        for (String difficulty : DIFFICULTIES) {
            stageMenuSliceProviderList.add(new StageMenuSliceProvider(systemChainFactoryProvider, systemContainer,
                    PRACTICE + DIFFICULTY + difficulty + STAGE));
        }
        return stageMenuSliceProviderList.toArray(new StageMenuSliceProvider[0]);
    }

    private static ShotMenuSliceProvider[] makeShotMenus(SystemChainFactoryProvider systemChainFactoryProvider,
                                                         SystemContainer systemContainer) {
        List<ShotMenuSliceProvider> shotMenuSliceProviderList = new ArrayList<>();
        shotMenuSliceProviderList.add(new ShotMenuSliceProvider(systemChainFactoryProvider, systemContainer,
                EXTRA + DIFFICULTY + EXTRAD + SHOT));
        for (String difficulty : DIFFICULTIES) {
            shotMenuSliceProviderList.add(new ShotMenuSliceProvider(systemChainFactoryProvider, systemContainer,
                    START + DIFFICULTY + difficulty + SHOT));
            for (int i = 0; i <= 6; ++i) {
                shotMenuSliceProviderList.add(new ShotMenuSliceProvider(systemChainFactoryProvider, systemContainer,
                        PRACTICE + DIFFICULTY + difficulty + STAGE + i + "_" + SHOT));
            }
        }
        return shotMenuSliceProviderList.toArray(new ShotMenuSliceProvider[0]);
    }

    private static ContinueMenuSliceProvider[] makeContinueMenus(SystemChainFactoryProvider systemChainFactoryProvider,
                                                                 SystemContainer systemContainer) {
        return new ContinueMenuSliceProvider[]{
                new ContinueMenuSliceProvider(systemChainFactoryProvider, systemContainer, 3),
                new ContinueMenuSliceProvider(systemChainFactoryProvider, systemContainer, 2),
                new ContinueMenuSliceProvider(systemChainFactoryProvider, systemContainer, 1)
        };
    }

    public AbstractSliceProvider[] getSliceProviders() {
        return sliceProviders;
    }
}