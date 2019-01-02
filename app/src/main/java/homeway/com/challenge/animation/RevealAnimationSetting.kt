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

    fun calculateRadius(): Float {
        return (centerX * centerX + centerY * centerY).toFloat()
    }
}