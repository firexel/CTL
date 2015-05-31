package com.seraph.luigi.android

import android.view.View
import com.seraph.luigi.Buffer
import com.seraph.luigi.Consumer
import com.seraph.luigi.map
import com.seraph.luigi.sinkTo

/**
 * Luigi
 * Created by aleksandr.naumov on 31.05.2015.
 */

public abstract class ViewController(private val api: ViewControllerApi) {
    private val layoutResourceIdBuffer = Buffer<Int>()
    public val layoutResourceIdConsumer: Consumer<Int>
        get() = layoutResourceIdBuffer

    init {
        layoutResourceIdBuffer map { resId: Int ->
            { vf: ViewFactory -> vf.inflate(resId) }
        } sinkTo api.viewFactoryConsumer
    }
}

public abstract class ViewControllerApi {
    public  abstract val viewFactoryConsumer: Consumer<(ViewFactory) -> View?>
}

public interface ViewFactory {
    fun inflate(layoutResourceId: Int): View?
}