package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Avatar {
	private Camera camera;
	private Point3D position;
	TriangleMesh mesh;
	private Texture avatarTexture;
	private boolean isShowing;

	public Avatar(Camera camera) {
		this.camera = camera;
		this.position = camera.getGlobalPosition();
		isShowing = false;
		try {
			this.mesh = new TriangleMesh
					("res/models/bunny_res4.ply", true);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setPosition(Point3D pos) {
		this.position = pos;
	}
	
	public Point3D getPosition() {
		return position;
	}
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
//    	CoordFrame3D avatarFrame = frame.translate(position);
//    			.translate(0, 1.1f, 0);

//    avatarFrame.draw(gl);
//    Shader.setPenColor(gl, Color.MAGENTA);
	gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, avatarTexture.getId());
    	mesh.draw(gl, frame);
}
    
	public void init(GL3 gl) {
		// TODO Auto-generated method stub
		avatarTexture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", false);
		mesh.init(gl);
	}
	
	public boolean isShowing() {
		return isShowing;
	}
	
//	public void changeView() {
//		if (isShowing) {
////			position = camera.getGlobalPosition();
//			isShowing = false;
//		}else {
////			position = camera.getGlobalPosition().translate(0, 0, 1);
//			isShowing = true;
//		}
//	}
	
	
}
