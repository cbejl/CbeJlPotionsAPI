package cbejl.plugins.potionsapi.abstraction;

import io.papermc.paper.potion.PotionMix;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.CraftingRecipe;

import javax.annotation.Nullable;
import java.util.ArrayList;

public interface CustomEffectType {
    /**
     * the vanilla area effect cloud's duration (600 ticks, 30 seconds)
     */
    int VANILLA_AREA_EFFECT_CLOUD_DURATION = 600;

    /**
     * the vanilla area effect cloud's duration on use (0 tick)
     */
    int VANILLA_AREA_EFFECT_CLOUD_DURATION_ON_USE = 0;

    /**
     * the vanilla area effect cloud's radius (3 blocks)
     */
    float VANILLA_AREA_EFFECT_CLOUD_RADIUS = 3.0f;

    /**
     * the vanilla area effect cloud's radius on use (0.5 blocks)
     */
    float VANILLA_AREA_EFFECT_CLOUD_RADIUS_ON_USE = -0.5f;

    /**
     * the vanilla area effect cloud's radius per tick (0.005 blocks)
     */
    float VANILLA_AREA_EFFECT_CLOUD_RADIUS_PER_TICK = 0.005f;

    /**
     * the default area effect cloud's reapplication delay (5 ticks)
     * note: this value might not be the same as the vanilla one
     */
    int DEFAULT_AREA_EFFECT_CLOUD_REAPPLICATION_DELAY = 5;

    /**
     * @return the namespaced key of the potion effect
     */
    NamespacedKey getKey();

    /**
     * check if the entity can be applied by this potion effect
     *
     * @param entity   the entity to check
     * @param properties the properties of the potion effect
     * @return true if the entity can be applied by this potion effect
     */
    boolean canBeApplied(LivingEntity entity, CustomEffectProperties properties);

    /**
     * if the effect can be removed by milk
     * this will be automatically called by CustomPotionManager when entity drinks milk.
     *
     * @param entity   the entity that drinks milk.
     * @return true if the effect can be removed by milk
     */
    boolean canBeRemovedByMilk(LivingEntity entity);

    /**
     * if the area effect cloud should spawn when the creepers with the effect exploded.
     *
     * @param creeper  the creeper that exploded
     * @param properties the properties of the potion effect that applied to the creeper
     * @return true if the area effect cloud should spawn when the creepers with the effect exploded.
     */
    default boolean spawnAreaEffectCloudOnCreeperExplosion(Creeper creeper, CustomEffectProperties properties) {
        return true;
    }

    /**
     * the things you want to do before the potion effect is applied to the entity<br>
     * this method will be called only once before the effect is applied to the entity<br>
     *
     * @param entity   the entity that is going to be applied the potion effect
     * @param properties the potion effect's properties
     */
    void beforeApply(LivingEntity entity, CustomEffectProperties properties);

    /**
     * the potion effect to the entity<br>
     * this method will be called every %checkInterval% ticks for %duration% ticks.<br>
     * if you want to make the effect instant, just make the duration and check interval the same.<br>
     *
     * @param entity   the entity to apply the potion effect
     * @param properties the properties of the potion effect that applied to the entity
     */
    void effect(LivingEntity entity, CustomEffectProperties properties);

    /**
     * the things you want at the end of the effect<br>
     * this method will be called only once after end of effect<br>
     *
     * @param entity   the entity that is going to be applied the potion effect
     * @param properties the potion effect's properties
     */
    void afterEffect(LivingEntity entity, CustomEffectProperties properties);

    /**
     * the potion effect when splash potion hit block
     * this method will be called automatically when the splash potion hit the block.
     *
     * @param block    the block that the potion hit
     * @param properties the properties of the potion effect that hit the block
     */
    default void splashPotionHitBlockEffect(Block block, CustomEffectProperties properties) {
    }

    /**
     * the potion effect when lingering potion hit block
     * this method will be called automatically when the lingering potion hit the block.
     *
     * @param block    the block that the potion hit
     * @param properties the properties of the potion effect that hit the block
     */
    default void lingeringPotionHitBlockEffect(Block block, CustomEffectProperties properties) {
    }

    default void tippedArrowHitBlockEffect(Block block, CustomEffectProperties properties){}

    /**
     * the potion effect when splash potion hit entity
     * this method will be called automatically when the splash potion hit the entity.
     *
     * @param entity   the block that the potion hit
     * @param properties the properties of the potion effect that hit the entity
     */
    default void splashPotionHitEntityEffect(Entity entity, CustomEffectProperties properties) {
    }

    /**
     * the potion effect when lingering potion hit entity<br>
     * this method will be called automatically when the lingering potion hit the entity.
     *
     * @param entity   the entity that the potion hit
     * @param properties the properties of the potion effect that hit the entity
     */
    default void lingeringPotionHitEntityEffect(Entity entity, CustomEffectProperties properties) {
    }

