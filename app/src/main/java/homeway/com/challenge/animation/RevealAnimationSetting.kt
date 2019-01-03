package homeway.com.challenge.animation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * This class will be used in conjunction with FabAnimationUtils. This class represents parameters
 * of a view that we will be doing a circular reveal/collapse against. We also utilize the @Parcelize
 * annotation for Parcelable implementation of this class
 */
@Parcelize
class RevealAnimationSetting(val centerX: Int,
                             val centerY: Int,
                             val width: Int,
                             val height: Int): Parcelable {

    /**
     * Calculate the radius of the given view
     *
     * @return the radius of a given view from the width and height parameters
     */
    fun calculateRadius(): Float {
        return Math.sqrt((width * width + height * height).toDouble()).toFloat()
    }
}