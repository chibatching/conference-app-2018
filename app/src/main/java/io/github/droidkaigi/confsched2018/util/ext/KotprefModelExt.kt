package io.github.droidkaigi.confsched2018.util.ext

import android.content.SharedPreferences
import android.support.annotation.CheckResult
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.pref.PreferenceKey
import io.github.droidkaigi.confsched2018.presentation.common.pref.Prefs
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

@CheckResult fun <T> KotprefModel.asFlowable(property: KProperty0<T>): Flowable<T> {
    return Flowable.create<T>({ emitter ->
        property.isAccessible = true
        val listenKey = (property.getDelegate() as? PreferenceKey)?.key ?: property.name
        property.isAccessible = false
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changeKey ->
            if (listenKey == changeKey) {
                emitter.onNext(property.get())
            }
        }
        emitter.setCancellable {
            Prefs.preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
        Prefs.preferences.registerOnSharedPreferenceChangeListener(listener)
    }, BackpressureStrategy.LATEST)
}
