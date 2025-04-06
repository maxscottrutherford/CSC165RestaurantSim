package game;

import tage.*;
import tage.shapes.*;
import org.joml.*;

public class game extends VariableFrameRateGame
{
	private static Engine engine;
	private GameObject dol;
	private ObjShape dolS;
	private TextureImage doltx;
	private Light light1;
	private double lastFrameTime, currFrameTime, elapsTime;

	public game() { super(); }

	public static void main(String[] args)
	{
		game game = new game();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void loadShapes()
	{
		// dolS = new ImportedModel("dolphinHighPoly.obj");
	}

	@Override
	public void loadTextures()
	{
		// doltx = new TextureImage("Dolphin_HighPolyUV.png");
	}

	@Override
	public void buildObjects()
	{
		dol = new GameObject(GameObject.root(), dolS, doltx);
		Matrix4f translation = new Matrix4f().translation(0, 0, 0);
		Matrix4f scale = new Matrix4f().scaling(3.0f);
		dol.setLocalTranslation(translation);
		dol.setLocalScale(scale);
	}

	@Override
	public void initializeLights()
	{
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		engine.getSceneGraph().addLight(light1);
	}

	@Override
	public void initializeGame()
	{
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;

		engine.getRenderSystem().setWindowDimensions(1900, 1000);
		engine.getRenderSystem().getViewport("MAIN").getCamera().setLocation(new Vector3f(0, 0, 5));
	}

	@Override
	public void update()
	{
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

		dol.setLocalRotation(new Matrix4f().rotation((float) elapsTime, 0, 1, 0));
	}
}
