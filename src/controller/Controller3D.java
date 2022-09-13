package controller;

import model.*;
import rasterize.Raster;
import renderer.GPURenderer;
import renderer.RendererZBuffer;
import shader.BasicColorShader;
import shader.TextureShader;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller3D {

    private final Panel panel;
    private Raster<Integer> imageRaster;
    private GPURenderer renderer;

    private List<Element> elementBuffer;
    private List<Integer> indexBuffer;
    private List<Vertex> vertexBuffer;
    private List<Solid> solids;
    private Solid solidSelected;
    private int solidNumber;
    private int axisNumber;
    private int defaultSolid;
    private Boolean wireFrame;
    private Boolean texture;
    private Cube cube;
    private Tetrahedron tetrahedron;
    private TruncatedTetrahedron truncatedTetrahedron;
    private Bicubics bicubics;

    private Mat4 model, view, projection;
    private Camera camera;
    private BasicColorShader shader;
    private TextureShader shaderTexture;
    private int endX;
    private int endY;
    private int moveX;
    private int moveY;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.imageRaster = panel.getRaster();
        this.renderer = new RendererZBuffer(imageRaster);

        initMatrices();
        initListeners(panel);
        createScene();
    }

    private void createScene() {
        this.imageRaster = panel.getRaster();
        this.renderer = new RendererZBuffer(imageRaster);

        elementBuffer = new ArrayList<>();
        indexBuffer = new ArrayList<>();
        vertexBuffer = new ArrayList<>();

        wireFrame = false;
        texture = false;
        solids = new ArrayList<>();
        defaultSolid = 1;
        axisNumber = 0;

        Axis axis = new Axis();
        cube = new Cube();
        tetrahedron = new Tetrahedron();
        truncatedTetrahedron = new TruncatedTetrahedron();
        bicubics = new Bicubics();

        solids.add(axis);
        solids.add(cube);
        solids.add(tetrahedron);
        solids.add(truncatedTetrahedron);
        solids.add(bicubics);

        solidSelected = solids.get(defaultSolid);

        shader = new BasicColorShader();

        shaderTexture = new TextureShader();

        vertexBuffer = solidSelected.getVertexBuffer();
        indexBuffer = solidSelected.getIndexBuffer();
        elementBuffer = solidSelected.getElementBuffer();

        display();
    }

    private void initMatrices() {
        model = new Mat4Identity();

        Vec3D e = new Vec3D(0, -5, 2);
        Vec3D v = new Vec3D(0, 5, -2);
        Vec3D up = new Vec3D(0, 0, 1);
        view = new Mat4ViewRH(e, v, up);

        camera = new Camera()
                .withPosition(e)
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-20));
        camera.getViewMatrix();

        projection = new Mat4PerspRH(
                Math.PI / 3,
                imageRaster.getHeight() / (float) imageRaster.getWidth(),
                0.5,
                50
        );
    }

    private void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    endX = e.getX();
                    endY = e.getY();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    endX = e.getX();
                    endY = e.getY();
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    moveX = endX;
                    moveY = endY;

                    endX = e.getX();
                    endY = e.getY();

                    int movementX = endX - moveX;
                    int movementY = endY - moveY;

                    camera = camera.addAzimuth((double) -movementX * Math.PI / 720);
                    camera = camera.addZenith((double) -movementY * Math.PI / 720);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    moveX = endX;
                    moveY = endY;

                    endX = e.getX();
                    endY = e.getY();

                    int movementX = endX - moveX;
                    int movementY = endY - moveY;

                    Mat4 rotation = new Mat4RotXYZ(0, -movementY * Math.PI / 180, movementX * Math.PI / 180);
                    solidSelected.setModel(solidSelected.getModel().mul(rotation));
                }
                display();
            }
        });

        panel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    camera = camera.forward(0.5);
                } else {
                    camera = camera.backward(0.5);
                }
                display();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    hardClear();
                } else if (e.getKeyCode() == KeyEvent.VK_H) {
                    JOptionPane.showMessageDialog(panel,
                            "Autor je © Pavel Hampl \n" +
                                    "W,A,S,D -> pohyb do stran po osách X, Y \n" +
                                    "Q,E -> pohyb nahoru nebo dolu na ose Z \n" +
                                    "R,T-> zoom objektů \n" +
                                    "J,K,L-> translace aktivního tělesa \n" +
                                    "Levé tlačítko myši -> otáčení s kamerou \n" +
                                    "Pravé tlačítko myši a klávesy Z,U,I -> rotace s tělesy \n" +
                                    "Pohyb kolečkem myši -> pohyb kamerou dopředu a dozadu \n" +
                                    "R,T-> zoom aktivního tělesa \n" +
                                    "B-> výběr aktivního tělesa \n" +
                                    "O.P-> změna mezi perspektivním pohledem a ortogonálním pohledem \n" +
                                    "V-> změna mezi wireFramem a vyplněnými plochami \n" +
                                    "N-> přepínání mezi texturou a barevnými plochami",
                            "Nápověda k ovládání aplikace",
                            JOptionPane.PLAIN_MESSAGE);
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.forward(0.5);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(0.5);
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(0.5);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(0.5);
                } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    camera = camera.up(0.5);
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    camera = camera.down(0.5);
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    Mat4 zoom = new Mat4Scale(0.5);
                    solidSelected.setModel(solidSelected.getModel().mul(zoom));
                } else if (e.getKeyCode() == KeyEvent.VK_T) {
                    Mat4 zoom = new Mat4Scale(1.5);
                    solidSelected.setModel(solidSelected.getModel().mul(zoom));
                } else if (e.getKeyCode() == KeyEvent.VK_Z) {
                    Mat4 rotX = new Mat4RotX(0.5);
                    solidSelected.setModel(solidSelected.getModel().mul(rotX));
                } else if (e.getKeyCode() == KeyEvent.VK_U) {
                    Mat4 rotY = new Mat4RotY(0.5);
                    solidSelected.setModel(solidSelected.getModel().mul(rotY));
                } else if (e.getKeyCode() == KeyEvent.VK_I) {
                    Mat4 rotZ = new Mat4RotZ(0.5);
                    solidSelected.setModel(solidSelected.getModel().mul(rotZ));
                } else if (e.getKeyCode() == KeyEvent.VK_J) {
                    Mat4 translX = new Mat4Transl(0.5, 0, 0);
                    solidSelected.setModel(solidSelected.getModel().mul(translX));
                } else if (e.getKeyCode() == KeyEvent.VK_K) {
                    Mat4 translY = new Mat4Transl(0, 0.5, 0);
                    solidSelected.setModel(solidSelected.getModel().mul(translY));
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    Mat4 translZ = new Mat4Transl(0, 0, 0.5);
                    solidSelected.setModel(solidSelected.getModel().mul(translZ));
                } else if (e.getKeyCode() == KeyEvent.VK_O) {
                    projection = new Mat4OrthoRH(40, 30, 1, 50);
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    projection = new Mat4PerspRH(
                            Math.PI / 3,
                            imageRaster.getHeight() / (float) imageRaster.getWidth(),
                            0.5,
                            50
                    );
                } else if (e.getKeyCode() == KeyEvent.VK_B) {
                    changeSolid();
                } else if (e.getKeyCode() == KeyEvent.VK_V) {
                    wireFrame = !wireFrame;
                    solidSelected.setWireFrame(wireFrame);
                    changeWireFrame();
                } else if (e.getKeyCode() == KeyEvent.VK_N) {
                    texture = !texture;
                    checkShader();
                }
                display();
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                solidNumber = 1;
                createScene();
                display();
            }
        });
    }

    private void changeSolid() {
        if (solidNumber == solids.size() - 1) {
            solidNumber = 1;
        } else {
            solidNumber++;
        }
        solidSelected = solids.get(solidNumber);
    }

    private void changeWireFrame() {
        cube.changeWireFrame();
        tetrahedron.changeWireFrame();
        truncatedTetrahedron.changeWireFrame();
        bicubics.changeWireFrame();
    }

    private void checkShader() {
        if (texture) {
            renderer.setShader(shaderTexture);
        } else {
            renderer.setShader(shader);
        }
    }

    private void hardClear() {
        model = new Mat4Identity();

        for (Solid solid : solids) {
            solid.setModel(model);
            solid.setWireFrame(false);
        }

        changeWireFrame();

        texture = false;
        checkShader();

        solidNumber = defaultSolid;
        solidSelected = solids.get(solidNumber);

        vertexBuffer = solidSelected.getVertexBuffer();
        indexBuffer = solidSelected.getIndexBuffer();
        elementBuffer = solidSelected.getElementBuffer();

        Vec3D e = new Vec3D(0, -5, 2);
        Vec3D v = new Vec3D(0, 5, -2);
        Vec3D up = new Vec3D(0, 0, 1);
        view = new Mat4ViewRH(e, v, up);

        camera = new Camera()
                .withPosition(e)
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-20));
        camera.getViewMatrix();

        projection = new Mat4PerspRH(
                Math.PI / 3,
                imageRaster.getHeight() / (float) imageRaster.getWidth(),
                0.5,
                50
        );
        imageRaster.clear();

        renderer.setView(camera.getViewMatrix());
        renderer.setProjection(projection);
        renderer.setModel(model);

        renderer.setModel(new Mat4Identity());
        renderer.draw(elementBuffer, indexBuffer, vertexBuffer);
    }


    private synchronized void display() {
        vertexBuffer = solidSelected.getVertexBuffer();
        indexBuffer = solidSelected.getIndexBuffer();
        elementBuffer = solidSelected.getElementBuffer();
        renderer.clear();
        renderer.setView(camera.getViewMatrix());
        renderer.setProjection(projection);

        renderer.setModel(solidSelected.getModel());
        checkShader();
        renderer.draw(elementBuffer, indexBuffer, vertexBuffer);

        renderer.setShader(shader);
        renderer.setModel(new Mat4Identity());
        renderer.draw(solids.get(axisNumber).getElementBuffer(), solids.get(axisNumber).getIndexBuffer(), solids.get(axisNumber).getVertexBuffer());

        panel.repaint();
    }

}
