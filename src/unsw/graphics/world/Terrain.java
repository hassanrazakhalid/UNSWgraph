package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.FromDataPoints;

import com.jogamp.common.util.Ringbuffer;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.sun.javafx.geom.Vec2d;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
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
public class Terrain extends BaseWorld {

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
    
	
	//Texture vars
	Texture texture;
    private String textureFileName = "res/textures/grass.bmp";
    private String textureExt = "bmp";


	private List<TriangleWorld> allTriangles = new ArrayList<>();

	/**
	 * Create a new terrain
	 *
	 * @param width
	 *            The number of vertices in the x-direction
	 * @param depth
	 *            The number of vertices in the z-direction
	 */
	public Terrain(int width, int depth, Vector3 sunlight) {
		this.width = width;
		this.depth = depth;
		altitudes = new float[width][depth];
		trees = new ArrayList<Tree>();
		roads = new ArrayList<Road>();
		this.sunlight = sunlight;
	}

	public void initTerrain() {
//		ArrayList<Point2D> vertices = new ArrayList<>();
//		ArrayList<Integer> indices = new ArrayList<Integer>();
//		for(int z = 0; z < depth -1; z++) {
//			for(int x = 0; x < width -1; x++) {
//				Point2D p = new Point2D(x, z);
//				vertices.add(p);
//				System.out.println("x = " + p.getX() + "z = " + p.getY());
//			}
//		}
//		
//		System.out.println("vertices list: " + vertices);
//		
//		for(int z = 0; z < depth -2; z++) {
//			for(int x = 0; x < width -2; x++) {
//				Point2D p0 = new Point2D(x, z);
//				Point2D p1 = new Point2D(x, z+1);
//				Point2D p2 = new Point2D(x+1, z+1);
//				
//				indices.add(vertices.indexOf(p0));
//				indices.add(vertices.indexOf(p1));
//				indices.add(vertices.indexOf(p2));
//				
//				System.out.println("p0,x = " + p0.getX() + "p0,z = " + p0.getY());
//				
//				p0 = new Point2D(x, z);
//				p1 = new Point2D(x+1, z);
//				p2 = new Point2D(x+1, z+1);
//				indices.add(vertices.indexOf(p0));
//				indices.add(vertices.indexOf(p1));
//				indices.add(vertices.indexOf(p2));
//				
//				
//				
//				
//			}
//		}
//	
//		System.out.println(indices);
			
		List<Point3D> vertices = new ArrayList<>();
		System.out.println(altitudes);
		for (int xOffset = 0; xOffset < width - 1; xOffset++) {
			for (int zOffset = 0; zOffset < depth - 1; zOffset++) {

				Point3D top_left = convertToPoint3d(xOffset, zOffset);
				Point3D bot_left = convertToPoint3d(xOffset, zOffset + 1);
				Point3D top_right = convertToPoint3d(xOffset + 1, zOffset);
				Point3D bot_right = convertToPoint3d(xOffset + 1, zOffset + 1);

				TriangleWorld tri = new TriangleWorld(new Line3D(top_left, bot_left), new Line3D(bot_left, top_right),
						new Line3D(top_right, top_left));
				allTriangles.add(tri);

				vertices.add(top_left);
				vertices.add(bot_left);
				vertices.add(bot_right);
				 vertices.add(convertToPoint3d(xOffset, zOffset));
				 vertices.add(convertToPoint3d(xOffset, zOffset+1));
				 vertices.add(convertToPoint3d(xOffset+1, zOffset));

				tri = new TriangleWorld(new Line3D(top_right, bot_left), new Line3D(bot_left, bot_right),
						new Line3D(bot_right, top_right));
				allTriangles.add(tri);

				vertices.add(top_left);
				vertices.add(bot_right);
				vertices.add(top_right);

				 vertices.add(convertToPoint3d(xOffset, zOffset+1));
				 vertices.add(convertToPoint3d(xOffset+1, zOffset+1));
				 vertices.add(convertToPoint3d(xOffset+1, zOffset));
				 vertices.add(convertToPoint3d(xOffset, zOffset+1));

			}
		}
		fan = new TriangleMesh(vertices,true);
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

	@Override
	public void initGL(GL3 gl) {
		// TODO Auto-generated method stub
		Shader.setPenColor(gl, Color.red);
		texture = new Texture(gl, textureFileName, textureExt, false);
		fan.init(gl);
		for (Tree tree : trees) {
			tree.initGL(gl);
		}
		Shader.setPoint3D(gl, "sunVec", getSunlight().asPoint3D());

		ambientIntensity = 0.1f;
		lightIntensity = 1f;

		ambientCoefficient = 1;
		diffuseCoefficient = 0.4f;
		specularCoefficient = 0.2f;
		phongExponent = 8;

		Shader.setPoint3D(gl, "lightIntensity", new Point3D(lightIntensity, lightIntensity, lightIntensity));
		Shader.setPoint3D(gl, "ambientIntensity", new Point3D(ambientIntensity, ambientIntensity, ambientIntensity));
		Shader.setPoint3D(gl, "ambientCoeff", new Point3D(ambientCoefficient, ambientCoefficient, ambientCoefficient));
		Shader.setPoint3D(gl, "diffuseCoeff", new Point3D(diffuseCoefficient, diffuseCoefficient, diffuseCoefficient));
		Shader.setPoint3D(gl, "specularCoeff",
				new Point3D(specularCoefficient, specularCoefficient, specularCoefficient));
		Shader.setFloat(gl, "phongExp", phongExponent);
	}	

	public void draw(GL3 gl, CoordFrame3D frame) {
		Shader.setPenColor(gl, Color.white);
		gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());
        
		fan.draw(gl, frame);
		drawTrees(gl, frame);
	}

