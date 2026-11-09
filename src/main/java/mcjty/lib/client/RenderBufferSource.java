package mcjty.lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.rendertype.RenderType;

/** Vertex destinations for extracted geometry and small offscreen or sky draws. */
@FunctionalInterface
public interface RenderBufferSource {

    VertexConsumer getBuffer(RenderType type);

    static Immediate immediate() {
        return new Immediate();
    }

    final class Immediate implements RenderBufferSource, AutoCloseable {

        private final StagedVertexBuffer vertices = new StagedVertexBuffer(() -> "McJtyLib immediate geometry", 16384);
        private final List<StagedVertexBuffer.Draw> draws = new ArrayList<>();
        private final List<PreparedRenderType> types = new ArrayList<>();

        private Immediate() {
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            var sorting = type.sortOnUpload() ? RenderSystem.getProjectionType().vertexSorting() : null;
            var draw = vertices.appendDraw(type.format(), type.primitiveTopology(), sorting);
            draws.add(draw);
            types.add(type.prepare());
            return vertices.getVertexBuilder(draw);
        }

        public void endBatch() {
            vertices.upload();
            for (int i = 0; i < draws.size(); i++) {
                var info = vertices.getExecuteInfo(draws.get(i));
                if (info != null) {
                    types.get(i).drawFromBuffer(info);
                }
            }
            vertices.endDraw();
            vertices.endFrame();
            draws.clear();
            types.clear();
        }

        @Override
        public void close() {
            vertices.close();
        }
    }
}
