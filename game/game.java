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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	private ThiefBehaviorController thiefController;
	private CustomerBehaviorController customerController;
	private GameLogic gameLogic;

	//Customer Order Values
	private String currentOrder = null;
	private long currentOrderStart = 0L;
	private static final long ORDER_DISPLAY_MS = 1000L;

	//Score/CashManager
	private CashManager cashManager;

	//Networking objects and related functions
	private GhostManager ghostManager;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;
	private String serverAddress = loadServerIP();
	private int serverPort = 6000;
	private boolean upPressed, downPressed, leftPressed, rightPressed;
	private float   stepTimer      = 0f;
	private final float STEP_INTERVAL = 0.5f;  // seconds between steps
	private boolean menuActive = true;
	private GameObject menuBG;
	private boolean showHUD = false;
	private AnimatedShape playerS;
	private InventoryManager inventory;
	private long hudMessageStartTime = 0;
	private final long hudMessageDuration = 3000; // 3 seconds
	private boolean showTimedHUDMessage = false;
	private boolean zoomedToPC = false;


	private String loadServerIP() {
		try {
			java.util.Scanner sc = new java.util.Scanner(new java.io.File("ipconfig.txt"));
			if (sc.hasNextLine())
				return sc.nextLine().trim();
		} catch (Exception e) {
			System.out.println("Could not read IP from file, using default 127.0.0.1");
		}
		return "127.0.0.1";
	}


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
	private GameObject terrain, oven, restaurant, bacon, bellPepper, cashRegister, cashRegister1, ceiling, chair, counter, customer, cuttingBoard, floor, knife, mushroom, pantryShelf, pepperoni,pantryShelf1,
	pizza, player, poster, posterWide, saucecan, signBoard, sodaCup, sodaMachine, table,table1, thief, waLL, oven1, speaker, car, pc, light, light1, light2 ;
