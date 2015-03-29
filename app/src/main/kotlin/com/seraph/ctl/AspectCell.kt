package com.seraph.ctl

/**
 * CTL
 * Created by seraph on 24.01.2015 0:57.
 */

public abstract class AspectCell<ET, AT>(default:AT, cell:Cell<ET>) : StatefulCell<AT>(default), TriggerListener<ET> {
    private val entityCell = cell

    init {
        writeToEntity(cell.value, value)
        cell.trigger.addListener(this)
    }

    protected abstract fun writeToEntity(entity:ET, aspect:AT)

    override fun write(newValue: AT) {
        super<StatefulCell>.write(newValue)
        if(trigger.isArmed) {
            writeToEntity(entityCell.value, newValue)
        }
    }

    override fun onTriggerArmed(trigger: Trigger<ET>, oldValue: ET, newValue: ET) {
        writeToEntity(newValue, value)
    }
}