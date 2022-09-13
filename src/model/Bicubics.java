package model;

import transforms.*;

import java.util.ArrayList;

public class Bicubics extends Solid {

    private Mat4 curve;
    private Bicubic bicubic;

    public static final int points = 30;

    private Point3D p11 = new Point3D(-2, 0, -1);
    private Point3D p12 = new Point3D(-2, -1, 0);
    private Point3D p13 = new Point3D(-2, -1, 1);
    private Point3D p14 = new Point3D(-2, 0, 2);

    private Point3D p21 = new Point3D(-1, 0, -1);
    private Point3D p22 = new Point3D(-1, 0, 0);
    private Point3D p23 = new Point3D(-1, 0, 1);
    private Point3D p24 = new Point3D(-1, 0, 2);

    private Point3D p31 = new Point3D(0, 0, -1);
    private Point3D p32 = new Point3D(0, 0, 0);
    private Point3D p33 = new Point3D(0, 0, 1);
    private Point3D p34 = new Point3D(0, 0, 2);

    private Point3D p41 = new Point3D(1, 0, -1);
    private Point3D p42 = new Point3D(1, -1, 0);
    private Point3D p43 = new Point3D(1, -1, 1);
    private Point3D p44 = new Point3D(1, 0, 2);

    public Bicubics() {
        curve = Cubic.BEZIER;
        bicubic = new Bicubic(curve, p11, p12, p13, p14, p21, p22, p23, p24, p31, p32, p33, p34, p41, p42, p43, p44);
        vertexBuffer = new ArrayList<>();
        indexBuffer = new ArrayList<>();
        Col color = new Col(255, 165, 0);

        // výpočet bodů
        for (int i = 0; i <= points; i++) {
            for (int j = 0; j <= points; j++) {
                Point3D p = bicubic.compute((double) i / points, (double) j / points);
                vertexBuffer.add(new Vertex(p, color));
            }
        }

        // generování indexů (spojení bodů)
        for (int i = 0; i < points; i++) {
            for (int j = 0; j < points; j++) {
                addIndices(i * (points + 1) + j);
                addIndices(i * (points + 1) + j + 1);
                addIndices((i + 1) * (points + 1) + j);

                addIndices(i * (points + 1) + j + 1);
                addIndices((i + 1) * (points + 1) + j);
                addIndices((i + 1) * (points + 1) + j + 1);
            }
        }
        changeWireFrame();
    }

    public void changeWireFrame() {
        if (getWireFrame()) {
            elementBuffer.clear();

            elementBuffer.add(new Element(TopologyType.LINE, 0, 5400));
        } else {
            elementBuffer.clear();

            elementBuffer.add(new Element(TopologyType.TRIANGLE, 0, 5400));
        }
    }

}
