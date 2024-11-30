package cbejl.plugins.potionsapi.service;

import cbejl.plugins.potionsapi.CbeJlPotionsAPI;
import cbejl.plugins.potionsapi.abstraction.CustomEffectProperties;
import cbejl.plugins.potionsapi.abstraction.CustomEffectType;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static cbejl.plugins.potionsapi.abstraction.PropertyKey.*;

public class CustomEffectManager {
    private static final List<CustomEffectType> CUSTOM_EFFECT_TYPES = new ArrayList<>();
    private static final Map<UUID, Set<CustomEffect>> ACTIVE_EFFECTS = new HashMap<>();
    private static final Map<UUID, Set<CustomEffect>> PAUSED_EFFECTS = new HashMap<>();
    private static final HashMap<AreaEffectCloud, CustomEffect> areaEffectClouds = new HashMap<>();

    static {
        //clear dead area effect clouds task
        CbeJlPotionsAPI.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(CbeJlPotionsAPI.getInstance(), () -> {
            ArrayList<AreaEffectCloud> toRemove = new ArrayList<>();
            for (AreaEffectCloud areaEffectCloud : areaEffectClouds.keySet()) {
                if (areaEffectCloud.isDead()) {
                    toRemove.add(areaEffectCloud);
                }
            }
            for (AreaEffectCloud areaEffectCloud : toRemove) {
                areaEffectClouds.remove(areaEffectCloud);
            }
        }, 0L, 40L);
    }

    /**
     * register a custom potion effect type
     * this will register the listeners either if your class implemented Listener interface
     *
     * @param customPotionEffectType the custom potion effect type
     */
    public static void registerPotionEffectType(CustomEffectType customPotionEffectType) {
        //add to type list
        CUSTOM_EFFECT_TYPES.add(customPotionEffectType);

        //register potion mix recipes
        ArrayList<PotionMix> potionMixes = customPotionEffectType.potionMixes();
        if (potionMixes != null) {
            PotionBrewer potionBrewer = CbeJlPotionsAPI.getInstance().getServer().getPotionBrewer();
            for (PotionMix potionMix : potionMixes) {
                potionBrewer.removePotionMix(potionMix.getKey());
                potionBrewer.addPotionMix(potionMix);
            }
        }
    }

    public static Map<UUID, Set<CustomEffect>> getActiveEffects() {
        return ACTIVE_EFFECTS;
    }

    public static List<CustomEffectType> getCustomEffectTypes() {
        return CUSTOM_EFFECT_TYPES;
    }

    public static Map<UUID, Set<CustomEffect>> getPausedEffects() {
        return PAUSED_EFFECTS;
    }

    public static void pauseEffects(UUID uuid) {
        if (!ACTIVE_EFFECTS.containsKey(uuid) || ACTIVE_EFFECTS.get(uuid).isEmpty()) return;

        if (PAUSED_EFFECTS.isEmpty() || !PAUSED_EFFECTS.containsKey(uuid)) {
            PAUSED_EFFECTS.put(uuid, new HashSet<>());
        }

        Iterator<CustomEffect> iterator = ACTIVE_EFFECTS.get(uuid).iterator();
        while (iterator.hasNext()) {
            CustomEffect effect = iterator.next();
            PAUSED_EFFECTS.get(uuid).add(CustomEffect.clone(effect));
            effect.cancel();
            iterator.remove();
        }
    }

    public static void resumeEffects(UUID uuid) {
        if (!PAUSED_EFFECTS.containsKey(uuid) || PAUSED_EFFECTS.get(uuid).isEmpty()) return;

        Iterator<CustomEffect> iterator = PAUSED_EFFECTS.get(uuid).iterator();
        while (iterator.hasNext()) {
            CustomEffect effect = iterator.next();

            effect.apply(Bukkit.getPlayer(uuid));

            iterator.remove();
        }
    }

    public static void addEffectToActive(CustomEffect effect) {
        UUID uuid = effect.getEntity().getUniqueId();
        if (!ACTIVE_EFFECTS.containsKey(uuid) || ACTIVE_EFFECTS.get(uuid).isEmpty()) {
            ACTIVE_EFFECTS.put(uuid, new HashSet<>());
        }

        ACTIVE_EFFECTS.get(uuid).add(effect);
    }

