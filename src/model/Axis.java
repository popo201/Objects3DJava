package model;

import transforms.Col;
import transforms.Point3D;

public class Axis extends Solid {

    public Axis() {
        // Osa X
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), new Col(255, 0, 0))); //0
        vertexBuffer.add(new Vertex(new Point3D(1, 0, 0), new Col(255, 0, 0))); //1
        vertexBuffer.add(new Vertex(new Point3D(.95, 0, 0.1), new Col(255, 0, 0))); //2
        vertexBuffer.add(new Vertex(new Point3D(.95, 0, -.1), new Col(255, 0, 0))); //3
        vertexBuffer.add(new Vertex(new Point3D(.95, 0.1, 0), new Col(255, 0, 0))); //4
        // Osa Y
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), new Col(0, 255, 0))); //5
        vertexBuffer.add(new Vertex(new Point3D(0, 1, 0), new Col(0, 255, 0))); //6
        vertexBuffer.add(new Vertex(new Point3D(0, .95, 0.1), new Col(0, 255, 0))); //7
        vertexBuffer.add(new Vertex(new Point3D(0, .95, -.1), new Col(0, 255, 0))); //8
        vertexBuffer.add(new Vertex(new Point3D(0.1, .95, 0), new Col(0, 255, 0))); //9
        // Osa Z
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), new Col(0, 0, 255))); //10
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 1), new Col(0, 0, 255))); //11
        vertexBuffer.add(new Vertex(new Point3D(0, 0.1, .95), new Col(0, 0, 255))); //12
        vertexBuffer.add(new Vertex(new Point3D(0, -.1, .95), new Col(0, 0, 255))); //13
        vertexBuffer.add(new Vertex(new Point3D(0.1, 0, .95), new Col(0, 0, 255))); //14

        // Osa X
        indexBuffer.add(0);
        indexBuffer.add(1);

        indexBuffer.add(1);
        indexBuffer.add(2);
        indexBuffer.add(3);

        indexBuffer.add(1);
        indexBuffer.add(2);
        indexBuffer.add(4);

        indexBuffer.add(1);
        indexBuffer.add(4);
        indexBuffer.add(3);

        indexBuffer.add(4);
        indexBuffer.add(2);
        indexBuffer.add(3);

        // Osa Y
        indexBuffer.add(5); //15
        indexBuffer.add(6);

        indexBuffer.add(6);
        indexBuffer.add(7);
        indexBuffer.add(8);

        indexBuffer.add(6);
        indexBuffer.add(7);
        indexBuffer.add(9);

        indexBuffer.add(6);
        indexBuffer.add(9);
        indexBuffer.add(8);

        indexBuffer.add(9);
        indexBuffer.add(7);
        indexBuffer.add(8);

        // Osa Z
        indexBuffer.add(10); //29
        indexBuffer.add(11);

        indexBuffer.add(11);
        indexBuffer.add(12);
        indexBuffer.add(13);

        indexBuffer.add(11);
        indexBuffer.add(12);
        indexBuffer.add(14);

        indexBuffer.add(11);
        indexBuffer.add(14);
        indexBuffer.add(13);

        indexBuffer.add(14);
        indexBuffer.add(12);
        indexBuffer.add(13);

        elementBuffer.add(new Element(TopologyType.LINE, 0, 2));
        elementBuffer.add(new Element(TopologyType.TRIANGLE, 2, 12));

        elementBuffer.add(new Element(TopologyType.LINE, 14, 2));
        elementBuffer.add(new Element(TopologyType.TRIANGLE, 16, 12));

        elementBuffer.add(new Element(TopologyType.LINE, 28, 2));
        elementBuffer.add(new Element(TopologyType.TRIANGLE, 30, 12));
    }

}
