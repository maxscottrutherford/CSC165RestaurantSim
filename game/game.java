package game;

import tage.*;
import tage.audio.AudioResource;
import tage.audio.AudioResourceType;
import tage.audio.IAudioManager;
import tage.audio.Sound;
import tage.audio.SoundType;
import tage.networking.IGameConnection.ProtocolType;
import tage.physics.PhysicsEngine;
import tage.physics.PhysicsObject;
import tage.shapes.*;

import java.awt.event.*;
import java.net.InetAddress;

import org.joml.*;
import org.joml.Math;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.gl2.GLUT;

public class game extends VariableFrameRateGame
{
	private static Engine engine;

	//Physics Engine
	private PhysicsEngine physicsEngine;

	//Camera 
	private Camera cam;

	//AI Controller
	private CustomerAIController customerAI;
	private GameLogic gameLogic;

	//Networking objects and related functions
	private GhostManager ghostManager;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;
	private String serverAddress = "127.0.0.1";
	private int serverPort = 6000;
	private boolean upPressed, downPressed, leftPressed, rightPressed;
	private float   stepTimer      = 0f;
	private final float STEP_INTERVAL = 0.5f;  // seconds between steps
	private boolean menuActive = true;
	private GameObject menuBG;
	private boolean showHUD = false;
	
	


	public GhostManager getGhostManager() {
		return ghostManager;
	}

	public void setIsConnected(boolean val) {
		isClientConnected = val;
	}

	public GameObject getPlayer() {
		return player;
	}

	public ObjShape getGhostShape() {
		return playerS;
	}

	public TextureImage getGhostTexture() {
		return playerTx;
	}

	public Engine getEngine() {
		return engine;
	}
	


	//Gameobjects
	private GameObject terrain, oven, restaurant, bacon, bellPepper, cashRegister, ceiling, chair, counter, customer, cuttingBoard, floor, knife, mushroom, pantryShelf, pepperoni,pantryShelf1,
	pizza, player, poster, posterWide, saucecan, signBoard, sodaCup, sodaMachine, table,table1, waLL, oven1;

	//Gameobject shapes
	private ObjShape terrainS, baconS,ovenS, restaurantS, bellPepperS, cashRegisterS, ceilingS, chairS, counterS, customerS, cuttingBoardS, floorS, knifeS, mushroomS, pantryShelfS, pepperoniS,
	pizzaS, playerS, posterS, posterWideS, saucecanS, signBoardS, sodaCupS, sodaMachineS, tableS, oven1S, waLLS;

	//Gameobject textures
	private TextureImage terrainTx, hills, ovenTx, baconTx, restaurantTx, bellPepperTx, cashRegisterTx, ceilingTx, chairTx, counterTx, customerTx, cuttingBoardTx, floorTx, knifeTx, mushroomTx, pantryShelfTx, pepperoniTx,
	pizzaTx, playerTx, posterTx, posterWideTx, saucecanTx, signBoardTx, sodaCupTx, sodaMachineTx, tablev, oven1tx, waLLTx;

	private Light light1;
	private double lastFrameTime, currFrameTime, elapsTime;
	private int fluffyClouds; // skyboxes
	private Sound insideSound, outsideSound, footstepSound;
	private IAudioManager audioMgr;
	private float restaurantRadius = 20f;
	// track which ambience is currently active:
    private enum Ambience { NONE, INSIDE, OUTSIDE }
    private Ambience currentAmbience = Ambience.NONE;
	private PhysicsObject playerPhys;
	private float moveForce = 2f;
	private int lastMouseX = -1;
	private float mouseSensitivity = 0.2f;
	private PlayerController playerController;
	private boolean hudViewportVisible = false;
	private Viewport hudViewport;
	
 
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
		restaurantS = new ImportedModel("restaurant.obj");
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
		terrainS = new TerrainPlane(1000);
		ovenS = new ImportedModel("cube.obj");
		oven1S = new ImportedModel("oven.obj");
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
		hills = new TextureImage("hills.png");
		restaurantTx = new TextureImage("Sam - Texture.png");
		ovenTx = new TextureImage("cube.png");
		TextureImage menuBgTx = new TextureImage("menu.png");
		oven1tx = new TextureImage("oven.png");
		//load the textures for the game objects
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
		Matrix4f scale = new Matrix4f().scaling(1f);
		Matrix4f playerScale = new Matrix4f().scaling(1.8f);

