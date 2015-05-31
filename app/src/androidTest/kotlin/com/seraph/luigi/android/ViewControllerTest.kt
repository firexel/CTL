package com.seraph.luigi.android

import com.seraph.luigi.bindTo
import junit.framework.TestCase

/**
 * Luigi
 * Created by aleksandr.naumov on 31.05.2015.
 */

public class ViewControllerTest : TestCase() {
    private var testApi:TestViewControllerApi = TestViewControllerApi();

    override fun setUp() {
        super.setUp()
        testApi = TestViewControllerApi()
    }

    public fun testNew() {
        testViewController {
            777 bindTo layoutResourceIdConsumer
        }
        testApi.assertRootViewIs(777)
    }

    private fun testViewController(body:ViewController.() -> Unit) {
        class MyViewController : ViewController(testApi) {
            init {
                body()
            }
        }
        MyViewController()
    }
}
