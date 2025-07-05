package mekanism.common;

import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.text.IHasTranslationKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//Note: This isn't an actual registry but should make things a bit cleaner
@MethodsReturnNonnullByDefault
public class MekanismDamageSource extends DamageSource implements IHasTranslationKey {

    public static final MekanismDamageSource LASER = new MekanismDamageSource("laser");
    public static final MekanismDamageSource RADIATION = new MekanismDamageSource("radiation").setDamageBypassesArmor();
    public static final MekanismDamageSource SMARTATTACKDAMAGE = new MekanismDamageSource("smartattackdamage");

    private final String translationKey;

    private final Vec3d damageLocation;

    @Nullable
    private Entity AttackingEntities;


    public MekanismDamageSource(String damageType) {
        this(damageType, null);
    }

    private MekanismDamageSource(@Nonnull String damageType, @Nullable Vec3d damageLocation) {
        super(Mekanism.MODID + "." + damageType);
        this.translationKey = "death.attack." + getDamageType();
        this.damageLocation = damageLocation;
    }

    /**
     * Gets a new instance of this damage source, that is positioned at the given location.
     */
    public MekanismDamageSource fromPosition(@Nonnull Vec3d damageLocation) {
        return new MekanismDamageSource(getDamageType(), damageLocation);
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Nullable
    @Override
    public Vec3d getDamageLocation() {
        return damageLocation;
    }

    @Override
    public MekanismDamageSource setProjectile() {
        super.setProjectile();
        return this;
    }

    @Override
    public MekanismDamageSource setExplosion() {
        super.setExplosion();
        return this;
    }

    @Override
    public MekanismDamageSource setDamageBypassesArmor() {
        super.setDamageBypassesArmor();
        return this;
    }

    @Override
    public MekanismDamageSource setDamageAllowedInCreativeMode() {
        super.setDamageAllowedInCreativeMode();
        return this;
    }

    @Override
    public MekanismDamageSource setDamageIsAbsolute() {
        super.setDamageIsAbsolute();
        return this;
    }

    @Override
    public MekanismDamageSource setFireDamage() {
        super.setFireDamage();
        return this;
    }

    @Override
    public MekanismDamageSource setDifficultyScaled() {
        super.setDifficultyScaled();
        return this;
    }

    @Override
    public MekanismDamageSource setMagicDamage() {
        super.setMagicDamage();
        return this;
    }


    public MekanismDamageSource setTrueSource(Entity entity) {
        AttackingEntities = entity;
        return this;
    }

    @Override
    @Nullable
    public Entity getTrueSource() {
        return AttackingEntities;
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase base) {
        if (damageType.equals(SMARTATTACKDAMAGE.getDamageType())) {
            EntityLivingBase name = base.getAttackingEntity();
            String s = "death.attack." + this.damageType;
            String s1 = s + ".player";
            return name != null && I18n.canTranslate(s1) ? new TextComponentTranslation(s1, base.getDisplayName(), name.getDisplayName(), name.getDisplayName()) : new TextComponentTranslation(s, base.getDisplayName());
        } else {
            return super.getDeathMessage(base);
        }
    }
}