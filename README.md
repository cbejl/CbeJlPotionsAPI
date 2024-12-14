<img src="/src/main/resources/icon.png" width=32px/> CbeJlPotionsAPI
---

Create you own potion effect!
- paper-api version: 1.21.1

Supports drinkable, splash and lingering potions. *<sub>And tipped arrows!</sub>*

## How to use?

#### Step 1.

Add an API dependency to your plugin.
Download [release](https://github.com/cbejl/CbeJlPotionsAPI/releases) and add local dependency
```xml
<dependency>
    <groupId>cbejl.plugins</groupId>
    <artifactId>CbeJlPotionsAPI</artifactId>
    <version>1.1</version> <!-- Change version on what you need -->
    <scope>system</scope>
    <systemPath>${project.basedir}/src/main/resources/CbeJlPotionsAPI-1.1.jar</systemPath> <!-- Change path on what you need -->
</dependency>
```

#### Step 2.

Create class which ``CustomEffectType.class`` implementation and implement all abstract methods.

[example](src/main/java/cbejl/plugins/potionsapi/examples/PotionOfDryness.java)

#### Step 3.

After create your own effect type, simply register the type using the ``CustomEffectManager.registerPotionEffectType(CustomPotionEffectType type)``

If you want to get a potion item with your custom effect, you can call the ``CustomEffectManager.getPotion(org.bukkit.NamespacedKey, CustomEffectProperties properties)`` or ``.getSplashPotion(...)`` or ``.getLingeringPotion(...)``method.

If you want to get a tipped arrow with your custom effect, you can call the ``CustomEffectManager.getTippedArrow(org.bukkit.NamespacedKey, CustomEffectProperties properties)`` method.

#### Step 4. PROFIT!

---

Plugin based on [CustomPotionAPI](https://github.com/Sheepion/CustomPotionAPI) by [Sheepion](https://github.com/Sheepion)
