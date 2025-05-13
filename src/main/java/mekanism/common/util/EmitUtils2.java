package mekanism.common.util;

import mekanism.common.lib.distribution.DoubleSplitInfo;
import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;

public class EmitUtils2 {

    /**
     * @param <HANDLER>        The handler of our target.
     * @param <TYPE>           The type of the number
     * @param <EXTRA>          Any extra information we may need.
     * @param <TARGET>         The emitter target.
     * @param availableTargets The targets to distribute toSend fairly among.
     * @param splitInfo        Information containing the split.
     * @param toSend           Any extra information such as gas stack or fluid stack.
     * @return The amount that actually got sent.
     */
    private static <HANDLER, TYPE extends Number & Comparable<TYPE>, EXTRA, TARGET extends Target<HANDLER, TYPE, EXTRA>> TYPE sendToAcceptors(
            TARGET availableTargets, SplitInfo<TYPE> splitInfo, EXTRA toSend) {
        if (availableTargets.getHandlerCount() == 0) {
            return splitInfo.getTotalSent();
        }

        //Simulate addition, sending when the requested amount is less than the amountPer
        // splitInfo gets adjusted to account for how much is actually sent
        availableTargets.sendPossible(toSend, splitInfo);

        //Only run this if we changed the amountPer from when we first/last ran things
        while (splitInfo.amountPerChanged) {
            splitInfo.amountPerChanged = false;
            //splitInfo gets adjusted to account for how much is actually sent,
            // and if amountPer got changed again, and we need to rerun this
            availableTargets.shiftNeeded(splitInfo);
        }

        //Evenly distribute the remaining amount we have to give between all targets and handlers
        // splitInfo gets adjusted to account for how much is actually sent
        availableTargets.sendRemainingSplit(splitInfo);
        return splitInfo.getTotalSent();
    }


    /**
     * @param availableTargets The EnergyAcceptorWrapper targets to send energy fairly to.
     * @param amountToSplit    The amount of energy to attempt to send
     * @return The amount that actually got sent
     */
    public static <HANDLER, TARGET extends Target<HANDLER, Double, Double>> Double sendToAcceptors(TARGET availableTargets, Double amountToSplit) {
        return sendToAcceptors(availableTargets, new DoubleSplitInfo(amountToSplit, availableTargets.getHandlerCount()), amountToSplit);
    }


}