    default void tippedArrowHitEntityEffect(Entity entity, CustomEffectProperties properties){}

    /**
     * get all the potion mix recipes that need to register.<br>
     * those recipes will be automatically registered to the potion brewer when you register this potion effect type.
     *
     * @return the potion mix recipes
     */
    @Nullable
    ArrayList<PotionMix> potionMixes();

    /**
     * get the display name of the potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the display name
     */
    Component potionDisplayName(CustomEffectProperties properties);

    /**
     * get the lore of the potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the lore
     */
    ArrayList<Component> potionLore(CustomEffectProperties properties);

    /**
     * get the color of the potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the potion effect properties
     * @return the color
     */
    Color potionColor(CustomEffectProperties properties);

    /**
     * get the lore of the splash potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getSplashPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the lore
     */
    ArrayList<Component> splashPotionLore(CustomEffectProperties properties);

    /**
     * get the display name of the splash potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getSplashPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the display name
     */
    Component splashPotionDisplayName(CustomEffectProperties properties);

    /**
     * get the color of the splash potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getSplashPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the potion effect properties
     * @return the color
     */
    Color splashPotionColor(CustomEffectProperties properties);

    /**
     * get the lore of the lingering potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getLingeringPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the lore
     */
    ArrayList<Component> lingeringPotionLore(CustomEffectProperties properties);

    /**
     * get the display name of the lingering potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getLingeringPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the display name
     */
    Component lingeringPotionDisplayName(CustomEffectProperties properties);

    /**
     * get the color of the lingering potion item
     * <br/>used when create the potion item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getLingeringPotion(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the potion effect properties
     * @return the color
     */
    Color lingeringPotionColor(CustomEffectProperties properties);

    /**
     * the initial duration which this cloud will exist for (in ticks).
     *
     * @param properties the potion effect properties
     * @return the duration ticks
     */
    default int areaEffectCloudDuration(CustomEffectProperties properties) {
        return VANILLA_AREA_EFFECT_CLOUD_DURATION;
    }

    /**
     * the amount that the duration of this cloud will INCREASE by when it applies an effect to an entity.
     * <br/>make this value negative to make the duration decrease.
     *
     * @param properties the potion effect properties
     * @return the duration ticks on use.
     */
    default int areaEffectCloudDurationOnUse(CustomEffectProperties properties) {
        return VANILLA_AREA_EFFECT_CLOUD_DURATION_ON_USE;
    }

    /**
     * the initial radius of the cloud.
     *
     * @param properties the potion effect properties
     * @return the radius
     */
    default float areaEffectCloudRadius(CustomEffectProperties properties) {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS;
    }

    /**
     * the amount that the radius of this cloud will INCREASE by when it applies an effect to an entity.
     * <br/>make this value negative to make the radius decrease.
     *
     * @param properties the potion effect properties
     * @return the radius on use.
     */
    default float areaEffectCloudRadiusOnUse(CustomEffectProperties properties) {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS_ON_USE;
    }

    /**
     * the amount that the radius of this cloud will INCREASE by each tick.
     * <br/>make this value negative to make it decrease.
     *
     * @param properties the potion effect properties
     * @return the radius on tick.
     */
    default float areaEffectCloudRadiusPerTick(CustomEffectProperties properties) {
        return VANILLA_AREA_EFFECT_CLOUD_RADIUS_PER_TICK;
    }

    /**
     * the time that an entity will be immune from subsequent exposure.
     *
     * @param properties the potion effect properties
     * @return the time in ticks.
     */
    default int areaEffectCloudReapplicationDelay(CustomEffectProperties properties) {
        return DEFAULT_AREA_EFFECT_CLOUD_REAPPLICATION_DELAY;
    }

    /**
     * get the lore of the tipped arrow item
     * <br/>used when create the tipped arrow item by  {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getTippedArrow(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the lore
     */
    @Nullable ArrayList<? extends Component> tippedArrowLore(CustomEffectProperties properties);

    /**
     * get the display name of the tipped arrow item
     * <br/>used when create the tipped arrow item by  {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getTippedArrow(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the properties of the potion effect
     * @return the display name
     */
    @Nullable Component tippedArrowDisplayName(CustomEffectProperties properties);

    /**
     * get the color of the tipped arrow item
     * <br/>used when create the tipped arrow item by {@link cbejl.plugins.potionsapi.service.CustomEffectManager#getTippedArrow(NamespacedKey, CustomEffectProperties)}
     *
     * @param properties the potion effect properties
     * @return the color
     */
    @Nullable Color tippedArrowColor(CustomEffectProperties properties);

    /**
     * get all the tipped arrow crafting recipes that need to register.<br>
     * <br/>those recipes will be automatically registered when you register this potion effect type.
     *
     * @return the tipped arrow crafting recipes
     */
    @Nullable ArrayList<CraftingRecipe> tippedArrowRecipe();
}
