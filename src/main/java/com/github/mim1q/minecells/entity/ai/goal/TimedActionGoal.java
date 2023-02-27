package com.github.mim1q.minecells.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvent;

import java.util.function.Predicate;

public class TimedActionGoal<E extends LivingEntity> extends Goal {

  protected final E entity;
  protected final CooldownGetter cooldownGetter;
  protected final CooldownSetter cooldownSetter;
  protected final StateSetter stateSetter;
  protected final int defaultCooldown;
  protected final int actionTick;
  protected final int length;
  protected final float chance;
  protected final SoundEvent chargeSound;
  protected final SoundEvent releaseSound;
  protected final float soundVolume;
  protected final Predicate<E> startPredicate;

  private int ticks = 0;

  public TimedActionGoal(Builder<E, ?> builder) {
    entity = builder.entity;
    cooldownGetter = builder.cooldownGetter;
    cooldownSetter = builder.cooldownSetter;
    stateSetter = builder.stateSetter;
    defaultCooldown = builder.defaultCooldown;
    actionTick = builder.actionTick;
    length = builder.length;
    chance = builder.chance;
    chargeSound = builder.chargeSound;
    releaseSound = builder.releaseSound;
    soundVolume = builder.soundVolume;
    startPredicate = builder.startPredicate;
  }

  @Override
  public boolean canStart() {
    int cooldown = cooldownGetter.getCooldown();
    return cooldown == 0
      && (this.chance == 1.0F || entity.getRandom().nextFloat() < chance)
      && startPredicate.test(entity);
  }

  @Override
  public boolean shouldContinue() {
    return this.ticks <= this.length;
  }

  @Override
  public void start() {
    this.ticks = 0;
    this.stateSetter.setState(State.CHARGE, true);
    this.playChargeSound();
  }

  @Override
  public void tick() {
    if (this.ticks == actionTick) {
      this.runAction();
      this.stateSetter.setState(State.CHARGE, false);
      this.stateSetter.setState(State.RELEASE, true);
      this.playReleaseSound();
    } else if (this.ticks < actionTick) {
      this.charge();
    } else {
      this.release();
    }
    this.ticks++;
  }

  protected int ticks() {
    return this.ticks;
  }

  protected void runAction() {

  }

  protected void charge() {

  }

  protected void release() {

  }

  protected void playSound(SoundEvent soundEvent) {
    if (soundEvent != null) {
      this.entity.playSound(soundEvent, this.soundVolume, 1.0F);
    }
  }

  protected void playChargeSound() {
    this.playSound(this.chargeSound);
  }

  protected void playReleaseSound() {
    this.playSound(this.releaseSound);
  }

  @Override
  public void stop() {
    cooldownSetter.setCooldown(defaultCooldown);
    this.stateSetter.setState(State.CHARGE, false);
    this.stateSetter.setState(State.RELEASE, false);
  }

  @Override
  public boolean shouldRunEveryTick() {
    return true;
  }

  public enum State {
    IDLE,
    CHARGE,
    RELEASE
  }

  public interface CooldownGetter {
    int getCooldown();
  }

  public interface CooldownSetter {
    void setCooldown(int cooldown);
  }

  public interface StateSetter {
    void setState(State state, boolean bool);
  }

  @SuppressWarnings("unchecked")
  public static class Builder<E extends LivingEntity, B extends Builder<E, B>> {
    public E entity;
    public CooldownGetter cooldownGetter;
    public CooldownSetter cooldownSetter;
    public StateSetter stateSetter;
    public int defaultCooldown = 100;
    public int actionTick = 10;
    public int length = 20;
    public float chance = 1.0F;
    public SoundEvent chargeSound;
    public SoundEvent releaseSound;
    public float soundVolume = 1.0F;
    public Predicate<E> startPredicate = (mob) -> true;

    public Builder(E entity) {
      this.entity = entity;
    }

    public B cooldownSetter(CooldownSetter cooldownSetter) {
      this.cooldownSetter = cooldownSetter;
      return (B) this;
    }

    public B cooldownGetter(CooldownGetter cooldownGetter) {
      this.cooldownGetter = cooldownGetter;
      return (B) this;
    }

    public B stateSetter(StateSetter stateSetter) {
      this.stateSetter = stateSetter;
      return (B) this;
    }

    public B defaultCooldown(int defaultCooldown) {
      this.defaultCooldown = defaultCooldown;
      return (B) this;
    }

    public B actionTick(int actionTick) {
      this.actionTick = actionTick;
      return (B) this;
    }

    public B length(int length) {
      this.length = length;
      return (B) this;
    }

    public B chance(float chance) {
      this.chance = chance;
      return (B) this;
    }

    public B chargeSound(SoundEvent chargeSound) {
      this.chargeSound = chargeSound;
      return (B) this;
    }

    public B releaseSound(SoundEvent releaseSound) {
      this.releaseSound = releaseSound;
      return (B) this;
    }

    public B soundVolume(float soundVolume) {
      this.soundVolume = soundVolume;
      return (B) this;
    }

    public B startPredicate(Predicate<E> predicate) {
      this.startPredicate = predicate;
      return (B) this;
    }

    protected void check() {
      if (this.cooldownGetter == null || this.cooldownSetter == null || this.stateSetter == null) {
        throw new IllegalStateException("cooldownGetter, cooldownSetter and stateSetter must be set");
      }
    }

    public TimedActionGoal<E> build() {
      this.check();
      return new TimedActionGoal<>(this);
    }
  }
}
