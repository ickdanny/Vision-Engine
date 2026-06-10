package util.tuple;

import java.util.Objects;

public class Tuple4<A, B, C, D> {
    public final A a;
    public final B b;
    public final C c;
    public final D d;

    public Tuple4(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4)) return false;
        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
        return Objects.equals(a, tuple4.a) &&
                Objects.equals(b, tuple4.b) &&
                Objects.equals(c, tuple4.c) &&
                Objects.equals(d, tuple4.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, d);
    }

    @Override
    public String toString() {
        return "Tuple4<" + a + ", " + b + ", " + c + ", " + d + '>';
    }
}