package com.seraph.ctl

import android.content.Context

/**
 * CTL
 * Created by seraph on 17.01.2015 17:57.
 */

public open class ViewConnector {
    public val context: Cell<Context?> = StatefulCell(null)
}

public class ActivityViewConnector : ViewConnector() {
    val lifecyclePhase: Cell<ActivityLifecyclePhase> = StatefulCell(ActivityLifecyclePhase.IDLE)
}
