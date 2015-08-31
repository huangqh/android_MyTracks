package com.supermap.mytracks.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.supermap.mytracks.R;
import com.supermap.mytracks.bean.WebLayer;
import com.supermap.mytracks.utils.DisplayUtil;

/**
 * <p>
 * web图层列表的适配器
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class MapLayersAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<WebLayer> weblayers = null;
    private Context context = null;
    private BitmapUtils bitmapUtils;

    public MapLayersAdapter(Context context) {
        this.context = context;
        this.bitmapUtils = new BitmapUtils(context.getApplicationContext());
        bitmapUtils.configDefaultLoadingImage(R.drawable.icon_maplist_snapshot);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.icon_maplist_snapshot_failed);
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
    }

    // public void addWebLayers(List<WebLayer> webLayers) {
    // if (this.weblayers == null) {
    // this.weblayers = new ArrayList<WebLayer>();
    // }
    // this.weblayers.addAll(webLayers);
    // }

    public void setWebLayers(List<WebLayer> webLayers) {
        if (webLayers == null) {
            this.weblayers = new ArrayList<WebLayer>();
        } else {
            this.weblayers = webLayers;
        }

    }

    public void clearData() {
        if (this.weblayers != null) {
            this.weblayers.clear();
        }
    }

    @Override
    public int getCount() {
        return weblayers.size();
    }

    @Override
    public Object getItem(int i) {
        return weblayers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup group) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.group_maplist_item, null);
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 地图名称
        holder.group_mapname.setText(this.weblayers.get(i).getTitle());
        // 地图缩略图
        // BitmapDrawable defaultDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.icon_maplist_snapshot);
        // resetThumbnailScale(holder.group_mapthumbnail, defaultDrawable.getBitmap());
        bitmapUtils.display(holder.group_mapthumbnail, weblayers.get(i).getThumbnail(), new CustomBitmapLoadCallBack());
        return convertView;
    }

    public class CustomBitmapLoadCallBack extends DefaultBitmapLoadCallBack<ImageView> {
        @Override
        public void onLoading(ImageView container, String uri, BitmapDisplayConfig config, long total, long current) {

        }

        @Override
        public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
            // super.onLoadCompleted(container, uri, bitmap, config, from);
            // 根据屏幕宽高重置缩略图尺寸
            resetThumbnailScale(container, bitmap);
            super.onLoadCompleted(container, uri, bitmap, config, from);
        }

        @Override
        public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
            resetThumbnailScale(container, ((BitmapDrawable) drawable).getBitmap());
            super.onLoadFailed(container, uri, drawable);
        }
    }

    private class ViewHolder {
        @ViewInject(R.id.group_mapthumbnail)
        private ImageView group_mapthumbnail;
        @ViewInject(R.id.group_mapname)
        private TextView group_mapname;
    }

    public List<WebLayer> getWebLayers() {
        return weblayers;
    }

    private void resetThumbnailScale(ImageView view, Bitmap bitmap) {
        // 根据屏幕宽高重置缩略图尺寸,web图层的缩列图是全幅图片，所以其宽高是256*256
        int newWidth = DisplayUtil.pxWidth(context) / 3 - (int) (DisplayUtil.getPxFromDp(context, 16d)); // （屏幕宽度/3 - 左边距-右边距）“边距各4dp，在布局文件中设置的”
        if (newWidth > 400) {
            newWidth = 400;// 设置图片最宽不得超过400个像素
        }
        float scale = (float) newWidth / 256;// 图片宽度被缩放的比例
        int newHeight = (int) (scale * 256);
        LayoutParams params = view.getLayoutParams();
        params.width = newWidth;
        params.height = newHeight;
        // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newWidth, newHeight);
        view.setLayoutParams(params);
    }
}
