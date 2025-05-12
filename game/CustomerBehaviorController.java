package game;

import org.joml.Vector3f;
import tage.GameObject;
import tage.ai.behaviortrees.BTStatus;

/**
 * Controls one customer through order cycle:
 * 1) ENTERING → walks to the counter
 * 2) WAITING_FOR_ORDER → idle until player presses F
 * 3) MOVING_TO_TABLE → walks to the designated chair
 * 4) WAIT_AT_TABLE → waits a fixed duration
 * 5) RETURN_TO_COUNTER → walks back to counter
 * 6) WAIT_FOR_SERVE → idle until player serves
 * 7) LEAVING -> walks out of restaurant
 * 8) WAIT_BEFORE_LOOP
 */
public class CustomerBehaviorController {
    private enum State {
        ENTERING,
        WAITING_FOR_ORDER,
        MOVING_TO_TABLE,
        WAIT_AT_TABLE,
        RETURN_TO_COUNTER,
        WAIT_FOR_SERVE,
        LEAVING,
        WAIT_BEFORE_LOOP
    }

    private static final float ENTRY_THRESHOLD = 1f;
    private static final float INTERACT_DIST   = 5f;
    private static final float WALK_SPEED      = 1.5f;
    private static final long  TABLE_WAIT_MS   = 8000L;
    private static final long LOOP_DELAY_MS = 10000L;

    private final GameObject customer;
    private final GameObject counterObj;
    private final GameObject chairObj;
    private final GameObject player;

    private MoveToWaypoint mover;
    private State          state;
    private long           timerStart;
    private boolean        orderTaken = false;
    private boolean        served     = false;

    public CustomerBehaviorController(GameObject customer,
                                      GameObject counterObj,
                                      GameObject chairObj,
                                      GameObject player) {
        this.customer   = customer;
        this.counterObj = counterObj;
        this.chairObj   = chairObj;
        this.player     = player;
        enterCounter();
    }

    private void enterCounter() {
        mover = new MoveToWaypoint(
            customer,
            counterObj.getWorldLocation(),
            ENTRY_THRESHOLD,
            WALK_SPEED
        );
        state = State.ENTERING;
    }

    private void moveToTable() {
        Vector3f target = new Vector3f(chairObj.getWorldLocation());
        mover = new MoveToWaypoint(
            customer,
            target,
            ENTRY_THRESHOLD,
            WALK_SPEED
        );
        state = State.MOVING_TO_TABLE;
    }

    private void waitAtTable() {
        timerStart = System.currentTimeMillis();
        state = State.WAIT_AT_TABLE;
    }

    private void returnToCounter() {
        mover = new MoveToWaypoint(
            customer,
            counterObj.getWorldLocation(),
            ENTRY_THRESHOLD,
            WALK_SPEED
        );
        state = State.RETURN_TO_COUNTER;
    }

    private void leaveRestaurant() {
        mover = new MoveToWaypoint(customer,
                new Vector3f(-70, 0, 0),
                ENTRY_THRESHOLD,
                WALK_SPEED
        );
        state = State.LEAVING;
    }

    private void waitBeforeLoop() {
        timerStart = System.currentTimeMillis();
        state = State.WAIT_BEFORE_LOOP;
    }

    /**
     * Called once per frame from game.update(elapsedTime).
     */
    public void update(float dt) {
        switch (state) {
            case ENTERING:
                if (mover.update(dt) == BTStatus.BH_SUCCESS) {
                    state = State.WAITING_FOR_ORDER;
                }
                break;

            case WAITING_FOR_ORDER:
                // idle until tryTakeOrder()
                break;

            case MOVING_TO_TABLE:
                if (mover.update(dt) == BTStatus.BH_SUCCESS) {
                    waitAtTable();
                }
                break;

            case WAIT_AT_TABLE:
                if (System.currentTimeMillis() - timerStart >= TABLE_WAIT_MS) {
                    returnToCounter();
                }
                break;

            case RETURN_TO_COUNTER:
                if (mover.update(dt) == BTStatus.BH_SUCCESS) {
                    state = State.WAIT_FOR_SERVE;
                }
                break;

            case WAIT_FOR_SERVE:
                // idle until tryServeOrder()
                break;

            case LEAVING:
                if (mover.update(dt) == BTStatus.BH_SUCCESS) {
                    waitBeforeLoop();
                }
                break;
            
            case WAIT_BEFORE_LOOP:
                if (System.currentTimeMillis() - timerStart >= LOOP_DELAY_MS) {
                    orderTaken = false;
                    served     = false;
                    enterCounter();
                }
                break;
        }
    }

    /**
     * Player presses F to take the order when at counter.
     */
    public void tryTakeOrder() {
        if (state != State.WAITING_FOR_ORDER || orderTaken) return;
        float d = customer.getWorldLocation()
                          .distance(player.getWorldLocation());
        if (d < INTERACT_DIST) {
            orderTaken = true;
            moveToTable();
        }
    }

    /**
     * Player presses F to serve once customer returns.
     */
    public void tryServeOrder() {
        if (state != State.WAIT_FOR_SERVE || served) return;
        float d = customer.getWorldLocation().distance(player.getWorldLocation());
        if (d < INTERACT_DIST) {
            served = true;
            leaveRestaurant();
        }
    }

    public boolean isWaitingForOrder() {
        return state == State.WAITING_FOR_ORDER;
    }

    public boolean isWaitingForServe() {
        return state == State.WAIT_FOR_SERVE;
    }

    public boolean isDone() {
        return state == State.LEAVING;
    }
}
