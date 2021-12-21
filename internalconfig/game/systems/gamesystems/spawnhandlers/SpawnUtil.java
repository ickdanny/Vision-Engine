package internalconfig.game.systems.gamesystems.spawnhandlers;

import internalconfig.game.components.Animation;
import internalconfig.game.components.AnimationComponent;
import internalconfig.game.components.ProgramRepository;
import internalconfig.game.components.SpriteInstruction;
import internalconfig.game.systems.gamesystems.GameUtil;
import util.math.RandomUtil;
import util.math.geometry.AbstractVector;
import util.math.geometry.Angle;
import util.math.geometry.CartesianVector;
import util.math.geometry.DoublePoint;
import util.math.geometry.GeometryUtil;
import util.math.geometry.PolarVector;
import util.messaging.AbstractPublishSubscribeBoard;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;

import static internalconfig.game.GameConfig.*;
import static internalconfig.game.systems.gamesystems.spawnhandlers.EnemyProjectileColors.*;

public final class SpawnUtil {
    private SpawnUtil() {
    }

    public static final double OUT = 45;

    public static final double TOP_OUT = 0 - OUT;
    public static final double BOTTOM_OUT = HEIGHT + OUT;
    public static final double LEFT_OUT = 0 - OUT;
    public static final double RIGHT_OUT = WIDTH + OUT;

    public static SpriteInstruction makeApparitionSpriteInstruction() {
        return new SpriteInstruction("apparition_1");
    }

    public static AnimationComponent makeApparitionAnimationComponent() {
        return new AnimationComponent(
                new Animation(true, "apparition_1", "apparition_2", "apparition_3", "apparition_4"),
                10
        );
    }

    public static double pickupInitSpeed(DoublePoint pos) {
        double y = pos.getY();

        //higher = lower speed
        double ratio = y / HEIGHT;
        return PICKUP_INIT_SPEED_BASE * (1 + (ratio * PICKUP_INIT_SPEED_MULTI));
    }

    public static void inbound(DoublePoint pos, double bound, Consumer<DoublePoint> spawner) {
        if (GameUtil.isOutOfBounds(pos, bound)) {
            spawner.accept(GameUtil.inboundPosition(pos, bound));
        } else {
            spawner.accept(pos);
        }
    }

    public static void pickupInbound(DoublePoint pos, Consumer<DoublePoint> spawner) {
        inbound(pos, PICKUP_INBOUND, spawner);
    }

    public static void randomPosition(double xLow,
                                      double xHigh,
                                      double yLow,
                                      double yHigh,
                                      Random random,
                                      Consumer<DoublePoint> spawner) {
        DoubleStream xStream = random.doubles(1, xLow, xHigh);
        DoubleStream yStream = random.doubles(1, yLow, yHigh);
        PrimitiveIterator.OfDouble xIterator = xStream.iterator();
        PrimitiveIterator.OfDouble yIterator = yStream.iterator();

        spawner.accept(new DoublePoint(xIterator.nextDouble(), yIterator.nextDouble()));
    }

    public static void randomPositions(double xLow,
                                       double xHigh,
                                       double yLow,
                                       double yHigh,
                                       int numberOfPositions,
                                       Random random,
                                       Consumer<DoublePoint> spawner) {
        if (numberOfPositions <= 0) {
            throw new IllegalArgumentException("less than 1 positions!");
        }
        DoubleStream xStream = random.doubles(numberOfPositions, xLow, xHigh);
        DoubleStream yStream = random.doubles(numberOfPositions, yLow, yHigh);
        PrimitiveIterator.OfDouble xIterator = xStream.iterator();
        PrimitiveIterator.OfDouble yIterator = yStream.iterator();

        for (int i = 0; i < numberOfPositions; ++i) {
            spawner.accept(new DoublePoint(xIterator.nextDouble(), yIterator.nextDouble()));
        }
    }

    public static void randomTopOutPosition(double xLow, double xHigh, Random random, Consumer<DoublePoint> spawner) {
        DoubleStream xStream = random.doubles(1, xLow, xHigh);
        PrimitiveIterator.OfDouble xItr = xStream.iterator();

        spawner.accept(new DoublePoint(xItr.nextDouble(), TOP_OUT));
    }