    public static void removeEffectFromActive(CustomEffect effect) {
        UUID uuid = effect.getEntity().getUniqueId();
        if (!ACTIVE_EFFECTS.containsKey(uuid) || ACTIVE_EFFECTS.get(uuid).isEmpty()) return;

        ACTIVE_EFFECTS.get(uuid).removeIf(x -> x.getType().getKey().equals(effect.getType().getKey()));
    }

    public static void stopEffect(LivingEntity entity, CustomEffectType type) {
        UUID uuid = entity.getUniqueId();
        if (!ACTIVE_EFFECTS.containsKey(uuid) || ACTIVE_EFFECTS.get(uuid).isEmpty()) return;

        Iterator<CustomEffect> iterator = ACTIVE_EFFECTS.get(uuid)
                .stream()
                .filter(x -> x.getType().getKey().equals(type.getKey()))
                .iterator();

        while (iterator.hasNext()) {
            CustomEffect effect = iterator.next();
            effect.cancel();
        }
    }

    public static void stopEffectByMilk(LivingEntity entity) {
        UUID uuid = entity.getUniqueId();
        if (!ACTIVE_EFFECTS.containsKey(uuid) || ACTIVE_EFFECTS.get(uuid).isEmpty()) return;

        Iterator<CustomEffect> iterator = ACTIVE_EFFECTS.get(uuid)
                .stream()
                .filter(x -> x.getType().canBeRemovedByMilk(entity))
                .iterator();

        while (iterator.hasNext()) {
            CustomEffect effect = iterator.next();
            effect.cancel();
        }
    }

    /**
     * set the properties of the area effect cloud<br>
     * note: this WILL NOT add the area effect cloud to the areaEffectClouds map
     *
     * @param potionEffect    the potion effect
     * @param areaEffectCloud the area effect cloud
     */
    public static void setAreaEffectCloudProperties(CustomEffect potionEffect, AreaEffectCloud areaEffectCloud) {
        CustomEffectProperties property = potionEffect.getProperties();
        CustomEffectType effectType = potionEffect.getType();
        areaEffectCloud.setDuration(effectType.areaEffectCloudDuration(property));
        areaEffectCloud.setDurationOnUse(effectType.areaEffectCloudDurationOnUse(property));
        areaEffectCloud.setRadius(effectType.areaEffectCloudRadius(property));
        areaEffectCloud.setRadiusOnUse(effectType.areaEffectCloudRadiusOnUse(property));
        areaEffectCloud.setRadiusPerTick(effectType.areaEffectCloudRadiusPerTick(property));
        areaEffectCloud.setReapplicationDelay(effectType.areaEffectCloudReapplicationDelay(property));
    }

    public static HashMap<AreaEffectCloud, CustomEffect> getAreaEffectClouds() {
        return areaEffectClouds;
    }

