package com.seawaveai.smartyy.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.seawaveai.smartyy.R;
import com.seawaveai.smartyy.bean.VideoItem;
import com.seawaveai.smartyy.util.StringUtil;

/**
 * 创建者     laoyoutiao
 * 创建时间   2016/11/23 16:42
 * 描述	     查询数据库,继承CursorAdapter
 * <p/>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class VideoListAdapter extends CursorAdapter {

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = View.inflate(context, R.layout.video_list_item,null);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {/*初始化view*/
       ViewHolder holder = (ViewHolder) view.getTag();
       //拿到解析的item
        VideoItem item = VideoItem.getVieoItem(cursor);

        holder.tv_name.setText(item.getName());
        holder.tv_duration.setText(StringUtil.parseDuration( item.getDuration() ) );/*格式化时间*/
        holder.tv_size.setText(Formatter.formatFileSize(context,item.getSize()));/*格式化空间的大小*/

    }

    class ViewHolder{
        TextView tv_name,tv_duration,tv_size;
        public ViewHolder(View view){
            tv_name = (TextView) view.findViewById(R.id.video_name);
            tv_duration = (TextView) view.findViewById(R.id.video_duration);
            tv_size = (TextView) view.findViewById(R.id.video_size);
        }
    }


}
