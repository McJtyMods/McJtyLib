package mcjty.lib.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.renderer.block.model.BakedQuad;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class mostly copied from
 * https://github.com/gigaherz/Survivalist/blob/master/src/main/java/gigaherz/survivalist/misc/QuadTransformer.java
 */
public class QuadTransformer {
    private static void processVertices(Matrix4f transform, VertexFormat fmt, int positionIndex, int[] inData, int[] outData) {
        // @todo 1.21
        int positionOffset = 0; // @WRONG
//        int positionOffset = fmt.getOffset(positionIndex);
        int stride = fmt.getVertexSize() / 4;
        for (int i = 0; i < 4; i++) {
            int offset = positionOffset + i * stride;
            float x = Float.intBitsToFloat(inData[offset]);
            float y = Float.intBitsToFloat(inData[offset + 1]);
            float z = Float.intBitsToFloat(inData[offset + 2]);

            // @todo 1.15
//            Vector4f pos = new Vector4f(x, y, z, 1);
//            transform.translate(pos);
//            if (pos.getW() != 1.0f) {
//                float v = 1 / pos.getW();
//                pos.scale(new Vector3f(v, v, v));
//            }

//            outData[offset] = Float.floatToRawIntBits(pos.getX());
//            outData[offset + 1] = Float.floatToRawIntBits(pos.getY());
//            outData[offset + 2] = Float.floatToRawIntBits(pos.getZ());
        }
    }

    private static int findPositionIndex(VertexFormat fmt) {
        int positionIndex;
        VertexFormatElement positionElement = null;
        List<VertexFormatElement> list = fmt.getElements();
        for (positionIndex = 0; positionIndex < list.size(); positionIndex++) {
            VertexFormatElement el = list.get(positionIndex);
            if (el.usage() == VertexFormatElement.Usage.POSITION) {
                positionElement = el;
                break;
            }
        }
        if (positionIndex == list.size() || positionElement == null) {
            throw new RuntimeException("WAT? Position not found");
        }
        if (positionElement.type() != VertexFormatElement.Type.FLOAT) {
            throw new RuntimeException("WAT? Position not FLOAT");
        }
        if (positionElement.byteSize() < 3) {
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
        VertexFormat fmt = null; // @todo 1.15 input.getFormat();
        int positionIndex = findPositionIndex(fmt);

        int[] data = input.getVertices();
        processVertices(transform, fmt, positionIndex, data, data);

        return new BakedQuad(data, input.getTintIndex(), input.getDirection(), input.getSprite(), input.isShade());
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

        VertexFormat fmt = null; // @todo 1.15 inputs.get(0).getFormat();
        int positionIndex = findPositionIndex(fmt);

        List<BakedQuad> outputs = Lists.newArrayList();
        for (BakedQuad input : inputs) {
            int[] inData = input.getVertices();
            int[] outData = Arrays.copyOf(inData, inData.length);
            processVertices(transform, fmt, positionIndex, inData, outData);

            outputs.add(new BakedQuad(outData, input.getTintIndex(), input.getDirection(), input.getSprite(), input.isShade()));
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
        if (inputs.isEmpty()) {
            return;
        }

        VertexFormat fmt = null; // @todo 1.15 inputs.get(0).getFormat();
        int positionIndex = findPositionIndex(fmt);

        for (BakedQuad input : inputs) {
            int[] data = input.getVertices();
            processVertices(transform, fmt, positionIndex, data, data);
        }
    }
}