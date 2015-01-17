package com.seraph.ctl

import junit.framework.TestCase
import android.content.Context
import android.test.ActivityInstrumentationTestCase2
import kotlin.test.assertTrue
import android.test.UiThreadTest
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import android.os.Bundle
import android.widget.LinearLayout
import com.seraph.ctl.ViewConnectorActivityTest.ViewConnectorTestActivity

/**
 * CTL
 * Created by seraph on 17.01.2015 17:55.
 */

public class ViewConnectorActivityTest :
        ActivityInstrumentationTestCase2<ViewConnectorTestActivity>(javaClass()) {

    private var testActivity: ViewConnectorTestActivity? = null

    override fun setUp() {
        super.setUp()
        testActivity = getActivity()
    }

    UiThreadTest
    public fun test_viewConnectorActivity_managesLifecyclePhaseCell() {
        var connector = testActivity!!.connector
        val instrumentation = getInstrumentation()
        assertEquals(ActivityLifecyclePhase.RESUMED, connector.lifecyclePhase.value)
        instrumentation.callActivityOnPause(testActivity)
        assertEquals(ActivityLifecyclePhase.STARTED, connector.lifecyclePhase.value)
        instrumentation.callActivityOnStop(testActivity)
        assertEquals(ActivityLifecyclePhase.CREATED, connector.lifecyclePhase.value)
        instrumentation.callActivityOnStart(testActivity)
        assertEquals(ActivityLifecyclePhase.STARTED, connector.lifecyclePhase.value)
        instrumentation.callActivityOnResume(testActivity)
        assertEquals(ActivityLifecyclePhase.RESUMED, connector.lifecyclePhase.value)
    }

    UiThreadTest
    public fun test_viewConnectorActivity_managesContextCell() {
        assertTrue(testActivity!!.connector.context.value == testActivity)
    }

    public class ViewConnectorTestActivity : ViewConnectorActivity() {
        override fun createViewConnector(): ActivityViewConnector {
            return ActivityViewConnector()
        }
    }
}
