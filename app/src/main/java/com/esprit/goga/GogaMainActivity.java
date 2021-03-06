package com.esprit.goga;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.esprit.android.util.MxxSystemBarTintUtil;
import com.esprit.android.util.MxxToastUtil;
import com.esprit.android.util.SystemBarTintManager;
import com.esprit.android.view.MxxPagerSlidingTabStrip;
import com.esprit.android.view.TypefaceSpan;
import com.esprit.goga.bean.FeedItem;
import com.esprit.goga.fragment.CommentsFragment;
import com.esprit.goga.fragment.GagFragment;
import com.esprit.goga.fragment.GagFragmentFresh;
import com.esprit.goga.fragment.GagFragmentHot;
import com.esprit.goga.fragment.ImageFragment;
import com.esprit.goga.fragment.UploadFragment;
import com.google.firebase.messaging.RemoteMessage;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

import io.paperdb.Paper;


public class GogaMainActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private View mCommentView;
    private MxxPagerSlidingTabStrip mTabStrip;
    private View tabLayout;
    //	private MxxScaleImageView scaleImageView;
    private ImageFragment mImageFragment;
    private CommentsFragment mCommentsFragment;
    private UploadFragment mUploadFragment;
    private  FloatingActionButton fab;
    RemoteMessage remoteMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        MxxSystemBarTintUtil.setSystemBarTintColor(this);
        SpannableString spannableString = new SpannableString("Goga");
        String font = "LockScreen_Clock.ttf";
        Paper.init(getApplicationContext());
        spannableString.setSpan(new TypefaceSpan(font, Typeface.createFromAsset(getAssets(), font)), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setContentView(R.layout.activity_goga_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(spannableString);
       // toolbar.setBackgroundColor(R.color.colorPrimary);
        setSupportActionBar(toolbar);
        mImageFragment = (ImageFragment) getSupportFragmentManager().findFragmentById(R.id.main_image_fragment);
        mCommentsFragment = (CommentsFragment) getSupportFragmentManager().findFragmentById(R.id.main_comments_fragment);
        mUploadFragment = (UploadFragment) getSupportFragmentManager().findFragmentById(R.id.main_upload_fragment);

        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        //mCommentView = findViewById(R.id.comment_view);
//		scaleImageView = (MxxScaleImageView) findViewById(R.id.main_mxxScaleImageView1);
        mViewPager.setOffscreenPageLimit(3);
        mTabStrip = (MxxPagerSlidingTabStrip) findViewById(R.id.main_tab);
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new GagFragmentHot() );
        //fragments.add(new GagFragmentTrending() );
        fragments.add(new GagFragmentFresh() );
        mViewPager.setAdapter(new GogaMainActivity.GagAdapter2(getSupportFragmentManager(),fragments,new String[]{"hot" ,"fresh"}));
        mTabStrip.setViewPager(mViewPager);
        initTint();
        mTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
                if(((ViewGroup)tabLayout).getChildAt(0).getTranslationY()!=0){
                    ((ViewGroup)tabLayout).getChildAt(0).setTranslationY(0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
        mTabStrip.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //getSupportFragmentManager().beginTransaction().hide(mImageFragment).commit();
            }
        });


        final PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(GogaMainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GogaMainActivity.this,UploadActivity.class));

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(GogaMainActivity.this, "You can't upload new post. Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };


         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                //showUploadFragment(true);
                TedPermission.with(GogaMainActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                        .check();
            }
        });
    }

    private FinalBitmap mFinalBitmap;

    public FinalBitmap getFinalBitmap(){
        if(mFinalBitmap==null){
            mFinalBitmap = FinalBitmap.create(this);
//			mFinalBitmap.configLoadingImage(R.drawable.favorites_nopicture_icon);

            mFinalBitmap.configBitmapMaxWidth(720);
            mFinalBitmap.configBitmapMaxHeight(720);
//			mFinalBitmap.configLoadfailImage(R.drawable.favorites_nopicture_icon);
            mFinalBitmap.configLoadingImage(null);
            mFinalBitmap.configLoadfailImage(null);
            mFinalBitmap.configDiskCacheSize(1024 * 1024 * 50);
        }
        return mFinalBitmap;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(mFinalBitmap!=null){
            mFinalBitmap.onPause();
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(mFinalBitmap!=null){
            mFinalBitmap.onResume();
        }
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mFinalBitmap!=null){
            mFinalBitmap.onDestroy();
        }
    }

//	public void startScaleAnimation(ImageView smallImageView){
//		scaleImageView.startScaleAnimation(smallImageView);

