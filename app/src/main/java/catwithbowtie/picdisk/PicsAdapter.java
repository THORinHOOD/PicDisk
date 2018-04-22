package catwithbowtie.picdisk;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import catwithbowtie.picdisk.activities.MainActivity;
import catwithbowtie.picdisk.activities.PhotoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Класс адаптер для работы с лентой изображений
 * @author Загитов Асгар
 * @version 1.0
 */
public class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.MyViewHolder> {
    /** Интерфейс, отвечающий за прокрутку ленты до конца*/
    OnBottomReachedListener onBottomReachedListener;
    /** Список изображений*/
    private List<Picture> pics;
    /** Основное окно*/
    private MainActivity act;

    /**
     * Конструктор класса
     * @param act основное окно
     * @param pics список изображений, с которыми будет работать адаптер
     */
    public PicsAdapter(MainActivity act, List<Picture> pics) {
        this.act = act;
        this.pics = pics;
    }

    /**
     * Метод создания карточки изображения
     * @param parent родительский view
     * @param viewType
     * @return холдер с изображением
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new MyViewHolder(itemView);
    }

    /**
     * Метод отображения карточки с изображениемы
     * @param holder холдер с изображением
     * @param position позиция в списке
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picture current = pics.get(position);

        //отобразить файл, если он загружен или отобразить progressBar
        if (current.isLoadedFile()) {
            holder.progressBar.setVisibility(ProgressBar.INVISIBLE);
            holder.pic.setVisibility(View.VISIBLE);
            Glide.with(act).load(current.getFile()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.pic);
        } else {
            holder.progressBar.setVisibility(ProgressBar.VISIBLE);
            holder.pic.setVisibility(View.INVISIBLE);
        }

        //устанваливаем для какого изображения устанавливается событие прокрутки экрана до конца
        if (position == pics.size() - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }

    /**
     * Метод возвращает количество изображений в списке
     * @return количество элементов в списке
     */
    @Override
    public int getItemCount() {
        return pics.size();
    }

    /**
     * Метод, устанавливающий событие
     * @param onBottomReachedListener событие
     */
    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    /**
     * Класс холдера (контейнера) для изображения в ленте
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /** Контейнер для отображения изображения*/
        public ImageView pic;
        /** Прогресс бар, который будет отоброжаться
         *  пока не загрузится изображение*/
        public ProgressBar progressBar;
        public MyViewHolder(View view) {
            super(view);
            pic = (ImageView) view.findViewById(R.id.image);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            itemView.setOnClickListener(this);
        }

        /**
         * Нажатие на изображение
         * При нажатии открывается PhotoActivity, в который будет передано нажатое изображение
         * @param view нажатое изображение
         */
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Picture current = pics.get(position);
                if (current.isLoadedFile()) {
                    Intent intent = new Intent(act, PhotoActivity.class);
                    intent.putExtra(PhotoActivity.PHOTO, current.getFile().getPath());
                    intent.putExtra(PhotoActivity.NAME, current.getName());
                    act.startActivity(intent);
                }
            }
        }
    }
}
