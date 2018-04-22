package catwithbowtie.picdisk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import catwithbowtie.picdisk.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import java.io.File;

/**
 * Окно для авторизации пользователя
 * @author Загитов Асгар
 * @version 1.0
 */
public class SplashActivity extends AppCompatActivity {

    /**  Код, при котором авторизация прошла успешно*/
    private Integer RESULT_OK_CODE = 200;
    /** Сдк для авторизации*/
    private YandexAuthSdk sdk;
    /** Токен*/
    private YandexAuthToken token;

    /**
     * Создание активити, в нём же происходит авторизация и при успешной
     * авторизации переход к MainActivity
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sdk = new YandexAuthSdk(new YandexAuthOptions(getApplicationContext(), true));
        startActivityForResult(sdk.createLoginIntent(this, null), RESULT_OK_CODE);
    }

    /**
     * При получении ответа от Сдк
     * @param requestCode код который отвечает за успешную авторизацию
     * @param resultCode код результата
     * @param data переданные данные
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RESULT_OK_CODE) {
            try {
                token = sdk.extractToken(resultCode, data);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.EXPIRES_IN, token.expiresIn());
                intent.putExtra(MainActivity.TOKEN, token.getValue());
                startActivity(intent);
            } catch (YandexAuthException e) {
                e.printStackTrace();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * При нажатии кнопки НАЗАД приложение свернётся
     */
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
