package revAmped.linear.components;


/**
 * Action to be performed while robot is moving.
 */
public interface MoveAction {

    enum moveActions {}


    /**
     * get distance in encoder ticks.
     * @return distance in encoder ticks.
     */
    int distanceInTick();

    /**
     * performs action. Note action should be idempotent
     */
    void perform();

    /**
     * stop action
     */
    void stop();
}
