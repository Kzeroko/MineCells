package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.entity.interfaces.IJumpAttackEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class JumpAttackGoal<E extends MineCellsEntity & IJumpAttackEntity> extends Goal {

    protected E entity;
    protected LivingEntity target;
    protected int ticks = 0;
    List<UUID> alreadyAttacked;


    public JumpAttackGoal(E entity) {
        this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
        this.entity = entity;
        this.alreadyAttacked = new ArrayList<>();
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.entity.getTarget();
        if (target == null)
            return false;

        double d = this.entity.distanceTo(target);

        boolean canJump = this.entity.getY() >= this.entity.getTarget().getY() && this.entity.canSee(target);
        return canJump && this.entity.getJumpAttackCooldown() == 0 && d >= 3.5d && d <= 15.0d && this.entity.getRandom().nextFloat() < 0.05f;
    }

    @Override
    public void start() {
        this.target = this.entity.getTarget();
        this.entity.setAttackState("jump");
        this.ticks = 0;
        this.alreadyAttacked.clear();

        if(!this.entity.world.isClient() && this.entity.getJumpAttackSoundEvent() != null) {
            this.entity.playSound(this.entity.getJumpAttackSoundEvent(),0.5f,1.0f);
        }
    }

    @Override
    public boolean shouldContinue() {
        return (this.ticks < this.entity.getJumpAttackLength() || !this.entity.isOnGround()) && this.target.isAlive();
    }

    @Override
    public void stop() {
        this.entity.resetAttackState();
        this.entity.setJumpAttackCooldown(this.entity.getJumpAttackMaxCooldown());
    }

    @Override
    public void tick() {
        if(this.target != null) {

            if(this.ticks < this.entity.getJumpAttackActionTick())
                this.entity.getLookControl().lookAt(this.target, 30.0f, 30.0f);
            if(this.ticks == this.entity.getJumpAttackActionTick())
                this.jump();
            else if(!this.entity.isOnGround())
                this.attack();

            this.ticks++;
        }
    }

    public void jump() {
        Vec3d diff = this.entity.getPos().add(this.target.getPos().multiply(-1.0d)).normalize();
        this.entity.setVelocity(diff.multiply(-2.0d, 0.0d, -2.0d).add(0.0d, 0.5d, 0.0d));
    }

    public void attack() {
        List<PlayerEntity> players = this.entity.world.getEntitiesByClass(PlayerEntity.class, this.entity.getBoundingBox().expand(0.25d), (e) -> !this.alreadyAttacked.contains(e.getUuid()));
        for(PlayerEntity player : players) {
            this.entity.tryAttack(player);
            this.alreadyAttacked.add(player.getUuid());
        }
    }
}
