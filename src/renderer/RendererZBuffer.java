package renderer;

import model.Element;
import model.TopologyType;
import model.Vertex;
import rasterize.DepthBuffer;
import rasterize.Raster;
import shader.Shader;
import transforms.*;

import java.util.List;
import java.util.Optional;

public class RendererZBuffer implements GPURenderer {

    private final Raster<Integer> imageRaster;
    private final Raster<Double> depthBuffer;

    private Mat4 model, view, projection;
    private Shader<Vertex, Col> shader;

    public RendererZBuffer(Raster<Integer> imageRaster) {
        this.imageRaster = imageRaster;
        this.depthBuffer = new DepthBuffer(imageRaster);

        model = new Mat4Identity();
        view = new Mat4Identity();
        projection = new Mat4Identity();
    }

    @Override
    public void draw(List<Element> elements, List<Integer> ib, List<Vertex> vb) {
        for (Element element : elements) {
            final TopologyType topologyType = element.getTopologyType();
            final int start = element.getStart();
            final int count = element.getCount();

            if (topologyType == TopologyType.TRIANGLE) {
                for (int i = start; i < start + count; i += 3) {
                    final Integer i1 = ib.get(i);
                    final Integer i2 = ib.get(i + 1);
                    final Integer i3 = ib.get(i + 2);
                    final Vertex v1 = vb.get(i1);
                    final Vertex v2 = vb.get(i2);
                    final Vertex v3 = vb.get(i3);
                    prepareTriangle(v1, v2, v3);
                }
            } else if (topologyType == TopologyType.TRIANGLE_STRIP) {
                for (int i = start; i < ((start + count) - 2); i++) {
                    final Integer i1 = ib.get(i);
                    final Integer i2 = ib.get(i + 1);
                    final Integer i3 = ib.get(i + 2);
                    final Vertex v1 = vb.get(i1);
                    final Vertex v2 = vb.get(i2);
                    final Vertex v3 = vb.get(i3);
                    prepareTriangle(v1, v2, v3);
                }
            } else if (topologyType == TopologyType.TRIANGLE_FAN) {
                for (int i = start; i < start + count; i += 3) {
                    final Integer i1 = ib.get(start);
                    final Integer i2 = ib.get(i + 1);
                    final Integer i3 = ib.get(i + 2);
                    final Vertex v1 = vb.get(i1);
                    final Vertex v2 = vb.get(i2);
                    final Vertex v3 = vb.get(i3);
                    prepareTriangle(v1, v2, v3);
                }
            } else if (topologyType == TopologyType.LINE) {
                for (int i = start; i < start + count; i += 2) {
                    final Integer i1 = ib.get(i);
                    final Integer i2 = ib.get(i + 1);
                    final Vertex v1 = vb.get(i1);
                    final Vertex v2 = vb.get(i2);
                    prepareLine(v1, v2);
                }
            } else if (topologyType == TopologyType.POINT) {
                for (int i = start; i < start + count; i++) {
                    final Integer i1 = ib.get(i);
                    final Vertex v1 = vb.get(i1);
                    preparePoint(v1);
                }
            }
        }
    }

    private void preparePoint(Vertex v1) {
        Vertex a = new Vertex(v1.getPoint().mul(model).mul(view).mul(projection), v1.getColor());

        if (-a.getW() > a.getX()) return;
        if (a.getX() > a.getW()) return;
        if (-a.getW() > a.getY()) return;
        if (a.getY() > a.getW()) return;
        if (0 > a.getZ()) return;
        if (a.getZ() > a.getW()) return;

        drawPoint(a);
    }

