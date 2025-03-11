package mekanism.common.integration.lookingat;

import mekanism.api.math.MathUtils;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.LangUtils;
import mekanism.common.util.TextComponentGroup;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class FluidElement extends LookingAtElement {

    @Nonnull
    protected final FluidStack stored;
    protected final int capacity;

    public FluidElement(@Nonnull FluidStack stored, int capacity) {
        super(0xFF000000, 0xFFFFFF);
        this.stored = stored;
        this.capacity = capacity;
    }

    @Override
    public int getScaledLevel(int level) {
        if (capacity == 0 || stored.amount == Integer.MAX_VALUE) {
            return level;
        }
        return MathUtils.clampToInt(level * (double) stored.amount / capacity);
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return stored == null ? null : MekanismRenderer.getFluidTexture(stored, MekanismRenderer.FluidType.STILL);
    }


    @Override
    public ITextComponent getText() {
        if (stored != null) {
            int amount = stored.amount;
            if (amount == Integer.MAX_VALUE) {
                return new TextComponentGroup().translation(LangUtils.localizeFluidStack(stored) + ": " + LangUtils.localize("gui.infinite"));
            }
            return new TextComponentGroup().translation(LangUtils.localizeFluidStack(stored) + ": " + stored.amount + " mB");
        }
        return new TextComponentGroup().translation(LangUtils.localize("gui.noFluid"));
    }

    @Override
    protected boolean applyRenderColor() {
        MekanismRenderer.color(stored);
        return true;
    }
}
