package catwithbowtie.picdisk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;
import catwithbowtie.picdisk.OnBottomReachedListener;
import catwithbowtie.picdisk.PicsAdapter;
import catwithbowtie.picdisk.Picture;
import catwithbowtie.picdisk.R;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;
import com.yandex.disk.rest.*;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Resource;
import com.yandex.disk.rest.json.ResourceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Основной активити с лентой изображений
 * @author Загитов Асгар
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXPIRES_IN = "EXPIRES_IN";
    public static final String TOKEN = "TOKEN";
    /** Токен*/
    private YandexAuthToken token;
    /** Клиент для работы с Диском*/
    private RestClient client;
    /** Лента изображений*/
    private RecyclerView recyclerView;
    /** Адаптер для работы со списком изображений*/
    private PicsAdapter adapter;
    /** Список изображений*/
    private List<Picture> allImages = new ArrayList<Picture>();

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new PicsAdapter(this, allImages);

        //установка события при прокрутки ленты вниз до конца, загрузка новых изображений
        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                getImages();
            }
        });

        //по условию задачи лента должна быть разбита на две колонки
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        token = new YandexAuthToken(getIntent().getStringExtra(TOKEN), getIntent().getLongExtra(EXPIRES_IN, 0));
        createClient();
        getImages();
    }

    /**
     * Метод, возвращающий клиент для работы с Диском
     * @return клиент
     */
    public RestClient getClient() {
        return client;
    }

    /**
     * Метод, оповещающий адаптер об изменении в списке с изображениями
     */
    public void notifyAdapter() {
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Метод, оповещающий адаптер об изменении состояния одного изображения в списке
     * @param id id изображения
     */
    public void notifyAdapterAboutItem(final int id) {
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyItemChanged(id);
            }
        });
    }

    /**
     * Метод для загрузки изображений с Диска
     */
    private void getImages() {
        getImages(allImages.size(), 10, this);
    }

    /**
     * Метод для загрузки изображений с Диска
     * @param offset сколько пропустить
     * @param count сколько загрузить
     * @param act контекст
     */
    private void getImages(final int offset, final int count, final MainActivity act) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ResourcesArgs.Builder builder = new ResourcesArgs.Builder();
                    //скачиваем информацию только об изображенияхы
                    builder.setMediaType("image");
                    builder.setOffset(offset);
                    builder.setLimit(count);

                    //запрос к Диску
                    ResourceList rl = client.getFlatResourceList(builder.build());
                    List<Resource> res = rl.getItems();
                    //Добавление изображений в список
                    for (int i = 0; i < res.size(); i++)
                        allImages.add(new Picture(res.get(i), act, client, i + offset));
                    //оповещение адаптера
                    notifyAdapter();
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ctx, "Проверьте подключение к интернету", Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * Метод для создания клиента с полученнным токеном
     */
    private void createClient() {
        client = new RestClient(new Credentials("", token.getValue()));
    }

    /**
     * Метод для сохранения токена
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (token != null) {
            outState.putString(TOKEN, token.getValue());
            outState.putLong(EXPIRES_IN, token.expiresIn());
        }
    }

    /**
     * Метод для загрузки токена
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        token = new YandexAuthToken(savedInstanceState.getString(TOKEN), savedInstanceState.getLong(EXPIRES_IN));
        createClient();
    }


    /**
     * При нажатии кнопки НАЗАД, приложение свернётся
     */
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
