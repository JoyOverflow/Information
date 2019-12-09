package ouyj.hyena.com.newsclient;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.loading_indicator)
    AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        ButterKnife.bind(this);
        loadingIndicator.show();
        final String imageUrl = getIntent().getStringExtra("image_url");
        if (imageUrl.endsWith(".gif")) {
            Glide.with(this)
                    .load(imageUrl)
                    .asGif()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) //跳过硬盘缓存
                    .placeholder(R.drawable.loading)
                    .into(image);
            loadingIndicator.hide();
        } else {
            Glide.with(this)
                    .load(imageUrl)
                    .asBitmap()
                    .centerCrop()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            image.setImageBitmap(resource);
                            loadingIndicator.hide();
                        }
                    });
        }
    }
}
