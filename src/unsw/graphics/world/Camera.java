package unsw.graphics.world;

import unsw.graphics.geometry.Point3D;

public class Camera {

	private Terrain terrain;
	private Point3D globalPosition;
	private float globalRotation;
	private boolean viewAvatar; 

	public Camera(Terrain terrain) {
		globalPosition = new Point3D(0, 1f, 0);
		globalRotation = 0;
		this.terrain = terrain;
	}
	
	public Point3D getGlobalPosition() {
		return globalPosition;
	}
	
	public void setGlobalPosition(float x, float z) {
		globalPosition = new Point3D(x, 1f + terrain.altitude(x, z), z);
	}
	
	
	public void up(Terrain terrain) {
		globalPosition = this.globalPosition.translate(-0.5f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -0.5f*(float)Math.cos(Math.toRadians(globalRotation)));
		setGlobalPosition(globalPosition.getX(), globalPosition.getZ());
		
		System.out.println("Moved to this altitude: " + terrain.altitude(globalPosition.getX(), globalPosition.getZ()));
	}
	public void down(Terrain terrain) {
		globalPosition = this.globalPosition.translate(0.5f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, 0.5f*(float)Math.cos(Math.toRadians(globalRotation)));
		setGlobalPosition(globalPosition.getX(), globalPosition.getZ());
	}

	public void left() {
		globalRotation += 10;
	}

	public void right() {
		globalRotation -= 10;
	}
	
	public void viewAvatar() {
		if(viewAvatar) {
			globalPosition = this.globalPosition.translate(-0.5f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -0.5f*(float)Math.cos(Math.toRadians(globalRotation)));
			viewAvatar = false;
			
		}else {
			globalPosition = this.globalPosition.translate(0.5f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, 0.5f*(float)Math.cos(Math.toRadians(globalRotation)));
			viewAvatar = true;
		}
	}

	public float getGlobalRotation() {
		return globalRotation;
	}

	public void setGlobalRotation(float globalRotation) {
		this.globalRotation = globalRotation;
	}


	
}
