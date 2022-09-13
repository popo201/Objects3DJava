package model;

import transforms.Col;
import transforms.Point3D;

public class Tetrahedron extends Solid {

    public Tetrahedron() {
        vertexBuffer.add(new Vertex(new Point3D(-1, -1, -1), new Col(255, 0, 0)));
        vertexBuffer.add(new Vertex(new Point3D(1, 1, -1), new Col(0, 255, 0)));
        vertexBuffer.add(new Vertex(new Point3D(-1, 1, -1), new Col(0, 0, 255)));

        vertexBuffer.add(new Vertex(new Point3D(-1, 1, 1), new Col(255, 255, 0)));

        changeWireFrame();
    }

    public void changeWireFrame() {
        if (getWireFrame()) {
            indexBuffer.clear();
            elementBuffer.clear();

            indexBuffer.add(0);
            indexBuffer.add(1);

            indexBuffer.add(0);
            indexBuffer.add(2);

            indexBuffer.add(0);
            indexBuffer.add(3);

            indexBuffer.add(1);
            indexBuffer.add(2);

            indexBuffer.add(1);
            indexBuffer.add(3);

            indexBuffer.add(2);
            indexBuffer.add(3);

            elementBuffer.add(new Element(TopologyType.LINE, 0, 12));
        } else {
            indexBuffer.clear();
            elementBuffer.clear();

            indexBuffer.add(0);
            indexBuffer.add(1);
            indexBuffer.add(2);

            indexBuffer.add(0);
            indexBuffer.add(1);
            indexBuffer.add(3);

            indexBuffer.add(0);
            indexBuffer.add(2);
            indexBuffer.add(3);

            indexBuffer.add(1);
            indexBuffer.add(2);
            indexBuffer.add(3);

            elementBuffer.add(new Element(TopologyType.TRIANGLE_STRIP, 0, 12));
        }
    }

}
