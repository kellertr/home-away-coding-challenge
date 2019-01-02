package homeway.com.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * The Base View Model will be utilized by all view models to help manage the RX Subscriptions
 * that each view model has.
 */
open class BaseViewModel : ViewModel() {
    protected val disposables = CompositeDisposable()

    // Dispose RxJava Subscribers
    public override fun onCleared() = disposables.clear()
}