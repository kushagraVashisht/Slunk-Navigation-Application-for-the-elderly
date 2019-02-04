package com.example.ritusharma.itproject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * This class handles Autocomplete requests from the Places API.
 */
public class PlaceArrayAdapter extends ArrayAdapter<PlaceArrayAdapter.PlaceAutocomplete> implements Filterable {

    private static final String TAG = "PlaceArrayAdapter";
    private GoogleApiClient mGoogleApiClient;
    private AutocompleteFilter mPlaceFilter;
    private LatLngBounds mBounds;
    private ArrayList<PlaceAutocomplete> mResultList;

    /*********************************************************************************************/

    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     * @param context
     * @param resource
     * @param bounds
     * @param filter
     */
    public PlaceArrayAdapter(Context context, int resource, LatLngBounds bounds,
                             AutocompleteFilter filter) {
        super(context, resource);
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    /*********************************************************************************************/

    /**
     * Method used for setting the googleApiClient
     * @param googleApiClient - google api client
     */
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }

    /*********************************************************************************************/

    /**
     * Returns the number of results from the query seach.
     */
    @Override
    public int getCount()
    {
        return mResultList.size();
    }

    /*********************************************************************************************/

    /**
     * Returns an item from the last query
     * @param position - returns the position of the query selected
     */
    @Override
    public PlaceAutocomplete getItem(int position)
    {
        return mResultList.get(position);
    }

    /*********************************************************************************************/

    /**
     * Returns the filter for the current set of autocomplete results
     */

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    mResultList = getPredictions(constraint);

                    if (mResultList != null) {
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }

                return results;
            }
            // Publishing the results
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /*********************************************************************************************/


    /**
     * Returns a list of predictions for a given query.
     */
    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.i(TAG, "Executing autocomplete query for: " + constraint);

            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
                    .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                            mBounds, mPlaceFilter);

            // Wait for predictions, set the timeout.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(getContext(), "Error: " + status.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting place predictions: " + status
                        .toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getFullText(null)));
            }
            // Buffer release
            autocompletePredictions.release();
            return resultList;
        }

        Log.e(TAG, "Google API client is not connected.");
        return null;
    }

    /*********************************************************************************************/

    class PlaceAutocomplete {
        public CharSequence placeId;
        public CharSequence description;

        /*********************************************************************************************/

        /**
         * Returns the placeID and description of the query selected
         * @param placeId - placeID for the query
         * @param description - Description of the query
         */

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        /*********************************************************************************************/

        /**
         * Returns the String version of the description
         */
        @Override
        public String toString()
        {
            return description.toString();
        }
    }

}
/*********************************************************************************************/
