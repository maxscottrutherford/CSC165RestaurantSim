package game;

import tage.ai.behaviortrees.BTCondition;
import tage.ai.behaviortrees.BTStatus;
import org.joml.Vector3f;
import tage.GameObject;

public class WaitUntilPlayerNear extends BTCondition {
    private GameObject npc, player;
    private float triggerDistance;

    public WaitUntilPlayerNear(GameObject npc, GameObject player, float dist, boolean toNegate) {
        super(toNegate);
        this.npc = npc;
        this.player = player;
        this.triggerDistance = dist;
    }

    @Override
    protected boolean check() {
        Vector3f npcPos    = npc.getWorldLocation();
        Vector3f playerPos = player.getWorldLocation();
        return npcPos.distance(playerPos) < triggerDistance;
    }
}
