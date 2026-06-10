package util.datastructure.fullintset;

public interface AbstractFullIntSet {
    boolean contains(int i);
    boolean retrieve(int i); //true if set previously contained the integer
    boolean addBack(int i);    //true if set previously did not contain the integer
    void addAllBack();
    int retrieveNextInt();
}