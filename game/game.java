package game;

import tage.*;
import tage.shapes.*;

import java.awt.event.*;

import org.joml.*;

public class game extends VariableFrameRateGame
{
	private static Engine engine;

	//Camera 
	private Camera cam;

	//Gameobjects
	private GameObject terrain, bacon, bellPepper, cashRegister, ceiling, chair, counter, customer, cuttingBoard, floor, knife, mushroom, pantryShelf, pepperoni,
	pizza, player, poster, posterWide, saucecan, signBoard, sodaCup, sodaMachine, table, waLL;

	//Gameobject shapes
	private ObjShape terrainS, baconS, bellPepperS, cashRegisterS, ceilingS, chairS, counterS, customerS, cuttingBoardS, floorS, knifeS, mushroomS, pantryShelfS, pepperoniS,
	pizzaS, playerS, posterS, posterWideS, saucecanS, signBoardS, sodaCupS, sodaMachineS, tableS, waLLS;

	//Gameobject textures
	private TextureImage terrainTx, heightMap, baconTx, bellPepperTx, cashRegisterTx, ceilingTx, chairTx, counterTx, customerTx, cuttingBoardTx, floorTx, knifeTx, mushroomTx, pantryShelfTx, pepperoniTx,
	pizzaTx, playerTx, posterTx, posterWideTx, saucecanTx, signBoardTx, sodaCupTx, sodaMachineTx, tablev, waLLTx;

	private Light light1;
	private double lastFrameTime, currFrameTime, elapsTime;
	private int fluffyClouds; // skyboxes
 
	public game() { super(); }

	public static void main(String[] args)
	{
		game game = new game();
		engine = new Engine(game);
		game.initializeSystem();
		game.game_loop();
	}
	
	//Load the shapes for the game objects
	
	@Override
	public void loadShapes()
	{
		baconS = new ImportedModel("bacon.obj");
		bellPepperS = new ImportedModel("bellPepper.obj");
		cashRegisterS = new ImportedModel("cashRegister.obj");
		ceilingS = new ImportedModel("ceiling.obj");
		chairS = new ImportedModel("chair.obj");
		counterS = new ImportedModel("counter.obj");
		customerS = new ImportedModel("customer.obj");
		cuttingBoardS = new ImportedModel("cuttingBoard.obj");
		floorS = new ImportedModel("floor.obj");
		knifeS = new ImportedModel("knife.obj");
		mushroomS = new ImportedModel("mushroom.obj");
		pantryShelfS = new ImportedModel("pantryShelf.obj");
		pepperoniS = new ImportedModel("pepperoni.obj");
		pizzaS = new ImportedModel("pizza.obj");
		playerS = new ImportedModel("player.obj");
		posterS = new ImportedModel("poster.obj");
		posterWideS = new ImportedModel("posterWide.obj");
		saucecanS = new ImportedModel("sauceCan.obj");
		signBoardS = new ImportedModel("signboard.obj");
		sodaCupS = new ImportedModel("sodaCup.obj");
		sodaMachineS = new ImportedModel("sodaMachine.obj");
		tableS = new ImportedModel("table.obj");
		waLLS = new ImportedModel("wall.obj");
		terrainS = new TerrainPlane(100);
	}
	
	//Load the textures for the game objects

