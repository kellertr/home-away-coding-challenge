package homeway.com.challenge.view

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import homeway.com.challenge.R
import homeway.com.model.venue.Venue

class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind( venue: Venue ){
        itemView.findViewById<TextView>(R.id.venueName).text = venue.name
        itemView.findViewById<TextView>(R.id.venueCategory).text = venue.categories[0].name //TODO View Model model
        itemView.findViewById<TextView>(R.id.venueDistance).text =
                itemView.context.getString(R.string.distance_to_seattle, venue.location.distance) //TODO View Model model

        //distance_to_seattle

    }
}