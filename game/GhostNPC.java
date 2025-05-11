package game;

import tage.GameObject;
import tage.TextureImage;
import tage.ObjShape;
import java.util.UUID;
import org.joml.Vector3f;
import org.joml.Matrix4f;

/**
 * A networked ghost representation of an NPC.
 * Mirrors GhostAvatar but used for non-player characters.
 */
public class GhostNPC extends GameObject {
    private UUID id;

    /**
     * Constructs a new GhostNPC.
     *
     * @param id       Unique identifier for this NPC
     * @param shape    Mesh shape for rendering
     * @param texture  Texture to apply to the mesh
     * @param position Initial world position
     */
    public GhostNPC(UUID id, ObjShape shape, TextureImage texture, Vector3f position) {
        super(GameObject.root(), shape, texture);
        this.id = id;
        setPosition(position);
    }

    /**
     * Returns the unique ID for this ghost NPC.
     */
    public UUID getID() {
        return id;
    }

    /**
     * Sets the world-space position of this ghost NPC.
     */
    public void setPosition(Vector3f position) {
        setLocalTranslation(new Matrix4f().translation(position));
    }

    /**
     * Gets the current world-space location of this ghost NPC.
     */
    public Vector3f getPosition() {
        return getWorldLocation();
    }
}
