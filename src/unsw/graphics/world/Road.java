package unsw.graphics.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;
import unsw.graphics.scene.MathUtil;

/**
 * COMMENT: Comment Road
 *
 * @author malcolmr
 */
public class Road extends BaseWorld {

	private List<Point2D> points;
	private float width;
	int segments;
	Terrain terrain;
	TriangleMesh roadMesh = null;
	/**
	 * Create a new road with the specified spine
	 *
	 * @param width
	 * @param spine
	 */

	// Texture vars
	Texture texture;
	private String textureFileName = "res/textures/black-road-texture.png";
	private String textureExt = "png";
	Point2DBuffer quadTexCoords;
	float global_y = -1f;//1.5f;
//	float global_y = 1.5f;

	public Road(float width, List<Point2D> spine) {
		this.width = width;
		this.points = spine;
		segments = 20;
	}

	/**
	 * The width of the road.
	 * 
	 * @return
	 */
	public double width() {
		return width;
	}

	/**
	 * Get the number of segments in the curve
	 * 
	 * @return
	 */
	public int size() {
		return points.size() / 3;
	}

	/**
	 * Get the specified control point.
	 * 
	 * @param i
	 * @return
	 */
	public Point2D controlPoint(int i) {
		return points.get(i);
	}

	/**
	 * Get a point on the spine. The parameter t may vary from 0 to size(). Points
	 * on the kth segment take have parameters in the range (k, k+1).
	 * 
	 * @param t
	 * @return
	 */
	public Point2D point(float t) {
		int i = (int) Math.floor(t);
		t = t - i;
		i *= 3;

		Point2D p0 = points.get(i++);
		Point2D p1 = points.get(i++);
		Point2D p2 = points.get(i++);
		Point2D p3 = points.get(i++);

		float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
		float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();

		return new Point2D(x, y);
	}

	public Point2D getNormal(float t) {

		int i = (int) Math.floor(t);
		t = t - i;

		i *= 3;

		Point2D p0 = points.get(i++);
		Point2D p1 = points.get(i++);
		Point2D p2 = points.get(i++);
		Point2D p3 = points.get(i++);

		// just to check from tutorial slides
		// t = 0.25f;
		// p0 = new Point2D(0, 0);
		// p1 = new Point2D(4, 16);
		// p2 = new Point2D(20, 8);
		// p3 = new Point2D(20, 0);

		// Point2D pt = point(t);

		float x_deri = computeBezierDerivative(t, p0.getX(), p1.getX(), p2.getX(), p3.getX());
		float y_deri = computeBezierDerivative(t, p0.getY(), p1.getY(), p2.getY(), p3.getY());
		Point2D normalized_tangent = MathUtil.normalize(new Point2D(x_deri, y_deri));
		return new Point2D(-normalized_tangent.getY(), normalized_tangent.getX());
	}

	/**
	 * Here taking derivative of cubuic curve
	 * 
	 * @param t
	 * @param a
	 *            control point1
	 * @param b
	 *            control point2
	 * @param c
	 *            control point3
	 * @param d
	 *            control point4
	 * @return
	 */

	private float computeBezierDerivative(float t, float a, float b, float c, float d) {
		a = 3 * (b - a);
		b = 3 * (c - b);
		c = 3 * (d - c);
		return a * (1 - t) * (1 - t) + 2 * b * (1 - t) * t + c * t * t;
	}

	/**
	 * Calculate the Bezier coefficients
	 * 
	 * @param i
	 * @param t
	 * @return
	 */
	private float b(int i, float t) {

		switch (i) {

		case 0:
			return (1 - t) * (1 - t) * (1 - t);

		case 1:
			return 3 * (1 - t) * (1 - t) * t;

		case 2:
			return 3 * (1 - t) * t * t;

		case 3:
			return t * t * t;
		}

		// this should never happen
		throw new IllegalArgumentException("" + i);
	}

	@Override
	public void initGL(GL3 gl) {
		// TODO Auto-generated method stub

	}
	
