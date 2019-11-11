package mcjty.lib.client;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector4f;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class mostly copied from
 * https://github.com/gigaherz/Survivalist/blob/master/src/main/java/gigaherz/survivalist/misc/QuadTransformer.java
 */
public class QuadTransformer {
    private static void processVertices(Matrix4f transform, VertexFormat fmt, int positionIndex, int[] inData, int[] outData) {
        int positionOffset = fmt.getOffset(positionIndex);
        int stride = fmt.getSize() / 4;
        for (int i = 0; i < 4; i++) {
            int offset = positionOffset + i * stride;
            float x = Float.intBitsToFloat(inData[offset]);
            float y = Float.intBitsToFloat(inData[offset + 1]);
            float z = Float.intBitsToFloat(inData[offset + 2]);

            Vector4f pos = new Vector4f(x, y, z, 1);
            transform.transform(pos);
            if (pos.w != 1.0f) {
                pos.scale(1 / pos.w);
            }

            outData[offset] = Float.floatToRawIntBits(pos.x);
            outData[offset + 1] = Float.floatToRawIntBits(pos.y);
            outData[offset + 2] = Float.floatToRawIntBits(pos.z);
        }
    }

    private static int findPositionIndex(VertexFormat fmt) {
        int positionIndex;
        VertexFormatElement positionElement = null;
        for (positionIndex = 0; positionIndex < fmt.getElementCount(); positionIndex++) {
            VertexFormatElement el = fmt.getElement(positionIndex);
            if (el.getUsage() == VertexFormatElement.Usage.POSITION) {
                positionElement = el;
                break;
            }
        }
        if (positionIndex == fmt.getElementCount() || positionElement == null) {
            throw new RuntimeException("WAT? Position not found");
        }
        if (positionElement.getType() != VertexFormatElement.Type.FLOAT) {
            throw new RuntimeException("WAT? Position not FLOAT");
        }
        if (positionElement.getSize() < 3) {
            throw new RuntimeException("WAT? Position not 3D");
        }
        return positionIndex;
    }

    /**
     * Processes a single quad rather inefficiently.
     *
     * @param input     A single quad to transform.
     * @param transform The matrix to apply. E.g. from TRSRTransformation#getMatrix()
     * @return A new BakedQuad object with the new position.
     */
    public static BakedQuad processOne(BakedQuad input, Matrix4f transform) {
        VertexFormat fmt = input.getFormat();
        int positionIndex = findPositionIndex(fmt);

        int[] data = input.getVertexData();
        processVertices(transform, fmt, positionIndex, data, data);

        return new BakedQuad(data, input.getTintIndex(), input.getFace(), input.getSprite(), input.shouldApplyDiffuseLighting(), fmt);
    }

    /**
     * Processes multiple quads, producing a new array of quads, with the positions transformed.
     *
     * @param inputs    The list of quads to transform
     * @param transform The matrix to apply. E.g. from TRSRTransformation#getMatrix()
     * @return A new array of new BakedQuad objects.
     */
    public static List<BakedQuad> processMany(List<BakedQuad> inputs, Matrix4f transform) {
        if (inputs.isEmpty()) {
            return Collections.emptyList();
        }

        VertexFormat fmt = inputs.get(0).getFormat();
        int positionIndex = findPositionIndex(fmt);

        List<BakedQuad> outputs = Lists.newArrayList();
        for (BakedQuad input : inputs) {
            int[] inData = input.getVertexData();
            int[] outData = Arrays.copyOf(inData, inData.length);
            processVertices(transform, fmt, positionIndex, inData, outData);

            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getFace(), input.getSprite(), input.shouldApplyDiffuseLighting(), fmt));
        }
        return outputs;
    }

    /**
     * Processes multiple quads in place.
     * WARNING: Modifies the input quads! Don't use it on a shared list of quads!
     *
     * @param inputs    The list of quads to transform
     * @param transform The matrix to apply. E.g. from TRSRTransformation#getMatrix()
     */
    public static void processManyInPlace(List<BakedQuad> inputs, Matrix4f transform) {
        if (inputs.size() == 0) {
            return;
        }

        VertexFormat fmt = inputs.get(0).getFormat();
        int positionIndex = findPositionIndex(fmt);

        for (BakedQuad input : inputs) {
            int[] data = input.getVertexData();
            processVertices(transform, fmt, positionIndex, data, data);
        }
    }
}