package game;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import tage.GameObject;
import tage.ai.behaviortrees.BTStatus;
import java.util.UUID;

/**
 * Controls a looping thief NPC:
 * 1) ENTERING → walks to the register
 * 2) STEALING → deducts cash and starts fleeing
 * 3) FLEEING → runs to exit
 * 4) WAIT_BEFORE_LOOP → pauses then restarts at spawn
 */
public class ThiefBehaviorController {
    private enum State { ENTERING, STEALING, FLEEING, WAIT_BEFORE_LOOP }

    private static final float ENTRY_THRESHOLD = 1f;
    private static final float FLEE_THRESHOLD  = 1f;
    private static final float INTERACT_DIST   = 5f;
    private static final double STEAL_AMOUNT   = 10.0;
    private static final long  LOOP_DELAY_MS   = 15000L;

    private final GameObject thief, registerObj, player;
    private final Vector3f   exitLocation;
    private final CashManager cashManager;
    private final Vector3f    startLocation;

    private MoveToWaypoint mover;
    private State           state;
    private boolean         caught     = false;
    private long            timerStart;

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
        this.startLocation = new Vector3f(thief.getWorldLocation());
        startEntering();
    }

    private void startEntering() {
        mover = new MoveToWaypoint(
            thief,
            registerObj.getWorldLocation().add(-1.5f, -2.3f, 0),
            ENTRY_THRESHOLD,
            1.5f
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

    private void waitBeforeLoop() {
        timerStart = System.currentTimeMillis();
        state      = State.WAIT_BEFORE_LOOP;
    }

    /**
     * Called each frame to advance the FSM.
     */
    public void update(float deltaSeconds) {
        switch(state) {
            case ENTERING:
                if (mover.update(deltaSeconds) == BTStatus.BH_SUCCESS) {
                    state = State.STEALING;
                    steal();
                }
                break;
            case FLEEING:
                if (mover.update(deltaSeconds) == BTStatus.BH_SUCCESS) {
                    waitBeforeLoop();
                }
                break;
            case WAIT_BEFORE_LOOP:
                if (System.currentTimeMillis() - timerStart >= LOOP_DELAY_MS) {
                    // teleport back to spawn and restart
                    thief.setLocalTranslation(
                        new Matrix4f().translation(startLocation)
                    );
                    caught = false;
                    startEntering();
                }
                break;
        }
    }

    /**
     * Attempt to catch the thief while fleeing.
     */
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

    /**
     * Always returns false since the thief loops indefinitely.
     */
    public boolean isDone() {
        return false;
    }
}
