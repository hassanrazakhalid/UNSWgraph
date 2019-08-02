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
    private Avatar avatar;
    private boolean viewAvatar;
    private Matrix4 viewMatrix;
    private boolean afterInitFirstTime = false;
    CoordFrame3D cameraFrame = new CoordFrame3D(Matrix4.identity());
    CoordFrame3D avatarFrame = new CoordFrame3D(Matrix4.identity());
    
    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        this.camera = new unsw.graphics.world.Camera(terrain);
        this.avatar = new Avatar(camera);
        viewAvatar = false;
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

		cameraFrame = CoordFrame3D.identity().rotateY(-camera.getGlobalRotation()).translate(new Point3D(0, 0, 0).minus(camera.getGlobalPosition()).asPoint3D());
		cameraFrame.draw(gl);
		avatarFrame = CoordFrame3D.identity().translate(camera.getGlobalPosition()).translate(0, -1, 0).rotateY(camera.getGlobalRotation());
		
//		avatarFrame = CoordFrame3D.identity().rotateY(camera.getGlobalRotation()).translate(new Point3D(0, 0, 0.5f).minus(camera.getGlobalPosition()).asPoint3D());
		avatarFrame.draw(gl);
//		avatarFrame = cameraFrame.translate(1, 0, 1);
		if (viewAvatar) {
//			avatarFrame = cameraFrame.translate(0, 0, 1);
			viewMatrix = CoordFrame3D.identity().translate(0, 0, -2).rotateY(-camera.getGlobalRotation()).translate(new Point3D(0, 0, 0).minus(camera.getGlobalPosition()).asPoint3D()).getMatrix();
		}else {
			viewMatrix = CoordFrame3D.identity().rotateY(-camera.getGlobalRotation()).translate(new Point3D(0, 0, 0).minus(camera.getGlobalPosition()).asPoint3D()).getMatrix();
//			avatarFrame	= cameraFrame;
					}
		
		Shader.setViewMatrix(gl, viewMatrix);
		terrain.draw(gl, CoordFrame3D.identity());
		avatar.draw(gl, avatarFrame.rotateY(-90).scale(3, 3, 3));
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
		
		avatar.init(gl);
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
		if(code == KeyEvent.VK_UP){
			camera.up(terrain);
		}
		if(code == KeyEvent.VK_DOWN){
			camera.down(terrain);
		}
		if(code == KeyEvent.VK_LEFT){
			camera.left();
		}
		if(code == KeyEvent.VK_RIGHT){
			camera.right();
		}
		if(code == KeyEvent.VK_SPACE) {
			if(viewAvatar) {
//				camera.viewAvatar();
//				cameraFrame = this.cameraFrame.translate(-0.5f*(float)Math.sin(Math.toRadians(camera.getGlobalRotation())), 0f, -0.5f*(float)Math.cos(Math.toRadians(camera.getGlobalRotation())));
//				avatar.changeView();
				viewAvatar = false;
			}else {
//				camera.viewAvatar();
//				cameraFrame = this.cameraFrame.translate(0.5f*(float)Math.sin(Math.toRadians(camera.getGlobalRotation())), 0f, 0.5f*(float)Math.cos(Math.toRadians(camera.getGlobalRotation())));
//				avatar.changeView();
				viewAvatar = true;
			}
			
		}
		
	}
}

	
