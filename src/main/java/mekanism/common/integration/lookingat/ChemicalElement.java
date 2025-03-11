package mekanism.common.integration.lookingat;

import mekanism.api.gas.GasStack;
import mekanism.api.math.MathUtils;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.util.LangUtils;
import mekanism.common.util.TextComponentGroup;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.ITextComponent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ChemicalElement extends LookingAtElement {

    @Nonnull
    protected final GasStack stored;
    protected final int capacity;

    protected ChemicalElement(@Nonnull GasStack stored, int capacity) {
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

    @Nullable
    @Override
    public TextureAtlasSprite getIcon() {
        return stored == null ? null : stored.getGas().getSprite();
    }

    @Override
    public ITextComponent getText() {
        if (stored != null) {
            long amount = stored.amount;
            if (amount == Integer.MAX_VALUE) {
                return new TextComponentGroup().translation(LangUtils.localizeGasStack(stored) + ": " + LangUtils.localize("gui.infinite"));
            }
            return new TextComponentGroup().translation(LangUtils.localizeGasStack(stored) + ": " + stored.amount + " mB");
        }
        return new TextComponentGroup().translation(LangUtils.localize("gui.noGas"));
    }

    @Override
    protected boolean applyRenderColor() {
        MekanismRenderer.color(stored);
        return true;
    }
}
