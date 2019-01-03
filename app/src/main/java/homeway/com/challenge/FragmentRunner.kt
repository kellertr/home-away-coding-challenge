import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import homeway.com.challenge.R

/**
 * This class will be responsible for simple fragment interactions within the application
 */
object FragmentRunner{

    /**
     * Activate a new fragment and add it to the backstack
     *
     * @param activity is the activity whose support fragment manager we will be using for this transaction
     * @param fragment is the fragment we are adding to the backstack
     */
    fun activateNewFragment(activity: FragmentActivity?, fragment: Fragment) {
        val fragmentTag = fragment.javaClass.simpleName

        val transaction = activity?.supportFragmentManager?.beginTransaction()?.
                replace(R.id.fragment_container, fragment,
                fragmentTag)?.addToBackStack(fragmentTag)?.commit()
    }
}