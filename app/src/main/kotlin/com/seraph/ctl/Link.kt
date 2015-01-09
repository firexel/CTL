package com.seraph.ctl

/**
 * CTL
 * Created by seraph on 09.01.2015 3:36.
 */
public abstract class Link<T>(dstCell: Cell<T>, transferTrigger: Trigger<T>) {
    public val dstCell: Cell<T> = dstCell;
    public val transferTrigger: Trigger<T> = transferTrigger;

    public abstract fun transfer()
    public open val isTransferNeeded: Boolean
        get() {
            return transferTrigger.isArmed
        }
}

public class DirectLink<T>(srcCell: Cell<T>, dstCell: Cell<T>, transferTrigger: Trigger<T>) :
        Link<T>(dstCell, transferTrigger) {

    private val srcCell: Cell<T> = srcCell

    override fun transfer() {
        dstCell.value = srcCell.value
    }
}