    public static void randomTopOutPositions(double xLow,
                                             double xHigh,
                                             int numberOfPositions,
                                             Random random,
                                             Consumer<DoublePoint> spawner) {
        if (numberOfPositions <= 0) {
            throw new IllegalArgumentException("less than 1 positions!");
        }
        DoubleStream xStream = random.doubles(numberOfPositions, xLow, xHigh);
        PrimitiveIterator.OfDouble xIterator = xStream.iterator();

        for (int i = 0; i < numberOfPositions; ++i) {
            spawner.accept(new DoublePoint(xIterator.nextDouble(), TOP_OUT));
        }
    }

    public static void randomLeftOutPosition(double yLow, double yHigh, Random random, Consumer<DoublePoint> spawner) {
        DoubleStream yStream = random.doubles(1, yLow, yHigh);
        PrimitiveIterator.OfDouble yItr = yStream.iterator();

        spawner.accept(new DoublePoint(LEFT_OUT, yItr.nextDouble()));
    }

    public static void randomLeftOutPositions(double yLow,
                                              double yHigh,
                                              int numberOfPositions,
                                              Random random,
                                              Consumer<DoublePoint> spawner) {
        if (numberOfPositions <= 0) {
            throw new IllegalArgumentException("less than 1 positions!");
        }
        DoubleStream yStream = random.doubles(numberOfPositions, yLow, yHigh);
        PrimitiveIterator.OfDouble yIterator = yStream.iterator();

        for (int i = 0; i < numberOfPositions; ++i) {
            spawner.accept(new DoublePoint(LEFT_OUT, yIterator.nextDouble()));
        }
    }

    public static void randomRightOutPosition(double yLow, double yHigh, Random random, Consumer<DoublePoint> spawner) {
        DoubleStream yStream = random.doubles(1, yLow, yHigh);
        PrimitiveIterator.OfDouble yItr = yStream.iterator();

        spawner.accept(new DoublePoint(RIGHT_OUT, yItr.nextDouble()));
    }

    public static void randomRightOutPositions(double yLow,
                                               double yHigh,
                                               int numberOfPositions,
                                               Random random,
                                               Consumer<DoublePoint> spawner) {
        if (numberOfPositions <= 0) {
            throw new IllegalArgumentException("less than 1 positions!");
        }
        DoubleStream yStream = random.doubles(numberOfPositions, yLow, yHigh);
        PrimitiveIterator.OfDouble yIterator = yStream.iterator();

        for (int i = 0; i < numberOfPositions; ++i) {
            spawner.accept(new DoublePoint(RIGHT_OUT, yIterator.nextDouble()));
        }
    }

    public static void randomSpeed(double low, double high, Random random, Consumer<Double> spawner) {
        DoubleStream speedStream = random.doubles(1, low, high);
        PrimitiveIterator.OfDouble speedItr = speedStream.iterator();

        spawner.accept(speedItr.next());
    }

    public static void randomSpeeds(double low, double high, int numberOfSpeeds, Random random, Consumer<Double> spawner) {
        if (numberOfSpeeds <= 0) {
            throw new IllegalArgumentException("less than 1 speed!");
        }
        DoubleStream speedStream = random.doubles(numberOfSpeeds, low, high);
        PrimitiveIterator.OfDouble speedItr = speedStream.iterator();

        for (int i = 0; i < numberOfSpeeds; ++i) {
            spawner.accept(speedItr.next());
        }
    }

    public static void randomAngle(double low, double high, Random random, Consumer<Angle> spawner) {
        DoubleStream angleStream = random.doubles(1, low, high);
        PrimitiveIterator.OfDouble angleItr = angleStream.iterator();

        spawner.accept(new Angle(angleItr.next()));
    }

    public static void randomAngles(double low, double high, int numberOfAngles, Random random, Consumer<Angle> spawner) {
        if (numberOfAngles <= 0) {
            throw new IllegalArgumentException("less than 1 angle!");
        }
        DoubleStream angleStream = random.doubles(numberOfAngles, low, high);
        PrimitiveIterator.OfDouble angleItr = angleStream.iterator();

        for (int i = 0; i < numberOfAngles; ++i) {
            spawner.accept(new Angle(angleItr.next()));
        }
    }

