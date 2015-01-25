package com.seraph.ctl

import android.content.Context
import android.view.View
import android.os.Looper

/**
 * CTL
 * Created by seraph on 17.01.2015 17:57.
 */

public abstract class ViewConnector {
    private val scope = Scope(HandlerExecutor(Looper.getMainLooper()))
    protected val viewFactory: Cell<(ViewFactoryVisitor) -> Unit> = StatefulCell(::defaultViewFactory)
    public val context: Cell<Context?> = StatefulCell(null)

    protected abstract fun onBuildScope(scope:Scope)
    internal final fun buildScope() {
        onBuildScope(scope)
        scope.build()
    }
}

private fun defaultViewFactory(visitor: ViewFactoryVisitor):Unit {
    throw ViewConnectorException("No factory provided for view")
}

public class ViewConnectorException(message:String) : RuntimeException(message)

public trait ViewFactoryVisitor {
    public fun setContentView(id:Int)
    public fun setContentView(v: View)
}