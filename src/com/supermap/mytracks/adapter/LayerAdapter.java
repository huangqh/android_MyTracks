package com.supermap.mytracks.adapter;

import java.util.List;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.supermap.mytracks.R;
import com.supermap.mytracks.bean.LayerControlBean;
import com.supermap.mytracks.bean.LayerType;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * <p>
 * 查看地图时图层列表控制的适配器
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class LayerAdapter extends BaseAdapter {
    public static final int TOGGLE_CHANGE=2; 
    private List<LayerControlBean> layers = null;
    private Context context = null;
    private Handler handler = null;
    
    public void setHandler(Handler handler){
        this.handler = handler;
    }
    public LayerAdapter(Context context) {
        this.context = context;
    }
    
    public void setLayers(List<LayerControlBean> layers) {
        this.layers = layers;
    }

    @Override
    public int getCount() {
        return this.layers.size();
    }

    @Override
    public Object getItem(int position) {
        return this.layers.get(layers.size()-position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean areAllItemsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        final ViewHolder holder;
        final LayerControlBean layerBean = this.layers.get(layers.size()-position-1);
        if (itemView == null) {
            holder = new ViewHolder();
            itemView = LayoutInflater.from(context).inflate(R.layout.lv_item_layer, null);
            ViewUtils.inject(holder, itemView);
            itemView.setTag(holder);        
        } else {
            holder = (ViewHolder) itemView.getTag();
        }
        if(layerBean.isVisible()){
            holder.togglebtn.setChecked(true);
        }else{
            holder.togglebtn.setChecked(false);
        }
        holder.togglebtn.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean on) {
                if (!LayerType.FEATURE_LAYER.equals(layerBean.getLayerType()) && !LayerType.MARKER_LAYER.equals(layerBean.getLayerType())
                        && !LayerType.OVERLAY_LAYER.equals(layerBean.getLayerType())) {
                    Toast.makeText(context, "底图不能隐藏哦~", Toast.LENGTH_SHORT).show();
                    holder.togglebtn.setChecked(true);
                    return;
                }
                if (on) {
                    holder.togglebtn.setChecked(true);
                    layerBean.setVisible(true);
                } else {
                    holder.togglebtn.setChecked(false);
                    layerBean.setVisible(false);
                }
                Bundle bd = new Bundle();
                bd.putSerializable("layerBean", layerBean);
                Message msg = new Message();
                msg.what = TOGGLE_CHANGE;
                msg.setData(bd);
                handler.sendMessage(msg);
            }
        });
        String layerName = layerBean.getLayerName(); 
        if (LayerType.BASE_LAYER.equals(layerBean.getLayerType())) {
            layerName += "(底图)";
        }
//        if (LayerType.FEATURE_LAYER.equals(layerBean.getLayerType())){
//            layerName = "矢  量";
//            holder.togglebtn.setEnabled(true);
//        } else if (LayerType.MARKER_LAYER.equals(layerBean.getLayerType())){
//            layerName = "标  注";
//            holder.togglebtn.setEnabled(true);
//        } else {
//            layerName = layerBean.getLayerType();
////            holder.togglebtn.setEnabled(false);
//        }
        holder.layerTitle.setText(layerName);
        return itemView;
    }
    
     public class ViewHolder {
        @ViewInject(R.id.tv_lv_item_layer_title)
        public TextView layerTitle;
        @ViewInject(R.id.iv_lv_item_layer_togglebtn)
        public ToggleButton togglebtn;
    }

}
