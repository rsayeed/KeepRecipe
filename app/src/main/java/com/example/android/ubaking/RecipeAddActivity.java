package com.example.android.ubaking;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.android.ubaking.data.RecipeContract;
import com.example.android.ubaking.model.Recipe;
import com.example.android.ubaking.model.RecipeIngredients;
import com.example.android.ubaking.model.RecipeStep;
import com.example.android.ubaking.utilities.RecipeDataUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;

/**
 * Created by rubab on 7/17/17.
 */

public class RecipeAddActivity extends AppCompatActivity {

    private static final String TAG = RecipeAddActivity.class.getSimpleName();

    private Vibrator vib;
    private Animation animShake;

    // Toolbar
    @BindView(R.id.my_toolbar)
    Toolbar toolbar;

    // Scrollview
    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    // Text Layout
    @BindView(R.id.recipeNameEditTextLayout)
    TextInputLayout mRecipeNameEditTextLayout;
    @BindView(R.id.quantityEditTextLayout)
    TextInputLayout mQuantityEditTextLayout;
    @BindView(R.id.measureEditTextLayout)
    TextInputLayout mMeasureEditTextLayout;
    @BindView(R.id.IngredientEditTextLayout)
    TextInputLayout mIngredientsEditTextLayout;
    @BindView(R.id.shortDescEditTextLayout)
    TextInputLayout mShortDescEditTextLayout;
    @BindView(R.id.longDescEditTextLayout)
    TextInputLayout mLongDescEditTextLayout;
    @BindView(R.id.videoURLEditTextLayout)
    TextInputLayout mvideoEditTextLayout;

    // Edit Text
    @BindView(R.id.recipeNameEditText)
    EditText mRecipeNameEditText;
    @BindView(R.id.quantityEditText)
    EditText mQuantityEditText;
    @BindView(R.id.measureEditText)
    EditText mMeasureEditText;
    @BindView(R.id.ingredientEditText)
    EditText mIngredientsEditText;
    @BindView(R.id.shortDescEditText)
    EditText mShortDescEditText;
    @BindView(R.id.longDescEditText)
    EditText mLongDescEditText;
    @BindView(R.id.videoURLEditText)
    EditText mvideoEditText;

    // Add button - Ingredient
    @BindView(R.id.recipeIngredients_addmoreBtn)
    ImageButton mAddIngredientsBtn;

    // Add button - Step
    @BindView(R.id.recipeSteps_addmoreBtn)
    ImageButton mAddStepsBtn;

    // Camera button
    @BindView(R.id.imageSelectionBtn)
    ImageButton mCameraButton;

    // Variable layouts
    @BindView(R.id.recipeIngredientExtraLayout)
    LinearLayout mRecipeIngredientLayout;

    @BindView(R.id.recipeStepExtraLayout)
    LinearLayout mRecipeStepLayout;

    // Divider
    @BindView(R.id.addActivityDivider)
    View mDividerView;

    // Counters to keep track of ingredients and step children
    private static int ingredientsChildCount = 0;
    private static int stepChildCount = 0;

    // Temp image file path
    private String mTempPhotoPath;