    public static void randomVelocity(double speedLow,
                                      double speedHigh,
                                      double angleLow,
                                      double angleHigh,
                                      Random random,
                                      Consumer<AbstractVector> spawner) {
        DoubleStream speedStream = random.doubles(1, speedLow, speedHigh);
        DoubleStream angleStream = random.doubles(1, angleLow, angleHigh);

        PrimitiveIterator.OfDouble speedItr = speedStream.iterator();
        PrimitiveIterator.OfDouble angleItr = angleStream.iterator();

        spawner.accept(new PolarVector(speedItr.next(), angleItr.next()));
    }

    public static void randomVelocities(double speedLow,
                                        double speedHigh,
                                        double angleLow,
                                        double angleHigh,
                                        int numberOfVelocities,
                                        Random random,
                                        Consumer<AbstractVector> spawner) {
        if (numberOfVelocities <= 0) {
            throw new IllegalArgumentException("less than 1 velocity!");
        }
        DoubleStream speedStream = random.doubles(numberOfVelocities, speedLow, speedHigh);
        DoubleStream angleStream = random.doubles(numberOfVelocities, angleLow, angleHigh);

        PrimitiveIterator.OfDouble speedItr = speedStream.iterator();
        PrimitiveIterator.OfDouble angleItr = angleStream.iterator();

        for (int i = 0; i < numberOfVelocities; ++i) {
            spawner.accept(new PolarVector(speedItr.next(), angleItr.next()));
        }
    }

    public static void randomVelocity(double speedLow,
                                      double speedHigh,
                                      Angle baseAngle,
                                      double angleRange,
                                      Random random,
                                      Consumer<AbstractVector> spawner) {
        DoubleStream speedStream = random.doubles(1, speedLow, speedHigh);
        DoubleStream angleOffsetStream = random.doubles(1, -angleRange, angleRange);

        PrimitiveIterator.OfDouble speedItr = speedStream.iterator();
        PrimitiveIterator.OfDouble angleOffsetItr = angleOffsetStream.iterator();

        spawner.accept(new PolarVector(speedItr.next(), baseAngle.add(angleOffsetItr.next())));
    }

    public static void randomVelocities(double speedLow,
                                        double speedHigh,
                                        Angle baseAngle,
                                        double angleRange,
                                        int numberOfVelocities,
                                        Random random,
                                        Consumer<AbstractVector> spawner) {
        if (numberOfVelocities <= 0) {
            throw new IllegalArgumentException("less than 1 velocity!");
        }
        DoubleStream speedStream = random.doubles(numberOfVelocities, speedLow, speedHigh);
        DoubleStream angleOffsetStream = random.doubles(numberOfVelocities, -angleRange, angleRange);

        PrimitiveIterator.OfDouble speedItr = speedStream.iterator();
        PrimitiveIterator.OfDouble angleOffsetItr = angleOffsetStream.iterator();

        for (int i = 0; i < numberOfVelocities; ++i) {
            spawner.accept(new PolarVector(speedItr.next(), baseAngle.add(angleOffsetItr.next())));
        }
    }

    public static void columnFormation(double speedLow, double speedHigh, int rows, Consumer<Double> spawner) {
        if (rows <= 0) {
            throw new IllegalArgumentException("rows cannot be <= 0!");
        }
        if (rows == 1) {
            spawner.accept((speedLow + speedHigh) / 2);
            return;
        }
        double speedDifference = speedHigh - speedLow;
        double increment = speedDifference / (rows - 1);
        double speed = speedLow; //all values used
        for (int i = 0; i < rows; ++i) {
            spawner.accept(speed);
            speed += increment;
        }
    }


    //mirrored over the y axis
    public static void mirrorFormation(DoublePoint basePos,
                                       AbstractVector baseVelocity,
                                       double axis,
                                       BiConsumer<DoublePoint, AbstractVector> spawner) {

        //in point slope form, our new X n follows the equation
        // n - a = -(o - a)
        // where a is our axis and o is the old X
        DoublePoint mirrorPos = new DoublePoint(-basePos.getX() + (2 * axis), basePos.getY());
        AbstractVector mirrorVelocity = new PolarVector(baseVelocity.getMagnitude(), baseVelocity.getAngle().flipY());

        spawner.accept(basePos, baseVelocity);
        spawner.accept(mirrorPos, mirrorVelocity);
    }

