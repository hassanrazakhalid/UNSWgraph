package unsw.graphics.world;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.geometry.Point3D;


public class Camera {

	private CoordFrame3D camFrame;
	private Point3D position;
	private Terrain terrain;
	private Point3D globalPosition;

	public Camera(Terrain terrain) {
		camFrame = new CoordFrame3D(Matrix4.identity());
		this.position = new Point3D(0, 0, 0);

	}
	
	public Point3D getCamGlobalPosition() {
		return globalPosition;
	}
	
	public void up(Terrain terrain) {
		setCamFrame(getCamFrame().translate(0, 0, 0.2f));
		float x = getCamFrame().getMatrix().getValues()[12];
		float z = getCamFrame().getMatrix().getValues()[14];
		float altitude = terrain.altitude(x, z);
		setCamFrame(getCamFrame().translate(0, altitude, 0));

	}
	public void down(Terrain terrain) {
		setCamFrame(getCamFrame().translate(0, 0, -0.2f));
		float x = getCamFrame().getMatrix().getValues()[12];
		float z = getCamFrame().getMatrix().getValues()[14];
		float altitude = terrain.altitude(x, z);
		setCamFrame(getCamFrame().translate(0, altitude, 0));

	}

	public void left() {
		setCamFrame(getCamFrame().rotateY(-30));
	}

	public void right() {
		setCamFrame(getCamFrame().rotateY(30));
		
	}
	
	public CoordFrame3D getCamFrame() {
		return camFrame;
	}
	
	public void setCamFrame(CoordFrame3D frame) {
		camFrame = frame;
	}

	
	
	
}