		player = new GameObject(GameObject.root(), playerS, playerTx);
		player.setLocalTranslation(new Matrix4f().translation(-50, 1, 10));
		player.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));
		player.setLocalScale(playerScale);

		customer = new GameObject(GameObject.root(), customerS, customerTx);
		customer.setLocalTranslation(new Matrix4f().translation(-70, 0, 0));
		customer.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));
		customer.setLocalScale(playerScale);

		restaurant = new GameObject(GameObject.root(), restaurantS, restaurantTx);
		restaurant.setLocalTranslation(new Matrix4f().translation(0, 0, 0));
		restaurant.setLocalRotation(new Matrix4f().rotationX((float) Math.toRadians(-180)));
		restaurant.setLocalScale(scale);

		table = new GameObject(GameObject.root(), tableS, tablev);
		table.setLocalTranslation(new Matrix4f().translation(-13, -1, 10));
		table.setLocalScale(new Matrix4f().scaling(2.5f));

		table1 = new GameObject(GameObject.root(), tableS, tablev);
		table1.setLocalTranslation(new Matrix4f().translation(-13, -1, -8));
		table1.setLocalScale(new Matrix4f().scaling(2.5f));

		chair = new GameObject(GameObject.root(), chairS, chairTx);
		chair.setLocalTranslation(new Matrix4f().translation(-9, 1.5f, -8));
		chair.setLocalScale(new Matrix4f().scaling(3.5f));

		chair = new GameObject(GameObject.root(), chairS, chairTx);
		chair.setLocalTranslation(new Matrix4f().translation(-13, 1.5f, -4f));
		chair.setLocalScale(new Matrix4f().scaling(3.5f));
		chair.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		chair = new GameObject(GameObject.root(), chairS, chairTx);
		chair.setLocalTranslation(new Matrix4f().translation(-13, 1.5f, 6f));
		chair.setLocalScale(new Matrix4f().scaling(3.5f));
		chair.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));

		chair = new GameObject(GameObject.root(), chairS, chairTx);
		chair.setLocalTranslation(new Matrix4f().translation(-9, 1.5f, 10f));
		chair.setLocalScale(new Matrix4f().scaling(3.5f));

		pantryShelf = new GameObject(GameObject.root(), pantryShelfS, pantryShelfTx);
		pantryShelf.setLocalTranslation(new Matrix4f().translation(14, 3.8f, 15.7f));
		pantryShelf.setLocalScale(new Matrix4f().scaling(3.5f));
		pantryShelf.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));

		pantryShelf1 = new GameObject(GameObject.root(), pantryShelfS, pantryShelfTx);
		pantryShelf1.setLocalTranslation(new Matrix4f().translation(16.5f, 3.8f, -13f));
		pantryShelf1.setLocalScale(new Matrix4f().scaling(3.5f));
		pantryShelf1.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		cashRegister = new GameObject(GameObject.root(), cashRegisterS, cashRegisterTx);
		cashRegister.setLocalTranslation(new Matrix4f().translation(1, 2.3f, 0.5f));
		cashRegister.setLocalScale(new Matrix4f().scaling(2.5f));
		cashRegister.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		cashRegister = new GameObject(GameObject.root(), cashRegisterS, cashRegisterTx);
		cashRegister.setLocalTranslation(new Matrix4f().translation(1, 2.3f, 6.5f));
		cashRegister.setLocalScale(new Matrix4f().scaling(2.5f));
		cashRegister.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		oven = new GameObject(GameObject.root(), ovenS, ovenTx);
		oven.setLocalTranslation(new Matrix4f().translation(0f, 0f, -10000f));
		oven.setLocalRotation(new Matrix4f().identity());
		oven.setLocalScale(new Matrix4f().scaling(5.2f));
	
		oven.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));

		oven1 = new GameObject(GameObject.root(), oven1S, oven1tx);
		oven1.setLocalTranslation(new Matrix4f().translation(6.5f, 0f, -13f));
		oven1.setLocalScale(new Matrix4f().scaling(1.5f));
		oven1.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));

		// build terrain object
		terrain = new GameObject(GameObject.root(), terrainS);
		terrain.setLocalTranslation(new Matrix4f().translation(0f,0f,0f));
		Matrix4f initialScale = (new Matrix4f()).scaling(100.0f, 1.0f, 100.0f);
		terrain.setLocalScale(initialScale);
		terrain.setIsTerrain(true);
		terrain.setTextureImage(terrainTx);
	
		// set tiling for terrain texture
		terrain.getRenderStates().setTiling(1);
		terrain.getRenderStates().setTileFactor(100);

		float[] tmp = new float[16];
		Matrix4f groundMat = new Matrix4f().translation(0f, 0f, 0f); // y=0
		groundMat.get(tmp);
		engine.getSceneGraph().addPhysicsBox(
			0f,                           // mass = 0 = static
			toDouble(tmp),               // transform
			new float[]{1000f, 1f, 1000f}  // size of terrain box (x,y,z)
);

	}

	@Override
	public void initializeLights()
	{
		Light.setGlobalAmbient(0.5f, 0.5f, 0.5f);
		light1 = new Light();
		light1.setLocation(new Vector3f(5.0f, 4.0f, 2.0f));
		engine.getSceneGraph().addLight(light1);
	}

	private void setupNetworking() {
	isClientConnected = false;
	try {
		protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, ProtocolType.UDP, this);
	} catch (Exception e) { e.printStackTrace(); }

	if (protClient != null) {
		protClient.sendJoinMessage();
	} else {
		System.out.println("Failed to create ProtocolClient");
	}
}

	private void processNetworking(double elapsTime) {
		if (protClient != null) {
			protClient.processPackets();
		}
	}

	

	@Override
	public void loadSounds() {
		audioMgr = engine.getAudioManager();

		// inside ambience
		AudioResource inRes = audioMgr.createAudioResource(
			"b.wav", AudioResourceType.AUDIO_STREAM);
		insideSound = new Sound(inRes, SoundType.SOUND_MUSIC, 60, true);
		insideSound.initialize(audioMgr);

	
		// outside ambience
		AudioResource outRes = audioMgr.createAudioResource(
			"a.wav", AudioResourceType.AUDIO_STREAM);
		outsideSound = new Sound(outRes, SoundType.SOUND_MUSIC, 30, true);
		outsideSound.initialize(audioMgr);

		AudioResource stepRes = audioMgr.createAudioResource(
			"footsteps.wav", AudioResourceType.AUDIO_SAMPLE);
		footstepSound = new Sound(stepRes, SoundType.SOUND_EFFECT, 80, true);
		footstepSound.initialize(audioMgr);
		footstepSound.setMinDistance(1f);
		footstepSound.setMaxDistance(10f);
		footstepSound.setRollOff(1f);

	
	}

	private void setEarParameters() {
		Camera cam = engine.getRenderSystem()
		.getViewport("MAIN")
		.getCamera();

		audioMgr.getEar().setLocation(cam.getLocation());


		audioMgr.getEar().setOrientation(
		cam.getN(), new Vector3f(0f, 1f, 0f));
	}



	private double[] toDouble(float[] f) {
		double[] d = new double[f.length];
		for (int i = 0; i < f.length; i++) d[i] = (double) f[i];
		return d;
	}
	
	@Override
	public void initializeGame()
	{	
		engine.disableGraphicsWorldRender();
		engine.disablePhysicsWorldRender();
		//engine.enablePhysicsWorldRender();

	
		engine.getRenderSystem().getGLCanvas().addMouseMotionListener(this);
		physicsEngine = engine.getSceneGraph().getPhysicsEngine();
		physicsEngine.setGravity(new float[]{0f, -9.8f, 0f});

		//enable customer AI
		customerAI = new CustomerAIController(customer, player);

		// Enable physics rendering
		engine.enableGraphicsWorldRender();

		// Setup physics objects
		PhysicsBuilder.setupStaticPhysics(engine);
		PhysicsBuilder.setupStaticBox(engine, oven1, new float[]{3.5f, 2f, 2.5f});
		PhysicsBuilder.setupStaticBox(engine, table, new float[]{5.5f, 5f, 5.5f});
		PhysicsBuilder.setupStaticBox(engine, table1, new float[]{5.5f, 5f, 5.5f});
		PhysicsBuilder.setupStaticBox(engine, pantryShelf, new float[]{5.5f, 6f, 3.5f});
		PhysicsBuilder.setupStaticBox(engine, pantryShelf1, new float[]{5.5f, 6f, 3.5f});

		// Setup player physics
		playerPhys = PhysicsBuilder.setupPlayerPhysics(engine, player);

		// Setup player movement controller
		playerController = new PlayerController(player, playerPhys, engine);

		ghostManager = new GhostManager(this);
		setupNetworking();
		lastFrameTime = System.currentTimeMillis();
		currFrameTime = System.currentTimeMillis();
		elapsTime = 0.0;
		engine.getRenderSystem().setWindowDimensions(1900, 1000);
		engine.getRenderSystem().getViewport("MAIN").getCamera().setLocation(new Vector3f(1, 3, 5));
		outsideSound.stop();
		outsideSound.play();
        currentAmbience = Ambience.OUTSIDE;
		gameLogic = new GameLogic(player, oven1, engine);


		
	}

	private void updateAmbience() {
        Vector3f p = player.getWorldLocation();
        boolean inside = p.distance(new Vector3f(0f,0f,0f))
                          < restaurantRadius;

						  if (inside && currentAmbience != Ambience.INSIDE) {
							// entering
							outsideSound.stop();       
							insideSound.stop();        
							insideSound.play();        
							currentAmbience = Ambience.INSIDE;
						}
						else if (!inside && currentAmbience != Ambience.OUTSIDE) {
							// exiting
							insideSound.stop();
							outsideSound.stop();
							outsideSound.play();
							currentAmbience = Ambience.OUTSIDE;
						}
    }

	private float[] toFloatArray(double[] arr)
		{ if (arr == null) return null;
		int n = arr.length;
		float[] ret = new float[n];
		for (int i = 0; i < n; i++)
		{ ret[i] = (float)arr[i];
		}
		return ret;
		}

	@Override
	public void update() {
		
		//System.out.println(player.getWorldLocation().x() + " " + player.getWorldLocation().y() + " " + player.getWorldLocation().z());
		Vector3f loc, fwd, up, right;
		lastFrameTime = currFrameTime;
		currFrameTime = System.currentTimeMillis();
		elapsTime += (currFrameTime - lastFrameTime) / 1000.0;

		loc = player.getWorldLocation();
		float height = terrain.getHeight(loc.x(), loc.z());
		player.setLocalLocation(new Vector3f(loc.x(), height, loc.z()));

		cam = engine.getRenderSystem().getViewport("MAIN").getCamera();
		loc = player.getWorldLocation();
		fwd = player.getWorldForwardVector();
		up = player.getWorldUpVector();
		right = player.getWorldRightVector();

		cam.setU(right);
		cam.setV(up);
		cam.setN(fwd);
		cam.setLocation(loc.add(up.mul(6f)).add(fwd.mul(-10.0f)));

		footstepSound.setLocation(player.getWorldLocation());
		processNetworking(elapsTime);

		boolean moving = upPressed || downPressed || leftPressed || rightPressed;
		if (moving) {
			if (!footstepSound.getIsPlaying()) footstepSound.play();
		} else {
			if (footstepSound.getIsPlaying()) footstepSound.stop();
		}

		updateAmbience();
		setEarParameters();
		playerController.updateMovement((float) elapsTime);
		physicsEngine.update((float) elapsTime);

		double[] tf = playerPhys.getTransform();
		player.setLocalTranslation(new Matrix4f().translation(
			(float) tf[12], (float) tf[13], (float) tf[14]
		));

		Matrix4f mat = new Matrix4f();
		for (GameObject go : engine.getSceneGraph().getGameObjects()) {
			if (go.getPhysicsObject() != null) {
				mat.set(toFloatArray(go.getPhysicsObject().getTransform()));
			}
		}

		if (customerAI != null) {
			customerAI.update((float) elapsTime);
		}
		if (gameLogic != null) gameLogic.update();

	}

	@Override
