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
    float scaleFactor = 0.2f;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
        try {
			triangleMesh = new TriangleMesh("res/models/tree.ply");
			
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
    
    
    public void draw(GL3 gl, CoordFrame3D frame) {
	    	CoordFrame3D treeFrame = frame.translate(position)
	    			.translate(0, 1f, 0)
	    			.scale(scaleFactor, scaleFactor, scaleFactor);

	    	treeFrame.draw(gl);
	    	triangleMesh.draw(gl, treeFrame);
    }



}
