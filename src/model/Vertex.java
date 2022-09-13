package model;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;

import java.util.Optional;

public class Vertex {

    private final Point3D point;
    private final Col color;
    private final Vec2D texCoord;

    public Vertex(Point3D point, Col color) {
        this.point = point;
        this.color = color;
        texCoord = new Vec2D(point.getX(), point.getY());
    }

    public Vertex mul(double t) {
        return new Vertex(point.mul(t), color.mul(t));
    }

    public Vertex add(Vertex v) {
        return new Vertex(point.add(v.getPoint()), color.add(v.getColor()));
    }

    public Optional<Vertex> dehomog() {
        return point.dehomog().map(vec3D -> new Vertex(new Point3D(vec3D), color));
    }

    public Point3D getPoint() {
        return point;
    }

    public Col getColor() {
        return color;
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public double getZ() {
        return point.getZ();
    }

    public double getW() {
        return point.getW();
    }

    public Vec2D getTexCoord() {
        return texCoord;
    }
}
