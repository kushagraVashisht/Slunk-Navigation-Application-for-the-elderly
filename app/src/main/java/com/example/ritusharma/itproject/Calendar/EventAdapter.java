package com.example.ritusharma.itproject.Calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ritusharma.itproject.R;

import java.util.List;

/**
 * The EventAdapter class manages the recycler view for event cards.
 *
 * Each Event displayed in the recycler view is wrapped in a card view object
 * */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    /* Class attributes namely
    * listEvent : list of events to be displayed
    * Context: activity context
    * */
    private List<Event> listEvent;
    private Context context;

    /**
     * Constructor
     *
     * @param listEvent : the list of event objects
     * @param c : the app context
     * */
    public EventAdapter(List<Event> listEvent, Context c) {
        this.listEvent = listEvent;
        this.context = c;
    }

    /*********************************************************************************************/

    @NonNull
    @Override
    /**
     * The oncreatemethod instantiates the recyclerview by inflating the calendar_layout.java class
     *
     * @param parent - the app context
     * @param viewType
     *
     * */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_event_item, parent, false);
        return new ViewHolder(v);
    }

    /*********************************************************************************************/

    @Override
    /**
     * This method binds the 2 components: the cardview to display each event and the list of events
     *
     * @param holder - the card view to show the event details
     * @param position - the index of the event to be displayed
     *
     * */
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // get the current event
        Event e = listEvent.get(position);

        // extract details to generate a preview string to store in a cardview
        String s = e.getStartDate() + "\t" + e.getStartTime() + "\n" + e.getEventName();
        s += "\n" + e.getEventDesc();

        // appedn string to the cardview
        holder.event.setText(s);

        holder.itemView.setOnClickListener((View v) -> {
            // Get the current state of the item
            boolean expanded = e.isExpanded();
            // Change the state
            e.setExpanded(!expanded);
            // Notify the adapter that item has changed
            notifyItemChanged(position);
        });

    }

    /*********************************************************************************************/

    @Override
    /**
     * Methods to obtain the number of events in the list of events
     * */
    public int getItemCount() {
        try {
            return listEvent.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /*********************************************************************************************/

    /**
     * The viewHolder class implements the card view to store events
     * */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /* Class attributes*/
        public TextView event;
        private View subitem;

        /**
         * Constructor
         *
         * @param itemView - UI components
         * */
        public ViewHolder(View itemView) {
            super(itemView);
            event = itemView.findViewById(R.id.eventCard);
            subitem = itemView.findViewById(R.id.subitem);
        }

        /*********************************************************************************************/

        /**
         * THis method binds the event to the card view
         *
         * @param e - the event to be displayed
         * */
        private void bind(Event e) {
            // Get the state
            boolean expanded = e.isExpanded();
            // Set the visibility based on state
            subitem.setVisibility(expanded ? View.VISIBLE : View.GONE);

        }
    }
}

/*********************************************************************************************/
