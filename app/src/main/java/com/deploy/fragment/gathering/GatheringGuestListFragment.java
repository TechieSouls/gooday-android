package com.deploy.fragment.gathering;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deploy.R;
import com.deploy.activity.CenesBaseActivity;
import com.deploy.adapter.GatheringGuestListItemAdapter;
import com.deploy.application.CenesApplication;
import com.deploy.bo.Event;
import com.deploy.bo.EventMember;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;

import java.util.ArrayList;
import java.util.List;

public class GatheringGuestListFragment extends CenesFragment {

    private static String TAG = "GatheringGuestListFragment";

    private ListView lvGuestListView;
    private RelativeLayout rlInvitedBubble, rlAcceptedBubble, rlDeclinedBubble;
    private TextView tvInvitedBubbleText, tvAcceptedBubbleText, tvDeclinedBubbleText;
    private TextView tvInvitedLabel, tvAcceptedLabel, tvDeclindlabel;
    private ImageView ivCloseGuestList;

    private GatheringGuestListItemAdapter gatheringGuestListItemAdapter;
    public List<EventMember> eventMembers, invitedMembers, acceptedMembers, declinedMembers;
    public Event event;

    private CenesApplication cenesApplication;
    public User loggedInUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gathering_guest_list, container, false);

        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = getCenesActivity().getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        lvGuestListView = (ListView) view.findViewById(R.id.lv_guest_list_view);

        rlInvitedBubble = (RelativeLayout) view.findViewById(R.id.rl_invited_bubble);
        rlAcceptedBubble = (RelativeLayout) view.findViewById(R.id.rl_accepted_bubble);
        rlDeclinedBubble = (RelativeLayout) view.findViewById(R.id.rl_declined_bubble);

        tvInvitedBubbleText = (TextView) view.findViewById(R.id.tv_invited_bubble_text);
        tvAcceptedBubbleText = (TextView) view.findViewById(R.id.tv_accepted_bubble_text);
        tvDeclinedBubbleText = (TextView) view.findViewById(R.id.tv_declined_bubble_text);

        tvInvitedLabel = (TextView) view.findViewById(R.id.tv_invited_label);
        tvAcceptedLabel = (TextView) view.findViewById(R.id.tv_accepted_label);
        tvDeclindlabel = (TextView) view.findViewById(R.id.tv_declined_label);

        ivCloseGuestList = (ImageView) view.findViewById(R.id.iv_close_guest_list);

        rlInvitedBubble.setOnClickListener(onClickListener);
        rlAcceptedBubble.setOnClickListener(onClickListener);
        rlDeclinedBubble.setOnClickListener(onClickListener);
        ivCloseGuestList.setOnClickListener(onClickListener);

        invitedMembers = new ArrayList<>();
        eventMembers = event.getEventMembers();
        int nonCenesCounts = 0;

        //Checking for host which should be at the top
        for (EventMember member: eventMembers) {
            if (member.getUserId() != null && member.getUserId().equals(event.getCreatedById())) {
                invitedMembers.add(member);
            }
        }

        //If its the event of logged in user then we will show names of all the members
        //Else we will show others
        if (event.getCreatedById().equals(loggedInUser.getUserId())) {

            for (EventMember member: eventMembers) {
                if (member.getUserId() != null && member.getUserId() != 0) {
                    if (!member.getUserId().equals(event.getCreatedById())) {
                        invitedMembers.add(member);
                    }
                }
            }

            for (EventMember member: eventMembers) {
                if (member.getUserId() == null || member.getUserId() == 0) {
                    invitedMembers.add(member);
                }
            }

        } else {
            for (EventMember member: eventMembers) {
                if (member.getUserId() != null && member.getUserId() != 0) {
                    if (!member.getUserId().equals(event.getCreatedById())) {
                        invitedMembers.add(member);
                    }
                } else {
                    nonCenesCounts++;
                }
            }
            if (nonCenesCounts > 0) {
                EventMember nonCenesMember = new EventMember();
                if (nonCenesCounts == 1) {
                    nonCenesMember.setName("and "+nonCenesCounts+" Other");
                } else {
                    nonCenesMember.setName("and "+nonCenesCounts+" Others");
                }
                invitedMembers.add(nonCenesMember);
            }
        }

        tvInvitedBubbleText.setText(eventMembers.size()+"");
        gatheringGuestListItemAdapter = new GatheringGuestListItemAdapter(this, invitedMembers);
        lvGuestListView.setAdapter(gatheringGuestListItemAdapter);

        acceptedMembers = new ArrayList<>();
        //Checking for host which should be at the top
        for (EventMember member: eventMembers) {
            if (member.getUserId() != null && member.getUserId().equals(event.getCreatedById())) {
                acceptedMembers.add(member);
            }
        }
        for (EventMember member: eventMembers) {
            if (member.getStatus() != null && member.getStatus().equals("Going") && !member.getUserId().equals(event.getCreatedById())) {
                acceptedMembers.add(member);
            }
        }
        tvAcceptedBubbleText.setText(acceptedMembers.size()+"");

        declinedMembers = new ArrayList<>();
        for (EventMember member: eventMembers) {
            if (member.getStatus() != null && member.getStatus().equals("NotGoing")) {
                declinedMembers.add(member);
            }
        }
        tvDeclinedBubbleText.setText(declinedMembers.size()+"");

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_invited_bubble:

                    rlInvitedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_white_blue_border));
                    rlAcceptedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_light_grey));
                    rlDeclinedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_light_grey));

                    tvInvitedBubbleText.setTextColor(Color.parseColor("#4A8FE3"));
                    tvAcceptedBubbleText.setTextColor(getResources().getColor(R.color.white));
                    tvDeclinedBubbleText.setTextColor(getResources().getColor(R.color.white));

                    tvInvitedLabel.setTextColor(getResources().getColor(R.color.black));
                    tvAcceptedLabel.setTextColor(Color.parseColor("#d3d3d3"));
                    tvDeclindlabel.setTextColor(Color.parseColor("#d3d3d3"));

                    gatheringGuestListItemAdapter = new GatheringGuestListItemAdapter(GatheringGuestListFragment.this, invitedMembers);
                    lvGuestListView.setAdapter(gatheringGuestListItemAdapter);

                    break;

                case R.id.rl_accepted_bubble:

                    rlAcceptedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_white_blue_border));
                    rlInvitedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_light_grey));
                    rlDeclinedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_light_grey));

                    tvInvitedBubbleText.setTextColor(getResources().getColor(R.color.white));
                    tvAcceptedBubbleText.setTextColor(Color.parseColor("#4A8FE3"));
                    tvDeclinedBubbleText.setTextColor(getResources().getColor(R.color.white));

                    tvInvitedLabel.setTextColor(Color.parseColor("#d3d3d3"));
                    tvAcceptedLabel.setTextColor(getResources().getColor(R.color.black));
                    tvDeclindlabel.setTextColor(Color.parseColor("#d3d3d3"));

                    gatheringGuestListItemAdapter = new GatheringGuestListItemAdapter(GatheringGuestListFragment.this, acceptedMembers);
                    lvGuestListView.setAdapter(gatheringGuestListItemAdapter);

                    break;

                case R.id.rl_declined_bubble:

                    rlDeclinedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_white_blue_border));
                    rlInvitedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_light_grey));
                    rlAcceptedBubble.setBackground(getResources().getDrawable(R.drawable.xml_circle_light_grey));

                    tvInvitedBubbleText.setTextColor(getResources().getColor(R.color.white));
                    tvAcceptedBubbleText.setTextColor(getResources().getColor(R.color.white));
                    tvDeclinedBubbleText.setTextColor(Color.parseColor("#4A8FE3"));

                    tvInvitedLabel.setTextColor(Color.parseColor("#d3d3d3"));
                    tvAcceptedLabel.setTextColor(Color.parseColor("#d3d3d3"));
                    tvDeclindlabel.setTextColor(getResources().getColor(R.color.black));

                    gatheringGuestListItemAdapter = new GatheringGuestListItemAdapter(GatheringGuestListFragment.this, declinedMembers);
                    lvGuestListView.setAdapter(gatheringGuestListItemAdapter);
                    break;

                case R.id.iv_close_guest_list:
                    ((CenesBaseActivity)getActivity()).onBackPressed();
                    break;

                    default:
                        System.out.println("Default");
            }
        }
    };
}