	private Point3D convertToPoint3d(int x, int z) {
		System.out.println(x + " ," + altitudes[z][x] + " ," + z);
		return new Point3D((float) x, altitudes[z][x], (float) z);
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
	 * Get the altitude at an arbitrary point. Non-integer points should be
	 * interpolated from neighbouring grid points
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public float altitude(float x, float z) {


//		 checking if point is integer, so just take the altitude
		 if(x==Math.round(x) && z==Math.round(z)) {
		 return altitudes[(int)x][(int) z];
		 }
		//
		float altitude = 0;
		//
		// int p1_z = (int) Math.floor(z);
		// int p2_z = (int) Math.ceil(z);
		//
		// int p1_x = (int) Math.floor(x);
		// int p2_x = (int) Math.ceil(x);
		//
		// Point3D pt1 = new Point3D(p1_x, 0, p1_z);
		// Point3D pt2 = new Point3D(p2_x, 0, p2_z);
		//
		// TriangleWorld result = null;
		// for (TriangleWorld triangleWorld : allTriangles) {
		// if(triangleWorld.isPointInTriangle(pt1)) {
		// result = triangleWorld;
		// break;
		// }
		// else if(triangleWorld.isPointInTriangle(pt2)) {
		// result = triangleWorld;
		// break;
		// }
		// }
		// if(result != null) {
		// System.out.println("Found Triangle");
		// }
		// altitude = altitudes[x][z];
		// TODO: Implement this

		// float p_z_1 = ((z - p1_z) / (p2_z - p1_z)) * p2_z;
		// float p_z_2 = ((p2_z - z) / (p2_z - p1_z)) * p1_z;
		// float p_z = p_z_1 + p_z_2;
		// // go left and find interecting point and go right find intersecting point,
		// find the interpolation
		//
		// altitude = p_z;
		
		 int x1 = (int) Math.floor(x);
	     int z1 = (int) Math.floor(z);
	     int x2 = (int) Math.ceil(x);
	     int z2 = (int) Math.ceil(z);

	     
	     Point3D p1 = convertToPoint3d(x1, z1);
	     Point3D p2 = convertToPoint3d(x2, z1);
	     Point3D p3 = convertToPoint3d(x1, z2);
	     Point3D p4 = convertToPoint3d(x2, z2);
	        
	     if(x > z) {
	    	   	Point3D r1 = p1;
	    	   	Point3D r2 = p2;
	    	   	Point3D r3 = p4;
	    	   	
	    	   	float L1 = ((r2.getZ()-r3.getZ())*(x-r3.getX())+(r3.getX()-r2.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
	    	   	float L2 = ((r3.getZ()-r1.getZ())*(x-r3.getX())+(r1.getX()-r3.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
	    	   	float L3 = 1 - L1 - L2;
	    	   	
	    	   	altitude = L1*r1.getY() + L2*r2.getY() + L3*r3.getY();
	    	   	
	     } else if (x < z) {
	    	   	Point3D r1 = p1;
	    	   	Point3D r2 = p3;
	    	   	Point3D r3 = p4;
	    	   	
	    	   	float L1 = ((r2.getZ()-r3.getZ())*(x-r3.getX())+(r3.getX()-r2.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
	    	   	float L2 = ((r3.getZ()-r1.getZ())*(x-r3.getX())+(r1.getX()-r3.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
	    	   	float L3 = 1 - L1 - L2;
	    	   	
	    	   	altitude = L1*r1.getY() + L2*r2.getY() + L3*r3.getY();
	     }
	     
	     
	     else {
	    	   	Point3D r1 = p1;
	    	   	Point3D r2 = p4;
	    	   	Point3D r3 = null;
	    	   	
	    	   	double t = r1.getX()-x;
	    	   	
	    	   	altitude = (float) (t*r1.getY() + (t-1)*r2.getY());
	    	   	
		}
	     
		return -altitude;
	}

	/**
	 * Add a tree at the specified (x,z) point. The tree's y coordinate is
	 * calculated from the altitude of the terrain at that point.
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
