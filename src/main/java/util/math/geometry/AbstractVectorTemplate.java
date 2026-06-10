package util.math.geometry;

import java.util.Objects;

abstract class AbstractVectorTemplate implements AbstractVector{

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractVector)) return false;
        AbstractVector other = (AbstractVector) o;
        return Double.compare(other.getX(), this.getX()) == 0 &&
                Double.compare(other.getY(), this.getX()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Vector{"
                + getX() + ", "
                + getY() + ", "
                + getMagnitude() + ", "
                + getAngle() + "}";
    }
}
