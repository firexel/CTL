package com.seraph.luigi.android

import android.view.View
import com.seraph.luigi.Buffer
import com.seraph.luigi.Consumer
import com.seraph.luigi.NoDataException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.fail

/**
 * Luigi
 * Created by aleksandr.naumov on 31.05.2015.
 */

public open class TestViewControllerApi : ViewControllerApi() {
    private val viewFactoryBuffer = Buffer<(ViewFactory) -> View?>();
    override val viewFactoryConsumer: Consumer<(ViewFactory) -> View?>
        get() = viewFactoryBuffer

    fun assertRootViewIs(layoutResId: Int) {
        val testFactory = TestViewFactory()
        var createFunction: ((ViewFactory) -> View?)? = null;
        try {
            createFunction = viewFactoryBuffer.produce()
        } catch(ex: NoDataException) {
            fail("No view factory set");
        }
        assertNotNull(testFactory, "Null view factory set")
        createFunction!!.invoke(testFactory)
        assertNotNull(testFactory.lastViewInflated, "No view inflating has occurred")
        assertEquals(layoutResId, testFactory.lastViewInflated,
                "View with id ${testFactory.lastViewInflated} has been inflated instead of ${layoutResId}"
        )
    }

    private class TestViewFactory : ViewFactory {
        public var lastViewInflated: Int? = null

        override fun inflate(layoutResourceId: Int): View? {
            lastViewInflated = layoutResourceId
            return null
        }
    }
}