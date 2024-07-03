package mcjty.lib.api.infusable;

import mcjty.lib.base.GeneralConfig;

/**
 * Implement this capability based interface in your TE if you want
 * your block to beinfusable with the RFTools Machine Infuser. As
 * soon as this interface is implemented then inserting your block
 * in the Machine Infuser will increase the 'infused' integer NBT
 * tag on the ItemBlock for your machine. This is a number that goes
 * from 0 (not infused) * to 256 (maximum infused).
 */
public interface IInfusable {

    int getInfused();

    void setInfused(int i);

    default float getInfusedFactor() {
        return ((float) getInfused()) / GeneralConfig.maxInfuse.get();
    }

}
