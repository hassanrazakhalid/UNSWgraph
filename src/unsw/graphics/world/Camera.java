package unsw.graphics.world;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.geometry.Point3D;

public class Camera {

	private Terrain terrain;
	private Point3D globalPosition;
	private float globalRotation;
	private float speed = 0.2f; 

	private boolean viewAvatar; 


	public Camera(Terrain terrain) {
		globalPosition = new Point3D(0, 1f, 0);
		globalRotation = 0;
		this.terrain = terrain;
	}
	
	public Point3D getGlobalPosition() {
		return globalPosition;
	}
	
	//takes a position and finds the altitude of the terrain at that position
	public void setGlobalPosition(float x, float z) {
		globalPosition = new Point3D(x, 1f + terrain.altitude(x, z), z);
	}
	
	//moves camera forward by translating its position according to the global rotation,
	//and the altitude at that point
	public void up(Terrain terrain) {
//		System.out.println(globalPosition.toString());
		globalPosition = this.globalPosition.translate(-speed*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -speed*(float)Math.cos(Math.toRadians(globalRotation)));
		globalPosition = this.globalPosition.translate(-0.4f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -0.4f*(float)Math.cos(Math.toRadians(globalRotation)));
		setGlobalPosition(globalPosition.getX(), globalPosition.getZ());
		
		System.out.println("Moved to this altitude: " + terrain.altitude(globalPosition.getX(), globalPosition.getZ()));
	}
	//moves camera backwards by translating its position according to the global rotation
	public void down(Terrain terrain) {

//		System.out.println(globalPosition.toString());
		globalPosition = this.globalPosition.translate(speed*(float)Math.sin(Math.toRadians(globalRotation)), 0f, speed*(float)Math.cos(Math.toRadians(globalRotation)));
		globalPosition = this.globalPosition.translate(0.4f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, 0.4f*(float)Math.cos(Math.toRadians(globalRotation)));
		setGlobalPosition(globalPosition.getX(), globalPosition.getZ());
	}
	
	public void draw(GL3 gl) {
		CoordFrame3D coord = CoordFrame3D.identity()
		.translate(globalPosition)
		.rotateX(globalRotation)
		.rotateZ(globalRotation);
		
		
		
		coord.draw(gl);
	}
	
	public void left() {
		globalRotation += 10;
	}

	public void right() {
		globalRotation -= 10;
	}
	
	public void viewAvatar() {
		if(viewAvatar) {
//			globalPosition = this.globalPosition.translate(-0.5f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -0.5f*(float)Math.cos(Math.toRadians(globalRotation)));
			viewAvatar = false;
			
		}else {
//			globalPosition = this.globalPosition.translate(0.5f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, 0.5f*(float)Math.cos(Math.toRadians(globalRotation)));
			viewAvatar = true;
		}
	}

	public float getGlobalRotation() {
		return globalRotation;
	}

	public void setGlobalRotation(float globalRotation) {
		this.globalRotation = globalRotation;
	}

//	public

	public Point3D getDirection() {
		return new Point3D(globalPosition.getX() + 1, globalPosition.getY(), globalPosition.getZ());
	}
	
	
}
