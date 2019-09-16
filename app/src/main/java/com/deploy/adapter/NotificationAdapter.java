package com.deploy.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.AsyncTasks.NotificationAsyncTask;
import com.deploy.R;
import com.deploy.activity.CenesBaseActivity;
import com.deploy.bo.Notification;
import com.deploy.fragment.NotificationFragment;
import com.deploy.fragment.gathering.GatheringPreviewFragment;
import com.deploy.fragment.gathering.GatheringPreviewFragmentBkup;
import com.deploy.fragment.gathering.GatheringsFragment;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends BaseAdapter {

    private List<Notification> notifications;
    private Activity activity;
    private LayoutInflater inflter;

    public NotificationAdapter(Activity activity, List<Notification> notifications) {
        this.activity = activity;
        this.notifications = notifications;
        this.inflter = (LayoutInflater.from(activity));
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_notifications, null);
            holder = new ViewHolder();
            holder.notificationMessage = (TextView) convertView.findViewById(R.id.notification_mesasge);
            holder.notificationTime = (TextView) convertView.findViewById(R.id.notifcation_time);
            holder.notifcationReadStatus = (TextView) convertView.findViewById(R.id.notification_readstatus);
            holder.senderPic = (RoundedImageView) convertView.findViewById(R.id.notification_sender_profile_pic);
            holder.notificationDay = (TextView) convertView.findViewById(R.id.notification_day);
            holder.llContainer = (LinearLayout) convertView.findViewById(R.id.ll_container);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Notification notification = (Notification) notifications.get(position);

        System.out.println(notification.toString());
        holder.notificationMessage.setText(Html.fromHtml( notification.getMessage() + " <b>" + notification.getTitle() + "</b>"));
        holder.notificationTime.setText(CenesUtils.ddMMM.format(notification.getNotificationTime()).toUpperCase());

        //if (notification.getSenderImage() != null && notification.getSenderImage() != "" && notification.getSenderImage() != "null") {
        //    Glide.with(activity).load(notification.getSenderImage()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.senderPic);
        //} else {
       //     holder.senderPic.setImageResource(R.drawable.default_profile_icon);
        //}

        try {
            Glide.with(activity).load(notification.getUser().getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.senderPic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int daysDiff = CenesUtils.daysBetween(notification.getNotificationTime(), new Date().getTime());
        if (daysDiff > 0) {
            holder.notificationDay.setText(daysDiff +" Days Ago");
        } else {

            int hours = CenesUtils.differenceInHours(notification.getNotificationTime(), new Date().getTime());
            System.out.println("Hours Diff : "+hours);
            if (hours == 0) {
                holder.notificationDay.setText("Just Now");
            } else if (hours == 1) {
                holder.notificationDay.setText(hours+" Hour Ago");
            } else {
                holder.notificationDay.setText(hours+" Hours Ago");
            }
        }
        if (notification.getReadStatus().equals("Read")) {
            holder.llContainer.setBackground(activity.getResources().getDrawable(R.drawable.xml_curved_corner_markread_fill));
            holder.notificationTime.setTextColor(activity.getResources().getColor(R.color.cenes_markread_color));
            holder.notificationDay.setTextColor(activity.getResources().getColor(R.color.cenes_markread_color));
            holder.notifcationReadStatus.setVisibility(View.VISIBLE);
        } else {
            holder.llContainer.setBackground(activity.getResources().getDrawable(R.drawable.xml_curved_corner_blue_fill));
            holder.notificationTime.setTextColor(activity.getResources().getColor(R.color.cenes_selectedText_color));
            holder.notificationDay.setTextColor(activity.getResources().getColor(R.color.cenes_selectedText_color));
            holder.notifcationReadStatus.setVisibility(View.GONE);
        }



        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                GatheringPreviewFragment gatheringPreviewFragment = new GatheringPreviewFragment();
                gatheringPreviewFragment.event = notification.getEvent();
                ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragment, NotificationFragment.TAG);

               /* if (notification.getType().equals("Gathering")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "notification");
                    bundle.putLong("eventId",notification.getNotificationTypeId());
                    bundle.putString("message", "Your have been invited to...");
                    bundle.putString("title", notification.getTitle());


                    new NotificationAsyncTask(((CenesBaseActivity)activity).getCenesApplication(), activity);
                    new NotificationAsyncTask.MarkNotificationReadTask(new NotificationAsyncTask.MarkNotificationReadTask.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject response) {
                            System.out.println(response.toString());
                        }
                    }).execute(notification.getNotificationTypeId());

                    GatheringPreviewFragmentBkup gatheringPreviewFragmentBkup = new GatheringPreviewFragmentBkup();
                    gatheringPreviewFragmentBkup.setArguments(bundle);

                    ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragmentBkup, GatheringPreviewFragmentBkup.TAG);
                }*/ /*else if (notification.getType().equals("Gathering") && notification.getNotificationTypeStatus().equalsIgnoreCase("old")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("dataFrom", "list");
                    bundle.putLong("eventId",notification.getNotificationTypeId());


                    GatheringPreviewFragmentBkup gatheringPreviewFragment = new GatheringPreviewFragmentBkup();
                    gatheringPreviewFragment.setArguments(bundle);


                    ((CenesBaseActivity)activity).replaceFragment(gatheringPreviewFragment, GatheringPreviewFragmentBkup.TAG);

                }*/ /*else if (child.getType().equals("Reminder")) {
                    startActivity(new Intent(getActivity(), ReminderActivity.class));
                    getActivity().finish();
                }*/
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView notificationMessage;
        private TextView notificationTime;
        private TextView notifcationReadStatus;
        private RoundedImageView senderPic;
        private LinearLayout llContainer;
        private TextView notificationDay;
    }
}
