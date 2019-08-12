package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.Vector4;
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
	// private Point3D lightPos;
	private float lightIntensity;

	// Properties of the material
	private float ambientCoefficient;
	private float diffuseCoefficient;
	private float specularCoefficient;
	private float phongExponent;
	
	private int isDay = 1;
	private int isAtteuationON = 0;
	private int fogEnabled = 0;
	private int dayNightMode = 0;

	private float cutOffAngle = 12.5f;
	private float outerAngle = 8.5f;
	private boolean afterInitFirstTime = false;
	float sunRotation = 0;
	float lightDir = -1f;

	// Texture vars
	Texture texture;
	private String textureFileName = "res/textures/grass.bmp";
	private String textureExt = "bmp";

	private Point3D skyColor = new Point3D(0.5f, 0.5f, 0.5f);

	Point2DBuffer quadTexCoords;
	int segments;

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
		segments = 10;
	}

	public void initTerrain() {
		ArrayList<Point3D> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<Integer>();

		int textureIndex = 0;
		quadTexCoords = new Point2DBuffer((depth) * (width) * 4);
		// creating a vertex for all points in the grid
		for (int z = 0; z < depth; z++) {
			for (int x = 0; x < width; x++) {
				Point3D p = convertToPoint3d(x, z);
				vertices.add(p);
			}
		}

		for (float i = 0; i < vertices.size() - 2; i += 2) {
			quadTexCoords.put(textureIndex++, i, i);
			quadTexCoords.put(textureIndex++, i + 1f, i);
			quadTexCoords.put(textureIndex++, i + 1f, i + 1f);
			quadTexCoords.put(textureIndex++, i, i + 1f);

		}

		// adding the indicies of the vertices in the order the triangles in the mesh is drawn
		for (int z = 0; z < depth - 1; z++) {
			for (int x = 0; x < width - 1; x++) {
				Point3D p0 = convertToPoint3d(x, z);
				Point3D p1 = convertToPoint3d(x, z + 1);
				Point3D p2 = convertToPoint3d(x + 1, z + 1);
				indices.add(vertices.indexOf(p0));
				indices.add(vertices.indexOf(p1));
				indices.add(vertices.indexOf(p2));

				p0 = convertToPoint3d(x, z);
				p1 = convertToPoint3d(x + 1, z + 1);
				p2 = convertToPoint3d(x + 1, z);
				indices.add(vertices.indexOf(p0));
				indices.add(vertices.indexOf(p1));
				indices.add(vertices.indexOf(p2));
			}
		}
		fan = new TriangleMesh(vertices, indices, true);

	}


	private void drawTrees(GL3 gl, CoordFrame3D frame) {
		for (Tree tree : trees) {
			tree.draw(gl, frame);
		}
	}

	private void drawRoads(GL3 gl, CoordFrame3D frame) {
		for (Road road : roads) {
			road.terrain = this;
			if (!afterInitFirstTime) {
				road.initGL(gl);
			}
			road.draw(gl, frame);
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
		ambientCoefficient = 0.4f;
		diffuseCoefficient = 0.5f;
		specularCoefficient = 0.8f;
		phongExponent = 1;

		Shader.setPoint3D(gl, "lightIntensity", new Point3D(lightIntensity, lightIntensity, lightIntensity));
		Shader.setPoint3D(gl, "ambientIntensity", new Point3D(ambientIntensity, ambientIntensity, ambientIntensity));
		Shader.setFloat(gl, "diffuseCoeff", diffuseCoefficient);
		Shader.setInt(gl, "isDay", isDay);

		Shader.setFloat(gl, "light.cutOff", (float) Math.cos(Math.toRadians(cutOffAngle)));
		Shader.setFloat(gl, "light.outerCutOff", (float) Math.cos(Math.toRadians(outerAngle)));
		
		Shader.setPoint3D(gl, "light.position", new Point3D(0, 0, 0f));

		Shader.setPoint3D(gl, "light.direction", new Point3D(0, -0.2f, -1));
		Shader.setFloat(gl, "light.ambientStrength", ambientCoefficient);
		Shader.setFloat(gl, "light.specularStrength", specularCoefficient);

		// Setting light properties
		Shader.setPoint3D(gl, "light.diffuse", new Point3D(0.8f, 0.8f, 0.8f));
		Shader.setPoint3D(gl, "light.specular", new Point3D(1.0f, 1.0f, 1.0f));
		Shader.setFloat(gl, "light.constant", 1.0f);
		Shader.setFloat(gl, "light.linear", 0.09f);
		Shader.setFloat(gl, "light.quadratic", 0.032f);

		// setting material phys
		Shader.setFloat(gl, "material.diffuse", 0);
		Shader.setFloat(gl, "material.specular", 1);
		Shader.setFloat(gl, "material.shininess", 32.0f);

		// material properties

		Shader.setFloat(gl, "light.constant", 1.0f);
		Shader.setFloat(gl, "light.linear", 0.09f);
		Shader.setFloat(gl, "light.quadratic", 0.032f);

		Shader.setFloat(gl, "phongExp", phongExponent);

		if(isDay == 1)
			dayMode();
		else
			nightMode();
	}

	public void draw(GL3 gl, CoordFrame3D frame) {

		Shader.setInt(gl, "isDay", isDay);
		Shader.setInt(gl, "isAtteuationON", isAtteuationON);
		Shader.setFloat(gl, "light.cutOff", (float) Math.cos(Math.toRadians(cutOffAngle)));

		
		Shader.setFloat(gl, "light.outerCutOff", (float) Math.cos(Math.toRadians(outerAngle)));
		
		Shader.setInt(gl, "isFogEnabled", fogEnabled);
		Shader.setPoint3D(gl, "skyColor", skyColor);

		if (dayNightMode == 1) {
			Vector4 sunVec = Matrix4.rotationX(sunRotation++).multiply(getSunlight().asPoint3D().asHomogenous());

			Shader.setPoint3D(gl, "sunVec", sunVec.asPoint3D());
			sunRotation = sunRotation % 360;
			float normalizedRot = sunRotation/255.0f;
			gl.glClearColor( normalizedRot,  normalizedRot, normalizedRot, 1);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			Shader.setFloat(gl, "light.ambientStrength", normalizedRot);//change ambient strength as well to have batter effect
		}
		else {
			Shader.setPoint3D(gl, "sunVec", getSunlight().asPoint3D());
		}
		
//		if (fogEnabled == 1) {
//
//			gl.glClearColor(skyColor.getX(), skyColor.getY(), skyColor.getZ(), 1);
//			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//		} else {
//			gl.glClearColor(1, 1, 1, 1);
//			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
//		}

		//For toggling DayNight mode
		Shader.setFloat(gl, "light.ambientStrength", ambientCoefficient);

		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());

		Shader.setPenColor(gl, Color.white);
		copyTextureData(gl);
		fan.draw(gl, frame);
		
		drawTrees(gl, frame);		// Drawing trees
		
		drawRoads(gl, frame);		// Drawing roads
		afterInitFirstTime = true;
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

	public void nightMode() {

		isDay = 0;
		ambientCoefficient = 0.4f;
	}

	public void dayMode() {

		isDay = 1;
		ambientCoefficient = 0.8f;
	}

	// method to convert a 2D point on the grid to 3D with its altitude included
	private Point3D convertToPoint3d(int x, int z) {
		float a = 0;
		if (x < 0 || z < 0 || x > width - 1 || z > depth - 1) {
			a = 0;
		} else {
			a = altitudes[x][z];
		}
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
		if (x < 0 || z < 0 || x > width - 1 || z > depth - 1) {			// if point outside the terrain, altitude = 0
			a = 0;
		}else {
			if (x == Math.round(x) && z == Math.round(z)) {				// if point is integer hence on the grid
					a = altitudes[(int) x][(int) z];
				} else {											// finding inside which square on the grid the point is
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
					// lower triangle of the square
					
					if(px ==  0) {
						double t = pz;
						a = (float) (t*p1.getY() + (1-t)*p3.getY());
					} else if (pz == 0) {
						double t = px;
						a = (float) (t*p1.getY() + (1-t)*p2.getY());
					} else if (px < pz) {
						Point3D r1 = p1;
						Point3D r2 = p3;
						Point3D r3 = p4;
	
						float L1 = ((r2.getZ() - r3.getZ()) * (x - r3.getX()) + (r3.getX() - r2.getX()) * (z - r3.getZ()))
								/ ((r2.getZ() - r3.getZ()) * (r1.getX() - r3.getX())
										+ (r3.getX() - r2.getX()) * (r1.getZ() - r3.getZ()));
						float L2 = ((r3.getZ() - r1.getZ()) * (x - r3.getX()) + (r1.getX() - r3.getX()) * (z - r3.getZ()))
								/ ((r2.getZ() - r3.getZ()) * (r1.getX() - r3.getX())
										+ (r3.getX() - r2.getX()) * (r1.getZ() - r3.getZ()));
						float L3 = 1 - L1 - L2;
	
						a = L1 * r1.getY() + L2 * r2.getY() + L3 * r3.getY();
						// upper triangle of the square
					} else if (px > pz) {
						Point3D r1 = p1;
						Point3D r2 = p4;
						Point3D r3 = p2;
	
						float L1 = ((r2.getZ() - r3.getZ()) * (x - r3.getX()) + (r3.getX() - r2.getX()) * (z - r3.getZ()))
								/ ((r2.getZ() - r3.getZ()) * (r1.getX() - r3.getX())
								+ (r3.getX() - r2.getX()) * (r1.getZ() - r3.getZ()));
						float L2 = ((r3.getZ() - r1.getZ()) * (x - r3.getX()) + (r1.getX() - r3.getX()) * (z - r3.getZ()))
								/ ((r2.getZ() - r3.getZ()) * (r1.getX() - r3.getX())
								+ (r3.getX() - r2.getX()) * (r1.getZ() - r3.getZ()));
						float L3 = 1 - L1 - L2;
	
						a = L1 * r1.getY() + L2 * r2.getY() + L3 * r3.getY();
						// on the diagonal splitting the two triangles
					} else if (px == pz) {
						Point3D r1 = p1;
						Point3D r2 = p4;
	
						double t = r2.getX() - x;
	
						a = (float) (t * r1.getY() + (1 - t) * r2.getY());
					}	else {
						System.out.println("COULDN'T INTERPOLATE");
					}	
				}
			}
		return a;
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch (code) {

		// to turn ON/ OFF attenuation
		case KeyEvent.VK_1:
			isAtteuationON = isAtteuationON == 0 ? 1 : 0;
			break;
		case KeyEvent.VK_2:// To show that attenuation works
			outerAngle += 1;
			break;
		case KeyEvent.VK_3:
			outerAngle -= 1;
			break;
		case KeyEvent.VK_F:
			fogEnabled = fogEnabled == 0 ? 1 : 0;
			break;
		case KeyEvent.VK_R:
			dayNightMode = dayNightMode == 0 ? 1 : 0;
			break;
		default:
			break;
		}

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
