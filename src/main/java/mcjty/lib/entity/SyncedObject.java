package mcjty.lib.entity;

public interface SyncedObject {

    void setInvalid();

    boolean isClientValueUptodate();

    void updateClientValue();
}
