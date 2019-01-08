package e.kyco.fitnessapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String WORKOUTS = "workouts";
    private static final String LENGTH_OF_WORKOUTS = "lengths";
    private static final String IMAGE_OF_WORKOUTS = "images";
    Spinner workoutTypeSpinner;
    ArrayAdapter<CharSequence> adapterForSpin;
    private FloatingActionButton saveFloatingActionButton;
    private EditText amountOfActivity;
    private SharedPreferences savedWorkouts;
    private SharedPreferences savedWorkoutLengths;
    private SharedPreferences savedWorkoutImages;
    private List<String> workOuts;
    private List<String> workoutLengthAL;
    private List<Workout> workoutConstruMade;
    private SearchesAdapter adapterOfSearchs;
    private ImageView currentImage;
    private String selectedItem;
    private int holdImageID;
    private Button progressButton;
    public List<String> testForButton;
    public int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentImage = (ImageView) findViewById(R.id.conditionImageView);
        workoutTypeSpinner = (Spinner) findViewById(R.id.workoutTypeSR);
        adapterForSpin = ArrayAdapter.createFromResource(this, R.array.workout_types_spin, android.R.layout.simple_spinner_item);
        adapterForSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutTypeSpinner.setAdapter(adapterForSpin);



        workoutTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();


                if (workoutTypeSpinner.getSelectedItem().toString().trim().equals("Running")) {
                    holdImageID = R.drawable.ic_directions_run_black_24dp;
                } else if (workoutTypeSpinner.getSelectedItem().toString().trim().equals("Walking")) {
                    holdImageID = R.drawable.ic_directions_walk_black_24dp;
                } else {
                    holdImageID = R.drawable.ic_directions_bike_black_24dp;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        //get ref to the edit text and attach a listener to
        amountOfActivity = ((TextInputLayout) findViewById(R.id.enterAmountTIL)).getEditText();
        amountOfActivity.addTextChangedListener(textWatcher);


        workoutTypeSpinner = findViewById(R.id.workoutTypeSR);
        adapterForSpin = ArrayAdapter.createFromResource(this, R.array.workout_types_spin, android.R.layout.simple_spinner_item);
        adapterForSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutTypeSpinner.setAdapter(adapterForSpin);


        saveFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        saveFloatingActionButton.setOnClickListener(saveButtonListener);
        updateSaveFAB();


        progressButton = (Button) findViewById(R.id.progressBTN);
        progressButton.setOnClickListener(progressButtonListener);

        //get the saved workouts
        savedWorkouts = getSharedPreferences(WORKOUTS, MODE_PRIVATE);
        savedWorkoutLengths = getSharedPreferences(LENGTH_OF_WORKOUTS, MODE_PRIVATE);
        savedWorkoutImages = getSharedPreferences(IMAGE_OF_WORKOUTS, MODE_PRIVATE);

        workOuts = new ArrayList<>(savedWorkouts.getAll().keySet());
        workoutConstruMade = new ArrayList<Workout>();
       // loadData();
        testForButton = new ArrayList<String>();
        loadData();
        workoutLengthAL = new ArrayList<>(savedWorkoutLengths.getAll().keySet());


        //reference to recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.progressRV);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterOfSearchs = new SearchesAdapter(workoutConstruMade, itemClickListener, itemLongClickListener);
        recyclerView.setAdapter(adapterOfSearchs);

        recyclerView.addItemDecoration(new ItemDivider(this));


    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int count, int after) {
            updateSaveFAB();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    //method to show or hide the floating action button depending on if theres input
    private void updateSaveFAB() {
        if (amountOfActivity.getText().toString().isEmpty())
            //hide if empty
            saveFloatingActionButton.hide();
        else
            //show if has input
            saveFloatingActionButton.show();

    }


    private final View.OnClickListener itemClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            };








    //calculate calories burned..online it seems like 60/mile was average
    //but theres no way to account for body weight and time done in in this case
    //so i just did 60 bc thats what i saw common value was across a few sources
    //this wouldnt work unless I did try/catch
    public final View.OnClickListener progressButtonListener = new View.OnClickListener() {
        public void onClick(View view) {
            double holdMiles = 0.0;
            double caloriesBurned = 0.0;
            try {
                for (int i = 0; i <= testForButton.size(); i++) {

                    holdMiles = Double.parseDouble(testForButton.get(i)) + holdMiles;
                }
            } catch (IndexOutOfBoundsException e) {

            }

            caloriesBurned = holdMiles * 60;
            final double toShow = caloriesBurned;
            final double toShowMiles = holdMiles;

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setTitle(R.string.progress);
            builder1.setMessage("Total Miles: " + holdMiles + "\nTotal Calories Burned: " + caloriesBurned);
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
           builder1.setNegativeButton(R.string.clear_all, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   workoutConstruMade.clear();
                   testForButton.clear();
                   adapterOfSearchs.notifyDataSetChanged();
                   dialogInterface.cancel();

               }
           });
           builder1.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   Intent sendIntent = new Intent();
                   sendIntent.setAction(Intent.ACTION_SEND);
                   sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out my workout progress!!" + "\nTotal Miles: " + toShowMiles + "\nTotal Calories Burned: " + toShow);
                   sendIntent.setType("text/plain");
                   startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sent_to)));
               }
           });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
    };


    private final View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String workoutLength = amountOfActivity.getText().toString();
            String typeOfWorkout = workoutTypeSpinner.getSelectedItem().toString();
            int imageOfWorkout = holdImageID;


            testForButton.add(workoutLength);
            if (!workoutLength.isEmpty()) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);

                addNewWorkout(typeOfWorkout, workoutLength, holdImageID);

                adapterOfSearchs.notifyDataSetChanged();
                amountOfActivity.setText("");
                amountOfActivity.requestFocus();


            }

            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editior = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(workoutConstruMade);
            editior.putString("workout list", json);
            editior.apply();

            SharedPreferences sharedPreferencesTwo = getSharedPreferences("shared preferences two", MODE_PRIVATE);
            SharedPreferences.Editor editiorTwo = sharedPreferencesTwo.edit();
            Gson gsonTwo = new Gson();
            String jsonTwo = gsonTwo.toJson(testForButton);
            editiorTwo.putString("progress list", jsonTwo);
            editiorTwo.apply();

        }
    };

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("workout list",null);
        Type type = new TypeToken<ArrayList<Workout>>() {}.getType();
        workoutConstruMade = gson.fromJson(json, type);

        SharedPreferences sharedPreferencesTwo = getSharedPreferences("shared preferences two", MODE_PRIVATE);
        Gson gsonTwo = new Gson();
        String jsonTwo = sharedPreferencesTwo.getString("progress list",null);
        Type typeTwo = new TypeToken<ArrayList<String>>() {}.getType();
        testForButton = gsonTwo.fromJson(jsonTwo, typeTwo);

       if (workoutConstruMade == null) {
            workoutConstruMade = new ArrayList<Workout>();
        }

        if (testForButton == null){
           testForButton = new ArrayList<String>();
        }
    }

    //add the workout to saved
    private void addNewWorkout(String typeOfWorkout, String distanceOfWorkout, int imageID) {


        SharedPreferences.Editor prefEdit = savedWorkouts.edit();
        SharedPreferences.Editor prefEditTwo = savedWorkoutLengths.edit();
        SharedPreferences.Editor prefEditThree = savedWorkoutImages.edit();

        prefEdit.putString(typeOfWorkout + count,typeOfWorkout);
        prefEditTwo.putString(distanceOfWorkout + count, distanceOfWorkout);
        prefEditThree.putInt(typeOfWorkout + count, imageID);
        count++;
        prefEdit.apply();
        prefEditTwo.apply();
        prefEditThree.apply();


        if (!workoutConstruMade.contains(typeOfWorkout)) {
            workoutConstruMade.add(new Workout(typeOfWorkout, distanceOfWorkout, imageID));
            //workoutC.add(typeOfWorkout);
            adapterOfSearchs.notifyDataSetChanged();
        }
        adapterOfSearchs.notifyDataSetChanged();

    }


    private final View.OnLongClickListener itemLongClickListener =
            new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // get the tag that the user long touched
                    final String tag = ((TextView) view).getText().toString();

                    // create a new AlertDialog
                    android.support.v7.app.AlertDialog.Builder builder =
                            new android.support.v7.app.AlertDialog.Builder(MainActivity.this);

                    // set the AlertDialog's title
                    builder.setTitle(
                            getString(R.string.share_edit_delete_title, tag));

                    // set list of items to display and create event handler
                    builder.setItems(R.array.dialog_items,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // share
                                            //shareSearch(tag);
                                            break;
                                        case 1: // edit
                                            // set EditTexts to match chosen tag and query
                                            // tagEditText.setText(tag);
                                            // queryEditText.setText(
                                            //  savedSearches.getString(tag, ""));
                                            break;
                                        case 2: // delete
                                            // deleteSearch(tag);
                                            break;
                                    }
                                }
                            }
                    );
                    // set the AlertDialog's negative Button
                    builder.setNegativeButton(getString(R.string.cancel), null);

                    builder.create().show(); // display the AlertDialog
                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}