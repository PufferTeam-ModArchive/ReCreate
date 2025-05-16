package su.sergiusonesimus.recreate.util;

public class ReCreateMath {

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static boolean equal(float a, float b) {
        return Math.abs(a - b) < 1.0E-5F;
    }

    public static boolean equal(double a, double b) {
        return Math.abs(a - b) < 1.0E-5F;
    }

}
