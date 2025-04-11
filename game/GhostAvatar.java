package game;

import tage.GameObject;
import tage.TextureImage;
import tage.ObjShape;
import java.util.UUID;
import org.joml.Vector3f;

public class GhostAvatar extends GameObject {
    private UUID id;

    public GhostAvatar(UUID id, ObjShape s, TextureImage t, Vector3f p) {
        super(GameObject.root(), s, t);
        this.id = id;
        setPosition(p);
    }

    public UUID getID() {
        return id;
    }

    public void setPosition(Vector3f p) {
        setLocalTranslation(new org.joml.Matrix4f().translation(p));
    }
    

    public Vector3f getPosition() {
        return getWorldLocation();
    }
}
