package com.czbix.klog.template

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.template.soy.data.SoyCustomValueConverter

class SoyCustomConverterModule : AbstractModule() {
    override fun configure() {
    }

    @Suppress("unused")
    @Provides
    @Singleton
    private fun provideSoyCustomConverter(): List<SoyCustomValueConverter> {
        return listOf(SoyCustomConverter())
    }
}
