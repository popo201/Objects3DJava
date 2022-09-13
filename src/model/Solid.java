package model;

import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Solid {

    List<Vertex> vertexBuffer;
    List<Integer> indexBuffer;
    List<Element> elementBuffer;
    private Mat4 model;
    private Boolean wireFrame;

    public Solid() {
        this.vertexBuffer = new ArrayList<>();
        this.indexBuffer = new ArrayList<>();
        this.elementBuffer = new ArrayList<>();
        this.model = new Mat4Identity();
        wireFrame = false;
    }

    final void addIndices(Integer... indices) {
        indexBuffer.addAll(Arrays.asList(indices));
    }

    public List<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public List<Element> getElementBuffer() {
        return elementBuffer;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public Boolean getWireFrame() {
        return wireFrame;
    }

    public void setWireFrame(Boolean wireFrame) {
        this.wireFrame = wireFrame;
    }

}