//	}

    public void showImageFragment(ImageView smallImageView , boolean show, FeedItem item){
        //showActionbarWithTabs(!show);
		/*Bundle bundle = new Bundle();
		bundle.putString("postId", item.getId());*/
        //mImageFragment.setArguments(bundle);

        if(show){
            getSupportFragmentManager().beginTransaction().show(mImageFragment).commit();
            mImageFragment.startScaleAnimation(smallImageView, item);
            fab.hide();


        }else{
            getSupportFragmentManager().beginTransaction().hide(mImageFragment).commit();
            fab.show();
        }

    }

    public void showUploadFragment(boolean show){
        if(show){
            getSupportFragmentManager().beginTransaction().show(mUploadFragment).commit();
            mUploadFragment.showUploadFragment();
            fab.hide();
        }else{
            getSupportFragmentManager().beginTransaction().hide(mUploadFragment).commit();
            fab.show();
        }
    }

    public void showCommentsFragment(boolean show,String id){
        if(show){

            getSupportFragmentManager().beginTransaction().show(mCommentsFragment).commit();

            mCommentsFragment.showComments(id);
            fab.hide();

        }else {
            getSupportFragmentManager().beginTransaction().hide(mCommentsFragment).commit();
            fab.show();
        }



    }

    public void showActionbarWithTabs(boolean show){
        if(show){
//			getActionBar().show();
            tabLayout.setVisibility(View.VISIBLE);
        }else{
//			getActionBar().hide();
            tabLayout.setVisibility(View.GONE);
        }
    }

    public View getTabStripLayout(){
        return ((ViewGroup)tabLayout).getChildAt(0);
    }

    private void initTint(){
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintDrawable(new ColorDrawable(getResources().getColor(R.color.mxx_item_theme_color_alpha)));
        //mPagerSlidingTabStrip.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), 0);
//		View rootView = findViewById(R.id.main_layout_root);
        tabLayout = findViewById(R.id.main_tab_layout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) tabLayout.getLayoutParams();
        layoutParams.topMargin = config.getPixelInsetTop(true) - 62;
//		layoutParams.height = MxxUiUtil.dip2px(this, 48) + config.getPixelInsetTop(true);
        tabLayout.requestLayout();
    }
    private class GagAdapter2 extends android.support.v4.app.FragmentPagerAdapter{

        String[] titles;// = new String[]{"hot", "trending" ,"fresh"};
        ArrayList<Fragment> fragments;
        public GagAdapter2(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }
        public GagAdapter2(FragmentManager fm,
                           ArrayList<Fragment> fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public GagFragment getItem(int position) {
            // TODO Auto-generated method stub
            return (GagFragment) fragments.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            return titles[position];
//			return super.getPageTitle(position);
        }

    }

    private class GagAdapter extends FragmentStatePagerAdapter {

        String[] titles;// = new String[]{"hot", "trending" ,"fresh"};
        ArrayList<Fragment> fragments;
        public GagAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }
        public GagAdapter(FragmentManager fm,
                          ArrayList<Fragment> fragments, String[] titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO Auto-generated method stub
            return titles[position];
//			return super.getPageTitle(position);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        if(mImageFragment!=null || mCommentsFragment!=null){
            menu.findItem(R.id.action_refresh).setVisible(!mImageFragment.canBack());
            menu.findItem(R.id.action_more).setVisible(!mImageFragment.canBack());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == R.id.action_refresh){
            GagFragment gagFragment = ((GogaMainActivity.GagAdapter2)mViewPager.getAdapter()).getItem(mViewPager.getCurrentItem());
            gagFragment.refresh();
            return true;
        }else if(item.getItemId() == R.id.action_about){
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }else if(item.getItemId() == R.id.action_profile){
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }else if(item.getItemId() == R.id.action_logout){
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs",MODE_PRIVATE).edit();
            editor.clear(); //clear all stored data
            editor.commit();
            startActivity(new Intent(this, LogInActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    long last_back_time = 0;
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if(mImageFragment != null && mImageFragment.canBack()){
            mImageFragment.goBack();

        }else if(mCommentsFragment != null && mCommentsFragment.canBack()) {
            mCommentsFragment.goBack();
        }else if(mUploadFragment != null && mUploadFragment.canBack()) {
            mUploadFragment.goBack();
        }else{
            long cur_time = System.currentTimeMillis();

            if((cur_time - last_back_time) < 1000 ){
                super.onBackPressed();
            }else{
                last_back_time = cur_time;
                //Toast.makeText(MainActivity.this,getResources().getString(R.string.str_back_twice_exit), Toast.LENGTH_SHORT).show();
                MxxToastUtil.showToast(GogaMainActivity.this, getResources().getString(R.string.str_back_twice_exit));
            }

        }
    }

}

