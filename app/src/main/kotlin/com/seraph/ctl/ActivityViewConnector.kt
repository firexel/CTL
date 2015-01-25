package com.seraph.ctl

import android.app.Activity
import android.os.Bundle
import kotlin.properties.Delegates
import android.view.View

public abstract class ActivityViewConnector() : ViewConnector() {
    public val lifecyclePhase: Cell<ActivityLifecyclePhase> = StatefulCell(ActivityLifecyclePhase.IDLE)

    internal fun createView(activity:Activity) {
        viewFactory.value(ActivityViewFactoryVisitor(activity))
    }
}

private class ActivityViewFactoryVisitor(activity:Activity) : ViewFactoryVisitor {
    private val activity:Activity = activity

    override fun setContentView(id: Int) {
        activity.setContentView(id)
    }

    override fun setContentView(v: View) {
        activity.setContentView(v)
    }
}

public enum class ActivityLifecyclePhase { IDLE; CREATED; STARTED; RESUMED; }

public abstract class ViewConnectorActivity : Activity() {
    public val connector: ActivityViewConnector by Delegates.lazy { createViewConnector() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connector.context.value = this
        connector.lifecyclePhase.value = ActivityLifecyclePhase.CREATED
        connector.buildScope()
        connector.createView(this)
    }

    override fun onStart() {
        super.onStart()
        connector.lifecyclePhase.value = ActivityLifecyclePhase.STARTED
    }

    override fun onResume() {
        super.onResume()
        connector.lifecyclePhase.value = ActivityLifecyclePhase.RESUMED
    }

    override fun onPause() {
        super.onPause()
        connector.lifecyclePhase.value = ActivityLifecyclePhase.STARTED
    }

    override fun onStop() {
        super.onStop()
        connector.lifecyclePhase.value = ActivityLifecyclePhase.CREATED
    }

    override fun onDestroy() {
        super.onDestroy()
        connector.lifecyclePhase.value = ActivityLifecyclePhase.IDLE
        connector.context.value = null
    }

    protected abstract fun createViewConnector(): ActivityViewConnector
}