;

	//Gameobject shapes
	private ObjShape terrainS, baconS,ovenS, restaurantS, bellPepperS, cashRegisterS, ceilingS, chairS, counterS, customerS, cuttingBoardS, floorS, knifeS, mushroomS, pantryShelfS, pepperoniS,
	pizzaS, posterS, posterWideS, saucecanS, signBoardS, sodaCupS, sodaMachineS, tableS, oven1S, waLLS, speakerS, carS, pcS, lightS;

	//Gameobject textures
	private TextureImage terrainTx, hills, ovenTx, baconTx, restaurantTx, bellPepperTx, cashRegisterTx, ceilingTx, chairTx, counterTx, customerTx, cuttingBoardTx, floorTx, knifeTx, mushroomTx, pantryShelfTx, pepperoniTx,
	pizzaTx, playerTx, posterTx, posterWideTx, saucecanTx, signBoardTx, sodaCupTx, sodaMachineTx, tablev, thiefTx, oven1tx, waLLTx, speakerTx, carTx, pcTx, pc1Tx, lightTx, player1Tx, theifTx;

	private double lastFrameTime, currFrameTime, elapsTime;
	private int sunsetSkybox; // skyboxes
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
	private boolean nearPC = false;
	private boolean orderingActive = false;
	private Vector3f savedCamPos, savedCamU, savedCamV, savedCamN;
	private String[] menuItems = {"1. Mushroom - $5", "2. Pepperoni - $4", "3. Cheese - $3", "4. Sauce - $2"};
	private int[] menuPrices = {5, 4, 3, 2};
	private long exitMenuStartTime = 0;
	private boolean isExitMenuScheduled = false;
	private boolean mainMenuActive = true;
	private boolean cDress = false;
	Light pclight = new Light(); 

	
 
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
		playerS = new AnimatedShape("playerModel.rkm", "playerModel.rks");
		playerS.loadAnimation("WALK", "playerModel.rka");
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
		speakerS = new ImportedModel("speaker.obj");
		pcS = new ImportedModel("pc.obj");
		lightS = new ImportedModel("light.obj");

		
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
		pizzaTx = new TextureImage("pizzaC.png");
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
		speakerTx = new TextureImage("speaker.png");
		pcTx = new TextureImage("pc.png");
		pc1Tx = new TextureImage("pc1.png");
		lightTx = new TextureImage("light.png");
		player1Tx = new TextureImage("player1.png");
		theifTx = new TextureImage("thief.png");
		//load the textures for the game objects
	}

	@Override
	public void loadSkyBoxes()
	{ 
		sunsetSkybox = (engine.getSceneGraph()).loadCubeMap("sunsetSkybox");
		(engine.getSceneGraph()).setActiveSkyBoxTexture(sunsetSkybox);
		(engine.getSceneGraph()).setSkyBoxEnabled(true);
	}

	@Override
	public void buildObjects()
	{
		Matrix4f scale = new Matrix4f().scaling(1f);
		Matrix4f playerScale = new Matrix4f().scaling(1.8f);

		menuBG = new GameObject(GameObject.root(), oven1S, oven1tx);
		menuBG.setLocalTranslation(new Matrix4f().translation(0f, 0f, -5f));
		menuBG.setLocalScale(new Matrix4f().scaling(100f)); // wide & flat
		menuBG.getRenderStates().setHasSolidColor(true);
		menuBG.getRenderStates().setColor(new Vector3f(0f, 0f, 0f)); // black

		player = new GameObject(GameObject.root(), playerS, playerTx);
		player.setLocalTranslation(new Matrix4f().translation(-46, 1, -16));
		player.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(50)));
		player.setLocalScale(playerScale);
		player.getRenderStates().setModelOrientationCorrection(
		(new Matrix4f()).rotationY((float)java.lang.Math.toRadians(90.0f)));
		player.getRenderStates().setModelOrientationCorrection(
		(new Matrix4f()).rotationX((float)java.lang.Math.toRadians(90.0f)));

		customer = new GameObject(GameObject.root(), customerS, customerTx);
		customer.setLocalTranslation(new Matrix4f().translation(-60, 0, 0));
		customer.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));
		customer.setLocalScale(playerScale);

		thief = new GameObject(GameObject.root(), customerS, theifTx);
		thief.setLocalTranslation(new Matrix4f().translation(-100, 0, 0));
		thief.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));
		thief.setLocalScale(playerScale);


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

		cashRegister1 = new GameObject(GameObject.root(), cashRegisterS, cashRegisterTx);
		cashRegister1.setLocalTranslation(new Matrix4f().translation(1, 2.3f, 6.5f));
		cashRegister1.setLocalScale(new Matrix4f().scaling(2.5f));
		cashRegister1.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		sodaMachine = new GameObject(GameObject.root(), sodaMachineS, sodaMachineTx);
		sodaMachine.setLocalTranslation(new Matrix4f().translation(1, 2f, -13f));
		sodaMachine.setLocalScale(new Matrix4f().scaling(3.5f));
		cashRegister1.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		sodaCup = new GameObject(GameObject.root(), sodaCupS, sodaCupTx);
		sodaCup.setLocalTranslation(new Matrix4f().translation(1, 2.3f, -13f));
		sodaCup.setLocalScale(new Matrix4f().scaling(1f));
		sodaCup.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));

		cuttingBoard = new GameObject(GameObject.root(), cuttingBoardS, cuttingBoardTx);
		cuttingBoard.setLocalTranslation(new Matrix4f().translation(21.5f, 2.6f, -2f));
		cuttingBoard.setLocalScale(new Matrix4f().scaling(5f));

		oven = new GameObject(GameObject.root(), ovenS, ovenTx);
		oven.setLocalTranslation(new Matrix4f().translation(0f, 0f, -10000f));
		oven.setLocalRotation(new Matrix4f().identity());
		oven.setLocalScale(new Matrix4f().scaling(5.2f));
	
		oven.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));

		oven1 = new GameObject(GameObject.root(), oven1S, oven1tx);
		oven1.setLocalTranslation(new Matrix4f().translation(6.5f, 0f, -13f));
		oven1.setLocalScale(new Matrix4f().scaling(1.5f));
		oven1.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));

		//speaker
		speaker = new GameObject(GameObject.root(), speakerS, speakerTx);
		speaker.setLocalTranslation(new Matrix4f().translation(-2f, 0f, -13f));
		speaker.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));
		speaker.setLocalScale(new Matrix4f().scaling(2.5f));

		//pc
		pc = new GameObject(GameObject.root(), pcS, pcTx);
		pc.setLocalTranslation(new Matrix4f().translation(1, 2.3f, -5f));
		pc.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));
		pc.setLocalScale(new Matrix4f().scaling(1f));

		//outside light
		light = new GameObject(GameObject.root(), lightS, lightTx);
		light.setLocalTranslation(new Matrix4f().translation(-21.8f, 9.2f, 0.6f));
		light.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));
		light.setLocalScale(new Matrix4f().scaling(2f));

		//dining area light
		light1 = new GameObject(GameObject.root(), lightS, lightTx);
		light1.setLocalTranslation(new Matrix4f().translation(-10f, 9.5f, 2f));
		light1.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));
		light1.setLocalScale(new Matrix4f().scaling(2f));

		//kitchen area light
		light2 = new GameObject(GameObject.root(), lightS, lightTx);
		light2.setLocalTranslation(new Matrix4f().translation(10f, 9.5f, 2f));
		light2.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(-90)));
		light2.setLocalScale(new Matrix4f().scaling(2f));

		// build terrain object
		terrain = new GameObject(GameObject.root(), terrainS);
		terrain.setLocalTranslation(new Matrix4f().translation(0f,0f,0f));
		Matrix4f initialScale = (new Matrix4f()).scaling(1000.0f, 1.0f, 1000.0f);
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
		Light.setGlobalAmbient(0.1f, 0.06f, 0.05f);
		
		//ceiling lights, 
		//ceiling 1 is kitchen light, 
		//ceiling 2 is dining area light
		Light ceiling1 = new Light(); //kitchen light
		ceiling1.setType(Light.LightType.POSITIONAL);
		ceiling1.setAmbient(0.2f, 0.15f, 0.1f);
		ceiling1.setDiffuse(4.5f, 3.5f, 3.5f);  
		ceiling1.setSpecular(0.8f, 0.9f, 0.6f);                 
		ceiling1.setLocation(new Vector3f(10f, 9f, 2f)); 
		ceiling1.setConstantAttenuation(1.0f);
		ceiling1.setLinearAttenuation(0.1f);
		ceiling1.setQuadraticAttenuation(0.05f);

		Light ceiling2 = new Light(); //dining area light
		ceiling2.setType(Light.LightType.POSITIONAL);
		ceiling2.setAmbient(0.2f, 0.15f, 0.1f);
		ceiling2.setDiffuse(3.0f, 2f, 2f); 
		ceiling2.setSpecular(0.8f, 0.9f, 0.6f); 
		ceiling2.setLocation(new Vector3f(-10f, 9f, 2f));
		ceiling2.setConstantAttenuation(1.0f);
		ceiling2.setLinearAttenuation(0.1f);
		ceiling2.setQuadraticAttenuation(0.05f);

		//outside of restaurant light
		Light doorLight = new Light();
		doorLight.setType(Light.LightType.POSITIONAL);
		doorLight.setLocation(new Vector3f(-22.3f, 8f, 0.4f));
		doorLight.setAmbient(0.2f, 0.2f, 0.2f);
		doorLight.setDiffuse(1.0f, 1.0f, 1.0f);
		doorLight.setSpecular(0.5f, 0.5f, 0.5f);
		doorLight.setConstantAttenuation(1.0f);
		doorLight.setLinearAttenuation(0.05f);
		doorLight.setQuadraticAttenuation(0.01f);
		engine.getSceneGraph().addLight(doorLight);

		Light spotlight1 = new Light();
		spotlight1.setType(Light.LightType.SPOTLIGHT);
		spotlight1.setAmbient(0.2f, 0.15f, 0.1f);
		spotlight1.setDiffuse(3f, 3f, 3f);
		spotlight1.setSpecular(2f, 2f, 2f);

		Vector3f lightPos = new Vector3f(-21f, 7f, 22f);
		spotlight1.setLocation(lightPos);
		spotlight1.setDirection(new Vector3f(0f, 1f, 0f));

		spotlight1.setCutoffAngle(60.0f);
		spotlight1.setOffAxisExponent(0.2f);

		spotlight1.setConstantAttenuation(1.0f);
		spotlight1.setLinearAttenuation(0.01f);
		spotlight1.setQuadraticAttenuation(0.005f);

		engine.getSceneGraph().addLight(spotlight1);


		engine.getSceneGraph().addLight(ceiling1);
		engine.getSceneGraph().addLight(ceiling2);
		engine.getSceneGraph().addLight(spotlight1);
		engine.getSceneGraph().addLight(doorLight);
		
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

	private void changeDress(){
			player.setTextureImage(player1Tx);
	}

	

	@Override
	public void loadSounds() {
		audioMgr = engine.getAudioManager();

		AudioResource bgRes = audioMgr.createAudioResource("b.wav", AudioResourceType.AUDIO_SAMPLE);
		insideSound = new Sound(bgRes, SoundType.SOUND_EFFECT, 60, true); // volume = 60
		insideSound.initialize(audioMgr);
		insideSound.setLocation(speaker.getWorldLocation());
		insideSound.setMaxDistance(40f); // adjust falloff range
		insideSound.setMinDistance(0.25f);
		insideSound.setRollOff(0.25f);
		insideSound.play();

	
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
		footstepSound.setMaxDistance(40f);
		footstepSound.setRollOff(5f);

	
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
		InventoryManager inventory = new InventoryManager();
		this.inventory = inventory;
		CashManager cashManager = new CashManager(50);
		this.cashManager = cashManager;
		engine.getRenderSystem().getGLCanvas().addMouseMotionListener(this);
		physicsEngine = engine.getSceneGraph().getPhysicsEngine();
		physicsEngine.setGravity(new float[]{0f, -9.8f, 0f});

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
		playerController = new PlayerController(player, playerPhys, engine, playerS);

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
		
		Vector3f thiefExit = new Vector3f(thief.getWorldLocation());
		thiefController = new ThiefBehaviorController(thief, cashRegister, thiefExit, player, cashManager);
		customerController = new CustomerBehaviorController(customer, cashRegister, chair, player);
		gameLogic = new GameLogic(player, customer, oven1, speaker, insideSound, inventory,
		 engine, pizzaS, pizzaTx, customerController);
		 if (mainMenuActive) {
				int screenWidth = engine.getRenderSystem().getGLCanvas().getWidth();
		int screenHeight = engine.getRenderSystem().getGLCanvas().getHeight();

		int centerX = screenWidth / 2;
		int titleY = screenHeight / 2 + 50;
		int bottomY = 100;

		// Title text - golden
		engine.getHUDmanager().setHUD1(
			"Restaurant Simulator by Max and Sam",
			new Vector3f(1f, 0.85f, 0.3f), // golden
			centerX - 200, titleY
		);

		// Subtitle - cyan
		engine.getHUDmanager().setHUD2(
			"Press Enter to Continue",
			new Vector3f(0.7f, 0.9f, 1f), // light blue
			centerX - 130, bottomY
		);

	
		engine.getHUDmanager().setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
		engine.getHUDmanager().setHUD2font(GLUT.BITMAP_TIMES_ROMAN_24);


}
	}

	public void showTimedMessage(String message, Vector3f color) {
		engine.getHUDmanager().setHUD2(message, color, 100, 590);
		hudMessageStartTime = System.currentTimeMillis();
		showTimedHUDMessage = true;
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
		if (mainMenuActive) return;  // skip game update logic
		System.out.println(player.getWorldLocation().x() + " " + player.getWorldLocation().y() + " " + player.getWorldLocation().z());
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

		if (!zoomedToPC) {
		cam.setU(right);
		cam.setV(up);
		cam.setN(fwd);
		cam.setLocation(loc.add(up.mul(6f)).add(fwd.mul(-10.0f)));
		}

		
		footstepSound.setLocation(player.getWorldLocation());
		processNetworking(elapsTime);

		boolean moving = upPressed || downPressed || leftPressed || rightPressed;
		if (moving) {
			if (!footstepSound.getIsPlaying()) footstepSound.play();
		} else {
			if (footstepSound.getIsPlaying()) footstepSound.stop();
		}

		insideSound.setLocation(speaker.getWorldLocation());
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

		
		if (gameLogic != null) gameLogic.update((float)elapsTime);

		customerController.update((float)elapsTime);

		if (currentOrder != null) {
			if (System.currentTimeMillis() - currentOrderStart > ORDER_DISPLAY_MS) {
				engine.getHUDmanager().setHUD1("", new Vector3f(), 0, 0);
				currentOrder = null;
			}
		}

		//logic for thief until we put it in GameLogic
		// only show thief prompt when NOT ordering
		if (!orderingActive
			&& thiefController != null
			&& !thiefController.isDone()) {
			thiefController.update((float)elapsTime);
			float d = player.getWorldLocation().distance(thief.getWorldLocation());
			if (d < 5f) {
				engine.getHUDmanager().setHUD5(
					"Press F to catch thief",
					new Vector3f(1f, 1f, 1f),
					900, 700
				);
			} else {
				engine.getHUDmanager().setHUD5(
					"",
					new Vector3f(),
					0, 0
				);
			}
		}


		if (thiefController != null && thiefController.isDone()) {
			thiefController.update((float)elapsTime);
		}

		//Hud for Inventory Tracking
		int canvasHeight = engine.getRenderSystem().getGLCanvas().getHeight();
		int startX = 20;
		int startY = canvasHeight - 40;

		Map<String, Integer> invMap = inventory.getInventory();
		List<String> lines = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : invMap.entrySet()) {
			lines.add(entry.getKey() + ": " + entry.getValue());
		}

		engine.getHUDmanager().setHUD3Lines(lines, new Vector3f(1, 0, 0), startX, startY);
		engine.getHUDmanager().setHUD3font(GLUT.BITMAP_TIMES_ROMAN_24);

		// Order
		Vector3f playerPos = player.getWorldLocation();
		Vector3f pcPos = pc.getWorldLocation();
		float distToPC = playerPos.distance(pcPos);
		nearPC = (distToPC < 5.0f);

		if (!orderingActive && nearPC) {
			engine.getHUDmanager().setHUD1("Press O to order ingredients", new Vector3f(1,1,1), 800, 700);
		} else if (!orderingActive) {
			engine.getHUDmanager().setHUD1("", new Vector3f(), 0, 0);
		}
		// Exit order mode after delay
		if (isExitMenuScheduled) {
			long now = System.currentTimeMillis();
			if (now - exitMenuStartTime > 3000) {
				exitOrderMode();
				isExitMenuScheduled = false;
			}
		}
		
		Vector3f pantryShelfPos = pantryShelf1.getWorldLocation();
		float distToPantry = playerPos.distance(pantryShelfPos);

		if (distToPantry < 5.0f) {
			engine.getHUDmanager().setHUD1("Switched to work clothes", new Vector3f(1,1,1), 800, 700);
			changeDress();
		} else {
			engine.getHUDmanager().setHUD1("", new Vector3f(), 0, 0);
		}

		
		// Money HUD
		GLCanvas canvas = engine.getRenderSystem().getGLCanvas();
		int canvasWidth = canvas.getWidth();
		String moneyText = "Cash: $" + cashManager.getBalance();
		int estimatedWidth = moneyText.length() * 8;
		int x = canvasWidth - estimatedWidth - 50; 
		int y = canvasHeight - 30; 

		engine.getHUDmanager().setHUD4(moneyText, new Vector3f(1f, 1f, 0.6f), x, y);

	}

	private void zoomCamToPC() {
		Camera cam = engine.getRenderSystem().getViewport("MAIN").getCamera();

		savedCamPos = cam.getLocation();
		savedCamU = cam.getU();
		savedCamV = cam.getV();
		savedCamN = cam.getN();

		Vector3f pcLoc = pc.getWorldLocation();
		Vector3f offset = new Vector3f(3.2f, 1.8f, 1.4f); 
		Vector3f newCamPos = new Vector3f(pcLoc).add(offset);

		cam.setLocation(newCamPos);
		cam.lookAt(pcLoc);

		zoomedToPC = true;
		pc.setTextureImage(pc1Tx);

		Light pclight = new Light(); 
		pclight.setType(Light.LightType.POSITIONAL);
		pclight.setAmbient(0.2f, 0.15f, 0.1f);
		pclight.setDiffuse(3.0f, 2f, 2f); 
		pclight.setSpecular(0.8f, 0.9f, 0.6f); 
		pclight.setLocation(new Vector3f(2, 3.3f, -5f));
		pclight.setConstantAttenuation(1.0f);
		pclight.setLinearAttenuation(0.1f);
		pclight.setQuadraticAttenuation(0.05f);
		engine.getSceneGraph().addLight(pclight);
		pclight.enable();
}



	private void exitOrderMode() {
		orderingActive = false;
		zoomedToPC = false; 

		engine.getHUDmanager().setHUD1("", new Vector3f(), 0, 0);
		engine.getHUDmanager().setHUD2("", new Vector3f(), 0, 0);
		engine.getHUDmanager().setHUD3("", new Vector3f(), 0, 0);

		Camera cam = engine.getRenderSystem().getViewport("MAIN").getCamera();
		cam.setLocation(savedCamPos);
		cam.setU(savedCamU);
		cam.setV(savedCamV);
		cam.setN(savedCamN);

		pc.setTextureImage(pcTx);
		pclight.disable();
}


	private void showOrderMenu() {
		orderingActive = true;

		String msg = "Select an ingredient to buy: 1. Mushroom = $5   2. Pepperoni = $4   3. Cheese = $3   4. Sauce = $2";

		// Get canvas dimensions
		int canvasWidth = engine.getRenderSystem().getGLCanvas().getWidth();
		int canvasHeight = engine.getRenderSystem().getGLCanvas().getHeight();

		// Roughly estimate string width (8px per character for Times Roman 24)
		int estimatedTextWidth = msg.length() * 8;

		// Center horizontally and push down from the top
		int x = (canvasWidth / 2) - (estimatedTextWidth / 2);
		int y = canvasHeight - 80;  // Adjust as needed for spacing

		// Show HUD1
		engine.getHUDmanager().setHUD1(msg, new Vector3f(1f, 1f, 0.6f), x, y);
		engine.getHUDmanager().setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
	}
	private void buyItem(int index) {
		int cost = menuPrices[index];
		String itemName = menuItems[index].split("\\.")[1].trim().split(" -")[0].toLowerCase();

		String msg;
		Vector3f color;

		if (cashManager.getBalance() >= cost) {
			cashManager.deductExpense(cost);
			inventory.addItem(itemName);
			msg = "Purchased: " + itemName + " for $" + cost;
			color = new Vector3f(0, 1, 0); // green
		} else {
			msg = "Not enough cash for " + itemName + " ($" + cost + ")";
			color = new Vector3f(1, 0, 0); // red
		}

		// Get canvas info
		int canvasWidth = engine.getRenderSystem().getGLCanvas().getWidth();
		int canvasHeight = engine.getRenderSystem().getGLCanvas().getHeight();

		// Estimate width of the text
		int estimatedTextWidth = msg.length() * 8;

		// Center horizontally, and position just below HUD1 (which is at y = canvasHeight - 80)
		int x = (canvasWidth / 2) - (estimatedTextWidth / 2);
		int y = canvasHeight - 110; // ~30 pixels below HUD1

		engine.getHUDmanager().setHUD2(msg, color, x, y);
		engine.getHUDmanager().setHUD2font(GLUT.BITMAP_TIMES_ROMAN_24);

		// Start delayed exit
		exitMenuStartTime = System.currentTimeMillis();
		isExitMenuScheduled = true;
	}




	@Override
	public void keyPressed(KeyEvent e) {
		
		if (mainMenuActive && e.getKeyCode() == KeyEvent.VK_ENTER) {
			mainMenuActive = false;
			engine.getHUDmanager().setHUD1("", new Vector3f(), 0, 0);
			engine.getHUDmanager().setHUD2("", new Vector3f(), 0, 0);
			
			// Remove background cube
			engine.getSceneGraph().removeGameObject(menuBG);
			menuBG = null;

			return;
		}

		// INTERACTIONS (F KEY)
		if (e.getKeyCode() == KeyEvent.VK_F && gameLogic != null) {
			float distToCust    = player.getWorldLocation().distance(customer.getWorldLocation());
			float distToOven    = player.getWorldLocation().distance(oven1.getWorldLocation());
			float distToThief   = player.getWorldLocation().distance(thief.getWorldLocation());
			float distToSpeaker = player.getWorldLocation().distance(speaker.getWorldLocation());

			// 1) Take order if they’re waiting at the counter
			if (distToCust < 5f && customerController.isWaitingForOrder()) {
				engine.getHUDmanager().setHUD1("Order received", new Vector3f(1,1,1), 400,50);
				engine.getHUDmanager().setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);

				customerController.tryTakeOrder();
			}
			// 2) Serve pizza if they’ve returned
			else if (distToCust < 5f && customerController.isWaitingForServe()) {
				customerController.tryServeOrder();

				if (customerController.isDone()) {
					// award money here
					cashManager.addIncome(10);
					engine.getHUDmanager().setHUD1("Served pizza! +$10", new Vector3f(0,1,0), 400,50);
					engine.getHUDmanager().setHUD1font(GLUT.BITMAP_TIMES_ROMAN_24);
				}
			}
			// 3) Oven, thief, speaker…
			else if (distToOven < 5f) {
				gameLogic.tryStartCooking();
			}
			else if (distToThief < 5f) {
				thiefController.tryCatch();
			}
			else if (distToSpeaker < 5f) {
				gameLogic.tryToggleMusic();
			}
		}


		// ORDERING SYSTEM START (O KEY)
		if (e.getKeyCode() == KeyEvent.VK_O && nearPC && !orderingActive) {
			zoomCamToPC();
			showOrderMenu();
		}

		// ORDERING SELECTION (1–4)
		if (orderingActive && e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_4) {
			int choice = e.getKeyCode() - KeyEvent.VK_1;
			buyItem(choice);
		}

		// CANCEL ORDER (Backspace)
		if (orderingActive && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			exitOrderMode();
		}
	}




	@Override
	public void keyTyped(KeyEvent e) {
		// Not used
	}
}