	@Override
	public void loadTextures()
	{
		baconTx = new TextureImage("bacon.png");
		bellPepperTx = new TextureImage("bellPepper.png");
		cashRegisterTx = new TextureImage("cashRegister.png");
		ceilingTx = new TextureImage("ceiling.png");
		chairTx = new TextureImage("chair.png");
		counterTx = new TextureImage("counters.png");
		customerTx = new TextureImage("customer.png");
		cuttingBoardTx = new TextureImage("cuttingBoard.png");
		floorTx = new TextureImage("floor.png");
		knifeTx = new TextureImage("knife.png");
		mushroomTx = new TextureImage("mushroom.png");
		pantryShelfTx = new TextureImage("pantryShelf.png");
		pepperoniTx = new TextureImage("pepperoni.png");
		pizzaTx = new TextureImage("peppPizza.png");
		playerTx = new TextureImage("player.png");
		posterTx = new TextureImage("poster.png");
		posterWideTx = new TextureImage("posterWide.png");
		saucecanTx = new TextureImage("sauceCan.png");
		signBoardTx = new TextureImage("signboard.png");
		sodaCupTx = new TextureImage("sodaCup.png");
		sodaMachineTx = new TextureImage("sodaMachine.png");
		tablev = new TextureImage("table.png");
		waLLTx = new TextureImage("wall.png");
		terrainTx = new TextureImage("terrain.jpg");
		heightMap = new TextureImage("wall.png");
	}

	@Override
	public void loadSkyBoxes()
	{ 
		fluffyClouds = (engine.getSceneGraph()).loadCubeMap("fluffyClouds");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(fluffyClouds);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void buildObjects()
	{
		Matrix4f scale = new Matrix4f().scaling(0.1f);
		Matrix4f playerScale = new Matrix4f().scaling(0.3f);

		player = new GameObject(GameObject.root(), playerS, playerTx);
		player.setLocalTranslation(new Matrix4f().translation(0, 1, 0));
		player.setLocalScale(playerScale);

		bacon = new GameObject(GameObject.root(), baconS, baconTx);
		bacon.setLocalTranslation(new Matrix4f().translation(2, 1, 4));
		bacon.setLocalScale(scale);

		mushroom = new GameObject(GameObject.root(), mushroomS, mushroomTx);
		mushroom.setLocalTranslation(new Matrix4f().translation(5, 1, 6));
		mushroom.setLocalScale(scale);

		terrain = new GameObject(GameObject.root(), terrainS, terrainTx);
		terrain.setHeightMap(heightMap);
		Matrix4f initialScale = (new Matrix4f()).scaling(40.0f, 0.0f, 40.0f);
        terrain.setLocalScale(initialScale);
		terrain.setLocalTranslation(initialScale);
        terrain.getRenderStates().setTiling(1);
        terrain.getRenderStates().setTileFactor(50);

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
		engine.getRenderSystem().getViewport("MAIN").getCamera().setLocation(new Vector3f(0, 1, 10));
	}

	@Override
	public void update()
	{
		//variables for following player
		Vector3f loc, fwd, up, right;

		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

		mushroom.setLocalRotation(new Matrix4f().rotation((float) elapsTime, 0, 1, 0));
		
		//updating camera to follow the player
		cam = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		loc = player.getWorldLocation();
		fwd = player.getWorldForwardVector();
		up = player.getWorldUpVector();
		right = player.getWorldRightVector();
		cam.setU(right);
		cam.setV(up);
		cam.setN(fwd);
		cam.setLocation(loc.add(up.mul(1f)).add(fwd.mul(-2.0f)));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Vector3f loc, fwd, right, newLoc;
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_W:
				fwd = player.getWorldForwardVector();
				loc = player.getWorldLocation();
				newLoc = loc.add(fwd.mul(0.2f));
				player.setLocalLocation(newLoc);
				break;
			case KeyEvent.VK_S:
				fwd = player.getWorldForwardVector();
				loc = player.getWorldLocation();
				newLoc = loc.add(fwd.mul(-0.2f));
				player.setLocalLocation(newLoc);
				break;
			case KeyEvent.VK_A:
				right = player.getWorldRightVector();
				loc = player.getWorldLocation();
				newLoc = loc.add(right.mul(-0.2f));
				player.setLocalLocation(newLoc);
				break;
			case KeyEvent.VK_D:
				right = player.getWorldRightVector();
				loc = player.getWorldLocation();
				newLoc = loc.add(right.mul(0.2f));
				player.setLocalLocation(newLoc);
				break;
		}
		super.keyPressed(e);
	}
}
