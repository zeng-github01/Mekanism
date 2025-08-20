package mekanism.client;

import mekanism.common.CardboardArmorHandler;
import mekanism.common.Mekanism;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Mekanism.MODID)
public class CardboardArmorHandlerClient {

    @SubscribeEvent
    public static void playerRendersAsBoxWhenSneaking(RenderPlayerEvent.Pre evt) {
        setModelVisibility(evt.getEntityPlayer(), evt.getRenderer(), false);
    }

    @SubscribeEvent
    public void renderEntityPost(RenderPlayerEvent.Post evt) {
        setModelVisibility(evt.getEntityPlayer(), evt.getRenderer(), true);
    }

    private static void setModelVisibility(EntityPlayer entity, Render<?> entityModel, boolean showModel) {
        if (entityModel instanceof RenderPlayer renderPlayer) {
            if (CardboardArmorHandler.testForStealth(entity)) {
                renderPlayer.getMainModel().bipedLeftArmwear.showModel = showModel;
                renderPlayer.getMainModel().bipedRightArmwear.showModel = showModel;
                renderPlayer.getMainModel().bipedLeftLegwear.showModel = showModel;
                renderPlayer.getMainModel().bipedRightLegwear.showModel = showModel;
                renderPlayer.getMainModel().bipedBodyWear.showModel = showModel;

                renderPlayer.getMainModel().bipedLeftArmwear.isHidden = !showModel;
                renderPlayer.getMainModel().bipedRightArmwear.isHidden = !showModel;
                renderPlayer.getMainModel().bipedLeftLegwear.isHidden = !showModel;
                renderPlayer.getMainModel().bipedRightLegwear.isHidden = !showModel;
                renderPlayer.getMainModel().bipedBodyWear.isHidden = !showModel;

                renderPlayer.getMainModel().bipedHead.showModel = showModel;
                renderPlayer.getMainModel().bipedHeadwear.showModel = showModel;
                renderPlayer.getMainModel().bipedBody.showModel = showModel;
                renderPlayer.getMainModel().bipedRightArm.showModel = showModel;
                renderPlayer.getMainModel().bipedLeftArm.showModel = showModel;
                renderPlayer.getMainModel().bipedRightLeg.showModel = showModel;
                renderPlayer.getMainModel().bipedLeftLeg.showModel = showModel;

                renderPlayer.getMainModel().bipedHead.isHidden = !showModel;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = !showModel;
                renderPlayer.getMainModel().bipedBody.isHidden = !showModel;
                renderPlayer.getMainModel().bipedRightArm.isHidden = !showModel;
                renderPlayer.getMainModel().bipedLeftArm.isHidden = !showModel;
                renderPlayer.getMainModel().bipedRightLeg.isHidden = !showModel;
                renderPlayer.getMainModel().bipedLeftLeg.isHidden = !showModel;
            }
        }
    }
}
