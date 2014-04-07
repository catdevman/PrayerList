package com.lucaspearson.prayerlist;

import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

public class PrayerDetailFragment extends Fragment {
	final static String ARG_POSITION = "position";
	final static String ARG_ID = "id";
	int mCurrentPosition = -1;
	long mCurrentID = -1;
	PrayerData prayerData = null;
	EditText etName, etDescription;
	RatingBar ratingbarPriority;
	Spinner spCategory;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		prayerData = new PrayerData(getActivity());
		// If activity recreated
		// (such as from screen
		// rotate), restore
		// the previous article
		// selection set by
		// onSaveInstanceState().
		// This is primarily
		// necessary when in the
		// two-pane layout.

		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
			mCurrentID = savedInstanceState.getInt(ARG_ID);
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.prayer_view, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already
		// been
		// applied to the fragment at this point so we can safely call the
		// method
		// below that sets the article text.
		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			// , args.getInt(ARG_ID)
			updateArticleView(args.getInt(ARG_POSITION), args.getInt(ARG_ID));
		} else if (mCurrentPosition != -1 && mCurrentID != -1) {
			// Set article based on saved instance state defined during
			// onCreateView
			// , mCurrentID
			updateArticleView(mCurrentPosition, mCurrentID);
		}
	}

	public void updateArticleView(int position, long id) {
		/*
		 * Use id to get Prayer to fill out view
		 */
		etName = (EditText) getActivity().findViewById(R.id.etName);
		etDescription = (EditText) getActivity().findViewById(
				R.id.etDescription);
		ratingbarPriority = (RatingBar) getActivity().findViewById(
				R.id.ratingbarPriority);
		spCategory = (Spinner) getActivity().findViewById(R.id.spCategory);
		mCurrentPosition = position;
		mCurrentID = id;

		Cursor c = prayerData.returnCursorWithID(mCurrentID);
		if (c.moveToFirst()) {
			Prayer p = new Prayer();
			p.setId(c.getInt(c.getColumnIndex("_id")));
			p.setName(c.getString(c.getColumnIndex("name")));
			p.setPriority(c.getInt(c.getColumnIndex("priority")));
			p.setCategory(c.getString(c.getColumnIndex("category")));
			p.setDescription(c.getString(c.getColumnIndex("description")));

			etName.setText(p.getName());
			ratingbarPriority.setRating(p.getPriority());
			List<String> listOfCategories = Arrays.asList(getResources()
					.getStringArray(R.array.categories));
			int categoryID = listOfCategories.indexOf(p.getCategory());
			spCategory.setSelection(categoryID);
			etDescription.setText(p.getDescription());
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the current article selection in case we need to recreate the
		// fragment
		outState.putInt(ARG_POSITION, mCurrentPosition);
		outState.putLong(ARG_ID, mCurrentID);
	}

}
