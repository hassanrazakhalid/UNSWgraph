package unsw.graphics.world;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleFan3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;

    TriangleMesh fan;
    
    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
        initTerrain();
    }
    
    private void initTerrain() {
    	
    	List<Point3D> vertices = new ArrayList<>();
    	
    	for(int xOffset = 0; xOffset< width-1; xOffset++) {
    		for(int zOffset = 0; zOffset< depth-1; zOffset++) {
    		
    		
//    		float convertedX = (float)xOffset / width;
//    		float convertedY = (float)yOffset / depth;
    		
    		vertices.add(convertToPoint3d(xOffset, zOffset));
    		vertices.add(convertToPoint3d(xOffset, zOffset+1));
    		vertices.add(convertToPoint3d(xOffset+1, zOffset));
    		
    		
    		vertices.add(convertToPoint3d(xOffset, zOffset+1));
    		vertices.add(convertToPoint3d(xOffset+1, zOffset+1));
    		vertices.add(convertToPoint3d(xOffset+1, zOffset));
//    		vertices.add(convertToPoint3d(xOffset, zOffset+1));
    		
    		}    	
    	}
    	fan = new TriangleMesh(vertices, false);
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
    	Shader.setPenColor(gl, Color.black);
    	fan.init(gl);
		fan.draw(gl, frame);
    }
    
    private Point3D convertToPoint3d(int x, int z) {
//    	return new Point3D((float)x / width, altitudes[x][z], (float)z / depth);
    	return new Point3D((float)x, altitudes[x][z], (float)z);
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        float altitude = 0;

        // TODO: Implement this
        
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }

}