	private List<Point3D> processPointOnCourse(float t) {
		Point2D pt = point(t);
//		System.out.println(pt.toString());
		float y = 0.01f;
//		if(!SharedData.instance.fileName.contains("test1")) {
		if(global_y == -1) {
			System.out.println(controlPoint(0));
			y = terrain.altitude(controlPoint(0).getX(), controlPoint(0).getY());
			y += 0.01;
			global_y = y;
		}
		Point2D tangent_pt1 = getNormal(t);
//		System.out.println(pt.toString());
		float halfWidth = (float) width() / 2;
		Point2D normal_line_pt = tangent_pt1.mulScaler(halfWidth).translate(pt.getX(), pt.getY());
		pt = tangent_pt1.mulScaler(-1).mulScaler(halfWidth).translate(pt.getX(), pt.getY());
//		pt = normal_line_pt.mulScaler(-1);
		
//		Point2D normal_line_pt = tangent_pt1.translate(pt.getX() , pt.getY());
//		pt = tangent_pt1.mulScaler(-1).translate(pt.getX(), pt.getY());

//		float distance = MathUtil.distance(pt, normal_line_pt);
//		System.out.println(distance);
		
//		normal_line_pt = new Point2D(normal_line_pt.getX(), normal_line_pt.getY() * (float) width() / 2);
//		pt = new Point2D(pt.getX(), pt.getY()* (float) width() / 2);
		
//		System.out.println(MathUtil.distance(pt, normal_line_pt));
		
		System.out.println("y = " + terrain.altitude(normal_line_pt.getX(), normal_line_pt.getY()) + " for pt =" + normal_line_pt.toString());
//		}
		Point3D normal_3d = new Point3D(normal_line_pt.getX(), global_y, normal_line_pt.getY());
		
		List<Point3D> res = new ArrayList<>();
		res.add(new Point3D(pt.getX(), global_y, pt.getY()));
		res.add(normal_3d);
		return res;
	}

	@Override
	public void draw(GL3 gl, CoordFrame3D frame) {
		// TODO Auto-generated method stub

		Vector4 orign = frame.getMatrix().getColumn(3);
		
		if (roadMesh == null) {
			texture = new Texture(gl, textureFileName, textureExt, false);
			List<Point3D> pts = new ArrayList<>();
			float dt = 1.0f / segments;
			
//			global_y = orign.asPoint3D().getY();//1.5f;
//			global_y += 1.01f;
			
//			float dt = 1.0f/segments;
//	    	
//	    	for(int i = 0; i <= segments; i++){        		
//	    		float t = i*dt;
//	    		curve.add(new Point2D(getX(t), getY(t)));
//	    	}
			System.out.println(size());
			float t;
			for (t = 0; t < size() - 0.001; t+=dt) {
//			for (int index = 0; index <= segments; index++) {
				
//				float final_t = t + dt;
				
//				System.out.println(final_t);
//				if(fin)
//				Point2D tangent_pt1 = getNormal(t);
//				Point2D pt = point(t);
//				System.out.println(pt.toString());
//
//				Point2D normal_line_pt = tangent_pt1.translate(pt.getX(), pt.getY()).mulScaler((float) width() / 2);
//				pt = tangent_pt1.mulScaler(-1).translate(pt.getX(), pt.getY()).mulScaler((float) width() / 2);
//
//				float y = terrain.altitude(normal_line_pt.getX(), normal_line_pt.getY());
//				y += 0.01;
//				Point3D normal_3d = new Point3D(normal_line_pt.getX(), y, normal_line_pt.getY());

				pts.addAll(processPointOnCourse(t));
//				pts.add(new Point3D(pt.getX(), y, pt.getY()));
//				pts.add(normal_3d);
			}
//			pts.addAll(processPointOnCourse(1f));
			System.out.println(pts.get(pts.size()-1));
			System.out.println(pts.get(pts.size()-2));
			
			int textureIndex = 0;
			quadTexCoords = new Point2DBuffer(pts.size() * 6);
			List<Integer> indces = new ArrayList<>();
			for (int i = 0; i < pts.size() - 2; i += 2) {
				//clockwise
				indces.add(i + 1);
				indces.add(i + 3);
				indces.add(i);
				
				indces.add(i + 3);
				indces.add(i + 2);
				indces.add(i);
				
				//anitclockwise
//				indces.add(i + 1);
//				indces.add(i);
//				indces.add(i + 3);
//				
//				
//				indces.add(i + 3);
//				indces.add(i);
//				indces.add(i + 2);
				
				
				quadTexCoords.put(textureIndex++, i, i);
		        quadTexCoords.put(textureIndex++, i+1f, i);
		        quadTexCoords.put(textureIndex++, i+1f, i+1f);
		        quadTexCoords.put(textureIndex++, i, i+1f);
		        
//				quadTexCoords.put(textureIndex++, i+1f, i);
//				quadTexCoords.put(textureIndex++, i, i+1f);
//				quadTexCoords.put(textureIndex++, i+1f, i);
//				quadTexCoords.put(textureIndex++, i+1f, i+1f);

				
				
			}
			roadMesh = new TriangleMesh(pts, indces, true);
			roadMesh.init(gl);
		}

		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());
		copyTextureData(gl);
		Shader.setPenColor(gl, Color.white);
		roadMesh.draw(gl, frame);
	}
	
	
	
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code  == KeyEvent.VK_1) {
			global_y += 0.1f;
			roadMesh = null;
		}
		else if(code  == KeyEvent.VK_2) {
			global_y -= 0.1f;
			roadMesh = null;
		}
		System.out.println(global_y);
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
}