    private void prepareLine(Vertex v1, Vertex v2) {
        Vertex a = new Vertex(v1.getPoint().mul(model).mul(view).mul(projection), v1.getColor());
        Vertex b = new Vertex(v2.getPoint().mul(model).mul(view).mul(projection), v2.getColor());

        if (-a.getW() > a.getX() && -b.getW() > b.getX()) return;
        if (a.getX() > a.getW() && b.getX() > b.getW()) return;
        if (-a.getW() > a.getY() && -b.getW() > b.getY()) return;
        if (a.getY() > a.getW() && b.getY() > b.getW()) return;
        if (0 > a.getZ() && 0 > b.getZ()) return;
        if (a.getZ() > a.getW() && b.getZ() > b.getW()) return;

        if (a.getZ() < b.getZ()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        if (a.getZ() < 0) {
            return;
        } else if (b.getZ() < 0) {
            double t1 = (0 - a.getZ()) / (b.getZ() - a.getZ());
            Vertex ab = a.mul(1 - t1).add(b.mul(t1));
            drawLine(a, ab);
        } else {
            drawLine(a, b);
        }
    }

    private void prepareTriangle(Vertex v1, Vertex v2, Vertex v3) {
        // 1. transformace vrcholů
        Vertex a = new Vertex(v1.getPoint().mul(model).mul(view).mul(projection), v1.getColor());
        Vertex b = new Vertex(v2.getPoint().mul(model).mul(view).mul(projection), v2.getColor());
        Vertex c = new Vertex(v3.getPoint().mul(model).mul(view).mul(projection), v3.getColor());

        // 2. ořezání
        if (-a.getW() > a.getX() && -b.getW() > b.getX() && -c.getW() > c.getX()) return;
        if (a.getX() > a.getW() && b.getX() > b.getW() && c.getX() > c.getW()) return;
        if (-a.getW() > a.getY() && -b.getW() > b.getY() && -c.getW() > c.getY()) return;
        if (a.getY() > a.getW() && b.getY() > b.getW() && c.getY() > c.getW()) return;
        if (0 > a.getZ() && 0 > b.getZ() && 0 > c.getZ()) return;
        if (a.getZ() > a.getW() && b.getZ() > b.getW() && c.getZ() > c.getW()) return;

        // 3. seřazení vrcholů podle Z (a.z > b.z > c.z)
        if (a.getZ() < b.getZ()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }
        if (b.getZ() < c.getZ()) {
            Vertex temp = b;
            b = c;
            c = temp;
        }
        if (a.getZ() < b.getZ()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        // 4. ořezání podle hrany Z
        if (a.getZ() < 0) {
            //není co zobrazit (vše je za námi)
            return;
        } else if (b.getZ() < 0) {
            // vrchol A je vidět, vrcholy B,C nejsou

            // odečíst minimum, dělit rozsahem
            double t1 = (0 - a.getZ()) / (b.getZ() - a.getZ());
            // 0 -> protože nový vrchol má mít Z souřadnici 0
            Vertex ab = a.mul(1 - t1).add(b.mul(t1));

            double t2 = (0 - a.getZ()) / (c.getZ() - a.getZ());
            Vertex ac = a.mul(1 - t2).add(c.mul(t2));

            drawTriangle(a, ab, ac);

        } else if (c.getZ() < 0) {
            double t1 = (0 - b.getZ()) / (c.getZ() - b.getZ());
            Vertex bc = b.mul(1 - t1).add(c.mul(t1));
            drawTriangle(a, b, bc);

            double t2 = (0 - a.getZ()) / (c.getZ() - a.getZ());
            Vertex ac = a.mul(1 - t2).add(c.mul(t2));
            drawTriangle(a, bc, ac);

        } else {
            // vidíme celý trojúhelník (podle Z)
            drawTriangle(a, b, c);
        }
    }

    private void drawPoint(Vertex a) {
        Optional<Vertex> oA = a.dehomog();

        if (oA.isEmpty()) return;

        a = oA.get();

        a = transformToWindow(a);

        drawPixel((int) a.getX(), (int) a.getY(), a.getZ(), a.getColor());
    }

    private void drawLine(Vertex a, Vertex b) {
        Optional<Vertex> oA = a.dehomog();
        Optional<Vertex> oB = b.dehomog();

        if (oA.isEmpty() || oB.isEmpty()) return;

        a = oA.get();
        b = oB.get();

        a = transformToWindow(a);
        b = transformToWindow(b);

        if (Math.abs(a.getX() - b.getX()) > Math.abs(a.getY() - b.getY())) {
            if (a.getX() > b.getX()) {
                Vertex temp = a;
                a = b;
                b = temp;
            }
            for (int x = (int) a.getX(); x < b.getX(); x++) {
                double t = (x - a.getX()) / (b.getX() - a.getX());
                Vertex ab = a.mul(1 - t).add(b.mul(t));
                drawPixel(x, (int) ab.getY(), ab.getZ(), ab.getColor());
            }
        } else {
            if (a.getY() > b.getY()) {
                Vertex temp = a;
                a = b;
                b = temp;
            }
            for (int y = (int) a.getY(); y < b.getY(); y++) {
                double t = (y - a.getY()) / (b.getY() - a.getY());
                Vertex ab = a.mul(1 - t).add(b.mul(t));
                drawPixel((int) ab.getX(), y, ab.getZ(), ab.getColor());
            }
        }
    }

    private void drawTriangle(Vertex a, Vertex b, Vertex c) {
        // 1. dehomogenizace
        Optional<Vertex> oA = a.dehomog();
        Optional<Vertex> oB = b.dehomog();
        Optional<Vertex> oC = c.dehomog();

        // zahodit trojúhelník, pokud některý vrchol má w==0
        if (oA.isEmpty() || oB.isEmpty() || oC.isEmpty()) return;

        a = oA.get();
        b = oB.get();
        c = oC.get();

        // 2. transformace do okna
        a = transformToWindow(a);
        b = transformToWindow(b);
        c = transformToWindow(c);

        // 3. seřazení podle Y
        // a.y < b.y < c.y
        if (a.getY() > b.getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }
        if (b.getY() > c.getY()) {
            Vertex temp = b;
            b = c;
            c = temp;
        }
        if (a.getY() > b.getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        // 4. interpolace podle Y
        // A -> B
        long start = (long) Math.max(Math.ceil(a.getY()), 0);
        double end = Math.min(b.getY(), imageRaster.getHeight() - 1);
        for (long y = start; y <= end; y++) {
            double t1 = (y - a.getY()) / (b.getY() - a.getY());
            double t2 = (y - a.getY()) / (c.getY() - a.getY());

            Vertex ab = a.mul(1 - t1).add(b.mul(t1));
            Vertex ac = a.mul(1 - t2).add(c.mul(t2));

            fillLine(y, ab, ac);
        }

        // B -> C
        long startBC = (long) Math.max(Math.ceil(b.getY()), 0);
        double endBC = Math.min(c.getY(), imageRaster.getHeight() - 1);
        for (long y = startBC; y <= endBC; y++) {
            double t1 = (y - b.getY()) / (c.getY() - b.getY());
            double t2 = (y - a.getY()) / (c.getY() - a.getY());

            Vertex bc = b.mul(1 - t1).add(c.mul(t1));
            Vertex ac = a.mul(1 - t2).add(c.mul(t2));

            fillLine(y, bc, ac);
        }
    }

    private Vertex transformToWindow(Vertex vertex) {
        Vec3D vec3D = new Vec3D(vertex.getPoint())
                .mul(new Vec3D(1, -1, 1)) // Y jde nahoru a my chceme, aby šlo dolů
                .add(new Vec3D(1, 1, 0)) // (0,0) je uprostřed a my chceme, aby bylo vlevo nahoře
                // máme <0;2> -> vynásobíme polovinou velikosti plátna
                .mul(new Vec3D(imageRaster.getWidth() / 2f, imageRaster.getHeight() / 2f, 1));

        return new Vertex(new Point3D(vec3D), vertex.getColor());
    }

    private void fillLine(long y, Vertex a, Vertex b) {
        if (a.getX() > b.getX()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        long start = (long) Math.max(Math.ceil(a.getX()), 0);
        double end = Math.min(b.getX(), imageRaster.getWidth() - 1);
        for (long x = start; x <= end; x++) {
            double t = (x - a.getX()) / (b.getX() - a.getX());
            Vertex finalVertex = a.mul(1 - t).add(b.mul(t));

            final Col finalColor = shader.shade(finalVertex);
            drawPixel((int) x, (int) y, finalVertex.getZ(), finalColor);
        }
    }

    private void drawPixel(int x, int y, double z, Col color) {
        Optional<Double> zOptional = depthBuffer.getElement(x, y);
        if (zOptional.isPresent() && z < zOptional.get()) {
            depthBuffer.setElement(x, y, z);
            imageRaster.setElement(x, y, color.getRGB());
        }
    }

    @Override
    public void clear() {
        imageRaster.clear();
        depthBuffer.clear();
    }

    @Override
    public void setShader(Shader<Vertex, Col> shader) {
        this.shader = shader;
    }

    @Override
    public void setModel(Mat4 model) {
        this.model = model;
    }

    @Override
    public void setView(Mat4 view) {
        this.view = view;
    }

    @Override
    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

}