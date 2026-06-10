package util.observer;

public class ConfigurablePushSubject<T> extends PushSubjectTemplate<T> {
    private T data;
    public ConfigurablePushSubject(){
        super();
    }
    public ConfigurablePushSubject(T data){
        super();
        this.data = data;
    }
    public void setPushData(T data) {
        this.data = data;
    }
    @Override
    protected T getPushData(){
        return data;
    }
}