package cbejl.plugins.potionsapi.examples;

import cbejl.plugins.potionsapi.CbeJlPotionsAPI;
import cbejl.plugins.potionsapi.abstraction.CustomEffectProperties;
import cbejl.plugins.potionsapi.abstraction.CustomEffectType;
import cbejl.plugins.potionsapi.service.CustomEffectManager;
import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PotionOfDryness implements CustomEffectType {
    public static NamespacedKey DRYNESS_KEY = new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_effect");
    //Key of you potion effect
    @Override
    public NamespacedKey getKey() {
        return DRYNESS_KEY;
    }

    //Allow to apply effect only on Player's
    @Override
    public boolean canBeApplied(LivingEntity entity, CustomEffectProperties properties) {
        return entity instanceof Player;
    }

    //Player can remove effect by milk
    @Override
    public boolean canBeRemovedByMilk(LivingEntity entity) {
        return true;
    }

    //Before start effect we do check block where entity stand and if it water replace it to air
    @Override
    public void beforeApply(LivingEntity entity, CustomEffectProperties properties) {
        Block block = entity.getLocation().getBlock();
        if (block.getType() == Material.WATER) {
            block.setType(Material.AIR);
            entity.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, entity.getLocation(), 3);
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
        } else if (block.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()) {
            waterlogged.setWaterlogged(false);
            block.setBlockData(waterlogged);
            entity.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, entity.getLocation(), 3);
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
        }
    }

    //if entity not underwater he takes damage equal to the amplifier
    @Override
    public void effect(LivingEntity entity, CustomEffectProperties properties) {
        if(!entity.isUnderWater()) {
            entity.damage(properties.getAmplifier() + 1, DamageSource.builder(DamageType.DRY_OUT).build());
        }
    }

    @Override
    public void afterEffect(LivingEntity entity, CustomEffectProperties properties) {

    }

    //create potion mixes for brewing stand
    @Override
    public @Nullable ArrayList<PotionMix> potionMixes() {
        //potion1 with amplifier 1 and 300 tick duration
        ItemStack potion1 = CustomEffectManager.getPotion(DRYNESS_KEY,
                new CustomEffectProperties(Material.POTION, 300, 0, 30, 0));

        //potion2 with amplifier 2 and 200 tick duration
        ItemStack potion2 = CustomEffectManager.getPotion(DRYNESS_KEY,
                new CustomEffectProperties(Material.POTION, 200, 1, 30, 0));

        //Experience bottle in bottom slots, magma block in ingredients slots, result potion1
        PotionMix mix1 = new PotionMix(
                new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_potion_0"),
                potion1,
                new RecipeChoice.MaterialChoice(Material.EXPERIENCE_BOTTLE),
                new RecipeChoice.MaterialChoice(Material.MAGMA_BLOCK)
        );

        //potion1 in bottom slots, glowstone dust in ingredients slots, result potion2
        PotionMix mix2 = new PotionMix(
                new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_potion_1"),
                potion2,
                new RecipeChoice.ExactChoice(potion1),
                new RecipeChoice.MaterialChoice(Material.GLOWSTONE_DUST)
        );

        return new ArrayList<>(List.of(mix1, mix2));
    }

    @Override
    public Component potionDisplayName(CustomEffectProperties properties) {
        return Component.text("Potion of Dryness");
    }

    @Override
    public ArrayList<Component> potionLore(CustomEffectProperties properties) {
        return new ArrayList<>(List.of(Component.text( String.format("Dryness %S1 (%S2 sec)", properties.getAmplifier() + 1, properties.getDuration() / 20))));
    }

    @Override
    public Color potionColor(CustomEffectProperties properties) {
        return Color.RED;
    }

    @Override
    public ArrayList<Component> splashPotionLore(CustomEffectProperties properties) {
        return potionLore(properties);
    }

    @Override
    public Component splashPotionDisplayName(CustomEffectProperties properties) {
        return Component.text("Splash potion of Dryness");
    }

    @Override
    public Color splashPotionColor(CustomEffectProperties properties) {
        return potionColor(properties);
    }

    @Override
    public ArrayList<Component> lingeringPotionLore(CustomEffectProperties properties) {
        return potionLore(properties);
    }

    @Override
    public Component lingeringPotionDisplayName(CustomEffectProperties properties) {
        return Component.text("Lingering potion of Dryness");
    }

    @Override
    public Color lingeringPotionColor(CustomEffectProperties properties) {
        return potionColor(properties);
    }
}
