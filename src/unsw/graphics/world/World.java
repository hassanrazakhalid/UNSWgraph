package unsw.graphics.world;

import java.io.File;
import java.io.FileNotFoundException;

import com.jogamp.opengl.GL;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;

/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D implements KeyListener{

    private Terrain terrain;
    private unsw.graphics.world.Camera camera;
    private boolean afterInitFirstTime = false;
    CoordFrame3D cameraFrame = new CoordFrame3D(Matrix4.identity());
    
    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        this.camera = new unsw.graphics.world.Camera(terrain);
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        terrain.initTerrain();
        World world = new World(terrain);
        world.start();
    }

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		
		if(!afterInitFirstTime) {
			afterInitFirstTime = true;
			terrain.initGL(gl);
		}
		//computing the frame for the view matrix
		cameraFrame = CoordFrame3D.identity().rotateY(-camera.getGlobalRotation()).translate(new Point3D(0, 0, 0).minus(camera.getGlobalPosition()).asPoint3D());
	
		Shader.setViewMatrix(gl, cameraFrame.getMatrix());
		terrain.draw(gl, CoordFrame3D.identity());
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
	}

	@Override
	public void init(GL3 gl) {
		super.init(gl);		
		shader = new Shader(gl, "shaders/vertex_phong_world.glsl",
                "shaders/fragment_phong_world.glsl");
        shader.use(gl);
        
        gl.glPointSize(10);
        
        // Turn on the depth buffer
        gl.glEnable(GL.GL_DEPTH_TEST);
        
        // Cull back faces
        gl.glEnable(GL.GL_CULL_FACE);
		getWindow().addKeyListener(this);
	}

	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		switch(code) {
		case KeyEvent.VK_UP:
			camera.up(terrain);
			break;
		case KeyEvent.VK_DOWN:
			camera.down(terrain);
			break;
		case KeyEvent.VK_LEFT:
			camera.left();
			break;
		case KeyEvent.VK_RIGHT:
			camera.right();
			break;
		case KeyEvent.VK_N:
			terrain.nightMode();
			break;
		case KeyEvent.VK_D:
			terrain.dayMode();
			break;
		default:
			break;
		}
	}
}

	
