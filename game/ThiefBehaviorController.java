package game;

import org.joml.Vector3f;
import tage.GameObject;
import tage.ai.behaviortrees.BTStatus;

/**
 * Controls a one-shot thief NPC that steals $5 from the register,
 * then flees to a fixed exit location. If the player presses F near
 * the thief during the flee, the $5 is refunded.
 */
public class ThiefBehaviorController {
    private enum State { ENTERING, STEALING, FLEEING, DONE }

    private static final float ENTRY_THRESHOLD = 1f;
    private static final float FLEE_THRESHOLD  = 1f;
    private static final float INTERACT_DIST   = 5f;
    private static final double STEAL_AMOUNT   = 5.0;

    private final GameObject thief, registerObj, player;
    private final Vector3f   exitLocation;
    private final CashManager cashManager;

    private MoveToWaypoint mover;
    private State           state;
    private boolean         caught = false;

    public ThiefBehaviorController(GameObject thief,
                                   GameObject registerObj,
                                   Vector3f exitLocation,
                                   GameObject player,
                                   CashManager cashManager) {
        this.thief        = thief;
        this.registerObj  = registerObj;
        this.exitLocation = new Vector3f(exitLocation);
        this.player       = player;
        this.cashManager  = cashManager;
        startEntering();
    }

    private void startEntering() {
        mover = new MoveToWaypoint(
            thief,
            registerObj.getWorldLocation(),
            ENTRY_THRESHOLD,
            2f
        );
        state = State.ENTERING;
    }

    private void steal() {
        cashManager.deductExpense(STEAL_AMOUNT);
        startFleeing();
    }

    private void startFleeing() {
        mover = new MoveToWaypoint(
            thief,
            exitLocation,
            FLEE_THRESHOLD,
           2f
        );
        state = State.FLEEING;
    }

    private void finish() {
        state = State.DONE;
    }

    public void update(float deltaSeconds) {
        switch (state) {
            case ENTERING:
                if (mover.update(deltaSeconds) == BTStatus.BH_SUCCESS) {
                    state = State.STEALING;
                    steal();
                }
                break;
            case FLEEING:
                if (mover.update(deltaSeconds) == BTStatus.BH_SUCCESS) {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void tryCatch() {
        if (state == State.FLEEING && !caught) {
            float d = thief.getWorldLocation()
                           .distance(player.getWorldLocation());
            if (d < INTERACT_DIST) {
                caught = true;
                cashManager.addIncome(STEAL_AMOUNT);
            }
        }
    }

    public boolean isDone() {
        return state == State.DONE;
    }
}

