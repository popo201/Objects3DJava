package shader;

import model.Vertex;
import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureShader implements Shader<Vertex, Col> {

    private BufferedImage image;

    public TextureShader() {
        image = null;
        try {
            image = ImageIO.read(new File("src/res/czech.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Col shade(Vertex vertex) {
        return new Col(image.getRGB((int) vertex.getTexCoord().getX() % image.getWidth(), (int) vertex.getTexCoord().getY() % image.getHeight()));
    }

}
