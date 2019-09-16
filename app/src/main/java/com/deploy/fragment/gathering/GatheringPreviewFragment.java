package com.deploy.fragment.gathering;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.AsyncTasks.GatheringAsyncTask;
import com.deploy.R;
import com.deploy.activity.CenesBaseActivity;
import com.deploy.application.CenesApplication;
import com.deploy.bo.Event;
import com.deploy.bo.EventMember;
import com.deploy.bo.User;
import com.deploy.coremanager.CoreManager;
import com.deploy.database.manager.UserManager;
import com.deploy.fragment.CenesFragment;
import com.deploy.fragment.dashboard.HomeFragment;
import com.deploy.util.CenesConstants;
import com.deploy.util.CenesUtils;
import com.deploy.util.RoundedImageView;
import com.google.gson.Gson;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONObject;

import java.util.Date;

public class GatheringPreviewFragment extends CenesFragment {

    private static String TAG = "GatheringPreviewFragment";

    private ImageView ivEventPicture, ivAcceptSendIcon, ivEditRejectIcon, ivDeleteIcon;
    private TextView tvEventTitle, tvEventDate, tvEventDescriptionDialogText;
    private RelativeLayout rlGuestListBubble, rlLocationBubble, rlDescriptionBubble, rlShareBubble;
    private RelativeLayout rvEventDescriptionDialog;
    private RelativeLayout rlDescriptionBubbleBackground;
    private ImageView ivDescriptionBubbleIcon;
    private CardView tinderCardView;
    private RelativeLayout rlParentVew, rlSkipText;
    private RoundedImageView ivProfilePicView;
    private SwipeFlingAdapterView flingContainer;

