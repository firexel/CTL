package com.seraph.ctl

import com.seraph.ctl.executor.Executor
import com.seraph.ctl.executor.ImmediateExecutor
import java.util.ArrayList

/**
 * CTL
 * Created by seraph on 09.01.2015 2:51.
 */
public class Scope(executor: Executor = ImmediateExecutor()) : TriggerListener<Any?> {
    private val executor: Executor = executor;
    private val pendingBuildings: MutableCollection<() -> Link<Any?>> = ArrayList()
    private val links: MutableCollection<Link<Any?>> = ArrayList()

    fun <T> link(dstCell: Cell<T>): LinkBuilder<T> {
        val builder = LinkBuilder(dstCell)
        pendingBuildings.add { builder.build() as Link<Any?> }
        return builder
    }

    public fun build() {
        pendingBuildings.forEach {
            val link = it()
            link.transferTrigger.addListener(this)
            links.add(link)
        }
        pendingBuildings.clear()
        updateAsync()
    }

    override fun onTriggerArmed(trigger: Trigger<Any?>, oldValue: Any?, newValue: Any?) {
        updateAsync()
    }

    private fun updateAsync() = executor.execute { update() }
    private fun update() {
        links.forEach {(link) ->
            if (link.isTransferNeeded) link.transfer()
            link.transferTrigger.disarm()
        }
    }

    public class LinkBuilder<T>(dstCell: Cell<T>) {
        private val dstCell: Cell<T> = dstCell;
        private var directSrcCell: Cell<T>? = null

        fun with(directConnectionCell: Cell<T>) {
            directSrcCell = directConnectionCell;
        }

        fun build(): Link<T> {
            if (directSrcCell != null) {
                return DirectLink(directSrcCell!!, dstCell, directSrcCell!!.trigger)
            } else {
                throw BuildException("Use with() method to set a link source")
            }
        }
    }
}

public class BuildException(s: String) : RuntimeException(s) {
}
