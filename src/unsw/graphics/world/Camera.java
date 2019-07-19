package unsw.graphics.world;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.scene.MathUtil;


public class Camera {

	private CoordFrame3D camFrame;
	private Terrain terrain;
	private Point3D globalPosition;
	private float globalRotation;
//	private float localRotation;
//	private float localTranslation;
	

	public Camera(Terrain terrain) {
		globalPosition = new Point3D(0, 0, 0);
		globalRotation = 0;
		this.terrain = terrain;
//		camFrame =  new CoordFrame3D(Matrix4.identity());
	}
	
	public Point3D getGlobalPosition() {
		return globalPosition;
	}
	
	public void setGlobalPosition(float x, float z) {
		globalPosition = new Point3D(x, terrain.altitude(x, z), z);
		camFrame.translate(globalPosition);
	}
	
	
	public void up(Terrain terrain) {
		globalPosition = this.globalPosition.translate(-(float)Math.sin(Math.toRadians(globalRotation)), 0f, -(float)Math.cos(Math.toRadians(globalRotation)));
		System.out.println("Global position x,y = " + globalPosition.getX() + globalPosition.getZ());
	}
	public void down(Terrain terrain) {
		globalPosition = this.globalPosition.translate((float)Math.sin(Math.toRadians(globalRotation)), 0f, (float)Math.cos(Math.toRadians(globalRotation)));

	}

	public void left() {
		globalRotation += 10;
	}

	public void right() {
		globalRotation -= 10;
	}
	
	public CoordFrame3D getCamFrame() {
		return camFrame;
	}
	
	public void setCamFrame(CoordFrame3D frame) {
		camFrame = frame;
	}

	public float getGlobalRotation() {
		return globalRotation;
	}

	public void setGlobalRotation(float globalRotation) {
		this.globalRotation = globalRotation;
	}


	
}
