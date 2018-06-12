package mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import java.util.ArrayList;

public class Main
        extends SimpleApplication
        implements ActionListener,
                  PhysicsCollisionListener {
    
    public static void main(String[] args) {
        Main app = new Main();
        //app.showSettings = false;
        app.start();
    }
    private BulletAppState bulletAppState;
    private PlayerCameraNode player;
    private boolean up = false, down = false, left = false, right = false;
    private Material boxMatColosion;
    private ArrayList<Geometry> listGeometry = new ArrayList<Geometry>();
    
    // 0 - Ponto Vazio
    // 1 - Carpete
    // 2 - Tijolo
    // 3 - Player
    // 4 - Ponto Certo
    // 5 - Caixa
    int mat[][] = { {2,2,2,2,2,0,0,0,0},
                    {2,3,1,1,2,0,2,2,2},
                    {2,1,5,5,2,0,2,4,2},
                    {2,1,5,1,2,0,2,4,2},
                    {2,2,2,1,2,2,2,4,2},
                    {0,2,2,1,1,1,1,1,2},
                    {0,2,1,1,1,2,1,1,2},
                    {0,2,1,1,1,2,2,2,2},
                    {0,2,2,2,2,2,0,0,0}

    };
    
    @Override
    public void simpleInitApp() {        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        createLigth();
        
        for(int i=0; i < mat.length; i++){
            for (int j=0; j < mat[0].length; j++){ 
                
                int resul = mat[i][j];
                
                if(resul == 0)                {
                    createCubo(i*2,j*2,"Preto.jpg",true, 0, "ground");
                }
                else if(resul == 4){
                    createCubo(i*2,j*2,"box-win.png",true, 0, "box-win");
                }
                else{
                    // 1
                    createCubo(i*2,j*2,"Carpete.jpg",true, 0, "normal");
                    
                    if(resul == 2){
                        createCubo(i*2,j*2,"Tijolo.jpg",false, 0, "tijolo");
                    }
                    else if(resul == 3){
                        createPlayer(i,j);
                    }
                    else if(resul == 5){
                        createCubo(i*2,j*2,"Caixa.jpg",false, 10f, "caixa");
                    }
                }  
            }
        }
        
        boxMatColosion = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMatColosion.setBoolean("UseMaterialColors", true);
        boxMatColosion.setColor("Ambient", ColorRGBA.Red);
        boxMatColosion.setColor("Diffuse", ColorRGBA.Red); 

        initKeys();

        bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    @Override
    public void simpleUpdate(float tpf) {

        player.upDateKeys(tpf, up, down, left, right);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        
    }

    private void createPlayer(int x, int z) {

        player = new PlayerCameraNode("player", assetManager, bulletAppState, cam);
        player.setLocalTranslation(10, 5, 5);
        rootNode.attachChild(player);
        flyCam.setEnabled(true);
        
    }

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        switch (binding) {
            case "CharLeft":
                if (value) {
                    left = true;
                } else {
                    left = false;
                }
                break;
            case "CharRight":
                if (value) {
                    right = true;
                } else {
                    right = false;
                }
                break;
        }
        switch (binding) {
            case "CharForward":
                if (value) {
                    up = true;
                } else {
                    up = false;
                }
                break;
            case "CharBackward":
                if (value) {
                    down = true;
                } else {
                    down = false;
                }
                break;
        }
    }

    private void createLigth() {
        
        DirectionalLight l1 = new DirectionalLight();
        l1.setDirection(new Vector3f(1, -0.7f, 0));
        rootNode.addLight(l1);

        DirectionalLight l2 = new DirectionalLight();
        l2.setDirection(new Vector3f(-1, 0, 0));
        rootNode.addLight(l2);

        DirectionalLight l3 = new DirectionalLight();
        l3.setDirection(new Vector3f(0, 0, -1.0f));
        rootNode.addLight(l3);

        DirectionalLight l4 = new DirectionalLight();
        l4.setDirection(new Vector3f(0, 0, 1.0f));
        rootNode.addLight(l4);


        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);


    }

    private Geometry createCubo(float x, float z, String texture, boolean ground, float rigidbody, String name) {
        /* A colored lit cube. Needs light source! */        
        
        Box boxMesh = new Box(1f, 1f, 1f);
        Geometry boxGeo = new Geometry(name, boxMesh);
        //Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
        Texture tijoloTex = assetManager.loadTexture("Textures/" + texture); 
        boxMat.setTexture("ColorMap", tijoloTex); 
        
        
        boxGeo.setMaterial(boxMat);
        
        if(ground)
            boxGeo.setLocalTranslation(x-2,4f,z-2);
        else
            boxGeo.setLocalTranslation(x-2,6f,z-2);
        rootNode.attachChild(boxGeo);
        
        RigidBodyControl boxPhysicsNode = new RigidBodyControl(rigidbody);
        boxGeo.addControl(boxPhysicsNode);
        bulletAppState.getPhysicsSpace().add(boxPhysicsNode);
        
        return boxGeo;
 
    }

    private void initKeys() {
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharForward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharBackward", new KeyTrigger(KeyInput.KEY_S));

        inputManager.addListener(this, "CharLeft", "CharRight");
        inputManager.addListener(this, "CharForward", "CharBackward");

    }
    
    
    @Override
    public void collision(PhysicsCollisionEvent event) {
        
        //Muda a cor do cubo ao ser clicado
        
        if(event.getNodeA().getName().equals("box-win") || 
           event.getNodeB().getName().equals("box-win")){

            if(event.getNodeA().getName().equals("caixa")){
                  Spatial s = event.getNodeA();
                  s.setMaterial(boxMatColosion);
            }
            else
            if(event.getNodeB().getName().equals("caixar")){
                  Spatial s = event.getNodeB();
                  s.setMaterial(boxMatColosion);
            }
            
        }
    }
}