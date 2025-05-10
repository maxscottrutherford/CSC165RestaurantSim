package game;

import org.joml.Vector3f;
import tage.GameObject;
import tage.ai.behaviortrees.*;

public class CustomerAIController {
    private BehaviorTree tree;
    private GameObject npc, player;

    public CustomerAIController(GameObject npc, GameObject player) {
        this.npc    = npc;
        this.player = player;
        setupBehaviorTree();
    }

    private void setupBehaviorTree() {
        tree = new BehaviorTree(BTCompositeType.SEQUENCE);
        tree.insertAtRoot(new BTSequence(0));

        // 1) walk to the counter
        tree.insert(0, new MoveToWaypoint(
            npc,
            new Vector3f(-3f, 1f, 0f), // counter location
            0.5f,  // threshold
            6f     // speed
        ));
        // 2) wait until player comes within 3 units
        tree.insert(0, new WaitUntilPlayerNear(npc, player, 10f, false));
        // 3) then walk to a table inside
        tree.insert(0, new MoveToWaypoint(
            npc,
            new Vector3f(-6f, 1f, 5f), // table location
            0.5f,
            4f
        ));
        // 4) wait 5 seconds at the table
        tree.insert(0, new WaitDuration(5f));
    }

    public void update(float elapsedMillis) {
        tree.update(elapsedMillis);
    }
}
