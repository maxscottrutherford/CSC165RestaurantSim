package game;

import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class WaitDuration extends BTAction {
    private float waitTime, elapsed = 0f;

    public WaitDuration(float seconds) {
        super();
        this.waitTime = seconds;
    }

    @Override
    protected void onInitialize() {
        elapsed = 0f;
    }

    @Override
    protected BTStatus update(float elapsedMillis) {
        elapsed += elapsedMillis / 1000f;
        return (elapsed >= waitTime)
            ? BTStatus.BH_SUCCESS
            : BTStatus.BH_RUNNING;
    }
}
