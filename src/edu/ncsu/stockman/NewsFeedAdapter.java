package edu.ncsu.stockman;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import edu.ncsu.stockman.activity.CommentActivity;
import edu.ncsu.stockman.model.Notification;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeedRow> {
	 List<NewsFeedRow>   data;
     Context context;
     int layoutResID;
     PopupWindow popUp;

public NewsFeedAdapter(Context context, int layoutResourceId,List<NewsFeedRow> data) {
     super(context, layoutResourceId, data);
     this.data=data;
     this.context=context;
     this.layoutResID=layoutResourceId;

     // TODO Auto-generated constructor stub
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {

     NewsHolder holder = null;
        View row = convertView;
         holder = null;

       if(row == null)
       {
           LayoutInflater inflater = ((Activity)context).getLayoutInflater();
           row = inflater.inflate(layoutResID, parent, false);
           holder = new NewsHolder();
           holder.itemName = (TextView)row.findViewById(R.id.example_itemname);           
           holder.itemName.setTextColor(getContext().getResources().getColor(R.color.kulur_white));           
           Typeface externalFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Comic Sans.ttf");
           holder.itemName.setTypeface(externalFont);
           holder.itemName.setTextSize(20);
           holder.button1=(Button)row.findViewById(R.id.swipe_button1);
           row.setTag(holder);
           Notification n = (data.get(position)).getItemName();
           holder.button1.setTag((data.get(position)).getItemName().id_notification);

           holder.profile_pic = (RoundedImageView) row.findViewById(R.id.profile_pic);
           Notification.setPic(context, n.type, n.text, holder.profile_pic);
       }
       else
       {
           holder = (NewsHolder)row.getTag();
       }
       popUp = new PopupWindow(context);
       NewsFeedRow itemdata = data.get(position);
       holder.itemName.setText(itemdata.getItemName().getText(context));
       
       
       holder.button1.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                       // TODO Auto-generated method stub
                	Intent intent = new Intent(getContext(),CommentActivity.class);
                	intent.putExtra("notification_id",(Integer)v.getTag());
             		getContext().startActivity(intent);             		
                 }
           });
       return row;

}

static class NewsHolder{
     TextView itemName;
     Button button1;
     RoundedImageView profile_pic;
     }


}
