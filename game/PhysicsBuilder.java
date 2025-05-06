package game;

import tage.Engine;
import tage.GameObject;
import tage.SceneGraph;
import tage.physics.PhysicsObject;

import org.joml.Matrix4f;

public class PhysicsBuilder {
	public static void setupStaticPhysics(Engine engine) {
		SceneGraph sg = engine.getSceneGraph();

		float radius = 20f, h = 5f, t = 0.5f, L = 42f, m0 = 0f;
		Matrix4f m = new Matrix4f();
		float[] tmp = new float[16];

		m.translation(0, h/2, -14); m.get(tmp);
		sg.addPhysicsBox(m0, toDouble(tmp), new float[]{L,h,t});

		m.translation(0, h/2, 16); m.get(tmp);
		sg.addPhysicsBox(m0, toDouble(tmp), new float[]{L,h,t});

		m.translation(1, h/2, -1); m.get(tmp);
		sg.addPhysicsBox(m0, toDouble(tmp), new float[]{2f,h,24f});

		m.translation(21, h/2, 0); m.get(tmp);
		sg.addPhysicsBox(m0, toDouble(tmp), new float[]{t,h,L});

		m.translation(-22, h/2, -9); m.get(tmp);
		sg.addPhysicsBox(m0, toDouble(tmp), new float[]{t,h,11f});

		m.translation(-21, h/2, 10); m.get(tmp);
		sg.addPhysicsBox(m0, toDouble(tmp), new float[]{t,h,11f});
	}

	public static PhysicsObject setupPlayerPhysics(Engine engine, GameObject player) {
		float[] tmp = new float[16];
		player.getLocalTranslation().get(tmp);

		PhysicsObject playerPhys = engine.getSceneGraph().addPhysicsCylinder(1f, toDouble(tmp), 0.5f, 0.2f);
		playerPhys.setFriction(1f);
		playerPhys.setBounciness(0f);

		player.setPhysicsObject(playerPhys);
		return playerPhys;
	}

	private static double[] toDouble(float[] input) {
		double[] out = new double[input.length];
		for (int i = 0; i < input.length; i++)
			out[i] = input[i];
		return out;
	}
}
