package com.lucaspearson.prayerlist;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

public class PrayerListActivity extends FragmentActivity implements
		PrayersFragment.OnPrayerSelectedListener {
	int mCurrentID = -1;
	PrayersFragment firstFragment;
	PrayerData prayerData;
	String name, description, category;
	int priority;
	EditText etName, etDescription;
	RatingBar ratingbarPriority;
	Spinner spCategory;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prayer_articles);
		prayerData = new PrayerData(this);
		etName = (EditText) findViewById(R.id.etName);
		etDescription = (EditText) findViewById(R.id.etDescription);
		ratingbarPriority = (RatingBar) findViewById(R.id.ratingbarPriority);
		spCategory = (Spinner) findViewById(R.id.spCategory);
		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first
		// fragment
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			// Create an instance of ExampleFragment
			firstFragment = new PrayersFragment();

			// In case this activity was started with special instructions from
			// an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, firstFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prayer_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_prayer:
			savePrayer();
			break;
		case R.id.action_settings:
			break;
		case R.id.save_prayer:

			break;
		case R.id.delete_prayer:
			deletePrayer();
			mCurrentID = -1;
			clearDetailView();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

	private void clearDetailView() {
		etName.setText("");
		ratingbarPriority.setRating(2);
		spCategory.setSelection(1);
		etDescription.setText("");
		//this.recreate();
	}

	public void onPrayerSelected(int position, int id) {
		// The user selected the headline of an article from the
		// HeadlinesFragment
		mCurrentID = id;
		// Capture the article fragment from the activity layout
		PrayerDetailFragment articleFrag = (PrayerDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.prayer_detail_fragment);

		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...

			// Call a method in the ArticleFragment to update its content

			articleFrag.updateArticleView(position, id);

		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it an argument for the selected article
			PrayerDetailFragment newFragment = new PrayerDetailFragment();
			Bundle args = new Bundle();
			args.putInt(PrayerDetailFragment.ARG_POSITION, position);
			args.putInt(PrayerDetailFragment.ARG_ID, id);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	private void deletePrayer() {
		if(mCurrentID != -1){
		prayerData.deletePrayerWithID(mCurrentID);
		}
	}

	private int savePrayer() {
		try {
			name = etName.getText().toString();
			priority = ratingbarPriority.getProgress();
			category = spCategory.getSelectedItem().toString();
			description = etDescription.getText().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long id = prayerData.insertData(name, description, priority, category);
		if (id != -1) {
			Toast.makeText(getBaseContext(),
					"Prayer was created with id: " + id, Toast.LENGTH_SHORT)
					.show();
			etName.setText("");
			ratingbarPriority.setRating(2);
			spCategory.setSelection(0);
			etDescription.setText("");
			this.recreate();
			return 0;
		} else {
			Toast.makeText(getBaseContext(), "Prayer was not created",
					Toast.LENGTH_SHORT).show();
			return 1;
		}
	}

}
