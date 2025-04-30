package game;

import tage.ai.behaviortrees.*;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import tage.GameObject;

public class MoveToWaypoint extends BTAction {
    private GameObject customer;
    private Vector3f   target;
    private float      threshold, speed;

    public MoveToWaypoint(GameObject customer, Vector3f wp, float thresh, float speed) {
        super();
        this.customer      = customer;
        this.target    = new Vector3f(wp);
        this.threshold = thresh;
        this.speed     = speed;
    }

    @Override
    public BTStatus update(float elapsedMillis) {
        Vector3f pos = customer.getWorldLocation();
        Vector3f dir = new Vector3f(target).sub(pos);
        float   dist = dir.length();
        dir.normalize();

        if (dist > threshold) {
            float step = speed * (elapsedMillis / 1000f);
            pos.add(dir.mul(step));
            customer.setLocalTranslation(
                new Matrix4f().translation(pos.x, pos.y, pos.z)
            );
            return BTStatus.BH_RUNNING;
        }
        return BTStatus.BH_SUCCESS;
    }
}
