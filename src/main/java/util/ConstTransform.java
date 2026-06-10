package util;

//the object being transformed should be effectively const
public interface ConstTransform<T> {
    T transform(T base);
}