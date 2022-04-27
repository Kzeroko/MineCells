package com.github.mim1q.minecells.client.model.projectile;

import com.github.mim1q.minecells.entity.projectile.BigGrenadeEntity;
import net.minecraft.client.model.*;

public class BigGrenadeEntityModel extends AbstractGrenadeEntityModel<BigGrenadeEntity> {

    public BigGrenadeEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-6.0F, 0.0F, -6.0F,
                                12.0F, 12.0F, 12.0F),
                ModelTransform.NONE
        );
        return TexturedModelData.of(modelData, 48, 24);
    }
}