    /**
     * get the potion effect from an item
     *
     * @param item the item
     * @return the potion effect, null if not found
     */
    public static @Nullable CustomEffect getCustomPotionEffect(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) {
            return null;
        }
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        // check if the potion is a custom potion
        String typeKey = pdc.get(EFFECT_TYPE, PersistentDataType.STRING);
        if (typeKey == null) {
            return null;
        }
        //get the effect information
        NamespacedKey type = NamespacedKey.fromString(typeKey);
        assert type != null;
        CustomEffectType customPotionEffectType = null;
        for (CustomEffectType potionEffectType : CUSTOM_EFFECT_TYPES) {
            if (potionEffectType.getKey().equals(type)) {
                customPotionEffectType = potionEffectType;
            }
        }
        // check if the potion effect type is valid
        if (customPotionEffectType == null) {
            return null;
        }
        Integer duration = pdc.get(EFFECT_DURATION, PersistentDataType.INTEGER);
        if (duration == null) {
            duration = 0;
        }
        Integer checkInterval = pdc.get(EFFECT_CHECK_INTERVAL, PersistentDataType.INTEGER);
        if (checkInterval == null) {
            checkInterval = 20;
        }
        Integer amplifier = pdc.get(EFFECT_AMPLIFIER, PersistentDataType.INTEGER);
        if (amplifier == null) {
            amplifier = 0;
        }
        Integer delay = pdc.get(EFFECT_DELAY, PersistentDataType.INTEGER);
        if (delay == null) {
            delay = 0;
        }
        return new CustomEffect(customPotionEffectType, item.getType(), null, duration, amplifier, checkInterval, delay);
    }

    public static CustomEffectType getCustomEffectType(NamespacedKey key) {
        for (CustomEffectType potionEffectType : CUSTOM_EFFECT_TYPES) {
            if (potionEffectType.getKey().equals(key)) {
                return potionEffectType;
            }
        }
        return null;
    }

    /**
     * create a custom potion item use given material
     *
     * @param material               the material of the potion
     * @param customPotionEffectType the custom potion effect type
     * @param property               the potion property
     * @return the custom potion item
     */
    public static ItemStack getPotion(Material material, NamespacedKey customPotionEffectType, CustomEffectProperties property) {
        ItemStack result = new ItemStack(material);
        ItemMeta meta = result.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        //set the name, lore, color, enchant glow.
        for (CustomEffectType potionEffectType : CUSTOM_EFFECT_TYPES) {
            if (potionEffectType.getKey().equals(customPotionEffectType)) {
                if (material.equals(Material.POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.potionColor(property));
                    meta.displayName(potionEffectType.potionDisplayName(property));
                    meta.lore(potionEffectType.potionLore(property));
                    meta.setMaxStackSize(16);
                } else if (material.equals(Material.SPLASH_POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.splashPotionColor(property));
                    meta.displayName(potionEffectType.splashPotionDisplayName(property));
                    meta.lore(potionEffectType.splashPotionLore(property));
                    meta.setMaxStackSize(8);
                } else if (material.equals(Material.LINGERING_POTION)) {
                    ((PotionMeta) meta).setColor(potionEffectType.lingeringPotionColor(property));
                    ((PotionMeta) meta).addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0, false, false, false), true);
                    meta.displayName(potionEffectType.lingeringPotionDisplayName(property));
                    meta.lore(potionEffectType.lingeringPotionLore(property));
                    meta.setMaxStackSize(1);
                }
                break;
            }
        }
        pdc.set(EFFECT_TYPE, PersistentDataType.STRING, customPotionEffectType.toString());
        pdc.set(EFFECT_DURATION, PersistentDataType.INTEGER, property.getDuration());
        pdc.set(EFFECT_CHECK_INTERVAL, PersistentDataType.INTEGER, property.getCheckInterval());
        pdc.set(EFFECT_AMPLIFIER, PersistentDataType.INTEGER, property.getAmplifier());
        pdc.set(EFFECT_DELAY, PersistentDataType.INTEGER, property.getDelay());
        result.setItemMeta(meta);
        return result;
    }

    /**
     * create a NORMAL VANILLA potion such as water bottle,
     * YOU CAN NOT USE THIS METHOD TO CREATE A POTION WITH CUSTOM EFFECT.
     *
     * @param material   the material of the potion.
     *                   you should only use Material.POTION or Material.SPLASH_POTION or Material.LINGERING_POTION.
     * @param potionType the potion type of the potion.
     *                   PotionType.WATER presents the water bottle.
     * @return the custom potion item
     */
    public static ItemStack getPotion(Material material, PotionType potionType) {
        ItemStack bottle = new ItemStack(material, 1);
        ItemMeta meta = bottle.getItemMeta();
        PotionMeta potionMeta = (PotionMeta) meta;
        potionMeta.setBasePotionType(potionType);
        bottle.setItemMeta(meta);
        return bottle;
    }

    /**
     * create a custom potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param property               the custom potion effect property
     * @return the custom potion item
     */
    public static ItemStack getPotion(NamespacedKey customPotionEffectType, CustomEffectProperties property) {
        return getPotion(Material.POTION, customPotionEffectType, property);
    }

    /**
     * create a custom splash potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param property               the custom potion effect property
     * @return the custom potion item
     */
    public static ItemStack getSplashPotion(NamespacedKey customPotionEffectType, CustomEffectProperties property) {
        return getPotion(Material.SPLASH_POTION, customPotionEffectType, property);
    }

    /**
     * create a custom lingering potion item
     *
     * @param customPotionEffectType the custom potion effect type
     * @param property               the custom potion effect property
     * @return the custom potion item
     */
    public static ItemStack getLingeringPotion(NamespacedKey customPotionEffectType, CustomEffectProperties property) {
        return getPotion(Material.LINGERING_POTION, customPotionEffectType, property);
    }
}
