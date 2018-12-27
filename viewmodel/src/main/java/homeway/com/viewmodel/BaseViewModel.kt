package homeway.com.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel {
    open class BaseViewModel : ViewModel() {
        protected val disposables = CompositeDisposable()

        // Dispose RxJava Subscribers
        override fun onCleared() = disposables.clear()
    }
}