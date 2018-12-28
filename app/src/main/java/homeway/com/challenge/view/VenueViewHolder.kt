package homeway.com.challenge.view

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import homeway.com.challenge.R
import homeway.com.viewmodel.model.VenueSearchDisplay

class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind( venue: VenueSearchDisplay, venueFavoritedInterface: VenueFavoritedInterface ){
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

        val favoriteVenueView = itemView.findViewById<ImageView>(R.id.venueFavorite)

        setFavoriteView(favoriteVenueView, venue.favorite)

        favoriteVenueView.setOnClickListener {
            venueFavoritedInterface.venueFavoriteAdjusted(venue, adapterPosition)

            setFavoriteView(favoriteVenueView, venue.favorite.not())
        }
    }

    private fun setFavoriteView( favoriteView: ImageView, favorite: Boolean ) =
            favoriteView.setImageResource(if (favorite) R.drawable.filled_star else R.drawable.empty_star )
}