package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point2DBuffer;
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
	Point2DBuffer quadTexCoords;

	public Avatar(Camera camera) {
		this.camera = camera;
		this.position = camera.getGlobalPosition();
		isShowing = false;
		try {
			this.mesh = new TriangleMesh("res/models/bunny_res4.ply", true, true);
			
			
			
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
    Shader.setPenColor(gl, Color.WHITE);
	gl.glActiveTexture(GL.GL_TEXTURE0);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, avatarTexture.getId());
    copyTextureData(gl);	
    mesh.draw(gl, frame);
    	
}
    
    private void copyTextureData(GL3 gl) {
		int[] names = new int[1];
		// Copy across the buffer for the texture coordinates
		gl.glGenBuffers(1, names, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, quadTexCoords.capacity() * 2 * Float.BYTES, quadTexCoords.getBuffer(),
				GL.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
	}
    
	public void init(GL3 gl) {
		// TODO Auto-generated method stub
		avatarTexture = new Texture(gl, "res/textures/BrightPurpleMarble.png", "png", false);
		mesh.init(gl);
	
		int textureIndex = 0;
		
		quadTexCoords = new Point2DBuffer(mesh.verticesSize() * 6);
		List<Integer> indces = new ArrayList<>();
		for (int i = 0; i < mesh.verticesSize() - 2; i += 2) {
			quadTexCoords.put(textureIndex++, i, i);
	        quadTexCoords.put(textureIndex++, i+1f, i);
	        quadTexCoords.put(textureIndex++, i+1f, i+1f);
	        quadTexCoords.put(textureIndex++, i, i+1f);
		}
	}
	
	public boolean isShowing() {
		return isShowing;
	}
	

	
	

}