    public static void mirrorFormation(DoublePoint basePos,
                                       Angle angle,
                                       double axis,
                                       BiConsumer<DoublePoint, Angle> spawner) {

        DoublePoint mirrorPos = new DoublePoint(-basePos.getX() + (2 * axis), basePos.getY());
        Angle mirrorAngle = angle.flipY();

        spawner.accept(basePos, angle);
        spawner.accept(mirrorPos, mirrorAngle);
    }

    //does not mirror the velocity
    public static void mirrorFormation(DoublePoint basePos,
                                       double axis,
                                       Consumer<DoublePoint> spawner) {

        DoublePoint mirrorPos = new DoublePoint(-basePos.getX() + (2 * axis), basePos.getY());

        spawner.accept(basePos);
        spawner.accept(mirrorPos);
    }

    public static void singleSideMirror(DoublePoint basePos,
                                        AbstractVector baseVelocity,
                                        double axis,
                                        BiConsumer<DoublePoint, AbstractVector> spawner) {

        DoublePoint mirrorPos = new DoublePoint(-basePos.getX() + (2 * axis), basePos.getY());
        AbstractVector mirrorVelocity = new PolarVector(baseVelocity.getMagnitude(), baseVelocity.getAngle().flipY());

        spawner.accept(mirrorPos, mirrorVelocity);
    }

    public static void singleSideMirror(DoublePoint basePos,
                                        Angle angle,
                                        double axis,
                                        BiConsumer<DoublePoint, Angle> spawner) {

        DoublePoint mirrorPos = new DoublePoint(-basePos.getX() + (2 * axis), basePos.getY());
        Angle mirrorAngle = angle.flipY();

        spawner.accept(mirrorPos, mirrorAngle);
    }

    //does not mirror the velocity
    public static void singleSideMirror(DoublePoint basePos,
                                        double axis,
                                        Consumer<DoublePoint> spawner) {

        DoublePoint mirrorPos = new DoublePoint(-basePos.getX() + (2 * axis), basePos.getY());

        spawner.accept(mirrorPos);
    }

    //block across the x axis, mirrored on y axis
    public static void blockFormation(DoublePoint center,
                                      double totalWidth,
                                      int spawns,
                                      Consumer<DoublePoint> spawner) {
        if (spawns <= 0) {
            throw new IllegalArgumentException("spawns <= 0!");
        } else if (spawns == 1) {
            spawner.accept(center);
            return;
        }
        double y = center.getY();
        double centerX = center.getX();
        double x = centerX - totalWidth / 2; //first value of x not a spawning position
        double increment = totalWidth / (spawns + 1);
        for (int i = 0; i < spawns; ++i) {
            spawner.accept(new DoublePoint(x += increment, y));
        }
    }

    public static void fullBlockFormation(DoublePoint center,
                                          double totalWidth,
                                          int spawns,
                                          Consumer<DoublePoint> spawner) {
        if (spawns <= 0) {
            throw new IllegalArgumentException("spawns <= 0!");
        } else if (spawns == 1) {
            spawner.accept(center);
            return;
        }
        double halfWidth = totalWidth / 2;
        double y = center.getY();
        double centerX = center.getX();
        if (spawns == 2) {
            spawner.accept(new DoublePoint(centerX - halfWidth, y));
            spawner.accept(new DoublePoint(centerX + halfWidth, y));
        }
        double x = centerX - totalWidth / 2; //first value of x is a spawning position
        spawner.accept(new DoublePoint(x, y));
        double increment = totalWidth / (spawns - 1);
        for (int i = 0; i < spawns - 1; ++i) {
            spawner.accept(new DoublePoint(x += increment, y));
        }
    }

    //block along the angle
    public static void blockFormation(DoublePoint center,
                                      Angle angle,
                                      double totalWidth,
                                      int spawns,
                                      Consumer<DoublePoint> spawner) {
        if (spawns <= 0) {
            throw new IllegalArgumentException("spawns <= 0!");
        } else if (spawns == 1) {
            spawner.accept(center);
        } else {
            double widthIncrement = totalWidth / (spawns + 1);
            DoublePoint pos = new PolarVector(-totalWidth / 2, angle).add(center); //first position not used
            AbstractVector incrementVector = new PolarVector(widthIncrement, angle);
            for (int i = 0; i < spawns; ++i) {
                pos = incrementVector.add(pos);
                spawner.accept(pos);
            }
        }
    }

