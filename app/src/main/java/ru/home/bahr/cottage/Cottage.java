package ru.home.bahr.cottage;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

public class Cottage extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;                         //левое меню
    private boolean mDrawerLayoutState;                         //для сохранения состояния меню (выдвинуто, закрыто)
    private static final String DRAWER_STATE = "DRAVER_STATE";  //указатель на сохраненное положение меню
    private static final int MENU_ID = -1;                       //выделяет пунк меню в котором сейчас находимся
    private Toolbar mToolbar;                                   //верхнее меню

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cottage);

        //тулбар
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_36dp);

        //отрисовка левого слоя выезжающего меню
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if(savedInstanceState!=null){
            if(savedInstanceState.getBoolean(DRAWER_STATE)){
                mDrawerLayout.openDrawer(Gravity.LEFT);//открытие левого меню
            }
        }else{
            mDrawerLayout.openDrawer(Gravity.LEFT); //открытие левого меню
        }

        //отрисовка меню
        CottageMenu menu = new CottageMenu(this, R.id.left_drawer, MENU_ID);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //обработчик кнопки меню
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override   //обработка кнопки назад
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){  //если открыто меню, то закрыть его
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    //сохранение состояния Activiti
    @Override
    protected void onSaveInstanceState(Bundle outState){
        mDrawerLayoutState = mDrawerLayout.isDrawerOpen(Gravity.LEFT);
        outState.putBoolean(DRAWER_STATE, mDrawerLayoutState);
        super.onSaveInstanceState(outState);
    }

}
