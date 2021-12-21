package util.spatialpartition;

import util.math.geometry.AABB;
import util.math.geometry.TwoFramePosition;

import java.util.List;

public interface AbstractSpatialPartition<T> {
    List<T> insertAndReturnCollisions(T identifier, AABB hitbox, TwoFramePosition position);
}