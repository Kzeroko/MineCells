package com.github.mim1q.minecells.client.render.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsRenderers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class KingdomPortalBlockEntityRenderer implements BlockEntityRenderer<KingdomPortalCoreBlockEntity> {
  private static final Identifier TEXTURE = MineCells.createId("textures/blockentity/kingdom_portal.png");
  private static final Identifier TEXTURE_GLOW = MineCells.createId("textures/blockentity/kingdom_portal_glow.png");
  private static final Identifier TEXTURE_UNLIT = MineCells.createId("textures/blockentity/kingdom_portal_unlit.png");

  private final KingdomPortalBlockEntityModel model;
  public KingdomPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    EntityModelLoader loader = ctx.getLayerRenderDispatcher();
    this.model = new KingdomPortalBlockEntityModel(loader.getModelPart(MineCellsRenderers.KINGDOM_PORTAL_LAYER));
  }

  @Override
  public void render(KingdomPortalCoreBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    if (entity.getWorld() == null) {
      return;
    }
    if (entity.getWorld().getBlockState(entity.getPos()).getBlock() != MineCellsBlocks.KINGDOM_PORTAL_CORE) {
      return;
    }
    Direction dir = entity.getDirection();
    if (dir == null) {
      return;
    }

    matrices.push();
    float rot = 180.0F - dir.asRotation();

    Vec3f offset = new Vec3f(entity.getOffset());

    matrices.translate(offset.getX(), offset.getY(), offset.getZ());
    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot));
    matrices.scale(-1.0F, -1.0F, 1.0F);

    VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE_UNLIT));
    this.model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

    entity.portalOpacity.update(entity.getWorld().getTime() + tickDelta);
    float alpha = entity.portalOpacity.getValue();
    if (entity.litProgress.getValue() >= 0.9F) {
      entity.portalOpacity.setupTransitionTo(1.0F, 5.0F);
    } else {
      entity.portalOpacity.setupTransitionTo(0.0F, 5.0F);
    }
    if (alpha > 0.0F) {
      VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
      this.model.render(matrices, vertexConsumer2, 0xF000F0, overlay, 1.0F, 1.0F, 1.0F, alpha);
      VertexConsumer vertexConsumer3 = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE_GLOW));
      this.model.render(matrices, vertexConsumer3, 0xF000F0, overlay, 1.0F, 1.0F, 1.0F, alpha * 0.75F);
    }
    matrices.pop();

    EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
    float distance = (float) dispatcher.getSquaredDistanceToCamera(
      entity.getPos().getX() + offset.getX(),
      entity.getPos().getY() + offset.getY(),
      entity.getPos().getZ() + offset.getZ()
    );
    distance = distance == 0 ? 0.01F : distance;
    float opacity = MathHelper.clamp(1000.0F / (distance * distance), 0.025F, 1.0F);
    if (opacity <= 0.025F) {
      return;
    }

    String key = MineCellsDimension.getTranslationKey(entity.getDimensionKey());
    if (entity.isUpstream()) {
      PlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
//        key = ((PlayerEntityAccessor) player).getLastDimensionTranslationKey();
      }
    }

    matrices.push();
    matrices.translate(offset.getX(), offset.getY(), offset.getZ());
    renderLabel(
      MinecraftClient.getInstance().textRenderer,
      dispatcher,
      Text.translatable(key),
      matrices,
      vertexConsumers,
      light,
      opacity
    );
    matrices.pop();
  }

  protected void renderLabel(
    TextRenderer textRenderer,
    EntityRenderDispatcher dispatcher,
    Text text,
    MatrixStack matrices,
    VertexConsumerProvider vertexConsumers,
    int light,
    float opacity
  ) {
    matrices.push();
    matrices.translate(0.0, 3.0F, 0.0);
    matrices.multiply(dispatcher.getRotation());
    matrices.scale(-0.025f, -0.025f, 0.025f);
    Matrix4f matrix4f = matrices.peek().getPositionMatrix();
    float h = -textRenderer.getWidth(text) / 2.0F;
    int color = 0xFFFFFF + ((int)(0xFF * opacity) << 24);
    int background = (int)(0x80 * opacity) << 24;
    textRenderer.draw(text, h, 0, color, false, matrix4f, vertexConsumers, false, background, light);
    matrices.pop();
  }

  @Override
  public int getRenderDistance() {
    return 256;
  }

  public static class KingdomPortalBlockEntityModel extends Model {

    ModelPart main;

    public KingdomPortalBlockEntityModel(ModelPart root) {
      super(RenderLayer::getEntityCutoutNoCull);
      this.main = root.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData root = modelData.getRoot();

      root.addChild("main",
        ModelPartBuilder.create()
          // Main portal
          .uv(0, 0)
          .cuboid(-21.0F, -21.0F, 0.0F, 42.0F, 42.0F, 0.0F, new Dilation(0.001F))
          // Bottom hieroglyph
          .uv(0, 48)
          .cuboid(-7.0F, 23.0F, -5.0F, 14.0F, 8.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-7.0F, 23.0F, 5.0F, 14.0F, 8.0F, 0.0F, new Dilation(0.01F))
          // Top hieroglyph
          .uv(0, 64)
          .cuboid(-3.5F, -31.5F, -5.0F, 7.0F, 9.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-3.5F, -31.5F, 5.0F, 7.0F, 9.0F, 0.0F, new Dilation(0.01F))
          // Top right hieroglyph
          .uv(0, 80)
          .cuboid(21.0F, -21.5F, -5.0F, 5.0F, 8.0F, 0.0F, new Dilation(0.01F))
          .cuboid(21.0F, -21.5F, 5.0F, 5.0F, 8.0F, 0.0F, new Dilation(0.01F))
          // Top left hieroglyph
          .uv(0, 96)
          .cuboid(-27.5F, -21.5F, -5.0F, 7.0F, 8.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-27.5F, -21.5F, 5.0F, 7.0F, 8.0F, 0.0F, new Dilation(0.01F))
          // Left hieroglyph
          .uv(16, 64)
          .cuboid(-31.0F, -2.0F, -5.0F, 6.0F, 8.0F, 0.0F, new Dilation(0.01F))
          .cuboid(-31.0F, -2.0F, 5.0F, 6.0F, 8.0F, 0.0F, new Dilation(0.01F))
          // Right hieroglyph
          .uv(16, 80)
          .cuboid(25.0F, -2.0F, -5.0F, 6.0F, 8.0F, 0.0F, new Dilation(0.01F))
          .cuboid(25.0F, -2.0F, 5.0F, 6.0F, 8.0F, 0.0F, new Dilation(0.01F)),
        ModelTransform.pivot(0.0F, 0.0F, 0.0F)
      );

      return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      matrices.push();
      this.main.render(matrices, vertices, light, overlay, red, green, blue, alpha);
      matrices.pop();
    }
  }
}
