package game;

import org.joml.*;
import org.joml.Math;

import tage.*;
import tage.physics.PhysicsObject;
import tage.shapes.AnimatedShape;

import java.awt.event.*;

public class PlayerController implements KeyListener, MouseMotionListener {
	private GameObject player;
	private PhysicsObject playerPhys;
	private AnimatedShape playerS;
	private Engine engine;
	private float mouseSensitivity = 0.2f;
	private int lastMouseX = -1;
	private boolean upPressed, downPressed, leftPressed, rightPressed;

	public PlayerController(GameObject player, PhysicsObject playerPhys, Engine engine, AnimatedShape playerS) {
		this.player = player;
		this.playerPhys = playerPhys;
		this.engine = engine;
		this.playerS = playerS;

		engine.getRenderSystem().getGLCanvas().addKeyListener(this);
		engine.getRenderSystem().getGLCanvas().addMouseMotionListener(this);
	}

	public void updateMovement(float elapsedTime) {
		Vector3f fwd = player.getWorldForwardVector();
		Vector3f right = player.getWorldRightVector();

		float fx = 0f, fy = 0f, fz = 0f;

		if (upPressed) {
			fx += fwd.x();
			fz += fwd.z();
		}
		if (downPressed) {
			fx -= fwd.x();
			fz -= fwd.z();
		}
		if (leftPressed) {
			fx -= right.x();
			fz -= right.z();
		}
		if (rightPressed) {
			fx += right.x();
			fz += right.z();
		}

		if (fx != 0 || fz != 0) {
			playerPhys.applyForce(fx * 15f, fy, fz * 15f, 0f, 0f, 0f);
		} else {
			float[] vel = playerPhys.getLinearVelocity();
			vel[0] = 0;
			vel[2] = 0;
			playerPhys.setLinearVelocity(vel);
		}

		// Sync transform to GameObject
		double[] tf = playerPhys.getTransform();
		player.setLocalTranslation(new Matrix4f().translation(
			(float) tf[12], (float) tf[13], (float) tf[14]
		));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {

			case KeyEvent.VK_W -> {upPressed = true; 
			playerS.playAnimation("WALK", 0.5f,
			AnimatedShape.EndType.LOOP, 100);} 

			case KeyEvent.VK_S -> {downPressed = true; playerS.stopAnimation();
				break;}
			case KeyEvent.VK_A -> leftPressed = true;
			case KeyEvent.VK_D -> rightPressed = true;
			case KeyEvent.VK_Q -> player.setLocalRotation(
				new Matrix4f().rotateY((float) Math.toRadians(5))
					.mul(player.getLocalRotation())
			);
			case KeyEvent.VK_E -> player.setLocalRotation(
				new Matrix4f().rotateY((float) Math.toRadians(-5))
					.mul(player.getLocalRotation())
			);
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W -> {upPressed = false; playerS.stopAnimation();}
			case KeyEvent.VK_S -> downPressed = false;
			case KeyEvent.VK_A -> leftPressed = false;
			case KeyEvent.VK_D -> rightPressed = false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (lastMouseX != -1) {
			int deltaX = e.getX() - lastMouseX;
			float rotationAmount = (float) Math.toRadians(-deltaX * mouseSensitivity);
			player.setLocalRotation(
				new Matrix4f().rotateY(rotationAmount).mul(player.getLocalRotation())
			);
		}
		lastMouseX = e.getX();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}
