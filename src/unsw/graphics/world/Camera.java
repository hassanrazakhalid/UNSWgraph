package unsw.graphics.world;


import com.jogamp.opengl.GL3;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.geometry.Point3D;

public class Camera {

	private Terrain terrain;
	private Point3D globalPosition;
	private float globalRotation;
	private float speed = 0.05f; 

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
	public void setGlobalPositionJump(float x, float y, float z) {
		globalPosition = new Point3D(x, y, z);
	}
	
	//moves camera forward by translating its position according to the global rotation,
	//and the altitude at that point
	public void up(Terrain terrain) {
		globalPosition = this.globalPosition.translate(-speed*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -speed*(float)Math.cos(Math.toRadians(globalRotation)));
		globalPosition = this.globalPosition.translate(-0.05f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, -0.05f*(float)Math.cos(Math.toRadians(globalRotation)));
		setGlobalPosition(globalPosition.getX(), globalPosition.getZ());
		
		System.out.println("Moved to this altitude: " + terrain.altitude(globalPosition.getX(), globalPosition.getZ()));
	}
	//moves camera backwards by translating its position according to the global rotation
	public void down(Terrain terrain) {
		globalPosition = this.globalPosition.translate(speed*(float)Math.sin(Math.toRadians(globalRotation)), 0f, speed*(float)Math.cos(Math.toRadians(globalRotation)));
		globalPosition = this.globalPosition.translate(0.05f*(float)Math.sin(Math.toRadians(globalRotation)), 0f, 0.05f*(float)Math.cos(Math.toRadians(globalRotation)));
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
	
	
	public void jump() {
		long startTime = java.lang.System.currentTimeMillis(); 						//time when jump button is pressed
//		System.out.println("Start Time = " + startTime);
		long endTime = startTime + 500;												//0.5 sec after start of jump = end of jump
//		System.out.println("End Time = " + endTime);
		Point3D startPos = this.globalPosition;
		while(System.currentTimeMillis() < endTime || System.currentTimeMillis() == endTime) {		//while jumping
			double time = (System.currentTimeMillis() - startTime);
//			System.out.println("Time = " + time);
			double jumpHeight = (8*time/1000 - 16*Math.pow(time/1000, 2));							//height follows a function
//			System.out.println("Jump Height = " + jumpHeight);
			this.globalPosition = this.globalPosition.translate(0, (float) (startPos.getY() + jumpHeight - this.globalPosition.getY()), 0);
			setGlobalPositionJump(startPos.getX(), (float) (startPos.getY() + jumpHeight), startPos.getZ());
		}
	}
	

	public float getGlobalRotation() {
		return globalRotation;
	}

	public void setGlobalRotation(float globalRotation) {
		this.globalRotation = globalRotation;
	}


	public Point3D getDirection() {
		return new Point3D(globalPosition.getX() + 1, globalPosition.getY(), globalPosition.getZ());
	}
	
	
}
