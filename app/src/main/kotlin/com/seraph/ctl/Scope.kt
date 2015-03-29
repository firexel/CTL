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

    synchronized fun <T> link(dstCell: Cell<T>): LinkBuilder<T> {
        val builder = LinkBuilder(dstCell)
        pendingBuildings.add { builder.build() as Link<Any?> }
        return builder
    }

    synchronized fun <T> unlink(cell: Cell<T>) {
        val newList = ArrayList(links.filter { !it.dstCell.identityEquals(cell) })
        links.clear()
        links.addAll(newList)
    }

    synchronized public fun build() {
        buildLinks()
        sortLinks()
        update()
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
            public fun pointTo(node: Node) {
                nexts.add(node)
                node.prevs.add(this)
            }
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
        components.forEach {
            nodesMap[it] = Node(it)
        }
        components map { nodesMap[it] } forEach { node ->
            node.payload.affectedComponents map { nodesMap[it] } forEach { node pointTo it }
            node.payload.precursorComponents map { nodesMap[it] } forEach { it pointTo node }
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

    synchronized private fun doAsync(func: () -> Unit) {
        executor.cancelAll()
        executor.execute(func)
    }

    synchronized public fun update() {
        links.forEach { link -> if (link.isTransferNeeded) link.transfer() }
        links.forEach { link -> link.transferTrigger.disarm() }
    }

    public class LinkBuilder<T>(dstCell: Cell<T>) {
        private val dstCell: Cell<T> = dstCell;
        private var link: Link<T>? = null;

        fun with(directConnectionCell: Cell<T>) {
            link = DirectLink(directConnectionCell, dstCell, directConnectionCell.trigger)
        }

        fun <P1> with(p1: Cell<P1>, rule: (P1) -> T) {
            class RuleLink1<T, P1>(dstCell: Cell<T>, p1: Cell<P1>) :
                    RuleLink<T>(dstCell, OrTrigger(p1.trigger)) {
                override fun transfer() {
                    dstCell.value = rule(p1.value) as T
                }
            }
            link = RuleLink1(dstCell, p1)
        }

        fun <P1, P2> with(p1: Cell<P1>, p2: Cell<P2>, rule: (P1, P2) -> T) {
            class RuleLink2<T, P1, P2>(dstCell: Cell<T>, p1: Cell<P1>, p2: Cell<P2>) :
                    RuleLink<T>(dstCell, OrTrigger(p1.trigger, p2.trigger)) {
                override fun transfer() {
                    dstCell.value = rule(p1.value, p2.value) as T
                }
            }
            link = RuleLink2(dstCell, p1, p2)
        }

        fun <P1, P2, P3> with(p1: Cell<P1>, p2: Cell<P2>, p3: Cell<P3>, rule: (P1, P2, P3) -> T) {
            class RuleLink3<T, P1, P2, P3>(dstCell: Cell<T>, p1: Cell<P1>, p2: Cell<P2>, p3: Cell<P3>) :
                    RuleLink<T>(dstCell, OrTrigger(p1.trigger, p2.trigger, p3.trigger)) {
                override fun transfer() {
                    dstCell.value = rule(p1.value, p2.value, p3.value) as T
                }
            }
            link = RuleLink3(dstCell, p1, p2, p3)
        }

        fun <P1, P2, P3, P4> with(p1: Cell<P1>, p2: Cell<P2>, p3: Cell<P3>, p4: Cell<P4>, rule: (P1, P2, P3, P4) -> T) {
            class RuleLink4<T, P1, P2, P3, P4>(dstCell: Cell<T>, p1: Cell<P1>, p2: Cell<P2>, p3: Cell<P3>, p4: Cell<P4>) :
                    RuleLink<T>(dstCell, OrTrigger(p1.trigger, p2.trigger, p3.trigger, p4.trigger)) {
                override fun transfer() {
                    dstCell.value = rule(p1.value, p2.value, p3.value, p4.value) as T
                }
            }
            link = RuleLink4(dstCell, p1, p2, p3, p4)
        }

        fun <P1, P2, P3, P4, P5> with(p1: Cell<P1>, p2: Cell<P2>, p3: Cell<P3>, p4: Cell<P4>, p5: Cell<P5>, rule: (P1, P2, P3, P4, P5) -> T) {
            class RuleLink5<T, P1, P2, P3, P4, P5>(dstCell: Cell<T>, p1: Cell<P1>, p2: Cell<P2>, p3: Cell<P3>, p4: Cell<P4>, p5: Cell<P5>) :
                    RuleLink<T>(dstCell, OrTrigger(p1.trigger, p2.trigger, p3.trigger, p4.trigger, p5.trigger)) {
                override fun transfer() {
                    dstCell.value = rule(p1.value, p2.value, p3.value, p4.value, p5.value) as T
                }
            }
            link = RuleLink5(dstCell, p1, p2, p3, p4, p5)
        }

        fun build(): Link<T> {
            return link ?: throw BuildException("Use with() method to set a link source")
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