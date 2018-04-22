package catwithbowtie.picdisk.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import catwithbowtie.picdisk.Picture;
import catwithbowtie.picdisk.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.io.File;

/**
 * Класс для отображения изображения во весь экран
 * @author Загитов Асгар
 * @version 1.0
 */
public class PhotoActivity extends AppCompatActivity {
    public static final String PHOTO = "PHOTO";
    public static final String NAME = "NAME";
    /** Для отображения изображения*/
    private ImageViewTouch picture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
        //установка настроек ActionBar'a
        getSupportActionBar().setTitle(getIntent().getStringExtra(NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        picture = (ImageViewTouch) findViewById(R.id.picture);
        String path =  getIntent().getStringExtra(PHOTO);
        //загрузка изображения
        Glide.with(this).load(new File(path)).diskCacheStrategy(DiskCacheStrategy.ALL).into(picture);
    }


    /**
     * При нажатии кнопки НАЗАД, откроется MainActivity
     * @param item какой элемент был нажат
     * @return успешна ли обработка нажатия
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
