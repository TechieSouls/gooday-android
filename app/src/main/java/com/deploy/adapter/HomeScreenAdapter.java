package com.deploy.adapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.R;
import com.deploy.activity.GatheringScreenActivity;
import com.deploy.activity.HomeScreenActivity;
import com.deploy.bo.Event;
import com.deploy.bo.EventMember;
import com.deploy.bo.User;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.dashboard.HomeFragment;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 23/8/17.
 */

public class HomeScreenAdapter extends BaseExpandableListAdapter {

    private int GATHERING_SUMMARY_RESULT_CODE = 1001, CREATE_GATHERING_RESULT_CODE = 1002;

    HomeScreenActivity context;
    List<String> headers;
    Map<String, List<Event>> eventsMap;
    LayoutInflater inflter;

    public HomeScreenAdapter(HomeScreenActivity applicationContext, List<String> headers, Map<String, List<Event>> eventsMap) {
        this.context = applicationContext;
        this.headers = headers;
        this.eventsMap = eventsMap;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.eventsMap.get(this.headers.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_rows, null);
            viewHolder = new ViewHolder();
            viewHolder.eventTitle = (TextView) convertView.findViewById(R.id.event_title);
            viewHolder.eventLocation = (TextView) convertView.findViewById(R.id.event_location);
            viewHolder.reminderTime = (TextView) convertView.findViewById(R.id.tv_reminder_time);
            viewHolder.eventBar = convertView.findViewById(R.id.event_bar);
            viewHolder.llEventRowItem = (LinearLayout) convertView.findViewById(R.id.event_row_item);
            viewHolder.llReminderRowItem = (LinearLayout) convertView.findViewById(R.id.ll_reminder_row_item);
            viewHolder.divider = (View) convertView.findViewById(R.id.view_divider);
            viewHolder.startTime = (TextView) convertView.findViewById(R.id.tv_start_time);
            viewHolder.ivOwnerImage = (RoundedImageView) convertView.findViewById(R.id.iv_owner);
            //viewHolder.homeEventMemberImages = (LinearLayout) convertView.findViewById(R.id.home_adapter_event_member_images);
            ////viewHolder.memberImagesContainer = (LinearLayout) convertView.findViewById(R.id.ll_member_images_container);
            //viewHolder.homeEventMemberImagesCount = (TextView) convertView.findViewById(R.id.tv_event_member_images_count);
            //viewHolder.homeAdapterHorizontalImageScrollView = (HorizontalScrollView) convertView.findViewById(R.id.home_adapter_horizontal_scroll_view);
            viewHolder.tvReminderTitle = (TextView) convertView.findViewById(R.id.tv_reminder_title);
            viewHolder.trash = (TextView) convertView.findViewById(R.id.trash);
            viewHolder.eventId = null;
            viewHolder.scheduleAs = null;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Event child = (Event) getChild(groupPosition, childPosition);

        if (childPosition == 0) {
            viewHolder.divider.setVisibility(View.GONE);
        } else {
            viewHolder.divider.setVisibility(View.VISIBLE);
        }
        if(child.getType().equalsIgnoreCase("Reminder")) {
            viewHolder.llEventRowItem.setVisibility(View.GONE);
            viewHolder.llReminderRowItem.setVisibility(View.VISIBLE);
            viewHolder.tvReminderTitle.setText(child.getTitle());
            viewHolder.reminderTime.setText(child.getStartTime());
            if(child.getStartTimeMillis() != null && child.getStartTimeMillis() < System.currentTimeMillis()) {
                viewHolder.tvReminderTitle.setTextColor(Color.RED);
            } else {
                viewHolder.tvReminderTitle.setTextColor(context.getResources().getColor(R.color.cenes_dark_gray));
            }
        } else {
            viewHolder.llEventRowItem.setVisibility(View.VISIBLE);
            viewHolder.llReminderRowItem.setVisibility(View.GONE);
            viewHolder.scheduleAs = child.getScheduleAs();
            viewHolder.eventId = child.getEventId();
            viewHolder.eventTitle.setText(child.getTitle());


            if (child.getIsFullDay() != null && child.getIsFullDay()) {
                viewHolder.startTime.setText("00:00AM");
            } else {
                viewHolder.startTime.setText(child.getStartTime());
            }

            String eventType = child.getSource();

            if(eventType.equalsIgnoreCase("cenes")) {
                viewHolder.eventBar.setBackgroundColor(context.getResources().getColor(R.color.cenes_new_orange));
            } else if(eventType.equalsIgnoreCase("google")) {
                if(child.getScheduleAs().equalsIgnoreCase("holiday")) {
                    viewHolder.eventBar.setBackgroundColor(context.getResources().getColor(R.color.cenes_teal));
                } else if (child.getScheduleAs().equalsIgnoreCase("event")) {
                    viewHolder.eventBar.setBackgroundColor(context.getResources().getColor(R.color.google_plus_red));
                }
            } else if(eventType.equalsIgnoreCase("facebook")) {
                viewHolder.eventBar.setBackgroundColor(context.getResources().getColor(R.color.facebook_blue));
            } else if(eventType.equalsIgnoreCase("outlook")) {
                viewHolder.eventBar.setBackgroundColor(context.getResources().getColor(R.color.outlook_blue));
            } else if(eventType.equalsIgnoreCase("apple")) {
                viewHolder.eventBar.setBackgroundColor(context.getResources().getColor(R.color.apple_gray));
            }

            if (child.getLocation() == null || child.getLocation().length() == 0) {
                viewHolder.eventLocation.setVisibility(View.GONE);
            } else {
                viewHolder.eventLocation.setVisibility(View.VISIBLE);
                viewHolder.eventLocation.setText(child.getLocation());
            }

            EventMember owner = child.getEventMembers().get(0);
            Glide.with(context).load(owner.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(viewHolder.ivOwnerImage);

            /*if (child.getIsFullDay()) {
                viewHolder.eventTime.setText("00:00 AM");
            } else {
                viewHolder.eventTime.setText(child.getStartTime());
            }*/
            /*viewHolder.homeEventMemberImagesCount.setVisibility(View.GONE);

            if (child.getEventMembers() != null && child.getEventMembers().size() > 0) {
                viewHolder.homeEventMemberImages.removeAllViews();
                viewHolder.homeAdapterHorizontalImageScrollView.setVisibility(View.VISIBLE);

                for (int i = 0; i < child.getEventMembers().size(); i++) {
                    EventMember em = child.getEventMembers().get(i);
                    if (i > 2) {
                        viewHolder.homeEventMemberImagesCount.setVisibility(View.VISIBLE);
                        viewHolder.homeEventMemberImagesCount.setText("+" + (child.getEventMembers().size() - 3));
                    } else {
                        RelativeLayout rlRoot = new RelativeLayout(context);
                        rlRoot.setLayoutParams(new RelativeLayout.LayoutParams(CenesUtils.dpToPx(60), CenesUtils.dpToPx(60)));

                        RelativeLayout.LayoutParams profileParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(50), CenesUtils.dpToPx(50));
                        profileParams.setMargins(CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10), CenesUtils.dpToPx(10));

                        RoundedImageView roundedImageView = new RoundedImageView(context, null);
                        roundedImageView.setLayoutParams(profileParams);
                        roundedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        RelativeLayout.LayoutParams starParams = new RelativeLayout.LayoutParams(CenesUtils.dpToPx(30), CenesUtils.dpToPx(30));
                        starParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        starParams.addRule(RelativeLayout.ALIGN_PARENT_END);

                        ImageView ivStar = new ImageView(context);
                        ivStar.setLayoutParams(starParams);
                        ivStar.setPadding(CenesUtils.dpToPx(4), CenesUtils.dpToPx(4), CenesUtils.dpToPx(8), CenesUtils.dpToPx(8));
                        ivStar.setImageResource(R.drawable.star);

                        rlRoot.addView(roundedImageView);

                        try {
                            if(em.isOwner()) {
                                rlRoot.addView(ivStar);
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        if (em.getPicture() != null && em.getPicture() != "null")
                            Glide.with(context).load(em.getPicture()).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(roundedImageView);
                        else
                            roundedImageView.setImageResource(R.drawable.default_profile_icon);

                        viewHolder.homeEventMemberImages.addView(rlRoot);
                    }
                }
            } else {
                viewHolder.homeAdapterHorizontalImageScrollView.setVisibility(View.GONE);
            }
            */

            viewHolder.llEventRowItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent data = new Intent(context, GatheringScreenActivity.class);
                    data.putExtra("dataFrom", "list");
                    data.putExtra("eventId", viewHolder.eventId);
                    //context.startActivityForResult(data, GATHERING_SUMMARY_RESULT_CODE);
                    context.startActivity(data);


                }
            });

