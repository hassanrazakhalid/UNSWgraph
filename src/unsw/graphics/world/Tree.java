package unsw.graphics.world;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
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
    float scaleFactor = 0.25f;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
        try {
			triangleMesh = new TriangleMesh("res/models/tree.ply");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
    	
    	CoordFrame3D modelMatrix = frame.translate(position)
    			.translate(0, 1.25f, 0)
    			.scale(scaleFactor, scaleFactor, scaleFactor);
    	
    	
    	triangleMesh.draw(gl, modelMatrix);
    	
    }

	@Override
	public void initGL(GL3 gl) {
		// TODO Auto-generated method stub
		triangleMesh.init(gl);
	}

}
