package homeway.com.challenge.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import homeway.com.challenge.R
import homeway.com.viewmodel.model.DisplayVenue

/**
 * The VenueViewHolder is the ViewHolder class for a DisplayVenue item
 */
class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    /**
     * This method will bind a venue to the given view holder
     *
     * @param venue is the venue we are binding to this view holder
     * @param venueFavoritedInterface is the interface that we will invoke when a user taps on
     *                                favoriting a venue
     */
    fun bind(venue: DisplayVenue, venueFavoritedInterface: VenueFavoritedInterface ){
        itemView.findViewById<TextView>(R.id.venueName).text = venue.name

        val categoryText = itemView.findViewById<TextView>(R.id.venueCategory)
        categoryText.text = ""
        categoryText.visibility = View.GONE

        venue.category?.let{
            categoryText.text = it
            categoryText.visibility = View.VISIBLE
        }

        val distanceText = itemView.findViewById<TextView>(R.id.venueDistance)

        distanceText.text = ""
        distanceText.visibility = View.GONE

        venue.distance?.let {
            distanceText.text = itemView.context.getString(R.string.distance_to_seattle, it)
            distanceText.visibility = View.VISIBLE
        }

        val categoryIconImageView = itemView.findViewById<ImageView>(R.id.venueImage)

        venue.categoryIconUrl?.let { imageUrl ->
            categoryIconImageView.visibility = View.VISIBLE
            Picasso.get().load( imageUrl ).into(categoryIconImageView)
        } ?: run { categoryIconImageView.visibility = View.GONE }

        val favoriteVenueView = itemView.findViewById<ImageView>(R.id.venueFavorite)

        setFavoriteView(favoriteVenueView, venue.favorite)

        favoriteVenueView.setOnClickListener {
            venueFavoritedInterface.venueFavoriteAdjusted(venue, adapterPosition)

            setFavoriteView(favoriteVenueView, venue.favorite.not())
        }
    }

    /**
     * setFavoriteView is a convenience method to update the favorited image view
     *
     * @param favoriteView is the corresponding image view to be updated with a filled in star or empty star
     * @param favorite is whether or not we should display this venue as a favorite
     */
    private fun setFavoriteView( favoriteView: ImageView, favorite: Boolean ) {

        favoriteView.apply {
            if( favorite ){
                setImageResource(R.drawable.filled_star)
                contentDescription = favoriteView.context.getString(R.string.content_description_favorite)
            } else {
                setImageResource(R.drawable.empty_star)
                contentDescription = favoriteView.context.getString(R.string.content_description_make_favorite)
            }
        }
    }

}