package com.lucaspearson.prayerlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	long mCurrentID = -1;
	PrayersFragment firstFragment;
	PrayerData prayerData;
	String name, description, category;
	int priority;
	EditText etName, etDescription;
	RatingBar ratingbarPriority;
	Spinner spCategory;
	String biblePref;
	SharedPreferences prefs;

	final static String BIBLE_PREFERENCE = "biblePref";
	final static String DEFAULT_BIBLE_PREFERENCE = "NIV";

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
		prefs = this.getSharedPreferences("com.lucaspearson.prayerlist",
				Context.MODE_PRIVATE);
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
		if (findViewById(R.id.fragment_container) == null) {
			getMenuInflater().inflate(R.menu.prayer_list, menu);
			return true;
		} else {
			// This is a phone menu has to be different because we can't talk to
			// the second fragment on a phone because it does not exist
			getMenuInflater().inflate(R.menu.phone_prayer_list, menu);

			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_prayer:
			addPrayer();
			break;
		case R.id.phone_add_prayer:
			//Replace current fragment with details fragment
			PrayerDetailFragment newFragment = new PrayerDetailFragment();
			Bundle args = new Bundle();
			args.putLong(PrayerDetailFragment.ARG_ID, mCurrentID);
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
			break;
		case R.id.action_settings:
			// Launch dialog that will save to SharedPreferences
			biblePref = prefs.getString(BIBLE_PREFERENCE,
					DEFAULT_BIBLE_PREFERENCE);
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

			alertDialog.setTitle("Shared Preferences");
			alertDialog
					.setSingleChoiceItems(R.array.bible_list_values, 0,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String[] strArray = getResources()
											.getStringArray(
													R.array.bible_list_values);
									biblePref = strArray[which];
								}
							})
					.setPositiveButton("Save",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Save to Shared Preferences!
									prefs.edit()
											.putString(BIBLE_PREFERENCE,
													biblePref).apply();

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});
			alertDialog.create().show();

			break;
		case R.id.phone_action_settings:
			biblePref = prefs.getString(BIBLE_PREFERENCE,
					DEFAULT_BIBLE_PREFERENCE);
			AlertDialog.Builder phoneAlertDialog = new AlertDialog.Builder(this);

			phoneAlertDialog.setTitle("Shared Preferences");
			phoneAlertDialog
					.setSingleChoiceItems(R.array.bible_list_values, 0,
							new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String[] strArray = getResources()
											.getStringArray(
													R.array.bible_list_values);
									biblePref = strArray[which];
								}
							})
					.setPositiveButton("Save",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Save to Shared Preferences!
									prefs.edit()
											.putString(BIBLE_PREFERENCE,
													biblePref).apply();

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});
			phoneAlertDialog.create().show();

			break;
		case R.id.save_prayer:
			savePrayer();
			updateLoader();
			break;
		case R.id.phone_save_prayer:
			savePrayer();
			updateLoader();
			break;
		case R.id.delete_prayer:
			AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);

			alertDialog2.setTitle("Delete Prayer");
			alertDialog2.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

							deletePrayer();
							mCurrentID = -1;
							updateLoader();
							clearDetailView();

						}
					}).setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
			alertDialog2.create().show();
			break;
		case R.id.phone_delete_prayer:
			break;
		case R.id.view_scripture:

			String bPref = prefs.getString(BIBLE_PREFERENCE,
					DEFAULT_BIBLE_PREFERENCE);
			String search = spCategory.getSelectedItem().toString();
			if (mCurrentID != -1 && !search.equals("Select one...")) {
				String biblegateway = "http://mobile.biblegateway.com/keyword/?search="
						+ search
						+ "&version1="
						+ bPref
						+ "&searchtype=all&limit=none&wholewordsonly=no";
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(biblegateway)));

			} else {
				Toast.makeText(getApplicationContext(),
						"Please set a category and save the prayer",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.phone_view_scripture:
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
	@Override
	public void onAttachFragment(Fragment fragment) {
		// TODO Auto-generated method stub
		super.onAttachFragment(fragment);
		}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(menu.equals(R.menu.phone_prayer_list)){
			
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private int savePrayer() {
		if (mCurrentID != -1) {
			try {
				name = etName.getText().toString();
				priority = ratingbarPriority.getProgress();
				category = spCategory.getSelectedItem().toString();
				description = etDescription.getText().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean flag = prayerData.updatePrayerWithID(mCurrentID, name,
					description, priority, category);
			if (flag) {
				Toast.makeText(getBaseContext(), "Prayer was updated!",
						Toast.LENGTH_SHORT).show();
				return 0;
			} else {
				Toast.makeText(getBaseContext(), "Prayer was not updated!",
						Toast.LENGTH_SHORT).show();
				return 2;
			}
		} else {
			try {
				name = etName.getText().toString();
				priority = ratingbarPriority.getProgress();
				category = spCategory.getSelectedItem().toString();
				description = etDescription.getText().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			long id = prayerData.insertData(name, description, priority,
					category);
			if (id != -1) {
				Toast.makeText(getBaseContext(),
						"Prayer was created with id: " + id, Toast.LENGTH_SHORT)
						.show();
				mCurrentID = id;
				return 0;
			} else {
				Toast.makeText(getBaseContext(), "Prayer was not created",
						Toast.LENGTH_SHORT).show();
				return 1;
			}

		}

	}

	private void updateLoader() {
		Intent intent = new Intent();
		intent.setAction(PrayerData.LOADER_RELOAD);
		sendBroadcast(intent);
	}

	private void clearDetailView() {
		etName.setText("");
		ratingbarPriority.setRating(2);
		spCategory.setSelection(0);
		etDescription.setText("");

	}

	public void onPrayerSelected(int position, long id) {
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
			args.putLong(PrayerDetailFragment.ARG_ID, id);
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

	private void addPrayer() {
		clearDetailView();
		mCurrentID = -1;
	}

	private void deletePrayer() {
		if (mCurrentID != -1) {
			prayerData.deletePrayerWithID(mCurrentID);
		}
	}

}
