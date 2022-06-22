package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.registry.ParticleRegistry;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CRIT, ClientPacketHandler::handleCrit);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.EXPLOSION, ClientPacketHandler::handleExplosion);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.CONNECT, ClientPacketHandler::handleConnect);
        ClientPlayNetworking.registerGlobalReceiver(PacketIdentifiers.ELEVATOR_DESTROYED, ClientPacketHandler::handleElevatorDestroyed);
    }

    private static void handleCrit(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        client.execute(() -> {
            if (client.player != null) {
                ParticleUtils.addAura(client.world, pos, ParticleTypes.CRIT, 8, 0.0D, 1.0D);
            }
        });
    }

    private static void handleExplosion(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        double radius = buf.readDouble();
        client.execute(() -> {
            if (client.player != null && client.world != null) {
                ParticleUtils.addParticle(client.world, ParticleRegistry.EXPLOSION, pos, new Vec3d(radius, 0.0D, 0.0D));
                ParticleUtils.addAura(client.world, pos, ParticleTypes.CRIT, 20, 0.0D, radius);
            }
        });
    }

    private static void handleConnect(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos0 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3d pos1 = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        client.execute(() -> {
            if (client.player != null && client.world != null) {
                double amount = pos0.distanceTo(pos1);
                Vec3d vel = pos1.subtract(pos0).normalize();
                for (int i = 0; i < amount; i++) {
                    Vec3d pos = MathUtils.lerp(pos0, pos1, i / (float)amount);
                    ParticleUtils.addParticle(client.world, ParticleTypes.ENCHANTED_HIT, pos, vel);
                }
            }
        });
    }

    private static void handleElevatorDestroyed(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        client.execute(() -> {
            if (client.world != null) {
                ParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState());
                Box box = new Box(pos.add(-1.0D, 0.0D, -1.0D), pos.add(1.0D, 0.5D, 1.0D));
                ParticleUtils.addInBox(client.world, particle, box, 25, new Vec3d(0.1D, 0.1D, 0.1D));
            }
        });
    }
}
