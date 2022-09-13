package shader;

import model.Vertex;
import transforms.Col;

public class BasicColorShader implements Shader<Vertex, Col> {

    @Override
    public Col shade(Vertex v) {
        return v.getColor();
    }

}
