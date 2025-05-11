package mekanism.common.network;

import io.netty.buffer.ByteBuf;
import mekanism.api.Coord4D;
import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.network.PacketOpenGui.OpenGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mekanism.common.network.PacketSimpleGui.handlers;

public class PacketOpenGui implements IMessageHandler<OpenGui, IMessage> {


    @Override
    public IMessage onMessage(OpenGui message, MessageContext context) {
        EntityPlayer player = PacketHandler.getPlayer(context);
        PacketHandler.handlePacket(() -> {
            if (!player.world.isRemote) {
                OpenGui.openServerGui(message.guiHandler, message.guiId, (EntityPlayerMP) player, player.world, message.coord4D);
            } else {
                FMLCommonHandler.instance().showGuiScreen(OpenGui.getGui(message.guiHandler, message.guiId, player, player.world, message.coord4D));
                player.openContainer.windowId = message.windowId;
            }
        }, player);
        return null;
    }

    public static class OpenGui implements IMessage {

        public Coord4D coord4D;

        public int guiHandler;

        public int guiId;

        public int windowId;


        public OpenGui() {
        }

        public OpenGui(Coord4D coord, int handler, int gui) {
            coord4D = coord;
            guiHandler = handler;
            guiId = gui;
        }


        public OpenGui(Coord4D coord, int handler, int gui, int id) {
            this(coord, handler, gui);
            windowId = id;
        }


        public static void openServerGui(int handler, int id, EntityPlayerMP playerMP, World world, Coord4D obj) {
            playerMP.closeContainer();
            playerMP.getNextWindowId();
            int window = playerMP.currentWindowId;
            Mekanism.packetHandler.sendTo(new OpenGui(obj, handler, id, window), playerMP);
            playerMP.openContainer = handlers.get(handler).getServerGui(id, playerMP, world, playerMP.getPosition());
            playerMP.openContainer.windowId = window;
            playerMP.openContainer.addListener(playerMP);

        }

        @SideOnly(Side.CLIENT)
        public static GuiScreen getGui(int handler, int id, EntityPlayer player, World world, Coord4D obj) {
            return (GuiScreen) handlers.get(handler).getClientGui(id, player, world, obj.getPos());
        }

        @Override
        public void fromBytes(ByteBuf dataStream) {
            coord4D = Coord4D.read(dataStream);
            guiHandler = dataStream.readInt();
            guiId = dataStream.readInt();
            windowId = dataStream.readInt();
        }

        @Override
        public void toBytes(ByteBuf dataStream) {
            coord4D.write(dataStream);
            dataStream.writeInt(guiHandler);
            dataStream.writeInt(guiId);
            dataStream.writeInt(windowId);
        }
    }
}
