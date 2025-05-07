package game;

import tage.ObjShape;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * A simple two‑triangle quad that spans NDC (–1,–1) to (1,1).
 */
public class ScreenQuad extends ObjShape {
    public ScreenQuad() {
        // 4 corner positions in clip space
        Vector3f[] verts = new Vector3f[]{
            new Vector3f(-1f,  1f, 0f),  // top‑left
            new Vector3f(-1f, -1f, 0f),  // bottom‑left
            new Vector3f( 1f, -1f, 0f),  // bottom‑right
            new Vector3f( 1f,  1f, 0f),  // top‑right
        };
        // matching UVs
        Vector2f[] uvs = new Vector2f[]{
            new Vector2f(0f, 1f),
            new Vector2f(0f, 0f),
            new Vector2f(1f, 0f),
            new Vector2f(1f, 1f),
        };
        // normals (pointing out of screen)
        Vector3f[] norms = new Vector3f[]{
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
            new Vector3f(0,0,1),
        };
        // two triangles
        int[] idx = new int[]{ 0,1,2,  2,3,0 };

        setVerticesIndexed(idx, verts);
        setTexCoordsIndexed(idx, uvs);
        setNormalsIndexed(idx, norms);
    }
}
