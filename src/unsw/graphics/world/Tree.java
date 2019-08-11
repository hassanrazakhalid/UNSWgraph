package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree extends BaseWorld {

    private Point3D position;
    TriangleMesh triangleMesh;
    float scaleFactor = 0.2f;
    
 // Texture vars
 	Texture texture = null;
 	private String textureFileName = "res/textures/tree.png";
 	private String textureExt = "png";
 	Point2DBuffer quadTexCoords;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
        try {
			triangleMesh = new TriangleMesh("res/models/tree.ply", true, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@Override
	public void initGL(GL3 gl) {
		// TODO Auto-generated method stub
		triangleMesh.init(gl);
	}
    
    public Point3D getPosition() {
        return position;
    }
    
    private void initTexture(GL3 gl) {
    		texture = new Texture(gl, textureFileName, textureExt, false);
		int textureIndex = 0;
		quadTexCoords = new Point2DBuffer(triangleMesh.verticesSize() * 6);
		for (int i = 0; i < triangleMesh.verticesSize() - 2; i += 2) {
			
			quadTexCoords.put(textureIndex++, i, i);
	        quadTexCoords.put(textureIndex++, i+1f, i);
	        quadTexCoords.put(textureIndex++, i+1f, i+1f);
	        quadTexCoords.put(textureIndex++, i, i+1f);			
		}
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
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	if(texture == null) {
    		initTexture(gl);
    	}
    		Shader.setPenColor(gl, Color.WHITE);
    		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());
		copyTextureData(gl);
	    	CoordFrame3D treeFrame = frame.translate(position)
	    			.translate(0, 1f, 0)
	    			.scale(scaleFactor, scaleFactor, scaleFactor);

//	    	treeFrame.draw(gl);
	    	triangleMesh.draw(gl, treeFrame);
    }


}
