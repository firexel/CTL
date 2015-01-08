package com.seraph.ctl

private class TestCell : Cell<String>() {
    var storedString:String = "";

    override fun write(newValue: String) {
        storedString = newValue
    }

    override fun read(): String = storedString
}