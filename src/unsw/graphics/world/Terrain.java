package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;

import unsw.graphics.Matrix4;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;

import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
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

//    Point3DBuffer quadTexCoords;
    Point2DBuffer quadTexCoords;

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
		ArrayList<Point3D> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(int z = 0; z < depth -1; z++) {
			for(int x = 0; x < width -1; x++) {
//				Point3D p = new Point3D(x,0, z);
				Point3D p = convertToPoint3d(x, z);
				vertices.add(p);
			}
		}
		for(int z = 0; z < depth -2; z++) {
			for(int x = 0; x < width -2; x++) {
				Point3D p0 = convertToPoint3d(x, z);
				Point3D p1 = convertToPoint3d(x, z+1);
				Point3D p2 = convertToPoint3d(x+1, z+1);
				indices.add(vertices.indexOf(p0));
				indices.add(vertices.indexOf(p1));
				indices.add(vertices.indexOf(p2));
				
				p0 = convertToPoint3d(x, z);
				p1 = convertToPoint3d(x+1, z+1);
				p2 = convertToPoint3d(x+1, z);
				indices.add(vertices.indexOf(p0));
				indices.add(vertices.indexOf(p1));
				indices.add(vertices.indexOf(p2));
			}
		}

		quadTexCoords = new Point2DBuffer(width * depth * 6);
//		quadTexCoords = new Point2DBuffer(width * depth * 6);
		
		int textureIndex = 0 ;
		for (int xOffset = 0; xOffset < width - 1; xOffset++) {
			for (int zOffset = 0; zOffset < depth - 1; zOffset++) {

//		        quadTexCoords.put(textureIndex++, 0, 0, 0);
//		        quadTexCoords.put(textureIndex++, 1f, 0f, 1);
//		        quadTexCoords.put(textureIndex++, 1f, 1f, 1);
				
				quadTexCoords.put(textureIndex++, 0, 0); // lower left
		        quadTexCoords.put(textureIndex++, 1f, 0f);
		        quadTexCoords.put(textureIndex++, 1f, 1f);
		        quadTexCoords.put(textureIndex++, 0f, 1f);

				
//				quadTexCoords.put(textureIndex++, 0, 1, 0);
//		        quadTexCoords.put(textureIndex++, 1f, 0f, 1);
//		        quadTexCoords.put(textureIndex++, 1f, 1f, 1);
//				quadTexCoords.put(textureIndex++, 0.5f, 1);
//		        quadTexCoords.put(textureIndex++, 1f, 0f);
//		        quadTexCoords.put(textureIndex++, 1f, 1f);
			}
		}
	
//		System.out.println(indices);
		fan = new TriangleMesh(vertices, indices, true);

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
		texture = new Texture(gl, textureFileName, textureExt, false);
		fan.init(gl);
		for (Tree tree : trees) {
			tree.initGL(gl);
		}
		Shader.setPoint3D(gl, "sunVec", getSunlight().asPoint3D());

		ambientIntensity = 0.1f;
		lightIntensity = 1f;

		ambientCoefficient = 1;
		diffuseCoefficient = 0.1f;
		specularCoefficient = 0.8f;
		phongExponent = 1;

		Shader.setPoint3D(gl, "lightIntensity", new Point3D(lightIntensity, lightIntensity, lightIntensity));
		Shader.setPoint3D(gl, "ambientIntensity", new Point3D(ambientIntensity, ambientIntensity, ambientIntensity));
		Shader.setPoint3D(gl, "ambientCoeff", new Point3D(ambientCoefficient, ambientCoefficient, ambientCoefficient));
		Shader.setPoint3D(gl, "diffuseCoeff", new Point3D(diffuseCoefficient, diffuseCoefficient, diffuseCoefficient));
		Shader.setPoint3D(gl, "specularCoeff",
				new Point3D(specularCoefficient, specularCoefficient, specularCoefficient));
		Shader.setFloat(gl, "phongExp", phongExponent);
		
		
		int[] names = new int[1];
		// Copy across the buffer for the texture coordinates
		gl.glGenBuffers(1, names, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, names[0]);
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                quadTexCoords.capacity * 2 * Float.BYTES,
                quadTexCoords.getBuffer(), GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
	}	

	public void draw(GL3 gl, CoordFrame3D frame) {
		Shader.setPenColor(gl, Color.black);
		gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());
        
		fan.draw(gl, frame);
		drawTrees(gl, frame);
		
		
	}

	private Point3D convertToPoint3d(int x, int z) {
		float a = 0;
		if (x < 0 || z < 0 || x > width -1 || z > depth -1) {
			a = 0;	
		} else {
			a = altitudes[x][z];
		}
		System.out.println(x + " ," + a + " ," + z);
		return new Point3D((float) x, a, (float) z);
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
		float a = 0;
		//checking if point is integer hence on the grid
		if(x==Math.round(x) && z==Math.round(z)) {
			if (x < 0 || z < 0 || x > width -1 || z > depth -1) {
				a = 0;	
			} else {
				a = altitudes[(int) x][(int) z];
			}
		// if point not on grid, calculating the altitude
		 } else {
				if (x < 0 || z < 0 || x > width -1 || z > depth -1) {
					a = 0;	
				} else {
					int x0 = (int) Math.floor(x);
					int z0 = (int) Math.floor(z);
					float px = x - x0;
					float pz = z - z0;
				     
				    int x1 = (int) Math.ceil(x);
				    int z1 = (int) Math.ceil(z);

				    Point3D p1 = convertToPoint3d(x0, z0);
				    Point3D p2 = convertToPoint3d(x1, z0);
				    Point3D p3 = convertToPoint3d(x0, z1);
				    Point3D p4 = convertToPoint3d(x1, z1);
			        
				    if(px < pz) {
				    		Point3D r1 = p1;
				    	  	Point3D r2 = p3;
				    	   	Point3D r3 = p4;
				    	   	
				    	   	float L1 = ((r2.getZ()-r3.getZ())*(x-r3.getX())+(r3.getX()-r2.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
				    	   	float L2 = ((r3.getZ()-r1.getZ())*(x-r3.getX())+(r1.getX()-r3.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
				    	   	float L3 = 1 - L1 - L2;
				    	   	
				    	   	a = L1*r1.getY() + L2*r2.getY() + L3*r3.getY();
			    	   	
				    } else if (px > pz) {
				    	   	Point3D r1 = p1;
				    	   	Point3D r2 = p4;
				    	   	Point3D r3 = p2;
				    	   	
				    	   	float L1 = ((r2.getZ()-r3.getZ())*(x-r3.getX())+(r3.getX()-r2.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
				    	   	float L2 = ((r3.getZ()-r1.getZ())*(x-r3.getX())+(r1.getX()-r3.getX())*(z-r3.getZ()))/(r2.getZ()-r3.getZ())*(r1.getX()-r3.getX())+(r3.getX()-r2.getX())*(r1.getZ()-r3.getZ());
				    	   	float L3 = 1 - L1 - L2;
				    	   	
				    	   	a = L1*r1.getY() + L2*r2.getY() + L3*r3.getY();
				    } else if (px == pz) {
				    	   	Point3D r1 = p1;
				    	   	Point3D r2 = p4; 	
				    	   	
				    	   	double t = r2.getX()-x;	
				    	   	
				    	   	a = (float) (t*r1.getY() + (1-t)*r2.getY());
				    }  	
				}
		 	}
		return a;
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
