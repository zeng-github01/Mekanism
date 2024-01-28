package mekanism.api;

@FunctionalInterface
public interface IContentsListener {

    /**
     * Called when the contents this listener is monitoring gets changed.
     */
    void onContentsChanged();
}
