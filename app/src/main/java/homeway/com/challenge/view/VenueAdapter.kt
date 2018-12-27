package homeway.com.challenge.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import homeway.com.challenge.R
import homeway.com.model.venue.Venue

class VenueAdapter( var data: List<Venue> = listOf(), val view: RecyclerView ) : RecyclerView.Adapter<VenueViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = VenueViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.venue_row,
            parent, false) )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int)
            = holder.bind(data.get(position))
}