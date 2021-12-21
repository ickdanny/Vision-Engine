package util.spatialpartition;

import util.math.geometry.AABB;
import util.math.geometry.TwoFramePosition;
import util.math.geometry.DoublePoint;

import java.util.ArrayList;
import java.util.List;

public class QuadTree<T> implements AbstractSpatialPartition<T> {

    private static final boolean HANDLE_OUT_OF_BOUNDS = false;

    private static final int DEFAULT_MAX_ELEMENTS = 8;
    private static final int DEFAULT_LEVEL = 4;

    private final int maxElements;

    private final int level;

    private final AABB bounds;
    private final QuadTree<T>[] subTrees;

    private List<CollisionElement<T>> elements;

    public QuadTree(AABB bounds){
        this(DEFAULT_MAX_ELEMENTS, DEFAULT_LEVEL, bounds);
    }

    @SuppressWarnings("unchecked")
    public QuadTree(int maxElements, int level, AABB bounds){
        if(level < 0){
            throw new IllegalArgumentException("QuadTree level < 0>");
        }

        this.maxElements = maxElements;
        this.level = level;
        this.bounds = bounds;
        subTrees = level > 0 ? (QuadTree<T>[])new QuadTree[4] : null;
        elements = new ArrayList<>();
    }

    @Override
    public List<T> insertAndReturnCollisions(T identifier, AABB hitbox, TwoFramePosition position) {

        DoublePoint pos = position.getPos();
        DoublePoint pastPos = position.getPastPos();

        AABB trueHitbox = hitbox.makeTrueHitbox(pos);
        AABB pastHitbox = !pos.equals(pastPos) ? hitbox.makeTrueHitbox(pastPos) : trueHitbox;
        AABB twoFrameHitbox = AABB.makeTwoFrameHitbox(trueHitbox, pastHitbox);

        CollisionElement<T> element = new CollisionElement<>(identifier, hitbox, twoFrameHitbox, trueHitbox, position);

        List<T> toRet = getCollisionList(element);

        insert(element);

        return toRet;
    }

    private List<T> getCollisionList(CollisionElement<T> element){
        return getCollisionList(element, new ArrayList<>());
    }
    private List<T> getCollisionList(CollisionElement<T> element, List<T> collisionList){
        for(CollisionElement<T> otherElement : elements){
            if(element.collides(otherElement)){
                collisionList.add(otherElement.getIdentifier());
            }
        }
        if(hasSubtrees()){
            for(QuadTree<T> subTree : subTrees){
                if(element.getTwoFrameHitbox().collides(subTree.bounds)) {
                    collisionList = subTree.getCollisionList(element, collisionList);
                }
            }
        }
        return collisionList;
    }

    private void insert(CollisionElement<T> element){
        if(element.getTwoFrameHitbox().collides(bounds)) { //can split into two methods since we check the subtree bounds anyway
            if (hasSubtrees()) {
                QuadTree<T> collidedSubTree = null;
                int numNodesCollided = 0;

                for (int i = 0; i < 4; ++i) {
                    if (element.getTwoFrameHitbox().collides(subTrees[i].bounds)) {
                        collidedSubTree = subTrees[i];
                        numNodesCollided++;
                    }
                }

                if (numNodesCollided == 1) { //if only 1 subTree collided, insert into that subTree
                    collidedSubTree.insert(element);
                } else { //otherwise insert into the top level tree (this)
                    addElementAndCheckSplit(element);
                }
            } else {
                addElementAndCheckSplit(element);
            }
        }
        else{
            if(HANDLE_OUT_OF_BOUNDS) {
                addElementAndCheckSplit(element);
            }
        }
    }

    private void addElementAndCheckSplit(CollisionElement<T> element){
        elements.add(element);
        if(canSplit()){
            split();
        }
    }

    public boolean hasSubtrees(){
        return subTrees != null && subTrees[0] != null;
    }

    private boolean canSplit(){
        return subTrees != null && subTrees[0] == null && level > 0;
    }

    private void split(){
        if(elements.size() < maxElements){
            return;
        }

        double xLow = bounds.getXLow();
        double xHigh = bounds.getXHigh();
        double yLow = bounds.getYLow();
        double yHigh = bounds.getYHigh();
        double xAvg = (xLow + xHigh)/2;
        double yAvg = (yLow + yHigh)/2;

        int nextLevel = level - 1;

        subTrees[0] = new QuadTree<>(maxElements, nextLevel, new AABB(xLow, xAvg, yLow, yAvg));
        subTrees[1] = new QuadTree<>(maxElements, nextLevel, new AABB(xAvg, xHigh, yLow, yAvg));
        subTrees[2] = new QuadTree<>(maxElements, nextLevel, new AABB(xAvg, xHigh, yAvg, yHigh));
        subTrees[3] = new QuadTree<>(maxElements, nextLevel, new AABB(xLow, xAvg, yAvg, yHigh));

        splitElementsIntoSubTrees();
    }

    private void splitElementsIntoSubTrees(){
        List<CollisionElement<T>> oldElements = elements;
        elements = new ArrayList<>();
        for(CollisionElement<T> element : oldElements){
            insert(element);
        }
    }

    public QuadTree<T>[] getSubTrees() {
        return subTrees;
    }
    public AABB getBounds() {
        return bounds;
    }

    //DEBUG
    public String makeDistributionReport(){
        int[] distributionArray = new int[level];
        fillDistributionArray(distributionArray);

        StringBuilder toRet = new StringBuilder();
        for (int i = level + 1; i > 0; --i) {
            toRet.append(distributionArray[i]).append(", ");
        }
        toRet.append(distributionArray[0]);
        return toRet.toString();
    }
    private void fillDistributionArray(int[] toRet){
        //add your current level
        toRet[level] += elements.size();
        //all children add
        if(hasSubtrees()) {
            for (int i = 0; i < 4; i++) {
                subTrees[i].fillDistributionArray(toRet);
            }
        }
    }
}