package unsw.graphics.world;



import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;
import com.sun.javafx.geom.Vec2d;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
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
    	List<Integer> indicies;
  
    	
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
    		
    		indicies.
    		
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
    
//    private Color shading(CoordFrame3D frame, Point3D point, Vector3 normal) {
//        // Compute the point and the normal in global coordinates
//        Point3D p = frame.transform(point);
//        Vector3 m = frame.transform(normal).normalize();
//        
//        // The vector from the point to the light source.
//        Point3D lightPos =  this.sunlight.asPoint3D().translate(point.asHomogenous().trim());
//        Vector3 s = lightPos.minus(p).normalize();
//        
//        // The ambient intensity (same for all points)
//        float ambient = ambientIntensity * ambientCoefficient;
//        
//        // The diffuse intensity at this point
//        float diffuse = lightIntensity * diffuseCoefficient * s.dotp(m);
//        
//        // The vector from the point to the camera
//        // Note: we're assuming the view transform is the identity transform
//        Vector3 v = p.asHomogenous().trim().scale(-1).normalize(); //v = normalize(-p)
//       
//        // The reflected vector
//        Vector3 r = s.negate().plus(m.scale(2*s.dotp(m)));
//        
//        // The specular intensity at this point
//        float specular = lightIntensity * specularCoefficient 
//                * (float) Math.pow(r.dotp(v), phongExponent);
//        
//        float intensity = MathUtil.clamp(ambient + diffuse + specular, 0, 1);
//        return new Color(intensity, intensity, intensity);
//    }

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
//
//        int x1 = (int) Math.floor(x);
//        int z1 = (int) Math.floor(z);
//        int x2 = Math.round(x);
//        int z2 = Math.round(z);
//        
//        Point2D p = new Point2D(x, z); 
//        Point2D p1 = new Point2D(x1, z1);
//        
//        
//        p = p0 + (p1 - p0) * s + (p2 - p0) * t
//        
//        Vec2d vec = new Vec2d(x2-x1, z2-z1);
//        vec.distance(x, x);
//        vec.
//        
//        if(x - x1 > 0.5) {
//        		if(z - z1 > 0.5) {
//        			int p1x = (int) Math.floor(x);
//        			int p1z = (int) Math.floor(x);
//        		}
//        		
//        }
//        
//        
//        int x3 = 0;
//        int z1 = (int) Math.floor(z);
//        
//        int z3 = 0;
//        double a1 = getGridAltitude(x1, z1);
//        double a2 = getGridAltitude(x2, z2);
//        double a3 = 0;
//        
//        		
//
//        float L1 = ((z2-z3)*(x-x3)+(x3-x2)*(z-z3))/(z2-z3)*(x1-x3)+(x3-x2)*(z1-z3);
//        float L2 = ((z3-z1)*(x-x3)+(x1-x3)*(z-z3))/(z2-z3)*(x1-x3)+(x3-x2)*(z1-z3);
//        float L3 = 1 - L1 - L2;
//        
//        altitude = (float) (L1*a1 + L2*a2 + L3*a3);
        
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
