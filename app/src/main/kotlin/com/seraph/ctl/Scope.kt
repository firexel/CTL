package com.seraph.ctl

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

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

    synchronized public fun build() {
        buildLinks()
        sortLinks()
        doAsync { update() }
    }

    private fun buildLinks() {
        pendingBuildings.forEach {
            val link = it()
            link.transferTrigger.addListener(this)
            links.add(link)
        }
        pendingBuildings.clear()
    }

    private fun sortLinks() {
        class Node(payload: ScopeComponent) {
            public val payload: ScopeComponent = payload
            public val prevs: MutableCollection<Node> = HashSet()
            public val nexts: MutableCollection<Node> = HashSet()
            override fun toString(): String = payload.toString()
        }

        val components = HashSet<ScopeComponent>()
        fun collectComponents(component: ScopeComponent) {
            if (!components.contains(component)) {
                components.add(component)
                component.affectedComponents.forEach { collectComponents(it) }
                component.precursorComponents.forEach { collectComponents(it) }
            }
        }
        links.forEach { collectComponents(it) }

        val nodesMap = HashMap<ScopeComponent, Node>()
        components.forEach { nodesMap.put(it, Node(it)) }
        components.forEach {
            val node = nodesMap.get(it)
            node.nexts.addAll(it.affectedComponents.map { nodesMap.get(it) })
            node.nexts.forEach { it.prevs.add(node) }
            node.prevs.addAll(it.precursorComponents.map { nodesMap.get(it) })
            node.prevs.forEach { it.nexts.add(node) }
        }

        val orderedNodes = ArrayList<Node>()
        val unprocessedNodes = ArrayList(nodesMap.values())
        while (unprocessedNodes.size() > 0) {
            val leafNodes = ArrayList(unprocessedNodes.filter { it.nexts.isEmpty() })
            if (leafNodes.isEmpty()) {
                throw BuildException("There are cycle between $unprocessedNodes")
            }
            leafNodes.forEach { leaf -> leaf.prevs.forEach { it.nexts.remove(leaf) } }
            orderedNodes.addAll(0, leafNodes)
            unprocessedNodes.removeAll(leafNodes)
        }

        links.clear()
        links.addAll(orderedNodes.map { it.payload }.filterIsInstance())
    }

    override fun onTriggerArmed(trigger: Trigger<Any?>, oldValue: Any?, newValue: Any?) {
        doAsync { update() }
    }

    private fun doAsync(func: () -> Unit) = executor.execute(func)

    synchronized public fun update() {
        links.forEach {(link) ->
            if (link.isTransferNeeded) {
                link.transfer()
            }
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

public trait ScopeComponent {
    public val affectedComponents: Collection<ScopeComponent>
        get() {
            return emptyList()
        }

    public val precursorComponents: Collection<ScopeComponent>
        get() {
            return emptyList()
        }
}