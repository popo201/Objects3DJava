package model;

import transforms.Col;
import transforms.Mat4Identity;
import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {
        vertexBuffer.add(new Vertex(new Point3D(-1, -1, -1), new Col(255, 255, 255)));
        vertexBuffer.add(new Vertex(new Point3D(1, -1, -1), new Col(0, 0, 255)));
        vertexBuffer.add(new Vertex(new Point3D(1, 1, -1), new Col(255, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(-1, 1, -1), new Col(0, 0, 255)));

        vertexBuffer.add(new Vertex(new Point3D(-1, 1, 1), new Col(255, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(-1, -1, 1), new Col(0, 255, 255)));
        vertexBuffer.add(new Vertex(new Point3D(1, -1, 1), new Col(255, 255, 0)));
        vertexBuffer.add(new Vertex(new Point3D(1, 1, 1), new Col(0, 0, 255)));

        changeWireFrame();
        setModel(new Mat4Identity());
    }

    public void changeWireFrame() {
        if (getWireFrame()) {
            indexBuffer.clear();
            elementBuffer.clear();

            indexBuffer.add(0);
            indexBuffer.add(1);

            indexBuffer.add(0);
            indexBuffer.add(3);

            indexBuffer.add(0);
            indexBuffer.add(5);

            indexBuffer.add(1);
            indexBuffer.add(2);

            indexBuffer.add(1);
            indexBuffer.add(6);

            indexBuffer.add(2);
            indexBuffer.add(3);

            indexBuffer.add(2);
            indexBuffer.add(7);

            indexBuffer.add(3);
            indexBuffer.add(4);

            indexBuffer.add(7);
            indexBuffer.add(6);

            indexBuffer.add(7);
            indexBuffer.add(4);

            indexBuffer.add(6);
            indexBuffer.add(5);

            indexBuffer.add(5);
            indexBuffer.add(4);

            elementBuffer.add(new Element(TopologyType.LINE, 0, 24));
        } else {
            indexBuffer.clear();
            elementBuffer.clear();

            indexBuffer.add(0);
            indexBuffer.add(1);
            indexBuffer.add(2);

            indexBuffer.add(0);
            indexBuffer.add(3);
            indexBuffer.add(2);

            indexBuffer.add(2);
            indexBuffer.add(3);
            indexBuffer.add(4);

            indexBuffer.add(2);
            indexBuffer.add(7);
            indexBuffer.add(4);

            indexBuffer.add(0);
            indexBuffer.add(3);
            indexBuffer.add(4);

            indexBuffer.add(0);
            indexBuffer.add(5);
            indexBuffer.add(4);

            indexBuffer.add(1);
            indexBuffer.add(2);
            indexBuffer.add(7);

            indexBuffer.add(1);
            indexBuffer.add(6);
            indexBuffer.add(7);

            indexBuffer.add(5);
            indexBuffer.add(0);
            indexBuffer.add(1);

            indexBuffer.add(1);
            indexBuffer.add(6);
            indexBuffer.add(5);

            indexBuffer.add(7);
            indexBuffer.add(6);
            indexBuffer.add(5);

            indexBuffer.add(7);
            indexBuffer.add(4);
            indexBuffer.add(5);

            elementBuffer.add(new Element(TopologyType.TRIANGLE, 0, 36));
        }
    }

}
