package cbejl.plugins.potionsapi.events;

import cbejl.plugins.potionsapi.service.CustomEffect;
import cbejl.plugins.potionsapi.service.CustomEffectManager;
import org.bukkit.Material;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import static cbejl.plugins.potionsapi.service.CustomEffectManager.*;

public class PotionConsumeListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) return;

        CustomEffect customEffect = getCustomPotionEffect(event.getItem());

        if (customEffect == null) return;

        if (!customEffect.getType().canBeApplied(event.getPlayer(), customEffect.getProperties())) return;

        customEffect.apply(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrinksMilk(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.MILK_BUCKET) {
            return;
        }
        CustomEffectManager.stopEffectByMilk(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        CustomEffect customEffect = getCustomPotionEffect(event.getEntity().getItem());
        if (customEffect == null) {
            return;
        }
        customEffect.setShooter(event.getEntity().getShooter());
        if(!event.getAffectedEntities().isEmpty()) {
            customEffect.getProperties().setRestDuration(customEffect.getProperties().getDuration() / event.getAffectedEntities().size());
        }

        event.getAffectedEntities().forEach(customEffect::apply);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event) {
        CustomEffect customPotionEffect = getCustomPotionEffect(event.getEntity().getItem());
        if (customPotionEffect == null) {
            return;
        }
        customPotionEffect.getProperties().setShooter(event.getEntity().getShooter());
        AreaEffectCloud areaEffectCloud = event.getAreaEffectCloud();
        setAreaEffectCloudProperties(customPotionEffect, areaEffectCloud);
        getAreaEffectClouds().put(event.getAreaEffectCloud(), customPotionEffect);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAreaEffectCloudApply(AreaEffectCloudApplyEvent event) {
        CustomEffect customPotionEffect = getAreaEffectClouds().get(event.getEntity());

        if (customPotionEffect != null) {
            event.getAffectedEntities().forEach(customPotionEffect::apply);
        }
    }


}
