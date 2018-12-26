package homeway.com.challenge

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : androidx.fragment.app.Fragment() {
    val TAG = MainActivityFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onResume() {
        super.onResume()

        view?.findViewById<View>(R.id.testListCall)?.setOnClickListener {
        }

        view?.findViewById<Button>(R.id.testDetailCall)?.setOnClickListener {
        }
    }
}
