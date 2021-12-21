package internalconfig;

@SuppressWarnings("SameParameterValue")
public enum InternalProperties {
    MUTE("MUTE", "false"),
    FULLSCREEN("FULLSCREEN", "false"),
    EXTRA("EXTRA", "false"),
    ;

    private final String name;
    private final String defaultValue;

    InternalProperties(String name, String defaultValue){
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
