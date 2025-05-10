package game;

import java.util.Random;
import org.joml.Vector3f;
import com.jogamp.opengl.util.gl2.GLUT;

import tage.Engine;
import tage.GameObject;
import tage.HUDmanager;
import tage.ai.behaviortrees.BTStatus;

public class GameLogic {
    private static final float INTERACT_DIST    = 5.0f;
    private static final float CUSTOMER_SPEED   = 4.0f;
    private static final float ORDER_RADIUS     = 20.0f;

    private GameObject player, customer, oven1;
    private HUDmanager  hud;
    private boolean     pizzaStarted   = false;
    private boolean     pizzaReady     = false;
    private long        cookingStart   = 0;
    private boolean     pizzaPrompt    = false;

    private boolean     orderTaken          = false;
    private boolean     orderPromptVisible  = false;
    private MoveToWaypoint moveAction       = null;
    private Random      rnd                 = new Random();

    public GameLogic(GameObject player, GameObject customer, GameObject oven1, Engine engine) {
        this.player   = player;
        this.customer = customer;
        this.oven1    = oven1;
        this.hud      = engine.getHUDmanager();
    }

    /** Called every frame from game.update(elapsTime) */
    public void update(float elapsedMillis) {
        handlePizzaLogic();
        handleOrderPrompt();

        // drive the customer once order is taken
        if (orderTaken && moveAction != null) {
            BTStatus s = moveAction.update(elapsedMillis);
            if (s == BTStatus.BH_SUCCESS) {
                moveAction = null;
                // if you ever want them to reorder later:
                // orderTaken = false;
            }
        }
    }

    /** when you press F near customer */
    public void tryTakeOrder() {
        float d = player.getWorldLocation().distance(customer.getWorldLocation());
        if (!orderTaken && d < INTERACT_DIST) {
            orderTaken = true;
            // clear prompt
            hud.setHUD1("", new Vector3f(1,1,1), 0, 0);

            // pick random table point
            float angle = rnd.nextFloat() * (float)Math.PI * 2;
            float xOff  = (float)Math.cos(angle)*ORDER_RADIUS;
            float zOff  = (float)Math.sin(angle)*ORDER_RADIUS;
            Vector3f target = new Vector3f(
                customer.getWorldLocation().x()+xOff,
                customer.getWorldLocation().y(),
                customer.getWorldLocation().z()+zOff
            );

            moveAction = new MoveToWaypoint(
                customer, target, 0.5f, CUSTOMER_SPEED
            );
        }
    }

    /** called when you press F near the oven */
    public void tryStartCooking() {
        float d = player.getWorldLocation()
                        .distance(oven1.getWorldLocation());
        if (!pizzaStarted && d < INTERACT_DIST) {
            pizzaStarted   = true;
            cookingStart   = System.currentTimeMillis();
            // show cooking HUD
            hud.setHUD1("Cooking Pizza...", 
                        new Vector3f(1,1,1), 900, 700);
            hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            pizzaPrompt    = false;
        }
    }


    // ----------------------------
    // internal helpers
    private void handleOrderPrompt() {
        if (orderTaken) return;

        float d = player.getWorldLocation().distance(customer.getWorldLocation());
        if (d < INTERACT_DIST && !orderPromptVisible) {
            hud.setHUD1("Press F to take order",
                        new Vector3f(1,1,1), 900, 700);
            hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            orderPromptVisible = true;
        }
        else if (d >= INTERACT_DIST && orderPromptVisible) {
            hud.setHUD1("", new Vector3f(1,1,1), 0, 0);
            orderPromptVisible = false;
        }
    }

    private void handlePizzaLogic() {
        float d = player.getWorldLocation().distance(oven1.getWorldLocation());

        if (!pizzaStarted && d < INTERACT_DIST && !pizzaPrompt) {
            hud.setHUD1("Press F to bake pizza",
                        new Vector3f(1,1,1), 900, 700);
            hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            pizzaPrompt = true;
        }
        else if (!pizzaStarted && d >= INTERACT_DIST && pizzaPrompt) {
            hud.setHUD1("", new Vector3f(1,1,1), 0, 0);
            pizzaPrompt = false;
        }

        if (pizzaStarted && !pizzaReady) {
            long elapsed = System.currentTimeMillis() - cookingStart;
            if (elapsed >= 5_000) {
                hud.setHUD1("Pizza is Ready!",
                            new Vector3f(0,1,0), 900, 700);
                hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
                pizzaReady = true;
            }
        }
    }
}
