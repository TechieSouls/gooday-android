package com.deploy.fragment.gathering;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.RotateAnimation;
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

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

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
    private ImageView invitationAcceptSpinner, invitationRejectSpinner;

    private CenesApplication cenesApplication;
    private VelocityTracker mVelocityTracker = null;
    private User loggedInUser;
    public Event event;
    public List<Event> pendingEvents;
    private EventMember eventOwner, loggedInUserAsEventMember;
    private boolean enableLeftToRightSwipe, enableRightToLeftSwipe;
    private boolean isNewOrEditMode;
    private int pendingEventIndex;
    int windowWidth, windowHeight;
    int screenCenter;
    int xCord, yCord, newXcord, newYCord;
    float x, y;
    boolean leftPartClicked, bottomBarClicked;
    boolean ifSwipedLeftToRight, ifSwipedRightToLeft, ifSwipedUp;
    boolean cardSwipedToExtent;
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
        invitationAcceptSpinner = (ImageView) view.findViewById(R.id.iv_invitation_accept_spinner);
        invitationRejectSpinner = (ImageView) view.findViewById(R.id.iv_invitation_decline_spinner);

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

        invitationAcceptSpinner.setVisibility(View.GONE);
        invitationRejectSpinner.setVisibility(View.GONE);

        ivEventPicture.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CenesUtils.dpToPx(getActivity().getWindowManager().getDefaultDisplay().getHeight())));

        if (pendingEvents != null && pendingEvents.size() > 0) {
            event =  pendingEvents.get(0);
            pendingEventIndex++;
        }

        if (event != null) {

            populateInvitationCard(event);
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

            tinderCardView.setX(0);
            tinderCardView.setY(0);

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:

                    x = (int) event.getX();
                    y = (int) event.getY();
                    xCord = 0;
                    tinderCardView.setX(0);
                    tinderCardView.setY(0);
                    tinderCardView.setRotation(0);
                    ifSwipedRightToLeft = false;

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

                    cardSwipedToExtent = false;

                    break;

                case MotionEvent.ACTION_UP:

                    if (ifSwipedRightToLeft) {


                        if (cardSwipedToExtent) {
                            tinderCardView.setRotation(-20);
                            tinderCardView.setX(-300);
                        } else {
                            tinderCardView.setRotation(0);
                        }
                        if (isNewOrEditMode) {

                            if (Math.abs(xCord - x) > 300) {
                                tinderCardView.setX(-300);
                                tinderCardView.setRotation(-20);

                            } else {
                                //tinderCardView.setX(newXcord);
                                tinderCardView.setX(0);
                                tinderCardView.setY(0);
                                tinderCardView.setRotation(0);
                            }
                            //tinderCardView.setY(newYCord);

                        } else {

                            if (Math.abs(newXcord) > 300) {
                                tinderCardView.setX(-300);
                                tinderCardView.setRotation(-20);

                            }
                            //tinderCardView.setY(newYCord);
                        }
                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        cardSwipedToExtent = false;
                        //ivAcceptSendIcon.setVisibility(View.GONE);
                        //ivEditRejectIcon.setVisibility(View.GONE);
                        if (pendingEvents != null && pendingEvents.size() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cardSwipedToExtent = false;
                                    tinderCardView.setX(0);
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0.0f);
                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);
                        }


                    } else if (ifSwipedLeftToRight) {

                        if (newXcord > 300) {
                            tinderCardView.setX(300);
                        } else {
                            tinderCardView.setX(newXcord);
                        }
                        tinderCardView.setRotation(20);
                        //tinderCardView.setY(newYCord);
                        ifSwipedLeftToRight = false;
                        ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        //ivAcceptSendIcon.setVisibility(View.GONE);
                        //ivEditRejectIcon.setVisibility(View.GONE);

                        if (pendingEvents != null && pendingEvents.size() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cardSwipedToExtent = false;
                                    tinderCardView.setX(0);
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0.0f);
                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);
                        }


                    } else if (ifSwipedUp) {

                        //ifSwipedLeftToRight = false;
                        //ifSwipedRightToLeft = false;
                        ifSwipedUp = false;
                        tinderCardView.setRotation(0);
                        tinderCardView.setY(-400);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tinderCardView.setX(0);
                                tinderCardView.setY(-windowHeight + 400);
                            }
                        }, 500);

                        if (pendingEvents != null && pendingEvents.size() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tinderCardView.setY(0);
                                    tinderCardView.setRotation(0);

                                    populateInvitationCard((GatheringPreviewFragment.this).event);
                                }
                            }, 500);
                        }

                    } else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                tinderCardView.setX(0);
                                tinderCardView.setY(0);
                                tinderCardView.setRotation(0);
                            }
                        }, 500);


                    }
                    bottomBarClicked = false;
                    leftPartClicked = false;
                     //}

                    break;



                case MotionEvent.ACTION_MOVE:

                    xCord = (int) event.getRawX();
                    yCord = (int) event.getRawY();

                    newXcord = (int)(xCord - x);
                    //newYCord = (int)(yCord - y);

                    Log.d("xCord : yCord : ",(xCord - x)+""+(yCord - y));
                    if (enableLeftToRightSwipe && enableRightToLeftSwipe && !ifSwipedUp) {

                        if (Math.abs(newXcord) < 300) {

                            tinderCardView.setX(newXcord);
                            tinderCardView.setY(newYCord);

                        } else {

                            if (newXcord < 0) {

                                tinderCardView.setX(-300);
                                cardSwipedToExtent = true;

                            } else if (newXcord > 0) {

                                tinderCardView.setX(300);
                                cardSwipedToExtent = true;

                            }

                        }

                    } else if (enableLeftToRightSwipe && !enableRightToLeftSwipe && !ifSwipedUp) {

                        if (enableLeftToRightSwipe && newXcord > 0) {

                            if (Math.abs(xCord - x) < 300 && Math.abs(newYCord) < 100) {

                                tinderCardView.setX(newXcord);
                                tinderCardView.setY(newXcord);

                            } else {
                                    tinderCardView.setX(300);
                                    cardSwipedToExtent = true;
                                    ifSwipedRightToLeft = true;
                            }


                        } else if (newYCord < 0) {

                            //if (Math.abs(yCord - y) < 300) {

                                tinderCardView.setX(0);
                                tinderCardView.setY(yCord - y);

                            //}

                        } else {

                            tinderCardView.setX(0);
                            //tinderCardView.setY(0);

                        }

                    } else if (!enableLeftToRightSwipe && enableRightToLeftSwipe && !ifSwipedUp) {

                        Log.d("RightToLeftSwipe : ", newXcord+" newYCord : "+newYCord);
                        if (enableRightToLeftSwipe && newXcord < 0) {


                                if (Math.abs(newXcord) < 300 ) {

                                    Log.d("Right To Left Sipe : ",(newXcord)+"  :   "+(yCord - y));
                                    tinderCardView.setX(newXcord);
                                    tinderCardView.setY(newYCord);
                                    ifSwipedRightToLeft = true;

                                } else {

                                    if (Math.abs(newYCord) < 200) {

                                        tinderCardView.setX(-300);
                                        cardSwipedToExtent = true;

                                    }
                                    ifSwipedRightToLeft = false;

                                }

                        }  else if (newYCord < 0) {

                                tinderCardView.setX(0);
                                tinderCardView.setY(yCord - y);

                        } else {

                            tinderCardView.setX(0);
                            tinderCardView.setY(0);
                            tinderCardView.setRotation(0);

                        }
                    }

                    Log.v("X And Xcord : ", (xCord - x)+" Screen Center : "+screenCenter);
                    if ((xCord - x) > 100 && enableLeftToRightSwipe && !ifSwipedUp) {
                        //If User swipe from left to right

                        if ((float)((xCord - x)/2 *  (Math.PI/32)) < 15) {
                            tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));
                        }
                        ifSwipedLeftToRight = true;
                        if ((xCord - x) > 200) {


                            if (isNewOrEditMode) {

                                if (!cardSwipedToExtent) {
                                    cardSwipedToExtent = true;

                                    createGathering();
                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    ((CenesBaseActivity) getActivity()).replaceFragment(new HomeFragment(), null);

                                }
                            } else {

                                Log.v("Swipe Cords : ",(xCord - x)+" ===  "+(screenCenter - 150));
                                    if (!cardSwipedToExtent) {
                                        cardSwipedToExtent = true;
                                        invitationAcceptSpinner.setVisibility(View.VISIBLE);
                                        rotate(360, invitationAcceptSpinner);

                                        String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=Going";
                                        updateAttendingStatus(queryStr);

                                        if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                                            (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                                            pendingEventIndex++;

                                        } else {

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                                                }
                                            }, 500);
                                        }

                                    }
                            }

                        } else {
                            ifSwipedLeftToRight = true;
                        }

                    } else if ((xCord - x) < -100 && enableRightToLeftSwipe && !ifSwipedUp) {
                        //If User swipe from right to left
                        ifSwipedRightToLeft = false;

                        if ((float)((xCord - x)/2 *  (Math.PI/32)) > -15) {
                            tinderCardView.setRotation((float)((xCord - x)/2 *  (Math.PI/32)));

                        }

                        if (Math.abs(xCord - x) > 200) {

                            ifSwipedRightToLeft = true;

                            if (isNewOrEditMode) {

                                //((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();

                            } else {

                                Log.v("Swipe Cords : ",Math.abs(xCord - x)+" ===  "+(screenCenter - 150));
                                if (!cardSwipedToExtent) {
                                    cardSwipedToExtent = true;

                                    invitationRejectSpinner.setVisibility(View.VISIBLE);
                                    rotate(360, invitationRejectSpinner);

                                    String queryStr = "eventId="+GatheringPreviewFragment.this.event.getEventId()+"&userId="+loggedInUser.getUserId()+"&status=NotGoing";
                                    updateAttendingStatus(queryStr);

                                    if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                                        (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                                        pendingEventIndex++;

                                    } else {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                                            }
                                        }, 500);

                                    }

                                }

                            }
                        } else {
                            ifSwipedRightToLeft = true;
                        }
                    }


                    //This is when user move the card up
                    Log.d("Y Cords : ",Math.abs(yCord  - y)+"");
                    if ((yCord - y) < 0) {
                        tinderCardView.setY(yCord - y);
                        tinderCardView.setX(0);

                        if ((yCord - y) > -100) {
                            tinderCardView.setRotation(0);
                        }

                    }
                    if ((yCord - y) < -300) {
                        //ivAcceptSendIcon.setAlpha(0.0f);
                        //rlSkipText.setAlpha(1.0f);
                        Log.e("Skip Text : ", rlSkipText.getScaleX()+"");
                        ifSwipedUp = true;
                        if (!cardSwipedToExtent) {
                            cardSwipedToExtent = true;

                            if (pendingEvents != null && pendingEvents.size() > 0 && pendingEventIndex < pendingEvents.size()) {

                                (GatheringPreviewFragment.this).event = pendingEvents.get(pendingEventIndex);
                                pendingEventIndex++;

                            } else {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((CenesBaseActivity) getActivity()).getSupportFragmentManager().popBackStack();
                                    }
                                }, 500);

                            }

                        }

                    } else {
                        ifSwipedUp = false;
                        //rlSkipText.setAlpha(0.0f);
                    }

                    break;

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

    private void rotate(float degree, ImageView imageView) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(500);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }

    public void populateInvitationCard(final Event event) {
        tvEventTitle.setText(event.getTitle());

        String eventDate = CenesUtils.EEEMMMMdd.format(new Date(event.getStartTime())) + "," + CenesUtils.hmmaa.format(new Date(event.getStartTime())) + "-" + CenesUtils.hmmaa.format(new Date(event.getEndTime()));
        tvEventDate.setText(eventDate);
        if (!CenesUtils.isEmpty(event.getEventPicture())) {
            if (((CenesBaseActivity)getActivity()) != null) {
                Glide.with(getContext()).load(event.getEventPicture());
                Glide.with(getContext()).load(event.getEventPicture()).into(ivEventPicture);
            }
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


        if (event.getEventMembers() != null && event.getEventMembers().size() > 0) {

            for (EventMember eventMember : event.getEventMembers()) {

                if (eventMember.getUserId() != null && eventMember.getUserId().equals(event.getCreatedById())) {
                    eventOwner = eventMember;
                    break;
                }
            }

            for (EventMember eventMember : event.getEventMembers()) {
                if (eventMember.getUserId() != null && eventMember.getUserId().equals(loggedInUser.getUserId())) {
                    loggedInUserAsEventMember = eventMember;
                    break;
                }
            }
        }

        if (eventOwner != null) {

            if (eventOwner.getUser() != null && !CenesUtils.isEmpty(eventOwner.getUser().getPicture())) {

                if (((CenesBaseActivity)getActivity()) != null) {

                    Glide.with(getContext()).load(eventOwner.getUser().getPicture()).apply(RequestOptions.centerCropTransform()).into(ivProfilePicView);

                }

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
            if (eventOwner != null && eventOwner.getUserId() != null && eventOwner.getUser().getUserId() == loggedInUser.getUserId()) {

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
                if (loggedInUserAsEventMember != null) {
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
        }

        tinderCardView.setX(0);
        tinderCardView.setY(0);
        ivAcceptSendIcon.setVisibility(View.VISIBLE);
        ivEditRejectIcon.setVisibility(View.VISIBLE);

    }
}