    public static void fullBlockFormation(DoublePoint center,
                                          Angle angle,
                                          double totalWidth,
                                          int spawns,
                                          Consumer<DoublePoint> spawner) {
        if (spawns <= 0) {
            throw new IllegalArgumentException("spawns <= 0!");
        } else if (spawns == 1) {
            spawner.accept(center);
        }
        double widthIncrement = totalWidth / (spawns - 1);
        DoublePoint pos = new PolarVector(-totalWidth / 2, angle).add(center); //first position used
        spawner.accept(pos);
        AbstractVector incrementVector = new PolarVector(widthIncrement, angle);
        for (int i = 0; i < spawns - 1; ++i) {
            pos = incrementVector.add(pos);
            spawner.accept(pos);
        }
    }

    //staggered in time
    public static void staggeredBlockFormation(int tick,
                                               int maxTick,
                                               int mod,
                                               DoublePoint firstPos,
                                               DoublePoint lastPos,
                                               Consumer<DoublePoint> spawner) {
        int tickFromZero = maxTick - tick;
        double tickRatio = ((double) tickFromZero) / (maxTick - mod);
        AbstractVector differenceVector = GeometryUtil.vectorFromAToB(firstPos, lastPos);
        differenceVector.scale(tickRatio);
        spawner.accept(differenceVector.add(firstPos));
    }

