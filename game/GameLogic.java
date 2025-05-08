package game;

import tage.*;
import org.joml.*;

import com.jogamp.opengl.util.gl2.GLUT;

public class GameLogic {
    private GameObject player;
    private GameObject oven1;
    private HUDmanager hud;
    private boolean pizzaStarted = false;
    private boolean pizzaReady = false;
    private long cookingStartTime = 0;
    private boolean promptVisible = false;

    public GameLogic(GameObject player, GameObject oven1, Engine engine) {
        this.player = player;
        this.oven1 = oven1;
        this.hud = engine.getHUDmanager();
    }

    public void update() {
        float distance = player.getWorldLocation().distance(oven1.getWorldLocation());

        if (!pizzaStarted && distance < 5.0f && !promptVisible) {
            hud.setHUD1("Press F to bake pizza", new Vector3f(1, 1, 1), 900, 700);
            hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            promptVisible = true;
        }

        if (!pizzaStarted && distance >= 5.0f && promptVisible) {
            hud.setHUD1("", new Vector3f(1, 1, 1), 0, 0);
            promptVisible = false;
        }

        if (pizzaStarted && !pizzaReady) {
            long elapsed = System.currentTimeMillis() - cookingStartTime;
            
            if (elapsed >= 5_000) {
                hud.setHUD1("Pizza is Ready!", new Vector3f(0, 1, 0), 900, 700);
                pizzaReady = true;
                hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            }
        }
    }

    public void tryStartCooking() {
        if (!pizzaStarted && player.getWorldLocation().distance(oven1.getWorldLocation()) < 5.0f) {
            hud.setHUD1("Cooking Pizza...", new Vector3f(1, 1, 1), 900, 700);
            cookingStartTime = System.currentTimeMillis();
            hud.setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
            pizzaStarted = true;
            promptVisible = false;
        }
    }
}