            /*viewHolder.memberImagesContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent data = new Intent(context, GatheringScreenActivity.class);
                    data.putExtra("dataFrom", "list");
                    data.putExtra("eventId", viewHolder.eventId);
                    //context.startActivityForResult(data, GATHERING_SUMMARY_RESULT_CODE);
                    context.startActivity(data);

                }
            });*/

            viewHolder.trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DeleteGatheringTask().execute(viewHolder.eventId);
                }
            });

        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.eventsMap.get(this.headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headers.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headers.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_home_data_headers, null);
            holder = new HeaderViewHolder();
            holder.lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        String headerTitle = (String) getGroup(groupPosition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.lblListHeader.setText(Html.fromHtml(headerTitle, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.lblListHeader.setText(Html.fromHtml(headerTitle));
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public int convertPpToDp(int pp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return (int) (pp / displayMetrics.density);
    }

    public int convertDpToPp(int dp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    static class ViewHolder {
        private Long eventId;
        private TextView eventTitle;
        private TextView eventLocation;
        private View eventBar;
        private LinearLayout llEventRowItem;
        private LinearLayout llReminderRowItem;
        private TextView reminderTime;
        private View divider;
        private TextView startTime;
        private RoundedImageView ivOwnerImage;
        //private LinearLayout homeEventMemberImages;
        //private LinearLayout memberImagesContainer;
        //private TextView homeEventMemberImagesCount;
        private TextView tvReminderTitle;
        private String scheduleAs;
        //private HorizontalScrollView homeAdapterHorizontalImageScrollView;
        private TextView trash;
    }

    static class HeaderViewHolder {
        private ExpandableListView expandableListView;
        private TextView lblListHeader;
    }

    class DeleteGatheringTask extends AsyncTask<Long, JSONObject, JSONObject> {
        ProgressDialog deleteGathDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deleteGathDialog = new ProgressDialog(context);
            deleteGathDialog.setMessage("Deleting..");
            deleteGathDialog.setIndeterminate(false);
            deleteGathDialog.setCanceledOnTouchOutside(false);
            deleteGathDialog.setCancelable(false);
            deleteGathDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Long... longs) {
            UserManager userManager = context.getCenesApplication().getCoreManager().userManager;
            User user = userManager.getUser();

            Long eventId = longs[0];
            user.setApiUrl(context.getCenesApplication().getCoreManager().urlManager.getApiUrl("dev"));
            String queryStr = "?event_id=" + eventId;
            JSONObject response = context.getCenesApplication().getCoreManager().apiManager.deleteEventById(user, queryStr, context);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            deleteGathDialog.dismiss();
            
            deleteGathDialog = null;
            try {
                if (response.getBoolean("success")) {
                    Toast.makeText(context, "Gathering Deleted", Toast.LENGTH_SHORT).show();
                    ((HomeFragment)context.getVisibleFragment()).initialSync();

                } else {
                    Toast.makeText(context, "Gathering Not Deleted", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}