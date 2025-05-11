package game;

import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.jogamp.opengl.util.gl2.GLUT;

import tage.Engine;
import tage.GameObject;
import tage.HUDmanager;
import tage.ObjShape;
import tage.TextureImage;
import tage.ai.behaviortrees.BTStatus;
import tage.audio.Sound;

public class GameLogic {
    private static final float INTERACT_DIST    = 5.0f;
    private static final float CUSTOMER_SPEED   = 4.0f;
    private static final float ORDER_RADIUS     = 20.0f;
    private long pizzaReadyTime = 0;
    private GameObject speaker;
    private Sound insideSound;
    private boolean isMusicPlaying = false;
    private boolean musicPromptVisible = false;
    private InventoryManager inventory;
    private ObjShape pizzaS;
    private TextureImage pizzaTx;
    private Engine engine;


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

    public GameLogic(GameObject player, GameObject customer, GameObject oven1,
                 GameObject speaker, Sound insideSound, InventoryManager inventory,
                 Engine engine, ObjShape pizzaS, TextureImage pizzaTx) {
        this.player = player;
        this.customer = customer;
        this.oven1 = oven1;
        this.speaker = speaker;
        this.insideSound = insideSound;
        this.inventory = inventory;
        this.hud = engine.getHUDmanager();
        this.pizzaS = pizzaS;
        this.pizzaTx = pizzaTx;
        this.engine = engine;

    }
    public void tryToggleMusic() {
        float d = player.getWorldLocation().distance(speaker.getWorldLocation());
        
        if (d < INTERACT_DIST) {
            if (!isMusicPlaying) {
                insideSound.play();
                hud.setHUD2("Speaker Music: Playing", new Vector3f(0, 1, 0), 900, 650);
            } else {
                insideSound.stop();
                hud.setHUD2("Speaker Music: Paused", new Vector3f(1, 1, 0), 900, 650);
            }
            hud.setHUD2font(GLUT.BITMAP_HELVETICA_18);
            isMusicPlaying = !isMusicPlaying;
        }
    }
    
    /** Called every frame from game.update(elapsTime) */
    public void update(float elapsedMillis) {
        handlePizzaLogic();
        handleOrderPrompt();
        handleMusicPrompt();


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

    private void handleMusicPrompt() {
        float d = player.getWorldLocation().distance(speaker.getWorldLocation());
        if (d < INTERACT_DIST && !musicPromptVisible) {
            hud.setHUD2("Press F to turn music ON/OFF", new Vector3f(1, 1, 1), 900, 650);
            hud.setHUD2font(GLUT.BITMAP_HELVETICA_18);
            musicPromptVisible = true;
        } else if (d >= INTERACT_DIST && musicPromptVisible) {
            hud.setHUD2("", new Vector3f(), 0, 0);
            musicPromptVisible = false;
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
        float d = player.getWorldLocation().distance(oven1.getWorldLocation());

        if (!pizzaStarted && d < INTERACT_DIST) {
            // Define required ingredients
            List<String> required = List.of("cheese", "sauce");

            if (!inventory.hasIngredients(required)) {
                hud.setHUD5("Missing ingredients!", new Vector3f(1, 0, 0), 900, 700);
                hud.setHUD5font(GLUT.BITMAP_TIMES_ROMAN_24);
                return;
            }

            // Consume ingredients and start cooking
            inventory.useIngredients(required);
            pizzaStarted = true;
            cookingStart = System.currentTimeMillis();

            hud.setHUD5("Cooking Pizza...", new Vector3f(1,1,1), 900, 700);
            hud.setHUD5font(GLUT.BITMAP_TIMES_ROMAN_24);
            pizzaPrompt = false;
        }
    }



    // ----------------------------
    // internal helpers
    private void handleOrderPrompt() {
        if (orderTaken) return;

        float d = player.getWorldLocation().distance(customer.getWorldLocation());
        if (d < INTERACT_DIST && !orderPromptVisible) {
            hud.setHUD5("Press F to take order",
                        new Vector3f(1,1,1), 900, 700);
            hud.setHUD5font(GLUT.BITMAP_TIMES_ROMAN_24);
            orderPromptVisible = true;
        }
        else if (d >= INTERACT_DIST && orderPromptVisible) {
            hud.setHUD5("", new Vector3f(1,1,1), 0, 0);
            orderPromptVisible = false;
        }
    }

    private void handlePizzaLogic() {
        float d = player.getWorldLocation().distance(oven1.getWorldLocation());

        if (!pizzaStarted && d < INTERACT_DIST && !pizzaPrompt) {
            hud.setHUD5("Press F to bake pizza",
                        new Vector3f(1,1,1), 900, 700);
            hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            pizzaPrompt = true;
        }
        else if (!pizzaStarted && d >= INTERACT_DIST && pizzaPrompt) {
            hud.setHUD5("", new Vector3f(1,1,1), 0, 0);
            pizzaPrompt = false;
        }

        if (pizzaStarted && !pizzaReady) {
            long elapsed = System.currentTimeMillis() - cookingStart;
             if (elapsed >= 5_000) {
        // existing HUD updates...
        pizzaReady = true;
        pizzaStarted = false;
        pizzaPrompt = false;

        // 🎯 Create pizza GameObject and attach to player hand
        GameObject pizzaInHand = new GameObject(player, pizzaS, pizzaTx);
        pizzaInHand.setLocalTranslation(new Matrix4f().translation(0.5f, 3.5f, 0.8f)); // offset in hand
        pizzaInHand.setLocalScale(new Matrix4f().scaling(0.8f)); 
        pizzaInHand.propagateTranslation(true);
        pizzaInHand.propagateRotation(true);
        pizzaInHand.applyParentRotationToPosition(true);

    }
        }

        if (pizzaReady && System.currentTimeMillis() - pizzaReadyTime > 3000) {
            hud.setHUD5("", new Vector3f(), 0, 0);
            pizzaReady = false;
        }
        
    }
}