    public static void ringFormation(Angle baseAngle,
                                     int symmetry,
                                     Consumer<Angle> spawner) {
        double angleAdd = GeometryUtil.fullAngleDivide(symmetry);

        Angle angle = baseAngle;

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(angle);

            angle = angle.add(angleAdd);
        }
        spawner.accept(angle);
    }

    public static void ringFormation(AbstractVector baseVelocity,
                                     int symmetry,
                                     Consumer<AbstractVector> spawner) {
        double angleAdd = GeometryUtil.fullAngleDivide(symmetry);

        AbstractVector velocity = new PolarVector(baseVelocity);

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(new PolarVector(velocity));

            velocity.setAngle(velocity.getAngle().add(angleAdd));
        }
        spawner.accept(velocity);
    }

    public static void ringFormation(DoublePoint center,
                                     AbstractVector baseVelocity,
                                     int symmetry,
                                     BiConsumer<DoublePoint, AbstractVector> spawner) {
        double angleAdd = GeometryUtil.fullAngleDivide(symmetry);

        AbstractVector velocity = new PolarVector(baseVelocity);

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(center, new PolarVector(velocity));

            velocity.setAngle(velocity.getAngle().add(angleAdd));
        }
        spawner.accept(center, velocity);
    }

    public static void ringFormation(DoublePoint center,
                                     DoublePoint basePos,
                                     AbstractVector baseVelocity,
                                     int symmetry,
                                     BiConsumer<DoublePoint, AbstractVector> spawner) {
        double angleAdd = GeometryUtil.fullAngleDivide(symmetry);

        AbstractVector vectorToPos = GeometryUtil.vectorFromAToB(center, basePos);
        vectorToPos = new PolarVector(vectorToPos);

        AbstractVector velocity = new PolarVector(baseVelocity);

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(vectorToPos.add(center), new PolarVector(velocity));

            vectorToPos.setAngle(vectorToPos.getAngle().add(angleAdd));
            velocity.setAngle(velocity.getAngle().add(angleAdd));
        }
        spawner.accept(vectorToPos.add(center), velocity);
    }

    public static void ringFormation(DoublePoint center,
                                     DoublePoint basePos,
                                     int symmetry,
                                     Consumer<DoublePoint> spawner) {
        double angleAdd = GeometryUtil.fullAngleDivide(symmetry);

        AbstractVector vectorToPos = GeometryUtil.vectorFromAToB(center, basePos);
        vectorToPos = new PolarVector(vectorToPos);

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(vectorToPos.add(center));

            vectorToPos.setAngle(vectorToPos.getAngle().add(angleAdd));
        }
        spawner.accept(vectorToPos.add(center));
    }

    public static void arcFormation(AbstractVector baseVelocity,
                                    int symmetry,
                                    double totalAngle,
                                    Consumer<AbstractVector> spawner) {
        double speed = baseVelocity.getMagnitude();
        double angle = baseVelocity.getAngle().getAngle() - (totalAngle / 2); //first value of angle is not being spawned
        double increment = totalAngle / (symmetry + 1);
        for (int i = 0; i < symmetry; ++i) {
            spawner.accept(new PolarVector(speed, angle += increment));
        }
    }

    public static void arcFormation(DoublePoint center,
                                    DoublePoint basePos,
                                    int symmetry,
                                    double totalAngle,
                                    Consumer<DoublePoint> spawner) {
        AbstractVector vectorToPos = GeometryUtil.vectorFromAToB(center, basePos);
        vectorToPos = new PolarVector(vectorToPos);

        double halfTotalAngle = totalAngle / 2;

        double angleToPos = vectorToPos.getAngle().getAngle() - halfTotalAngle;
        double increment = totalAngle / (symmetry + 1);

        vectorToPos.setAngle(angleToPos + increment);

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(vectorToPos.add(center));

            vectorToPos.setAngle(vectorToPos.getAngle().add(increment));
        }
        spawner.accept(vectorToPos.add(center));
    }

    public static void arcFormation(DoublePoint center,
                                    DoublePoint basePos,
                                    AbstractVector baseVelocity,
                                    int symmetry,
                                    double totalAngle,
                                    BiConsumer<DoublePoint, AbstractVector> spawner) {
        AbstractVector vectorToPos = GeometryUtil.vectorFromAToB(center, basePos);
        vectorToPos = new PolarVector(vectorToPos);

        double halfTotalAngle = totalAngle / 2;

        double angleToPos = vectorToPos.getAngle().getAngle() - halfTotalAngle;
        double speed = baseVelocity.getMagnitude();
        double angle = baseVelocity.getAngle().getAngle() - halfTotalAngle;
        double increment = totalAngle / (symmetry + 1);

        vectorToPos.setAngle(angleToPos + increment);
        AbstractVector velocity = new PolarVector(speed, angle + increment);

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(vectorToPos.add(center), new PolarVector(velocity));

            vectorToPos.setAngle(vectorToPos.getAngle().add(increment));
            velocity.setAngle(velocity.getAngle().add(increment));
        }
        spawner.accept(vectorToPos.add(center), velocity);
    }

    public static void arcFormationIncrement(AbstractVector baseVelocity,
                                             int symmetry,
                                             double angleIncrement,
                                             Consumer<AbstractVector> spawner) {
        double speed = baseVelocity.getMagnitude();
        double baseAngle = baseVelocity.getAngle().getAngle();
        double angle = baseAngle - ((symmetry - 1) * angleIncrement / 2);
        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(new PolarVector(speed, angle));
            angle += angleIncrement;
        }
        spawner.accept(new PolarVector(speed, angle));
    }

    public static void arcFormationIncrement(DoublePoint center,
                                             DoublePoint basePos,
                                             AbstractVector baseVelocity,
                                             int symmetry,
                                             double angleIncrement,
                                             BiConsumer<DoublePoint, AbstractVector> spawner) {
        AbstractVector vectorToPos = GeometryUtil.vectorFromAToB(center, basePos);
        vectorToPos = new PolarVector(vectorToPos);

        double angleSubtract = (symmetry - 1) * angleIncrement / 2;

        vectorToPos.setAngle(vectorToPos.getAngle().getAngle() - angleSubtract);
        double angle = baseVelocity.getAngle().getAngle() - angleSubtract;
        double speed = baseVelocity.getMagnitude();

        for (int i = 0; i < symmetry - 1; ++i) {
            spawner.accept(vectorToPos.add(center), new PolarVector(speed, angle));

            vectorToPos.setAngle(vectorToPos.getAngle().add(angleIncrement));
            angle += angleIncrement;
        }
        spawner.accept(vectorToPos.add(center), new PolarVector(speed, angle));
    }

    public static void spiralFormation(int tick,
                                       int maxTick,
                                       double baseAngle,
                                       double angularVelocity,
                                       Consumer<Angle> spawner) {
        int tickFromZero = maxTick - tick;
        spawner.accept(new Angle(baseAngle + (tickFromZero * angularVelocity)));
    }

    public static void sineFormation(int tick,
                                     double baseAngle,
                                     double tickMulti,
                                     double angleBound,
                                     Consumer<Angle> spawner) {
        spawner.accept(new Angle(baseAngle + Math.sin(tick * tickMulti * Math.PI) * angleBound));
    }

    public static void whipFormation(int tick,
                                     int maxTick,
                                     double speedLow,
                                     double speedHigh,
                                     Consumer<Double> spawner) {
        int tickFromZero = maxTick - tick;
        double tickRatio = ((double) tickFromZero) / maxTick;
        double speedDifference = speedHigh - speedLow;
        spawner.accept(speedLow + (tickRatio * speedDifference));
    }

    public static EnemyProjectileColors randomColor(Random random) {
        int index = RandomUtil.randIntInclusive(0, 11, random);
        switch (index) {
            case 0:
                return RED;
            case 1:
                return ORANGE;
            case 2:
                return YELLOW;
            case 3:
                return CHARTREUSE;
            case 4:
                return GREEN;
            case 5:
                return SPRING;
            case 6:
                return CYAN;
            case 7:
                return AZURE;
            case 8:
                return BLUE;
            case 9:
                return VIOLET;
            case 10:
                return MAGENTA;
            case 11:
                return ROSE;
            default:
                throw new RuntimeException("unexpected index: " + index);
        }
    }

    public static class PickupSpawner {
        public static void spawnSmallPowerPickup(AbstractPublishSubscribeBoard sliceBoard,
                                                 SpawnBuilder spawnBuilder,
                                                 DoublePoint pos) {
            inbound(pos, PICKUP_INBOUND, (p) -> sliceBoard.publishMessage(
                    spawnBuilder.makeSmallPowerPickup(p, new CartesianVector(0, -SpawnUtil.pickupInitSpeed(p)))
                            .setProgram(ProgramRepository.PICKUP_ACCELERATE_DOWN.getProgram())
                            .packageAsMessage()
            ));
        }

        public static void spawnSmallPowerPickup(AbstractPublishSubscribeBoard sliceBoard,
                                                 SpawnBuilder spawnBuilder,
                                                 DoublePoint pos,
                                                 double speed) {
            inbound(pos, PICKUP_INBOUND, (p) -> sliceBoard.publishMessage(
                    spawnBuilder.makeSmallPowerPickup(p, new CartesianVector(0, -speed))
                            .setProgram(ProgramRepository.PICKUP_ACCELERATE_DOWN.getProgram())
                            .packageAsMessage()
            ));
        }

        public static void spawnLargePowerPickup(AbstractPublishSubscribeBoard sliceBoard,
                                                 SpawnBuilder spawnBuilder,
                                                 DoublePoint pos) {
            inbound(pos, PICKUP_INBOUND, (p) -> sliceBoard.publishMessage(
                    spawnBuilder.makeLargePowerPickup(p, new CartesianVector(0, -SpawnUtil.pickupInitSpeed(p)))
                            .setProgram(ProgramRepository.PICKUP_ACCELERATE_DOWN.getProgram())
                            .packageAsMessage()
            ));
        }

        public static void spawnLifePickup(AbstractPublishSubscribeBoard sliceBoard,
                                           SpawnBuilder spawnBuilder,
                                           DoublePoint pos) {
            inbound(pos, PICKUP_INBOUND, (p) -> sliceBoard.publishMessage(
                    spawnBuilder.makeLifePickup(p, new CartesianVector(0, -SpawnUtil.pickupInitSpeed(p)))
                            .setProgram(ProgramRepository.PICKUP_ACCELERATE_DOWN.getProgram())
                            .packageAsMessage()
            ));
        }

        public static void spawnBombPickup(AbstractPublishSubscribeBoard sliceBoard,
                                           SpawnBuilder spawnBuilder,
                                           DoublePoint pos) {
            inbound(pos, PICKUP_INBOUND, (p) -> sliceBoard.publishMessage(
                    spawnBuilder.makeBombPickup(p, new CartesianVector(0, -SpawnUtil.pickupInitSpeed(p)))
                            .setProgram(ProgramRepository.PICKUP_ACCELERATE_DOWN.getProgram())
                            .packageAsMessage()
            ));
        }
    }
}
