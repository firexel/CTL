package com.seraph.ctl

import android.test.ActivityInstrumentationTestCase2
import kotlin.test.assertTrue
import android.test.UiThreadTest
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import com.seraph.ctl.ViewConnectorActivityTest.ViewConnectorTestActivity
import android.view.View
import android.widget.LinearLayout

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

    UiThreadTest
    public fun test_viewConnectorActivity_shouldGetContentViewFromViewConnector() {
        assertNotNull(getActivity()?.view)
    }

    public class ViewConnectorTestActivity : ViewConnectorActivity() {
        internal var view: View? = null

        override fun createViewConnector(): ActivityViewConnector {
            return TestActivityViewConnector()
        }

        override fun setContentView(view: View?) {
            super.setContentView(view)
            this.view = view
        }
    }

    internal class TestActivityViewConnector : ActivityViewConnector() {
        override fun onBuildScope(scope: Scope) {
            scope.link(viewFactory).with(context) { context ->
                { v ->
                    v.setContentView(LinearLayout(context))
                }
            }
        }
    }
}
