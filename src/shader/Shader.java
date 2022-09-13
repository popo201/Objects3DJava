package shader;

@FunctionalInterface
public interface Shader<V, C> {

    C shade(V v);

}
