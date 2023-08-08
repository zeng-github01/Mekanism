package mekanism.api.transmitters;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;

public enum TransmissionType {
    ENERGY("EnergyNetwork", "Energy", 0xFF4F9C57, 126, 0),
    FLUID("FluidNetwork", "Fluids", 0xFF3263A9, 144, 0),
    GAS("GasNetwork", "Gases", 0xFFC7AF4A, 162, 0),
    ITEM("InventoryNetwork", "Items", 0xFFA8A8A8, 216, 0),
    HEAT("HeatNetwork", "Heat", 0xFFC88858, 180, 0);

    private String name;
    private String transmission;
    private int color;

    private int buttonx;

    private int buttony;

    TransmissionType(String n, String t, int tabcolor, int buttonx, int buttony) {
        name = n;
        transmission = t;
        color = tabcolor;
        this.buttonx = buttonx;
        this.buttony = buttony;
    }

    public static boolean checkTransmissionType(ITransmitter sideTile, TransmissionType type) {
        return type.checkTransmissionType(sideTile);
    }

    public static boolean checkTransmissionType(TileEntity tile1, TransmissionType type) {
        return checkTransmissionType(tile1, type, null);
    }

    public static boolean checkTransmissionType(TileEntity tile1, TransmissionType type, TileEntity tile2) {
        return type.checkTransmissionType(tile1, tile2);
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public String getTransmission() {
        return transmission;
    }

    public int getButtonx() {
        return buttonx;
    }

    public int getButtony() {
        return buttony;
    }

    public String localize() {
        return I18n.translateToLocal(getTranslationKey());
    }

    public String getTranslationKey() {
        return "transmission." + getTransmission();
    }

    public boolean checkTransmissionType(ITransmitter transmitter) {
        return transmitter.getTransmissionType() == this;
    }

    public boolean checkTransmissionType(TileEntity sideTile, TileEntity currentTile) {
        return sideTile instanceof ITransmitter && ((ITransmitter) sideTile).getTransmissionType() == this;
    }
}