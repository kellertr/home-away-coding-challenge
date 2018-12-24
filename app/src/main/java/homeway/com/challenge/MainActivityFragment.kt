package homeway.com.challenge

import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import homeway.com.network.FourSquareAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {
    val TAG = MainActivityFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onResume() {
        super.onResume()

        val disposables = CompositeDisposable()

        view?.findViewById<View>(R.id.testListCall)?.setOnClickListener {
            val subscription = FourSquareAPI.create().getPlaces(searchTerm = "coffee").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( {
                        Log.d(TAG, "Success")
                    }, {
                        Log.d(TAG,"Error", it)
                    } )

            disposables.add(subscription)
        }

        view?.findViewById<Button>(R.id.testDetailCall)?.setOnClickListener {
            val subscription = FourSquareAPI.create().getVenueDetails(venueId = "5082d773e4b0354c599f2e77").subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( {
                        Log.d(TAG, "Success")
                    }, {
                        Log.d(TAG, "Error", it)
                    } )

            disposables.add(subscription)
        }
    }
}
