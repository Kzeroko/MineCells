package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.mixin.world.TrunkPlacerTypeInvoker;
import com.github.mim1q.minecells.world.feature.tree.BigPromenadeTreeTrunkPlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class MineCellsPlacerTypes {
  public static final TrunkPlacerType<PromenadeTreeTrunkPlacer> PROMENADE_TRUNK_PLACER = TrunkPlacerTypeInvoker.register(
    MineCells.createId("promenade_trunk_plancer").toString(),
    PromenadeTreeTrunkPlacer.CODEC
  );

  public static final TrunkPlacerType<BigPromenadeTreeTrunkPlacer> PROMENADE_BIG_TRUNK_PLACER = TrunkPlacerTypeInvoker.register(
    MineCells.createId("big_promenade_trunk_plancer").toString(),
    BigPromenadeTreeTrunkPlacer.CODEC
  );

  public static void init() { }
}
