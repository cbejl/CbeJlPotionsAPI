package cbejl.plugins.potionsapi.service;

import cbejl.plugins.potionsapi.CbeJlPotionsAPI;
import cbejl.plugins.potionsapi.abstraction.CustomEffectProperties;
import cbejl.plugins.potionsapi.abstraction.CustomEffectType;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CustomEffect extends BukkitRunnable {
    private final CustomEffectProperties properties;
    private final CustomEffectType type;
    private LivingEntity entity;

    public CustomEffect(LivingEntity entity, CustomEffectType type, CustomEffectProperties properties) {
        this.properties = properties;
        this.type = type;
        this.entity = entity;
    }

    public CustomEffect(@NotNull CustomEffectType effectType, Material potion,
                        @Nullable ProjectileSource shooter, int duration,
                        int amplifier, int checkInterval, int delay) {
        this.type = effectType;
        this.properties = new CustomEffectProperties(potion, shooter, duration, duration,
                amplifier, false, checkInterval, delay);
    }

    public static CustomEffect clone(CustomEffect effect) {
        return new CustomEffect(effect.getEntity(), effect.getType(), effect.getProperties().clone());
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public void setShooter(ProjectileSource source) {
        properties.setShooter(source);
    }

    public CustomEffectType getType() {
        return type;
    }

    public CustomEffectProperties getProperties() {
        return properties;
    }

    public void apply(LivingEntity entity) {
        if (!this.getType().canBeApplied(entity, this.getProperties())) return;

        CustomEffectManager.stopEffect(entity, type);

        CustomEffect x = clone(this);

        x.setEntity(entity);
        x.getType().beforeApply(entity, x.getProperties());

        x.runTaskTimer(CbeJlPotionsAPI.getInstance(),
                x.getProperties().getDelay(),
                x.getProperties().getCheckInterval());

        CustomEffectManager.addEffectToActive(x);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        type.afterEffect(entity, properties);
        CustomEffectManager.removeEffectFromActive(this);
        super.cancel();
    }

    @Override
    public void run() {
        if (entity instanceof Player player && !player.isOnline()) {
            cancel();
            return;
        }

        if (entity.isDead() || !entity.isValid()) {
            cancel();
            return;
        }
        type.effect(entity, properties);
        properties.setRestDuration(properties.getRestDuration() - properties.getCheckInterval());
        if (properties.getRestDuration() <= 0) {
            type.afterEffect(entity, properties);
            cancel();
        }
    }
}
