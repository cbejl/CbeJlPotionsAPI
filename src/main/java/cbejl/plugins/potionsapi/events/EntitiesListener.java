package cbejl.plugins.potionsapi.events;

import cbejl.plugins.potionsapi.abstraction.CustomEffectType;
import cbejl.plugins.potionsapi.service.CustomEffect;
import cbejl.plugins.potionsapi.service.CustomEffectManager;
import org.bukkit.Material;
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
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof ThrownPotion thrownPotion)) {
            return;
        }
        CustomEffect customEffect = getCustomPotionEffect(thrownPotion.getItem());
        if (customEffect == null) {
            return;
        }
        CustomEffectType customPotionEffectType = customEffect.getType();
        customEffect.setShooter(thrownPotion.getShooter());
        //handle potion hit block effect
        Block block = event.getHitBlock();
        if (block != null) {
            if (thrownPotion.getItem().getType() == Material.SPLASH_POTION) {
                customPotionEffectType.splashPotionHitBlockEffect(block, customEffect.getProperties());
            } else if (thrownPotion.getItem().getType() == Material.LINGERING_POTION) {
                customPotionEffectType.lingeringPotionHitBlockEffect(block, customEffect.getProperties());
            }
        }
        //handle potion hit entity effect
        Entity entity = event.getHitEntity();
        if (entity != null) {
            if (thrownPotion.getItem().getType() == Material.SPLASH_POTION) {
                customPotionEffectType.splashPotionHitEntityEffect(entity, customEffect.getProperties());
            } else if (thrownPotion.getItem().getType() == Material.LINGERING_POTION) {
                customPotionEffectType.lingeringPotionHitEntityEffect(entity, customEffect.getProperties());
            }
        }
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

}
