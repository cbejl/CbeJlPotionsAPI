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
import org.bukkit.inventory.*;
import org.checkerframework.checker.units.qual.A;
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
        if (!entity.isUnderWater()) {
            entity.damage(properties.getAmplifier() + 1, DamageSource.builder(DamageType.DRY_OUT).build());
        }
    }

    @Override
    public void afterEffect(LivingEntity entity, CustomEffectProperties properties) {

    }

    //create potion mixes for brewing stand
    @Override
    public @Nullable ArrayList<PotionMix> potionMixes() {
        //potion with amplifier 1 and 1200 tick duration
        ItemStack potion = CustomEffectManager.getPotion(DRYNESS_KEY,
                new CustomEffectProperties(Material.POTION, 1200, 0, 30, 0));

        //splash potion with amplifier 1 and 500 tick duration
        ItemStack splashPotion = CustomEffectManager.getSplashPotion(DRYNESS_KEY,
                new CustomEffectProperties(Material.SPLASH_POTION, 500, 0, 30, 0));

        //lingering potion with amplifier 1 and 300 tick duration
        ItemStack lingeringPotion = CustomEffectManager.getLingeringPotion(DRYNESS_KEY,
                new CustomEffectProperties(Material.LINGERING_POTION, 300, 0, 30, 0));

        //Experience bottle in bottom slots, magma block in ingredients slots, result potion1
        PotionMix mix1 = new PotionMix(
                new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_potion"),
                potion,
                new RecipeChoice.MaterialChoice(Material.EXPERIENCE_BOTTLE),
                new RecipeChoice.MaterialChoice(Material.MAGMA_BLOCK)
        );

        //potion in bottom slots, gunpowder in ingredients slots, result splash potion
        PotionMix mix2 = new PotionMix(
                new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_splash_potion"),
                splashPotion,
                new RecipeChoice.ExactChoice(potion),
                new RecipeChoice.MaterialChoice(Material.GUNPOWDER)
        );

        //splash potion in bottom slots, dragon breath in ingredients slots, result lingering potion
        PotionMix mix3 = new PotionMix(
                new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_lingering_potion"),
                lingeringPotion,
                new RecipeChoice.ExactChoice(splashPotion),
                new RecipeChoice.MaterialChoice(Material.DRAGON_BREATH)
        );

        return new ArrayList<>(List.of(mix1, mix2, mix3));
    }

    @Override
    public Component potionDisplayName(CustomEffectProperties properties) {
        return Component.text("§fPotion of Dryness");
    }

    @Override
    public ArrayList<Component> potionLore(CustomEffectProperties properties) {
        return new ArrayList<>(List.of(Component.text(String.format("§f§сDryness %S (%S sec)", properties.getAmplifier() + 1, properties.getDuration() / 20))));
    }

    @Override
    public Color potionColor(CustomEffectProperties properties) {
        return Color.fromRGB(0x969992);
    }

    @Override
    public ArrayList<Component> splashPotionLore(CustomEffectProperties properties) {
        return potionLore(properties);
    }

    @Override
    public Component splashPotionDisplayName(CustomEffectProperties properties) {
        return Component.text("§fSplash potion of Dryness");
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
        return Component.text("§fLingering potion of Dryness");
    }

    @Override
    public Color lingeringPotionColor(CustomEffectProperties properties) {
        return potionColor(properties);
    }

    @Override
    public @Nullable ArrayList<? extends Component> tippedArrowLore(CustomEffectProperties properties) {
        return potionLore(properties);
    }

    @Override
    public @Nullable Component tippedArrowDisplayName(CustomEffectProperties properties) {
        return Component.text("§fArrow of Dryness");
    }

    @Override
    public @Nullable Color tippedArrowColor(CustomEffectProperties properties) {
        return potionColor(properties);
    }

    //create crafting recipes for tipped arrow (return null if you dont need tipped arrows)
    @Override
    public @Nullable ArrayList<CraftingRecipe> tippedArrowRecipe() {
        ItemStack arrow = CustomEffectManager.getTippedArrow(DRYNESS_KEY,
                new CustomEffectProperties(Material.POTION, 300, 0, 30, 0));

        arrow.setAmount(8);

        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(CbeJlPotionsAPI.getInstance(), "dryness_tipped_arrow"), arrow);

        recipe.addIngredient(8, Material.ARROW);
        recipe.addIngredient(CustomEffectManager.getLingeringPotion(
                DRYNESS_KEY,
                new CustomEffectProperties(Material.LINGERING_POTION, 300, 0, 30, 0)
        ));

        return new ArrayList<>(List.of(recipe));
    }
}
