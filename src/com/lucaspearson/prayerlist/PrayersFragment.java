package com.lucaspearson.prayerlist;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PrayersFragment extends ListFragment {
	ArrayList<Prayer> results = null;
	OnPrayerSelectedListener mCallback;
	PrayerData prayerData;

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnPrayerSelectedListener {
		/** Called by PrayersFragment when a list item is selected */
		public void onPrayerSelected(int position, int id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prayerData = new PrayerData(PrayersFragment.this.getActivity());
		// We need to use a different list item layout for devices older than
		// Honeycomb
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;
		/*
		 * Use database to make array adapter
		 */
		setupTable();
		if (results != null) {
			setListAdapter(new ArrayAdapter<Prayer>(this.getActivity(), layout,
					results));

		} else {
			TextView tv = new TextView(getActivity());
			tv.setText("No Prayers created please press add prayer or sign in.");
			getListView().setEmptyView(tv);
		}
	}

	private void setupTable() {
		results = new ArrayList<Prayer>();
		Cursor c = prayerData.returnAllCursorByPriority();
		int count = c.getCount();
		c.moveToFirst();
		for (Integer j = 0; j < count; j++) {
			Prayer p = new Prayer();
			p.setId(c.getInt(c.getColumnIndex("_id")));
			p.setName(c.getString(c.getColumnIndex("name")));
			p.setPriority(c.getInt(c.getColumnIndex("priority")));
			p.setCategory(c.getString(c.getColumnIndex("category")));
			p.setDescription(c.getString(c.getColumnIndex("description")));
			results.add(p);

			c.moveToNext();
		}

	}

	@Override
	public void onStart() {
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.article_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnPrayerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnPrayerSelectedListener");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity of selected item
		Prayer p = (Prayer) results.get(position);
		int pID = p.getId();
		mCallback.onPrayerSelected(position, pID);
		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}

}
