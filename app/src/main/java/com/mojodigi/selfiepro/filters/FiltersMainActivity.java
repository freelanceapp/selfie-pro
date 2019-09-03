package com.mojodigi.selfiepro.filters;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mojodigi.selfiepro.R;
import com.zomato.photofilters.imageprocessors.Filter;

public class FiltersMainActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener{

    public static final String IMAGE_NAME = "dog.jpg";
    static { System.loadLibrary("NativeImageProcessor"); }

    //@BindView(R.id.image_preview)
    ImageView imagePreview;
    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;
    // the final image after applying
    Bitmap finalImage;
    FiltersListFragment filtersListFragment;
    // EditImageFragment editImageFragment;
    // @BindView(R.id.viewpager)
    ViewPager filters_viewpager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters_main);

        imagePreview  = (ImageView)findViewById(R.id.image_preview);


        loadImage();

        filters_viewpager = (ViewPager)findViewById(R.id.filters_viewpager);;
        setupViewPager(filters_viewpager);
    }


    private void setupViewPager(ViewPager viewPager) {
        FiltersPagerAdapter adapter = new FiltersPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        // editImageFragment = new EditImageFragment();
        // editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));

        // adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);
    }


    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        //resetControls();
        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

}
