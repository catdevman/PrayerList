package com.lucaspearson.prayerlist;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

public class PrayersFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {
	private static final int LOADER_ID_FOR_DB = 5;
	OnPrayerSelectedListener mCallback;
	SimpleCursorAdapter simpleCursorAdapter;

	// deliver messages
	public interface OnPrayerSelectedListener {
		/** Called by PrayersFragment when a list item is selected */
		public void onPrayerSelected(int position, long id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getLoaderManager().initLoader(LOADER_ID_FOR_DB, null, this);
		setupListView();
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
	
		mCallback.onPrayerSelected(position, id);
		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}
	
//	private void registerListClickCallback(){
//		ListView lv = getListView();
//		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				mCallback.onPrayerSelected(position, id);
//				// Set the item as checked to be highlighted when in two-pane layout
//				getListView().setItemChecked(position, true);
//			}
//		});
//	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		PrayerData prayerData = new PrayerData(getActivity());
		return prayerData;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
		switch (loader.getId()) {
		case LOADER_ID_FOR_DB:
			populateListView(c);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		simpleCursorAdapter.swapCursor(null);
	}

	private void populateListView(Cursor c) {
		simpleCursorAdapter.swapCursor(c);
	}

	private void setupListView() {
		String[] dataColumns = { "name", "category" };
		int[] viewIDs = { R.id.item_name, R.id.item_category };

		simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.list_item, null, dataColumns, viewIDs, 0);
		setListAdapter(simpleCursorAdapter);
		// setListAdapter(new ArrayAdapter<Prayer>(this.getActivity(), layout,
		// results));
	}

}
