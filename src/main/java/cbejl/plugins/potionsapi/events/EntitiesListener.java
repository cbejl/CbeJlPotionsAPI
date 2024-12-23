package cbejl.plugins.potionsapi.events;

import cbejl.plugins.potionsapi.abstraction.CustomEffectType;
import cbejl.plugins.potionsapi.service.CustomEffect;
import cbejl.plugins.potionsapi.service.CustomEffectManager;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static cbejl.plugins.potionsapi.service.CustomEffectManager.*;

public class EntitiesListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    private void onPlayerJoin(PlayerJoinEvent event) {
        CustomEffectManager.resumeEffects(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        CustomEffectManager.pauseEffects(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion thrownPotion)) {
            return;
        }
        projectileProcessing(thrownPotion.getItem(), thrownPotion, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTippedArrowHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }
        projectileProcessing(arrow.getItemStack(), arrow, event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Creeper creeper) {
            if (!getActiveEffects().get(creeper.getUniqueId()).isEmpty()) {
                for (CustomEffect potionEffect : getActiveEffects().get(creeper.getUniqueId())) {
                    if (potionEffect.getType().spawnAreaEffectCloudOnCreeperExplosion(creeper, potionEffect.getProperties())) {
                        AreaEffectCloud areaEffectCloud = (AreaEffectCloud) creeper.getWorld().spawnEntity(creeper.getLocation(), EntityType.AREA_EFFECT_CLOUD, CreatureSpawnEvent.SpawnReason.EXPLOSION);
                        areaEffectCloud.setColor(potionEffect.getType().lingeringPotionColor(potionEffect.getProperties()));
                        areaEffectCloud.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0), true);
                        setAreaEffectCloudProperties(potionEffect, areaEffectCloud);
                        getAreaEffectClouds().put(areaEffectCloud, potionEffect);
                    }
                }
            }
        }
    }

    private static void projectileProcessing(ItemStack itemStack, Projectile projectile, ProjectileHitEvent event) {
        CustomEffect customEffect = getCustomPotionEffect(itemStack);
        if (customEffect == null) {
            return;
        }
        CustomEffectType customPotionEffectType = customEffect.getType();
        customEffect.setShooter(projectile.getShooter());
        //handle potion hit block effect
        Block block = event.getHitBlock();
        if (block != null) {
            switch (itemStack.getType()) {
                case SPLASH_POTION -> customPotionEffectType.splashPotionHitBlockEffect(block, customEffect.getProperties());
                case LINGERING_POTION -> customPotionEffectType.lingeringPotionHitBlockEffect(block, customEffect.getProperties());
                case TIPPED_ARROW -> customPotionEffectType.tippedArrowHitBlockEffect(block, customEffect.getProperties());
            }
        }
        //handle potion hit entity effect
        Entity entity = event.getHitEntity();
        if (entity != null) {
            switch (itemStack.getType()) {
                case SPLASH_POTION -> customPotionEffectType.splashPotionHitEntityEffect(entity, customEffect.getProperties());
                case LINGERING_POTION -> customPotionEffectType.lingeringPotionHitEntityEffect(entity, customEffect.getProperties());
                case TIPPED_ARROW -> {
                    customPotionEffectType.tippedArrowHitEntityEffect(entity, customEffect.getProperties());
                    if(entity instanceof LivingEntity livingEntity) customEffect.apply(livingEntity);
                }
            }
        }
    }

}
