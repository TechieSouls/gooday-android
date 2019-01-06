package com.deploy.adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deploy.R;
import com.deploy.activity.SearchFriendActivity;
import com.deploy.service.SearchFriendService;
import com.deploy.util.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by mandeep on 11/9/17.
 */

public class SearchFriendAdapter extends BaseAdapter {

    private SearchFriendActivity myActivity;
    private JSONArray friends;
    LayoutInflater inflter;
    private FriendsAdapter mFriendsAdapter;
    private RecyclerView recyclerView;

    public SearchFriendAdapter(SearchFriendActivity context, JSONArray friends) {
        this.myActivity = context;
        this.friends = friends;
        this.inflter = (LayoutInflater.from(context));
        recyclerView = (RecyclerView) this.myActivity.findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return friends.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return this.friends.getJSONObject(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final FriendViewHolder holder;

        if (view == null) {
            holder = new FriendViewHolder();
            view = this.inflter.inflate(R.layout.adapter_search_friends, null);
            holder.inviteFriendName = (TextView) view.findViewById(R.id.invite_friend_name);
            holder.inviteFriendPicture = (RoundedImageView) view.findViewById(R.id.invite_friend_picture);
            holder.cenesMemberIcon = (CheckBox) view.findViewById(R.id.iv_cenes_member_icon);
            holder.inviteFriendNameCenesUserText = (TextView) view.findViewById(R.id.invite_friend_name_cenes_user_text);
            view.setTag(holder);

        } else {
            holder = (FriendViewHolder) view.getTag();
        }

        JSONObject friendObj = null;
        try {
            friendObj = friends.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.inviteFriendName.setText(friends.getJSONObject(position).getString("name"));
           /* if (friends.getJSONObject(position).getString("photo") != null && friends.getJSONObject(position).getString("photo") != "null") {
                Glide.with(myActivity).load(friends.getJSONObject(position).getString("photo")).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.inviteFriendPicture);
            } else {
                holder.inviteFriendPicture.setImageResource(R.drawable.default_profile_icon);
            }*/
            String photo = null;
            if (friends.getJSONObject(position).getString("user") != "null") {
                photo = friends.getJSONObject(position).getJSONObject("user").getString("photo");
            }

            //if (friends.getJSONObject(position).getString("photo") != null && friends.getJSONObject(position).getString("photo") != "null") {
            if (photo != null) {
                Glide.with(myActivity).load(photo).apply(RequestOptions.placeholderOf(R.drawable.default_profile_icon)).into(holder.inviteFriendPicture);
            } else {
                holder.inviteFriendPicture.setImageResource(R.drawable.default_profile_icon);
            }
           // System.out.println("cenesMember : "+ friends.getJSONObject(position).getString("cenesMember"));
            if ("yes".equals(friends.getJSONObject(position).getString("cenesMember"))) {
                //holder.inviteFriendNameCenesUserText.setVisibility(View.VISIBLE);
                //holder.cenesMemberIcon.setVisibility(View.GONE);
            } else {
                //holder.inviteFriendNameCenesUserText.setVisibility(View.GONE);
                //holder.cenesMemberIcon.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            holder.cenesMemberIcon.setOnCheckedChangeListener(null);
            holder.cenesMemberIcon.setTag(position);
            myActivity.checkboxButtonHolder.put(friendObj.getInt("userContactId"), holder.cenesMemberIcon);
            if (myActivity.checkboxStateHolder.containsKey(friendObj.getInt("userContactId"))) {
                if (myActivity.checkboxStateHolder.get(friendObj.getInt("userContactId"))){
                    holder.cenesMemberIcon.setSelected(true);
                    holder.cenesMemberIcon.setChecked(true);
                    holder.cenesMemberIcon.setButtonDrawable(R.drawable.circle_selected);
                } else {
                    holder.cenesMemberIcon.setButtonDrawable(R.drawable.circle_unselected);
                    holder.cenesMemberIcon.setSelected(false);
                    holder.cenesMemberIcon.setChecked(false);
                }
            } else {
                holder.cenesMemberIcon.setButtonDrawable(R.drawable.circle_unselected);
                holder.cenesMemberIcon.setSelected(false);
                holder.cenesMemberIcon.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.cenesMemberIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JSONObject friendObj = null;
                Integer userContactId = null;
                try {
                    friendObj = friends.getJSONObject((Integer)holder.cenesMemberIcon.getTag());
                    userContactId = friendObj.getInt("userContactId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    if (!isChecked) {
                        myActivity.checkboxStateHolder.remove(userContactId);
                        myActivity.checkboxObjectHolder.remove(userContactId);
                        holder.cenesMemberIcon.setButtonDrawable(R.drawable.circle_unselected);
                    } else {
                        myActivity.checkboxStateHolder.put(userContactId, isChecked);
                        myActivity.checkboxObjectHolder.put(userContactId, friendObj);
                        holder.cenesMemberIcon.setButtonDrawable(R.drawable.circle_selected);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (myActivity.checkboxObjectHolder.size() > 0) {
                    myActivity.findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.VISIBLE);
                    myActivity.runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {


                           List<Map<String, String>> jsonObjectArrayList = SearchFriendService.getListOfMapFromJsonList(myActivity.checkboxObjectHolder.values());
                            mFriendsAdapter = new FriendsAdapter(myActivity, jsonObjectArrayList, recyclerView);
                            mFriendsAdapter.notifyDataSetChanged();
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(myActivity, LinearLayoutManager.HORIZONTAL, false);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mFriendsAdapter);
                            recyclerView.invalidate();
                        }
                    }));

                } else {
                    myActivity.findViewById(R.id.rl_selected_friends_recycler_view).setVisibility(View.GONE);
                }

                System.out.println(myActivity.checkboxStateHolder.toString());
            }
        });
        return view;
    }

    class FriendViewHolder {
        private TextView inviteFriendName,inviteFriendNameCenesUserText;
        private RoundedImageView inviteFriendPicture;
        private CheckBox cenesMemberIcon;
    }

    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) myActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
