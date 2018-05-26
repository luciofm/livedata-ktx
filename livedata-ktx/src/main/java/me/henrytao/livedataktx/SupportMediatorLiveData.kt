/*
 * Copyright (c) 2018 Henry Tao <hi@henrytao.me>.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.henrytao.livedataktx

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicInteger

open class SupportMediatorLiveData<T>(internal val isSingle: Boolean = false, private val versionProvider: (() -> Int)? = null) : MediatorLiveData<T>() {

    private var _version = AtomicInteger()
    internal val version: Int get() = versionProvider?.let { it() } ?: _version.get()


    @Deprecated("Use observe extension")
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val observerVersion = version
        super.observe(owner, Observer {
            if (!isSingle || observerVersion < version) {
                observer.onChanged(it)
            }
        })
    }

    @Deprecated("Use observe extension without LifecycleOwner")
    override fun observeForever(observer: Observer<in T>) {
        super.observeForever(observer)
    }

    override fun setValue(value: T?) {
        _version.incrementAndGet()
        super.setValue(value)
    }

    override fun postValue(value: T?) {
        _version.incrementAndGet()
        super.postValue(value)
    }
}