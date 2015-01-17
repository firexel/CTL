package com.seraph.ctl

import android.app.Activity
import kotlin.properties.Delegates
import android.os.Bundle

/**
 * CTL
 * Created by seraph on 18.01.2015 0:25.
 */
public abstract class ViewConnectorActivity : Activity() {
    public val connector: ActivityViewConnector by Delegates.lazy { createViewConnector() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connector.context.value = this
        connector.lifecyclePhase.value = ActivityLifecyclePhase.CREATED
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

public enum class ActivityLifecyclePhase { IDLE; CREATED; STARTED; RESUMED; }
