package com.zzg.materialdesign;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.zzg.materialdesign.widgets.flashview.FlashView;
import com.zzg.materialdesign.widgets.flashview.constants.EffectConstants;
import com.zzg.materialdesign.widgets.fragment.CardViewFragment;
import com.zzg.materialdesign.widgets.fragment.ItemFragment;
import com.zzg.materialdesign.widgets.fragment.MyItemRecyclerViewAdapter;
import com.zzg.materialdesign.widgets.fragment.dummy.CardsContent;
import com.zzg.materialdesign.widgets.fragment.dummy.DummyContent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ItemFragment.OnListFragmentInteractionListener, CardViewFragment.OnListFragmentInteractionListener {

    private SwipeRefreshLayout mSwipeLayout;
    private SearchView mSearchView;
    private String mSearchText;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        headerView.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PrivateInfoActivity.class));
            }
        });




        viewPager = (ViewPager) findViewById(R.id.viewPager);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                this);
        viewPager.setAdapter(adapter);

        //TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //分享按钮
        MenuItem shareItem = menu.findItem(R.id.action_share);
//        ShareActionProvider myShareActionProvider =
//                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);


//        //消除历史分享记录
//        if (myShareActionProvider != null) {
//            ShareActionProvider.OnShareTargetSelectedListener listener = new ShareActionProvider.OnShareTargetSelectedListener() {
//                public boolean onShareTargetSelected(
//                        ShareActionProvider source, Intent intent) {
//                    startActivity(intent);
//                    return true;
//                }
//            };
//            myShareActionProvider.setShareHistoryFileName(null);
//            myShareActionProvider.setOnShareTargetSelectedListener(listener);
//            myShareActionProvider.setShareIntent(myShareIntent);
//        }
        //搜索按钮
        final MenuItem item = menu.findItem(R.id.ab_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchText = query;
                doSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchText = newText;
                doSearch();
                return true;
            }
        });
        return true;
    }

    /**
     * 搜索过滤
     */

    private void doSearch() {
        //获取当前主页面下的tab
        int selectedTabPosition = tabLayout.getSelectedTabPosition();
        switch (selectedTabPosition) {
            case 0:
                filterList(selectedTabPosition);
                break;
            case 1:
                break;
        }
    }

    /**
     * 过滤list
     *
     * @param selectedTabPosition
     */
    private void filterList(int selectedTabPosition) {
        View childAt = viewPager.getChildAt(selectedTabPosition);
        //因为所有的list都用recycleview所以都强转成recycleview
        RecyclerView recyclerView = (RecyclerView) childAt;
        MyItemRecyclerViewAdapter adapter = (MyItemRecyclerViewAdapter) recyclerView.getAdapter();
        adapter.filter(mSearchText);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_share) {
            //分享一张图片
            Intent myShareIntent = new Intent(Intent.ACTION_SEND);
            myShareIntent.setType("image/png");
//            Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                    + getResources().getResourcePackageName(R.drawable.card1) + "/"
//                    + getResources().getResourceTypeName(R.drawable.card1) + "/"
//                    + getResources().getResourceEntryName(R.drawable.card1));
            Uri uri = Uri.parse("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
            myShareIntent.putExtra(Intent.EXTRA_STREAM,uri);
            startActivity(Intent.createChooser(myShareIntent,
                    "分享 图片"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this, item.content + "   " + item.details + "    ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListFragmentInteraction(CardsContent.CardItem item) {
        Toast.makeText(this, item.details + "    ", Toast.LENGTH_SHORT).show();
    }
}