public void keyPressed(KeyEvent e) {
	if (menuActive && e.getKeyCode() == KeyEvent.VK_ENTER) {
		engine.getSceneGraph().removeGameObject(menuBG);
		menuBG = null;
		menuActive = false;

		// Clear HUD
		engine.getHUDmanager().setHUD1("", new Vector3f(), 0, 0);
		engine.getHUDmanager().setHUD2("", new Vector3f(), 0, 0);
		engine.enableGraphicsWorldRender();
		engine.enablePhysicsWorldRender();
		return;
	}

	if (e.getKeyCode() == KeyEvent.VK_L) {
	hudViewportVisible = !hudViewportVisible;

	if (hudViewportVisible) {
		// Create HUD viewport centered on screen
		hudViewport = engine.getRenderSystem().addViewport("HUD", 0.25f, 0.2f, 0.5f, 0.5f);
		hudViewport.setHasBorder(true);
		hudViewport.setBorderColor(1f, 1f, 1f);
		hudViewport.setBorderWidth(2);

		// Set HUD camera to view oven
		Camera hudCam = hudViewport.getCamera();
		hudCam.setLocation(new Vector3f(0f, 13f, -10000f));
		hudCam.lookAt(oven);
	} else {
		hudViewport = engine.getRenderSystem().addViewport("HUD", 0.0f, 0.0f, 0.0f, 0.0f);
		
	}
}
if (e.getKeyCode() == KeyEvent.VK_F && gameLogic != null) {
	gameLogic.tryStartCooking();
}

}


	@Override
public void keyTyped(KeyEvent e) {
    // Not used
}
}
