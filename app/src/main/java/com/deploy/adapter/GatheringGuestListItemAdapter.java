package com.deploy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.R;
import com.deploy.bo.EventMember;
import com.deploy.fragment.gathering.GatheringGuestListFragment;
import com.deploy.fragment.gathering.GatheringPreviewFragment;
import com.deploy.util.CenesUtils;

import java.util.List;

public class GatheringGuestListItemAdapter extends BaseAdapter {

    private GatheringGuestListFragment gatheringGuestListFragment;
    private List<EventMember> eventMembers;
    private LayoutInflater inflter;

    public GatheringGuestListItemAdapter(GatheringGuestListFragment gatheringGuestListFragment, List<EventMember> eventMembers) {

        this.gatheringGuestListFragment = gatheringGuestListFragment;
        this.eventMembers = eventMembers;
        this.inflter = (LayoutInflater.from(gatheringGuestListFragment.getContext()));

    }
    @Override
    public int getCount() {
        return eventMembers.size();
    }

    @Override
    public EventMember getItem(int position) {
        return eventMembers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflter.inflate(R.layout.adapter_gathering_guest_list_item, null);

            viewHolder.ivProfilePic = (ImageView) convertView.findViewById(R.id.iv_profile_pic);
            viewHolder.tvCenesName = (TextView) convertView.findViewById(R.id.tv_cenes_name);
            viewHolder.tvPhonebookName = (TextView) convertView.findViewById(R.id.tv_phonebook_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        EventMember eventMember = getItem(position);

        System.out.println(eventMember.toString());
        String host = "";
        if (eventMember.getUserId() != null && eventMember.getUserId().equals(gatheringGuestListFragment.event.getCreatedById())) {
            host = " (Host)";
        }
        if (!CenesUtils.isEmpty(eventMember.getName())) {
            viewHolder.tvCenesName.setText(eventMember.getName()+" "+host);
        } else {
            viewHolder.tvCenesName.setText("  "+host);
        }

        if (eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getName()) && gatheringGuestListFragment.loggedInUser.getUserId().equals(gatheringGuestListFragment.event.getCreatedById())) {
            viewHolder.tvPhonebookName.setText(eventMember.getUser().getName());
        } else {
            viewHolder.tvPhonebookName.setText("");
        }

        viewHolder.ivProfilePic.setImageResource(R.drawable.profile_pic_no_image);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_pic_no_image);
        requestOptions.circleCrop();
        if (!CenesUtils.isEmpty(eventMember.getPicture())) {
            Glide.with(gatheringGuestListFragment.getContext()).load(eventMember.getPicture()).apply(requestOptions).into(viewHolder.ivProfilePic);
        } else if (eventMember.getUser() != null && !CenesUtils.isEmpty(eventMember.getUser().getPicture())) {
            Glide.with(gatheringGuestListFragment.getContext()).load(eventMember.getUser().getPicture()).apply(requestOptions).into(viewHolder.ivProfilePic);
        }


        return convertView;
    }

    class ViewHolder {

        private ImageView ivProfilePic;
        private TextView tvCenesName, tvPhonebookName;

    }
}
