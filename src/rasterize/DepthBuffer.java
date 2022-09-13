package rasterize;

import java.util.Arrays;
import java.util.Optional;

public class DepthBuffer implements Raster<Double> {

    private final double[][] data;
    private final int width;
    private final int height;
    private double clearValue;

    public DepthBuffer(Raster<?> raster) {
        width = raster.getWidth();
        height = raster.getHeight();
        data = new double[width][height];

        setClearValue(1d);
        clear();
    }

    @Override
    public void clear() {
        for (double[] d : data) {
            Arrays.fill(d, clearValue);
        }
    }

    @Override
    public void setClearValue(Double clearValue) {
        this.clearValue = clearValue;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Optional<Double> getElement(int x, int y) {
        if (checkBounds(x, y)) {
            return Optional.of(data[x][y]);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void setElement(int x, int y, Double z) {
        if (checkBounds(x, y)) {
            data[x][y] = z;
        }
    }

}
