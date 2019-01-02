import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import homeway.com.challenge.R

object FragmentRunner{

    /**
     * Activate a new fragment and add it to the backstack
     *
     * @param fragment is the fragment we are adding to the backstack
     */
    fun activateNewFragment(activity: FragmentActivity?, fragment: Fragment) {
        val fragmentTag = fragment.javaClass.simpleName

        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment,
                        fragmentTag)?.addToBackStack(fragmentTag)?.commit()

    }
}