    private CenesApplication cenesApplication;
    private VelocityTracker mVelocityTracker = null;
    private User loggedInUser;
    public Event event;
    private EventMember eventOwner, loggedInUserAsEventMember;
    private boolean enableLeftToRightSwipe, enableRightToLeftSwipe;
    private boolean isNewOrEditMode;
    int windowWidth, windowHeight;
    int screenCenter;
    int xCord, yCord, newXcord, newYCord;
    float x, y;
    boolean leftPartClicked, bottomBarClicked;
    boolean ifSwipedLeftToRight, ifSwipedRightToLeft, ifSwipedUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_gathering_preview, container, false);

        ivProfilePicView = (RoundedImageView) view.findViewById(R.id.iv_profile_pic);
        ivEventPicture = (ImageView) view.findViewById(R.id.iv_event_picture);
        tvEventTitle = (TextView) view.findViewById(R.id.tv_event_title);
        tvEventDate = (TextView) view.findViewById(R.id.tv_event_date);
        tvEventDescriptionDialogText = (TextView) view.findViewById(R.id.tv_event_description_dialog_text);

        rlGuestListBubble = (RelativeLayout) view.findViewById(R.id.rl_guest_list_bubble);
        rlLocationBubble = (RelativeLayout) view.findViewById(R.id.rl_location_bubble);
        rlDescriptionBubble = (RelativeLayout) view.findViewById(R.id.rl_description_bubble);
        rlShareBubble = (RelativeLayout) view.findViewById(R.id.rl_share_bubble);

        rlSkipText = (RelativeLayout) view.findViewById(R.id.rl_skip_text);
        rvEventDescriptionDialog = (RelativeLayout) view.findViewById(R.id.rv_event_description_dialog);
        rlDescriptionBubbleBackground = (RelativeLayout) view.findViewById(R.id.rl_description_bubble_background);
        ivDescriptionBubbleIcon = (ImageView) view.findViewById(R.id.iv_description_bubble_icon);

        ivAcceptSendIcon = (ImageView) view.findViewById(R.id.iv_accept_icon);
        ivEditRejectIcon = (ImageView) view.findViewById(R.id.iv_edit_reject_icon);
        ivDeleteIcon = (ImageView) view.findViewById(R.id.iv_delete_icon);



        tinderCardView = (CardView) view.findViewById(R.id.tinderCardView);
        rlParentVew = (RelativeLayout) view.findViewById(R.id.rl_parent_vew);

        rlGuestListBubble.setOnClickListener(onClickListener);
        rlLocationBubble.setOnClickListener(onClickListener);
        rlDescriptionBubble.setOnClickListener(onClickListener);
        rlShareBubble.setOnClickListener(onClickListener);
        ivDeleteIcon.setOnClickListener(onClickListener);
        ivEditRejectIcon.setOnClickListener(onClickListener);
        tinderCardView.setOnTouchListener(onTouchListener);

        ((CenesBaseActivity)getActivity()).hideFooter();

        cenesApplication = getCenesActivity().getCenesApplication();
        CoreManager coreManager = cenesApplication.getCoreManager();
        UserManager userManager = coreManager.getUserManager();
        loggedInUser = userManager.getUser();

        windowWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        windowHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        screenCenter = windowWidth / 2;


        ivEventPicture.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CenesUtils.dpToPx(getActivity().getWindowManager().getDefaultDisplay().getHeight())));
        if (event != null) {

            tvEventTitle.setText(event.getTitle());

            String eventDate = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime()))+","+CenesUtils.hmmaa.format(new Date(event.getStartTime()))+"-"+CenesUtils.hmmaa.format(new Date(event.getEndTime()));
            tvEventDate.setText(eventDate);
            if (!CenesUtils.isEmpty(event.getEventPicture())) {
                Glide.with(getContext()).load(event.getThumbnail()).into(ivEventPicture);
                Glide.with(getContext()).load(event.getEventPicture()).into(ivEventPicture);
            }


            if (event.getEventId() != null) {
                if (event.isEditMode()) {
                    ivAcceptSendIcon.setImageResource(R.drawable.invitation_send_button);
                } else {
                    ivAcceptSendIcon.setImageResource(R.drawable.invitation_accept_button);
                }
            } else {
                ivAcceptSendIcon.setImageResource(R.drawable.invitation_send_button);
            }
        }

        if (event.getEventMembers() != null && event.getEventMembers().size() > 0) {

            for (EventMember eventMember: event.getEventMembers()) {

                if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                    eventOwner = eventMember;
                    break;
                }
            }

            for (EventMember eventMember: event.getEventMembers()) {
                if (eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId())) {
                    loggedInUserAsEventMember = eventMember;
                    break;
                }
            }
        }

        if (eventOwner != null) {

            if (eventOwner.getUser() != null && !CenesUtils.isEmpty(eventOwner.getUser().getPicture())) {

                Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(RequestOptions.centerCropTransform()).into(ivProfilePicView);

            } else {

                ivProfilePicView.setImageResource(R.drawable.profile_pic_no_image);
            }
        }


        if (event.getEventId() == null) {
            enableLeftToRightSwipe = true;
            enableRightToLeftSwipe = true;
            isNewOrEditMode = true;
        } else {

            //If Logged In User is the owner of the event
            if (eventOwner.getUserId() != null && eventOwner.getUser().getUserId() == loggedInUser.getUserId()) {

                isNewOrEditMode = true;
                if (event.isEditMode()) {
                    enableLeftToRightSwipe = true;
                } else {
                    //User cannot accept or send invitation
                    enableLeftToRightSwipe = false;
                }

                //User Can edit or delete the invitation
                enableRightToLeftSwipe = true;
                ivEditRejectIcon.setImageResource(R.drawable.invitation_edit_button);
                ivDeleteIcon.setVisibility(View.VISIBLE);

            } else {

                isNewOrEditMode = false;
                ivEditRejectIcon.setImageResource(R.drawable.invitation_decline_button);
                ivDeleteIcon.setVisibility(View.GONE);

                //If this is the event from somebody. Then user can accept decline that event
                if (CenesUtils.isEmpty(loggedInUserAsEventMember.getStatus())) {
                    enableLeftToRightSwipe = true;
                    enableRightToLeftSwipe = true;
                } else {
                    if ("Going".equals(loggedInUserAsEventMember.getStatus())) {

                        enableLeftToRightSwipe = false;
                        enableRightToLeftSwipe = true;

                    } else if ("NotGoing".equals(loggedInUserAsEventMember.getStatus())) {


                        enableLeftToRightSwipe = true;
                        enableRightToLeftSwipe = false;

                    }
                }
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.rl_guest_list_bubble:

                    GatheringGuestListFragment gatheringGuestListFragment = new GatheringGuestListFragment();
                    gatheringGuestListFragment.eventMembers = event.getEventMembers();
                    ((CenesBaseActivity)getActivity()).replaceFragment(gatheringGuestListFragment, GatheringPreviewFragment.TAG);
                    break;
                case R.id.rl_location_bubble:

                    hideDescriptionMessage();

                    if (!CenesUtils.isEmpty(event.getLatitude())) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("")
                                .setMessage(event.getLocation())
                                .setPositiveButton("Get Directions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Create a Uri from an intent string. Use the result to create an Intent.
                                        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+event.getLongitude()+","+event.getLongitude()+"");

                                        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        // Make the Intent explicit by setting the Google Maps package
                                        mapIntent.setPackage("com.google.android.apps.maps");

                                        // Attempt to start an activity that can handle the Intent
                                        startActivity(mapIntent);

                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    } else  if (!CenesUtils.isEmpty(event.getLocation())) {

                        new AlertDialog.Builder(getActivity())
                                .setTitle(event.getLocation())
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();

                    } else {

                        new AlertDialog.Builder(getActivity())
                                .setTitle("No Location Selected")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();

                    }
                    break;

                case R.id.rl_description_bubble:

                    if (!CenesUtils.isEmpty(event.getDescription())) {

                        if (rvEventDescriptionDialog.getVisibility() == View.GONE) {

                            ivDescriptionBubbleIcon.setImageResource(R.drawable.message_on_icon);
                            rlDescriptionBubbleBackground.setAlpha(1);
                            rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_white));
                            tvEventDescriptionDialogText.setText(event.getDescription());
                            rvEventDescriptionDialog.setVisibility(View.VISIBLE);
                        } else {
                            hideDescriptionMessage();
                        }

                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Description Not Available.")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();
                    }
                    break;

                case R.id.rl_share_bubble:

                    String name = "Your Friend";
                    if (eventOwner.getName() != null) {
                        name = eventOwner.getName();
                    } else if (eventOwner.getUser() != null && eventOwner.getUser().getName() != null) {
                        name = eventOwner.getUser().getName();
                    }
                    String shrareUrl = name+"invites you to "+event.getTitle()+". RSVP through the Cenes app. Link below: \n";
                    shrareUrl = shrareUrl + CenesConstants.webDomainEventUrl+""+event.getKey();


                    if (event.getEventId() != null) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shrareUrl);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Event cannot be shared this time.")
                                .setMessage("")
                                .setCancelable(false)
                                .setPositiveButton("Ok", null).show();

                    }

                    break;

                case R.id.iv_delete_icon:

                    deleteGathering();
                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                    break;

                case R.id.iv_edit_reject_icon:

                    event.setEditMode(true);
                    CreateGatheringFragment createGatheringFragment = new CreateGatheringFragment();
                    createGatheringFragment.event = event;
                    ((CenesBaseActivity)getActivity()).replaceFragment(createGatheringFragment, null);
                    break;

                    default:
                        System.out.println("Heyyy you did it.");
            }
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int index = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(index);

            xCord = (int)event.getRawX();
            yCord = (int) event.getRawY();

            ivAcceptSendIcon.setAlpha(0.0f);
            tinderCardView.setX(0);
            tinderCardView.setY(0);

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    x = (int) event.getX();
                    y = (int) event.getY();

                    if (x < screenCenter) {
                        leftPartClicked = true;
                    } else {
                        leftPartClicked = false;
                    }
                    if (y > (windowHeight - 200)) {
                        bottomBarClicked = true;
                    }

                    Log.d("Bottom : ", (y > (windowHeight - 100))+"");
                    Log.v("On touch", x + " " + y+ " Screen Center : "+screenCenter);

                    //tinderCardView.setX(0);
                    //tinderCardView.setY(0);
                    //tinderCardView.setRotation(0);
                    if(mVelocityTracker == null) {
                        // Retrieve a new VelocityTracker object to watch the
                        // velocity of a motion.
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    else {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(event);

                    break;

                case MotionEvent.ACTION_UP:
                    //mVelocityTracker.addMovement(event);
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    //mVelocityTracker.computeCurrentVelocity(5000);
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.
                    //Log.d("", "X velocity: " + mVelocityTracker.getXVelocity(pointerId));
                    //Log.d("", "Y velocity: " + mVelocityTracker.getYVelocity(pointerId));

                    //if (!isNewOrEditMode) {

                    if (ifSwipedRightToLeft) {

                        tinderCardView.setX(newXcord);
                        tinderCardView.setY(newYCord);
                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;

                        if (isNewOrEditMode) {

                            //((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                        } else {
                            String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing";
                            updateAttendingStatus(queryStr);
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                        }

                    } else if (ifSwipedLeftToRight) {

                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;

                        if (isNewOrEditMode) {

                            createGathering();
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            ((CenesBaseActivity) getActivity()).replaceFragment(new HomeFragment(), null);

                        } else {

                            String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=Going";
                            updateAttendingStatus(queryStr);
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                        }


                        tinderCardView.setX(0);
                        tinderCardView.setY(0);
                        tinderCardView.setRotation(0);



                    } else if (ifSwipedUp) {

                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        tinderCardView.setX(0);
                        tinderCardView.setY(-windowHeight);
                        tinderCardView.setRotation(0);

                        ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                    } else {

                        tinderCardView.setX(0);
                        tinderCardView.setY(0);
                        tinderCardView.setRotation(0);

                    }
                    rlSkipText.setAlpha(0.0f);
                    bottomBarClicked = false;
                    leftPartClicked = false;
                     //}

                    break;

                case MotionEvent.ACTION_MOVE:
                    xCord = (int) event.getRawX();
                    yCord = (int) event.getRawY();

                    newXcord = (int)(xCord - x);
                    newYCord = (int)(yCord - y);

                    Log.d("xCord : yCord : ",(xCord - x)+""+(yCord - y));
                    if (enableLeftToRightSwipe && enableRightToLeftSwipe) {

                        tinderCardView.setX(xCord - x);
                        tinderCardView.setY(yCord - y);

                    } else if (enableLeftToRightSwipe && !enableRightToLeftSwipe) {

                        if (enableLeftToRightSwipe && newXcord > 0) {

                            tinderCardView.setX(xCord - x);
                            tinderCardView.setY(yCord - y);

                        } else if (newYCord < 0) {

                            tinderCardView.setX(xCord - x);
                            tinderCardView.setY(yCord - y);

                        } else {

                            tinderCardView.setX(0);
                            tinderCardView.setY(0);

                        }

                    } else if (!enableLeftToRightSwipe && enableRightToLeftSwipe) {

                        if (enableRightToLeftSwipe && newXcord < 0) {

                            tinderCardView.setX(xCord - x);
                            tinderCardView.setY(yCord - y);

                        }  else if (newYCord < 0) {

                            tinderCardView.setX(xCord - x);
                            tinderCardView.setY(yCord - y);

                        } else {

                            tinderCardView.setX(0);
                            tinderCardView.setY(0);

                        }
                    }

                    //Log.v("On Move", xCord + " " + yCord+" Rotation : "+(xCord - screenCenter)+""+" X :  "+x+", Y : "+y);

                    //Log.v("On Move", "Send Icon : "+(float)((xCord - x)/2 *  (Math.PI/32))+" xCord -  X Diff : "+(xCord - x)+ " YCord - Y Diff : " +(yCord - y)+" Rotation : "+(xCord - screenCenter)+""+" X :  "+x+", Y : "+y);
                    //Log.v("Alpha Vaklue : ",(float) ((float)(((xCord - x) - screenCenter/2)/screenCenter) * (Math.PI/32))*50+"");

                   Log.v("X And Xcord : ", (xCord - x)+" Screen Center : "+screenCenter);
                    if ((xCord - x) > 100 && enableLeftToRightSwipe) {
                        //If User swipe from left to right

                        ivAcceptSendIcon.setAlpha((float) ((float)(((xCord - x) - screenCenter/2)/screenCenter) * (Math.PI/32))*50);

                        if ((xCord - x) < (screenCenter - 200)) {

                            if ((float)((xCord - x)/2 *  (Math.PI/32)) < 15) {
                                tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));
                            }

                        } else {
                            ifSwipedLeftToRight = true;
                        }

                    } else if ((xCord - x) < -100 && enableRightToLeftSwipe) {
                        //If User swipe from right to left

                        ivDeleteIcon.setAlpha(Math.abs((float) ((float)(((xCord - x) - screenCenter/2)/screenCenter) * (Math.PI/32))*50));
                        ivEditRejectIcon.setAlpha(Math.abs((float) ((float)(((xCord - x) - screenCenter/2)/screenCenter) * (Math.PI/32))*50));

                        if (Math.abs(xCord - x) < (screenCenter - 200)) {

                            if ((float)((xCord - x)/2 *  (Math.PI/32)) > -15) {
                                tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));

                                ifSwipedRightToLeft = false;
                            }
                        } else {
                            ifSwipedRightToLeft = true;
                        }
                    }

                    if ((xCord - x) > (screenCenter/2)) {
                        //ivAcceptSendIcon.setAlpha(1.0f);
                        rlSkipText.setAlpha(0.0f);
                    }

                    if (Math.abs(yCord  - y) > windowHeight/4) {
                        ivAcceptSendIcon.setAlpha(0.0f);
                        rlSkipText.setAlpha(1.0f);
                        ifSwipedUp = true;
                    } else {
                        ifSwipedUp = false;
                        rlSkipText.setAlpha(0.0f);
                    }
                    /*if (!bottomBarClicked) {

                        if (leftPartClicked) {

                            tinderCardView.setX((float)(Math.abs(xCord - x) + (Math.PI/32)));
                            tinderCardView.setRotation((float) ((xCord - screenCenter/2) * (Math.PI/32)));

                        } else {

                            System.out.println(xCord - windowWidth);
                            tinderCardView.setX(-(float)(Math.abs(xCord - windowWidth) + (Math.PI/32)));
                            tinderCardView.setY((float)(Math.abs(yCord - windowHeight) * (Math.PI/32)));

                            tinderCardView.setRotation(-(float)(Math.abs(xCord - x) * (Math.PI/32)));
                        }

                    } else {

                        tinderCardView.setRotation(0);
                        tinderCardView.setY((yCord - windowHeight)/2);
                        rlSkipText.setAlpha((float)(Math.abs(y-yCord) * (Math.PI)));
                    }*/
                        /*if (y <  (windowHeight - 100)) {
                        //If the swipe is greater than center
                        if (x < (screenCenter) && enableLeftToRightSwipe) {


                            ivAcceptSendIcon.setAlpha((float)xCord/screenCenter);
                            Log.v("xFromCenter", xCord+" "+screenCenter+" "+(float)xCord/screenCenter+"");
                            if (xCord > (screenCenter + 150)) {
                                enableLeftToRightSwipe = false;

                                if (isNewOrEditMode) {

                                    createGathering();
                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    ((CenesBaseActivity) getActivity()).replaceFragment(new HomeFragment(), null);

                                } else {

                                    String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=Going";
                                    updateAttendingStatus(queryStr);
                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                                }

                            } else {
                                System.out.println("xCord : "+xCord+" screenCenter : "+screenCenter/2 +"  Math.PI : "+Math.PI+" ROTATION : "+(float) ((xCord - screenCenter/2) * (Math.PI/32)));
                                tinderCardView.setRotation(20);
                            }

                        } else if (x > (screenCenter) && enableRightToLeftSwipe) {

                            //If xcord is in center and is less than screensize i.e move more away from screen center.
                            if ((xCord/2) < (screenCenter/2 - 100)) {

                                if (isNewOrEditMode) {

                                    //((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                                } else {
                                    String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing";
                                    updateAttendingStatus(queryStr);
                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                                }
                                tinderCardView.setY(yCord);
                                tinderCardView.setX(xCord);
                            }
                            System.out.println("xCord : "+xCord/2+" screenCenter : "+screenCenter +" Math.PI : "+Math.PI+" ROTATION : "+(-(float) (Math.abs(xCord - screenCenter) * (Math.PI/32))));
                            if ((float) (Math.abs(xCord - screenCenter) * (Math.PI/32)) < 30) {
                                tinderCardView.setRotation(-(float) (Math.abs(xCord - screenCenter) * (Math.PI/32)));
                            }

                        }
                    } else {


                        if (yCord > (windowHeight - 50)) {
                            ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                        }
                        //tinderCardView.setX(xCord);
                        System.out.println("screenCenter : "+screenCenter+",  windowHeight : "+windowHeight+", yCord : "+yCord+"  - ------ "+(windowHeight - yCord)/2);
                        tinderCardView.setY((yCord - windowHeight)/2);

                    }*/

                    break;
                /*case MotionEvent.ACTION_MOVE:

                    xCord = (int) event.getRawX();
                    yCord = (int) event.getRawY();

                    System.out.println("screenCenter : "+screenCenter+" , X Cord : "+xCord+ ", Y Cord : "+yCord);

                    tinderCardView.setX(x);
                    ///tinderCardView.setY(yCord - y);
                    tinderCardView.setRotation((float) ((xCord - screenCenter) * (Math.PI / 32)));

                    break;

                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        tinderCardView.setX(0);
                        tinderCardView.setY(0);
                        break;*/

                default:

                    System.out.println("Default");
            }


            return true;
        }
    };

    public void hideDescriptionMessage() {
        rvEventDescriptionDialog.setVisibility(View.GONE);
        rlDescriptionBubbleBackground.setAlpha(0.3f);
        ivDescriptionBubbleIcon.setImageResource(R.drawable.message_off_icon);
        rlDescriptionBubbleBackground.setBackground(getResources().getDrawable(R.drawable.xml_circle_black_faded));
    }


    public void createGathering() {

        //If its the create event requiest, then we will remove event owner from the
        //event members list. Lets server handle the owner as event member
        if (event.getEventId() == null) {
            for (EventMember eventMember: event.getEventMembers()) {
                if (eventMember.getUserId() != null && eventMember.getUserId() == loggedInUser.getUserId()) {
                    event.getEventMembers().remove(eventMember);
                    break;
                }
            }
        }
        System.out.println("Going to create Gathering.");
        Gson gson = new Gson();
        try {
            JSONObject postata = new JSONObject(gson.toJson(event));

            new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
            new GatheringAsyncTask.CreateGatheringTask(new GatheringAsyncTask.CreateGatheringTask.AsyncResponse() {
                @Override
                public void processFinish(JSONObject response) {

                }
            }).execute(postata);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAttendingStatus(String queryStr) {

        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
        new GatheringAsyncTask.UpdateStatusActionTask(new GatheringAsyncTask.UpdateStatusActionTask.AsyncResponse() {
            @Override
            public void processFinish(Boolean response) {

            }
        }).execute(queryStr);
    }

    public void deleteGathering() {

        new GatheringAsyncTask(cenesApplication, (CenesBaseActivity)getActivity());
        new GatheringAsyncTask.DeleteGatheringTask(new GatheringAsyncTask.DeleteGatheringTask.AsyncResponse() {
            @Override
            public void processFinish(JSONObject response) {

            }
        }).execute(event.getEventId());

    }
}
