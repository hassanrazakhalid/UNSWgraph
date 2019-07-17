package unsw.graphics.world;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.geometry.Point3D;


public class Camera {

	private CoordFrame3D camFrame;
	private Terrain terrain;
	private Point3D globalPosition;
	private float localRotation;
	private float localTranslation;
	

	public Camera(Terrain terrain) {
		globalPosition = new Point3D(0, 0, 0);
		localRotation = 0;
		localTranslation = 0;
	}
	
	public Point3D getCamGlobalPosition() {
		return globalPosition;
	}
	
	public void setGlobalPosition(float x, float z) {
		globalPosition = new Point3D(x, terrain.altitude(x, z), z);
		camFrame.translate(globalPosition);
	}
	
	public float getLocalRotation() {
		return localRotation;
	}
	
	public float getLocalTranslation() {
		return localTranslation;
	}
	
	public void up(Terrain terrain) {
		localTranslation += 0.2f;
//		setCamFrame(CoordFrame3D.identity().rotateY(localRotation).translate(0, 0, localZ));
//		setCamFrame(camFrame.translate(0, 0, 0.2f));
//		float x = camFrame.getMatrix().getValues()[12];
//		float z = camFrame.getMatrix().getValues()[14];
//		setCamFrame(camFrame.translate(0, terrain.altitude(x, z), 0));
		
//		float x = getCamFrame().getMatrix().getValues()[12];
//		float z = getCamFrame().getMatrix().getValues()[14];
//		float altitude = terrain.altitude(x, z);
//		setCamFrame(getCamFrame().translate(0, altitude, 0));

	}
	public void down(Terrain terrain) {
		localTranslation -= 0.2f;
//		setCamFrame(CoordFrame3D.identity().rotateY(localRotation).translate(0, 0, localZ));
//		setCamFrame(camFrame.translate(0, 0, -0.2f));
//		float x = camFrame.getMatrix().getValues()[12];
//		float z = camFrame.getMatrix().getValues()[14];
//		setCamFrame(camFrame.translate(0, terrain.altitude(x, z), 0));
		
//		setCamFrame(getCamFrame().translate(0, 0, -0.2f));
//		float x = getCamFrame().getMatrix().getValues()[12];
//		float z = getCamFrame().getMatrix().getValues()[14];
//		float altitude = terrain.altitude(x, z);
//		setCamFrame(getCamFrame().translate(0, altitude, 0));

	}

	public void left() {
		localRotation -= 10;
//		setCamFrame(camFrame.rotateY(-20));
	}

	public void right() {
		localRotation += 10;
//		setCamFrame(camFrame.rotateY(20));
	}
	
	public CoordFrame3D getCamFrame() {
		return camFrame;
	}
	
	public void setCamFrame(CoordFrame3D frame) {
		camFrame = frame;
	}

	
	
	
}
