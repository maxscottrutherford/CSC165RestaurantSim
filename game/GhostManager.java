package game;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.UUID;
import tage.GameObject;
import tage.TextureImage;
import tage.ObjShape;
import org.joml.Vector3f;
import org.joml.Matrix4f;

public class GhostManager {
    private game gameInstance;
    private Vector<GhostAvatar> ghostAvatars = new Vector<>();

    public GhostManager(game g) {
        this.gameInstance = g;
    }

    public void createGhost(UUID id, Vector3f pos) throws IOException {
        ObjShape s = gameInstance.getGhostShape();
        TextureImage t = gameInstance.getGhostTexture();
        //to prevent duplicate ghosts
        if (findAvatar(id) != null) {
            System.out.println("Ghost already exists for: " + id);
            return;
        }        
        GhostAvatar ghost = new GhostAvatar(id, s, t, pos);
        ghost.setLocalScale(new Matrix4f().scaling(0.25f));
        ghostAvatars.add(ghost);
    }

    public void removeGhostAvatar(UUID id) {
        GhostAvatar g = findAvatar(id);
        if (g != null) {
            gameInstance.getEngine().getSceneGraph().removeGameObject(g);
            ghostAvatars.remove(g);
        } else {
            System.out.println("Ghost not found to remove: " + id);
        }
    }

    public void updateGhostAvatar(UUID id, Vector3f pos) {
        GhostAvatar g = findAvatar(id);
        if (g != null) {
            g.setPosition(pos);
        } else {
            System.out.println("Ghost not found to update: " + id);
        }
    }

    private GhostAvatar findAvatar(UUID id) {
        for (GhostAvatar g : ghostAvatars) {
            if (g.getID().equals(id)) return g;
        }
        return null;
    }
}

