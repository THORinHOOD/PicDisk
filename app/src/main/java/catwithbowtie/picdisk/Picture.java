package catwithbowtie.picdisk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import catwithbowtie.picdisk.activities.MainActivity;
import com.yandex.disk.rest.ProgressListener;
import com.yandex.disk.rest.RestClient;
import com.yandex.disk.rest.exceptions.ServerException;
import com.yandex.disk.rest.json.Resource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Класс изображения, хранящий необходимую информацию о нём
 * @author Загитов Асгар
 * @version 1.0
 */
public class Picture {
    /**название изображения*/
    private String name;
    /**путь к файлу*/
    private String path;
    /**файл, для скачивания изображения с Диска*/
    private File file;
    /**файл в bmp*/
    private Bitmap bmp;
    /** id изображения*/
    private int id;
    /** состояние - загружен ли файл*/
    private boolean loadedFile;

    /**
     *  @see <p>Конструктор изображения, в нём же происходит загрузка изображения</p>
     *  @param res - информация, полученная с Диска о файле
     *  @param act - основное окно
     *  @param client - клиент для работы с Диском
     *  @param id - id изображения
     */
    public Picture(final Resource res, final MainActivity act, final RestClient client, final int id) {
        file = new File(act.getFilesDir(), res.getName());
        file.delete();
        this.name = res.getName();
        this.path = res.getPath().getPath();
        loadedFile = false;
        this.id = id;

        //загрузка файла
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.downloadFile(path, file, new ProgressListener() {

                        @Override
                        public void updateProgress(long loaded, long total) {
                            //если файл загружен, оповестить адаптер
                            if (loaded == total) {
                                loadedFile = true;
                                act.notifyAdapterAboutItem(id);
                            }
                        }

                        @Override
                        public boolean hasCancelled() {
                            return false;
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServerException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    /** @see <p>Метод, для получения имени</p>
     *  @return название изображения
     * */
    public String getName() {
        return name;
    }

    /** @see <p>Метод, для получения пути</p>
     *  @return путь до изображения
     * */
    public String getPath() {
        return path;
    }

    /** @see <p>Метод, возвращающий состояние загрузки файла</p>
     *  @return если файл загружен - true, иначе - false
     * */
    public boolean isLoadedFile() {
        return loadedFile;
    }

    /** @see <p>Метод, возвращающий файл</p>
     *  @return файл
     * */
    public File getFile() {
        return file;
    }

    /** @see <p>Метод, возвращающий id</p>
     *  @return id
     * */
    public int getId() {
        return id;
    }

    /** @see <p>Метод для получения файла в bmp</p>
     *  @return bmp файл, если он загружен, иначе - null
     * */
    public Bitmap getBmp() {
        if (loadedFile)
            if (bmp == null) {
                return bmp = BitmapFactory.decodeFile(file.getPath());
            } else {
                return bmp;
            }
        else {
            return null;
        }
    }
}
