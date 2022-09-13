package renderer;

import model.Element;
import model.Vertex;
import shader.Shader;
import transforms.Col;
import transforms.Mat4;

import java.util.List;

public interface GPURenderer {

    void draw(List<Element> elements, List<Integer> ib, List<Vertex> vb);

    void clear();

    void setShader(Shader<Vertex, Col> shader);

    void setModel(Mat4 model);

    void setView(Mat4 view);

    void setProjection(Mat4 projection);

}
