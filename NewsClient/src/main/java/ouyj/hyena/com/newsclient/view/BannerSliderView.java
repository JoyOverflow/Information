package ouyj.hyena.com.newsclient.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.loopj.android.image.SmartImageView;

import ouyj.hyena.com.newsclient.R;

/**
 * Created by Administrator on 2016/10/24.
 */

public class BannerSliderView extends BaseSliderView {
    public BannerSliderView(Context context) {
        super(context);
    }
    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text2, null);


        String desc=getDescription();
        TextView description = v.findViewById(R.id.description);
        description.setText(desc);

        String url=getUrl();
        SmartImageView target = v.findViewById(R.id.daimajia_slider_image);
        target.setImageUrl(url);


        bindEventAndShow(v, target);
        return v;
    }
}
