package homeway.com.challenge.animation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RevealAnimationSetting(val centerX: Int,
                             val centerY: Int,
                             val width: Int,
                             val height: Int): Parcelable