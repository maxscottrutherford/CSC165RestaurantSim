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

public class game extends VariableFrameRateGame
{
	private static Engine engine;

	//Physics Engine
	private PhysicsEngine physicsEngine;

	//Camera 
	private Camera cam;

	//Networking objects and related functions
	private GhostManager ghostManager;
	private ProtocolClient protClient;
	private boolean isClientConnected = false;
	private String serverAddress = "127.0.0.1";
	private int serverPort = 6000;
	private boolean upPressed, downPressed, leftPressed, rightPressed;
	private float   stepTimer      = 0f;
	private final float STEP_INTERVAL = 0.5f;  // seconds between steps
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
	private GameObject terrain, restaurant, bacon, bellPepper, cashRegister, ceiling, chair, counter, customer, cuttingBoard, floor, knife, mushroom, pantryShelf, pepperoni,
	pizza, player, poster, posterWide, saucecan, signBoard, sodaCup, sodaMachine, table, waLL;

	//Gameobject shapes
	private ObjShape terrainS, baconS, restaurantS, bellPepperS, cashRegisterS, ceilingS, chairS, counterS, customerS, cuttingBoardS, floorS, knifeS, mushroomS, pantryShelfS, pepperoniS,
	pizzaS, playerS, posterS, posterWideS, saucecanS, signBoardS, sodaCupS, sodaMachineS, tableS, waLLS;

	//Gameobject textures
	private TextureImage terrainTx, hills, baconTx, restaurantTx, bellPepperTx, cashRegisterTx, ceilingTx, chairTx, counterTx, customerTx, cuttingBoardTx, floorTx, knifeTx, mushroomTx, pantryShelfTx, pepperoniTx,
	pizzaTx, playerTx, posterTx, posterWideTx, saucecanTx, signBoardTx, sodaCupTx, sodaMachineTx, tablev, waLLTx;

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
		player.setLocalTranslation(new Matrix4f().translation(-50, 2, 10));
		player.setLocalRotation(new Matrix4f().rotationY((float) Math.toRadians(90)));
		player.setLocalScale(playerScale);

		restaurant = new GameObject(GameObject.root(), restaurantS, restaurantTx);
		restaurant.setLocalTranslation(new Matrix4f().translation(0, 0, 0));
		restaurant.setLocalRotation(new Matrix4f().rotationX((float) Math.toRadians(-180)));
		restaurant.setLocalScale(scale);

		

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

		engine.enablePhysicsWorldRender();
		engine.getRenderSystem().getGLCanvas().addMouseMotionListener(this);
		physicsEngine = engine.getSceneGraph().getPhysicsEngine();
		physicsEngine.setGravity(new float[]{0f, -9.8f, 0f});

		// Enable physics rendering
		engine.enableGraphicsWorldRender();
		engine.enablePhysicsWorldRender();

		// Setup physics objects
		PhysicsBuilder.setupStaticPhysics(engine);

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
	}
}
