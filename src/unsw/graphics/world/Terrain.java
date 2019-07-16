package unsw.graphics.world;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleFan3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.scene.MathUtil;



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
    
    private float ambientIntensity;
//    private Point3D lightPos;
    private float lightIntensity;
    
    // Properties of the material
    private float ambientCoefficient;
    private float diffuseCoefficient;
    private float specularCoefficient;
    private float phongExponent;
    
    private List<TriangleWorld> allTriangles = new ArrayList<>();
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

    			
    			Point3D top_left = convertToPoint3d(xOffset, zOffset);
    			Point3D bot_left = convertToPoint3d(xOffset, zOffset+1);
    			Point3D top_right = convertToPoint3d(xOffset+1, zOffset);
    			Point3D bot_right = convertToPoint3d(xOffset+1, zOffset+1);
    			
    			
    			TriangleWorld tri = new TriangleWorld(
    					new Line3D(top_left, bot_left),
    					new Line3D(bot_left, top_right),
    					new Line3D(top_right, top_left)
    					);
    			allTriangles.add(tri);
    			
    			vertices.add(top_left);
    			vertices.add(bot_left);
    			vertices.add(top_right);
//    		vertices.add(convertToPoint3d(xOffset, zOffset));
//    		vertices.add(convertToPoint3d(xOffset, zOffset+1));
//    		vertices.add(convertToPoint3d(xOffset+1, zOffset));
    			
    			tri = new TriangleWorld(
    					new Line3D(top_right, bot_left),
    					new Line3D(bot_left, bot_right),
    					new Line3D(bot_right, top_right)
    					);
    			allTriangles.add(tri);
    		
    			vertices.add(top_right);
    			vertices.add(bot_left);
    			vertices.add(bot_right);
    		
//    		vertices.add(convertToPoint3d(xOffset, zOffset+1));
//    		vertices.add(convertToPoint3d(xOffset+1, zOffset+1));
//    		vertices.add(convertToPoint3d(xOffset+1, zOffset));
//    		vertices.add(convertToPoint3d(xOffset, zOffset+1));
    		
    		}    	
    	}
    	fan = new TriangleMesh(vertices, true);
    }
    
    private void drawTrees(GL3 gl, CoordFrame3D frame) {
    	for (Tree tree : trees) {
			tree.draw(gl, frame);
		}
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }
    
    public void init(GL3 gl) {
    	Shader.setPenColor(gl, Color.red);
    	fan.init(gl);
    	for (Tree tree : trees) {
			tree.initGL(gl);
		}
    	Shader.setPoint3D(gl, "sunVec" ,getSunlight().asPoint3D());
    	
    	ambientIntensity = 0.1f;
        lightIntensity = 1f;
        
        ambientCoefficient = 1;
        diffuseCoefficient = 0.4f;
        specularCoefficient = 0.2f;
        phongExponent = 8;
    	
    	Shader.setPoint3D(gl, "lightIntensity" ,new Point3D(lightIntensity, lightIntensity, lightIntensity));
    	Shader.setPoint3D(gl, "ambientIntensity" ,new Point3D(ambientIntensity, ambientIntensity, ambientIntensity));
    	Shader.setPoint3D(gl, "ambientCoeff" ,new Point3D(ambientCoefficient, ambientCoefficient, ambientCoefficient));
    	Shader.setPoint3D(gl, "diffuseCoeff" ,new Point3D(diffuseCoefficient, diffuseCoefficient, diffuseCoefficient));
    	Shader.setPoint3D(gl, "specularCoeff" ,new Point3D(specularCoefficient, specularCoefficient, specularCoefficient));
    	Shader.setFloat(gl, "phongExp", phongExponent);    	
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	Shader.setPenColor(gl, Color.white);
		fan.draw(gl, frame);
		drawTrees(gl, frame);
    }
    
    private Point3D convertToPoint3d(int x, int z) {
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
    	
    	//checking if point is integer, so just take the altitude
//    	if(x==Math.round(x) && z==Math.round(z)) {
//    		return altitudes[(int)x][(int) z];
//    	}
//    	
        float altitude = 0;
//        
//        int p1_z = (int) Math.floor(z);
//        int p2_z = (int) Math.ceil(z);
//        
//        int p1_x = (int) Math.floor(x);
//        int p2_x = (int) Math.ceil(x);
//        
//        Point3D pt1 = new Point3D(p1_x, 0, p1_z);
//        Point3D pt2 = new Point3D(p2_x, 0, p2_z);
//        
//        TriangleWorld result = null;
//        for (TriangleWorld triangleWorld : allTriangles) {
//        	 if(triangleWorld.isPointInTriangle(pt1)) {
//        		 result = triangleWorld;
//        		 break;
//        	 }
//        	 else if(triangleWorld.isPointInTriangle(pt2)) {
//        		 result = triangleWorld;
//        		 break;
//        	 }
//		}
//        if(result != null) {
//        	System.out.println("Found Triangle");
//        }
//        altitude = altitudes[x][z];
        // TODO: Implement this
        
        
//        float p_z_1 = ((z - p1_z) / (p2_z - p1_z)) * p2_z;
//        float p_z_2 = ((p2_z - z) / (p2_z - p1_z)) * p1_z;
//        float p_z = p_z_1 + p_z_2;
//        // go left and find interecting point and go right find intersecting point, find the interpolation
//        
//        altitude = p_z;
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
