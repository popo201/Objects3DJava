package model;

public class Element {

    private final TopologyType topologyType;
    private final int start; // na jakém indexu v IB začít
    private final int count; // kolik indexů z IB celkem použít

    /**
     * @param topologyType topology type of the element
     * @param start        index of the starting item in index buffer
     * @param count        how many indices to take from index buffer
     */
    public Element(TopologyType topologyType, int start, int count) {
        this.topologyType = topologyType;
        this.start = start;
        this.count = count;
    }

    public TopologyType getTopologyType() {
        return topologyType;
    }

    public int getStart() {
        return start;
    }

    public int getCount() {
        return count;
    }

}
