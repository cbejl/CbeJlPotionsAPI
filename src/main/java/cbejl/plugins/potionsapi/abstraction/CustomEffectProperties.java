package cbejl.plugins.potionsapi.abstraction;

import org.bukkit.Material;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nullable;

public class CustomEffectProperties {
    /**
     * the potion item that carries the effect
     */
    private final Material potion;

    /**
     * the effect's duration
     */
    private final int duration;
    /**
     * the effect's check interval
     */
    private final int checkInterval;
    /**
     * the ticks before the effect starts.
     */
    private final int delay;
    /**
     * the effect's rest duration
     */
    private int restDuration;
    /**
     * the effect's amplifier
     */
    private int amplifier;
    private boolean ambient;
    /**
     * the shooter of the splash/lingering potion
     */
    private @Nullable ProjectileSource shooter;

    /**
     * @param potion        the potion item that carries the effect
     * @param duration      the effect's duration
     * @param amplifier     the effect's amplifier
     * @param checkInterval the effect's check interval
     * @param delay         the ticks before the effect starts.
     */
    public CustomEffectProperties(@Nullable Material potion, int duration, int amplifier, int checkInterval, int delay) {
        this(potion, null, duration, duration, amplifier, false, checkInterval, delay);
    }

    /**
     * @param potion        the potion item that carries the effect
     * @param shooter       the shooter of the splash/lingering potion
     * @param duration      the effect's duration
     * @param restDuration  the effect's rest duration
     * @param amplifier     the effect's amplifier
     * @param ambient       the effect's ambient
     * @param checkInterval the effect's check interval
     * @param delay         the ticks before the effect starts.
     */
    public CustomEffectProperties(@Nullable Material potion, @Nullable ProjectileSource shooter,
                                  int duration, int restDuration, int amplifier,
                                  boolean ambient, int checkInterval, int delay) {
        this.potion = potion;
        this.shooter = shooter;
        this.duration = duration;
        this.restDuration = restDuration;
        this.amplifier = amplifier;
        this.ambient = ambient;
        this.checkInterval = checkInterval;
        this.delay = delay;
    }

    /**
     * get the mutable potion item that carries the effect
     *
     * @return the potion item
     */
    public Material getPotion() {
        return potion;
    }

    /**
     * @return the effect's duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @return the effect's rest duration BEFORE this time's effect being called.
     */
    public int getRestDuration() {
        return restDuration;
    }

    /**
     * set the effect's rest duration<br>
     * edit this value to change the effect's rest duration
     *
     * @param restDuration the effect's new rest duration
     */
    public void setRestDuration(int restDuration) {
        this.restDuration = restDuration;
    }

    /**
     * @return the effect's amplifier
     */
    public int getAmplifier() {
        return amplifier;
    }

    /**
     * set the effect's amplifier<br>
     * edit this value to change the effect's amplifier
     *
     * @param amplifier the effect's new amplifier
     */
    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    /**
     * @return if the effect is ambient
     */
    public boolean isAmbient() {
        return ambient;
    }

    /**
     * set if the effect is ambient<br>
     * edit this value to change the effect's ambient
     *
     * @param ambient the effect's new ambient
     */
    public void setAmbient(boolean ambient) {
        this.ambient = ambient;
    }

    /**
     * @return the effect's check interval.
     */
    public int getCheckInterval() {
        return checkInterval;
    }

    /**
     * @return the ticks before the effect starts.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @return the shooter of the splash/lingering potion
     */
    public @Nullable ProjectileSource getShooter() {
        return shooter;
    }

    /**
     * set the shooter of the splash/lingering potion
     *
     * @param shooter the new shooter
     */
    public void setShooter(@Nullable ProjectileSource shooter) {
        this.shooter = shooter;
    }

    /**
     * @return a copy of this property
     */
    public CustomEffectProperties clone() {
        return new CustomEffectProperties(potion, shooter, duration, restDuration, amplifier, ambient, checkInterval, delay);
    }
}
