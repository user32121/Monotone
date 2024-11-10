package juniper.monotone.task;

public class InputManager {
    public static boolean forward, back, left, right, jump, sprint, sneak;

    public static void reset() {
        forward = back = left = right = jump = sprint = sneak = false;
    }
}