    private Uri imageURI;
    private Uri selectedImageUri;

    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int PICTURE_REQUEST_CODE = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_add_recipe);

        // Bind the views
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change the title of ActionBar
        getSupportActionBar().setTitle(getResources().getString(R.string.actionBar_add));

        animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Set a listener on ingredients field
        mIngredientsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verify that there is text
                if (!s.toString().isEmpty()) {
                    // Display add button again if user wishes to add another ingredients section
                    mAddIngredientsBtn.setVisibility(View.VISIBLE);
                } else {
                    // We don't want user to unncessarily add views
                    mAddIngredientsBtn.setVisibility(View.INVISIBLE);

                }
            }
        });

        // Set a listener on Short Description field of Steps section
        mShortDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verify that there is text
                if (!s.toString().isEmpty()) {
                    // Display add button again if user wishes to add another ingredients section
                    mAddStepsBtn.setVisibility(View.VISIBLE);
                } else {
                    // We don't want user to unncessarily add views
                    mAddStepsBtn.setVisibility(View.INVISIBLE);

                }
            }
        });

        // Disable keyboard pop up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Process the click of the add ingredient
     */
    @OnClick(R.id.recipeIngredients_addmoreBtn)
    public void onClickAddIngredient() {

        // Inflate ingredients section
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.ingredients_group, null);

        final ImageButton deleteButton = (ImageButton) rowView
                .findViewById(R.id.deleteIngredientBtn);

        final EditText ingredientsEditText = (EditText) rowView.findViewById(R.id.ingredientEditText);

        // Hide the add button until we know the user has entered something in ingredient field
        mAddIngredientsBtn.setVisibility(View.INVISIBLE);

        deleteButton.setOnClickListener(new handleDeleteRecipeIngredientClick());

        ingredientsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verify that there is text
                if (!s.toString().isEmpty()) {
                    // Display add button again if user wishes to add another ingredients section
                    mAddIngredientsBtn.setVisibility(View.VISIBLE);
                } else {
                    // We don't want user to unncessarily add views
                    mAddIngredientsBtn.setVisibility(View.INVISIBLE);

                }
            }
        });

        // Add the section to layout below existing sections
        mRecipeIngredientLayout.addView(rowView, ++ingredientsChildCount);

    }

    class handleDeleteRecipeIngredientClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onClickRemoveIngredient(v);

        }
    }


    /**
     * Process the click of the remove ingredient
     */
    @OnClick(R.id.deleteIngredientBtn)
    public void onClickRemoveIngredient(View view) {

        Log.v(TAG, "Remove Ingedient view");

        if (ingredientsChildCount <= 0) {
            ingredientsChildCount = 0;
            return;
        } else {

            ingredientsChildCount--;
            mRecipeIngredientLayout.removeView((View) view.getParent());
        }
    }

    /**
     * Process the click of the add Steps
     */
    @OnClick(R.id.recipeSteps_addmoreBtn)
    public void onClickAddSteps() {

        // Inflate ingredients section
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.steps_group, null);

        final ImageButton deleteStepButton = (ImageButton) rowView
                .findViewById(R.id.deleteStepBtn);

        final EditText stepShortDescEditText = (EditText) rowView.findViewById(R.id.shortDescEditText);

        // Hide the add button until we know the user has entered something in ingredient field
        mAddStepsBtn.setVisibility(View.INVISIBLE);

        deleteStepButton.setOnClickListener(new handleDeleteRecipeStepClick());

        stepShortDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verify that there is text
                if (!s.toString().isEmpty()) {
                    // Display add button again if user wishes to add another ingredients section
                    mAddStepsBtn.setVisibility(View.VISIBLE);
                } else {
                    // We don't want user to unncessarily add views
                    mAddStepsBtn.setVisibility(View.INVISIBLE);

                }
            }
        });

        // Add the section to layout below existing sections
        mRecipeStepLayout.addView(rowView, ++stepChildCount);

    }

    class handleDeleteRecipeStepClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onClickRemoveStep(v);

        }
    }


    /**
     * Process the click of the remove step
     */
    @OnClick(R.id.deleteStepBtn)
    public void onClickRemoveStep(View view) {

        Log.v(TAG, "Remove Step view");

        if (stepChildCount <= 0) {
            stepChildCount = 0;
            return;
        } else {

            stepChildCount--;
            mRecipeStepLayout.removeView((View) view.getParent());
        }
    }


    /**
     * Process the click of the Image Chooser
     */
    @OnClick(R.id.imageSelectionBtn)
    public void onClickImageChooser() {

        // Validate user permissions and then display photo select options
        checkPermissions();

    }


    /**
     * Check permissions during run time in order to run camera application
     */
    public void checkPermissions() {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

        } else {
            // Launch the camera if the permission exists
            displayImageChooser();
        }
    }

    /**
     * This is the callback method for the requestPermission method above
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    displayImageChooser();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Create a new File for picture. Create Intent choosers for every applicable image
     * selecting application available on the device
     */
    public void displayImageChooser() {

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = timeStamp + ".jpg";

        final File sdImageMainDirectory = new File(storageDir, fname);
        mTempPhotoPath = sdImageMainDirectory.getAbsolutePath();
        imageURI = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
    }

    /**
     * Callback method for startActivityForResult. Verify status and determine whether we have
     * a valid image URI that we can save to the DB
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();

                    //data.getExtras().get("data");

                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (isCamera) {
                    // FROM CAMERA
                    selectedImageUri = imageURI;
                } else {
                    // From gallery a pic was chosen
                    selectedImageUri = data == null ? null : data.getData();
                }

                if (selectedImageUri != null) {

                    Log.v(TAG, "Image URI not null");

                    InputStream inputStream = null;
                    try {

                        inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        mCameraButton.setImageBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Log.v(TAG, "URI FOR SELECTED IMAGE: " + selectedImageUri);

                // Save to gallery
                galleryAddPic();
            }
        } else if (resultCode == RESULT_CANCELED) {

            Log.v(TAG, "Cancelled Camera: " + requestCode);
        } else {

            Log.v(TAG, "NO GOOD CAMERA");

            // Delete the temp file
            deleteImageFile(this, mTempPhotoPath);
        }
    }

    /**
     * This method will ensure that a new picture taken with the camera app shows up
     * in the gallery
     */
    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mTempPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    /**
     * Process the click of the Add button
     */
    @OnClick(R.id.addButton)
    public void onClickAddRecipe() {

        Recipe recipe = new Recipe();

        // Validate recipe name, every ingredient field, and every short Desc field
        if (!checkRecipeName()) {
            mRecipeNameEditText.setAnimation(animShake);
            mRecipeNameEditText.startAnimation(animShake);
            vib.vibrate(120);
            return;
        }

        // Store the recipe name and image path if there is one
        recipe.setRecipeName(mRecipeNameEditText.getText().toString().trim());

        if (selectedImageUri != null) {

            // Store the image URL
            recipe.setImageURL(selectedImageUri.toString());
        }

        // List to hold ingredients and the steps
        List<RecipeIngredients> recipeIngredientsList = new ArrayList<>();
        List<RecipeStep> recipeStepList = new ArrayList<>();

        RecipeIngredients recipeIngredients;
        RecipeStep recipeStep;

        /* GATHER ALL THE INGREDIENTS */
        for (int i = 0; i < mRecipeIngredientLayout.getChildCount(); i++) {

            recipeIngredients = new RecipeIngredients();

            View v = mRecipeIngredientLayout.getChildAt(i);

            if (v instanceof LinearLayout) {

                LinearLayout parentLinearLayout = (LinearLayout) v;

                // Get the Quantity, Measure, and Ingredient
                for (int x = 1; x <= 3; x++) {

                    View ingredEditTextLayout = parentLinearLayout.getChildAt(x);

                    if (ingredEditTextLayout instanceof TextInputLayout) {

                        TextInputLayout ingredEditTextTextLayoutView = (TextInputLayout) ingredEditTextLayout;

                        // Get the child from text layout
                        View ingredFrameLayout = ingredEditTextTextLayoutView.getChildAt(0);

                        if (ingredFrameLayout instanceof FrameLayout) {

                            FrameLayout frameLayout = (FrameLayout) ingredFrameLayout;

                            View ingredientField = frameLayout.getChildAt(0);

                            if (ingredientField instanceof EditText) {

                                EditText ingredient = (EditText) ingredientField;

                                switch (x) {

                                    case 1:
                                        recipeIngredients.setQuantity(ingredient.getText().toString());
                                        break;
                                    case 2:
                                        recipeIngredients.setMeasure(ingredient.getText().toString());
                                        break;
                                    case 3:
                                        if (!checkRecipeIngredient(ingredEditTextTextLayoutView, ingredient)) {
                                            ingredient.setAnimation(animShake);
                                            ingredient.startAnimation(animShake);
                                            vib.vibrate(120);
                                            return;
                                        }
                                        recipeIngredients.setIngredient(ingredient.getText().toString());
                                        break;
                                }
                            }

                        }

                    }

                }

            }

            recipeIngredientsList.add(recipeIngredients);
        }

        Log.v(TAG, "TOTAL RECIPE INGRED OBJECTS " + recipeIngredientsList.size());

        /* GATHER ALL THE STEPS */
        for (int i = 0; i < mRecipeStepLayout.getChildCount(); i++) {

            recipeStep = new RecipeStep();

            View v = mRecipeStepLayout.getChildAt(i);

            if (v instanceof LinearLayout) {

                LinearLayout parentLinearLayout = (LinearLayout) v;

                // Get the Short Desc, Details, and Media URL if there is any
                for (int x = 1; x <= 3; x++) {

                    View stepEditTextLayout = parentLinearLayout.getChildAt(x);

                    if (stepEditTextLayout instanceof TextInputLayout) {

                        TextInputLayout stepEditTextTextLayoutView = (TextInputLayout) stepEditTextLayout;

                        // Get the child from text layout
                        View stepFrameLayout = stepEditTextTextLayoutView.getChildAt(0);

                        if (stepFrameLayout instanceof FrameLayout) {

                            FrameLayout frameLayout = (FrameLayout) stepFrameLayout;

                            View stepField = frameLayout.getChildAt(0);

                            if (stepField instanceof EditText) {

                                EditText step = (EditText) stepField;

                                switch (x) {

                                    case 1:
                                        if (!checkRecipeStep(stepEditTextTextLayoutView, step)) {
                                            step.setAnimation(animShake);
                                            step.startAnimation(animShake);
                                            vib.vibrate(120);
                                            return;
                                        }
                                        recipeStep.setShortDesc(step.getText().toString());
                                        break;
                                    case 2:
                                        if (step.getText().toString().isEmpty()) {
                                            recipeStep.setDesc(recipeStep.getShortDesc());
                                        } else {
                                            recipeStep.setDesc(step.getText().toString());
                                        }
                                        break;
                                    case 3:
                                        if (step.getText().toString().isEmpty()) {
                                            recipeStep.setVideoURL("");
                                        } else {
                                            if (!checkVideoURL(stepEditTextTextLayoutView, step)) {
                                                step.setAnimation(animShake);
                                                step.startAnimation(animShake);
                                                vib.vibrate(120);
                                                return;
                                            }
                                            recipeStep.setVideoURL(step.getText().toString());
                                        }
                                        recipeStep.setThumbURL("");
                                        Log.v(TAG, "Video URL: " + recipeStep.getVideoURL());
                                        break;
                                }
                            }

                        }

                    }

                }

            }

            recipeStepList.add(recipeStep);
        }

        Log.v(TAG, "RECIPE STEPS TOTAL: " + recipeStepList.size());

        // Store the recipe to the database and get the ID
        int id = RecipeDataUtils.insertRecipeAndGetId(recipe, this);

        recipe.setRecipeId(String.valueOf(id));

        Log.v(TAG, "NEW RECIPE ID: " + id);

        if (id != -1) {

            // Loop through Recipe Ingredients and Recipe Steps and update steps with appropriate id
            for (int ingredInx = 0; ingredInx < recipeIngredientsList.size(); ingredInx++) {

                RecipeIngredients recipeIngred = recipeIngredientsList.get(ingredInx);

                recipeIngred.setRecipeId(String.valueOf(id));
            }

            // Loop through Recipe Ingredients and Recipe Steps and update steps with appropriate id
            for (int stepInx = 0; stepInx < recipeStepList.size(); stepInx++) {

                RecipeStep recipeStp = recipeStepList.get(stepInx);

                recipeStp.setRecipeId(String.valueOf(id));
            }

            // Insert the Ingredients and Steps to the database
            RecipeDataUtils.insertRecipeIngredientsAndSteps(recipeIngredientsList, recipeStepList, this);

            // Save the recipe ingredients and steps to the recipe object
            recipe.setRecipeIngredients(recipeIngredientsList);
            recipe.setRecipeSteps(recipeStepList);

            getIntent().putExtra("newRecipe", recipe);

            Log.v(TAG, "recipe name new: " + recipe.getRecipeName());

            // Return code for callback
            setResult(RESULT_OK, getIntent());

            // Go back to Main Activity
            finish();

        } else {

            Log.v(TAG, "PROBLEM INSERTING RECIPE");
            Toast.makeText(this, R.string.contact_admin, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Validate that the user has successfully entered a recipe name
     *
     * @return
     */
    private boolean checkRecipeName() {

        if (mRecipeNameEditText.getText().toString().trim().isEmpty()) {

            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, mRecipeNameEditText.getBottom());
                }
            });

            mRecipeNameEditTextLayout.setErrorEnabled(true);
            mRecipeNameEditTextLayout.setError(getString(R.string.error_recipe_name));
            mRecipeNameEditText.setError(getString(R.string.error_recipe_name));
            return false;
        }
        mRecipeNameEditTextLayout.setErrorEnabled(false);
        return true;
    }

    /**
     * Validate that the user has successfully entered a recipe name
     *
     * @return
     */
    private boolean checkRecipeIngredient(TextInputLayout textInputLayout, final EditText editText) {

        if (editText.getText().toString().trim().isEmpty()) {

            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, editText.getBottom());
                }
            });

            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.error_recipe_ingredient));
            editText.setError(getString(R.string.error_recipe_ingredient));
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    /**
     * Validate that the user has successfully entered a recipe step
     *
     * @return
     */
    private boolean checkRecipeStep(TextInputLayout textInputLayout, final EditText editText) {

        if (editText.getText().toString().trim().isEmpty()) {

            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, editText.getBottom());
                }
            });

            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.error_recipe_stepDesc));
            editText.setError(getString(R.string.error_recipe_stepDesc));
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        return true;
    }

    /**
     * Validate that the user has successfully a youtube link only
     *
     * @return
     */
    private boolean checkVideoURL(TextInputLayout textInputLayout, final EditText editText) {

        if (!editText.getText().toString().trim().contains("youtu")) {

            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, editText.getBottom());
                }
            });

            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.error_recipe_video));
            editText.setError(getString(R.string.error_recipe_video));
            return false;
        }
        textInputLayout.setErrorEnabled(false);
        return true;
    }


    /**
     * Deletes image file for a given path.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be deleted.
     */
    static boolean deleteImageFile(Context context, String imagePath) {
        // Get the file
        File imageFile = new File(imagePath);

        // Delete the image
        boolean deleted = imageFile.delete();

        // If there is an error deleting the file, show a Toast
        if (!deleted) {
            String errorMessage = context.getString(R.string.error_img);
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }

        return deleted;
    }

}
