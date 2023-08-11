package com.example.appchat.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.appchat.Object.Friend;
import com.example.appchat.Adapter.DeXuat_AD;
import com.example.appchat.Adapter.add_friend;
import com.example.appchat.Object.Account;
import com.example.appchat.Object.User;
import com.example.appchat.databinding.FragmentFriendBinding;
import com.example.appchat.variable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.appchat.Adapter.Friend_AD;
import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment
{

    FragmentFriendBinding friendBinding;
    TabLayout tab;
    Context context;
    DeXuat_AD dx ; // đề xuất adapter
    add_friend add_friend; // yêu cầu kết bạn adapter

    Friend_AD friend_ad ; // friend adapter
    public DatabaseReference getLUser; // dùng để lấy ds user đề xuất
    public  List<User> LUser; // Ds user đề xuất
    public  List<User> LUser2;
    private  List<Friend> LRequest; // List yêu cầu
    private  List<Friend> LRequest2;
    private  List<Friend> LFriend; // Ds bạn bè


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        friendBinding = FragmentFriendBinding.inflate(inflater,container,false);
        tab = friendBinding.menuTab;
        tab.addTab(tab.newTab().setText("Bạn bè"));
        tab.addTab(tab.newTab().setText("Yêu cầu"));
        tab.addTab(tab.newTab().setText("Đề xuất"));
        LFriend = new ArrayList<>();
        friend_ad = new Friend_AD(context,LFriend);
        LRequest = new ArrayList<>();
        add_friend = new add_friend(context,LRequest);
        LUser = new ArrayList<>();
        dx = new DeXuat_AD(context,LUser);
        friendBinding.rFriend.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        friendBinding.rFriend.setAdapter(friend_ad);
        getLDeXuat();
        getFriend();
        getRequest();
        tab.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                  if(tab.getPosition() == 0)
                  {
                      friendBinding.rFriend.setAdapter(friend_ad);
                  }
                  else
                      if(tab.getPosition() == 1)
                      {
                          friendBinding.rFriend.setAdapter(add_friend);
                      }
                      else
                      {
                          friendBinding.rFriend.setAdapter(dx);
                      }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        friendBinding.btSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                friend_ad.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                friend_ad.getFilter().filter(newText);
                search2(newText);
                search(newText);
                return false;
            }
        });
        return friendBinding.getRoot();
    }

    private void search2(String newText)
    {

        if(newText.trim().isEmpty())
        {
            LRequest = LRequest2;
        }
        else
        {
            List<Friend> messages = new ArrayList<>();
            for(Friend friend : LRequest2)
            {
                String name = friend.getName();
                if(name.toLowerCase().contains(newText.toLowerCase()))
                {
                    messages.add(friend);
                }
            }
            LRequest = messages;
        }
        add_friend.LoadRequest(LRequest);
    }

    private void search(String txt) {


        if(txt.isEmpty())
        {
            LUser = LUser2;
        }
        else
        {
            List<User> messages = new ArrayList<>();
            for(User user : LUser2)
            {
                String name = user.getName();
                if(name.toLowerCase().contains(txt.toLowerCase()))
                {
                    messages.add(user);
                }

            }
            LUser = messages;
        }
        dx.load(LUser);
    }

    public void getLDeXuat ()
    {
        getLUser = FirebaseDatabase.getInstance().getReference("LUser");
        getLUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LUser = new ArrayList<>();
                for (DataSnapshot sn : snapshot.getChildren())
                {
                    User user = sn.getValue(User.class);
                    if(variable.account.getId() != user.getId())
                       LUser.add(user);
                }
                filterUser("MyRequest/"+variable.account.getId());
                dx.load(LUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void  filterUser (String path) // lọc những ai nếu đã là bạn và đã gửi yêu cầu r thì k đề xuất nx
    {
        DatabaseReference LFilter = FirebaseDatabase.getInstance().getReference(path);
        for(int i = LUser.size()-1; i>=0;i--)
        {
            User user = LUser.get(i);
            Query query = LFilter.orderByChild("id").equalTo(user.getId());
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {
                    LUser.remove(user);
                    LUser2 = LUser;
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getRequest() // lấy ds yêu cầu kết bạn
    {
        DatabaseReference get = FirebaseDatabase.getInstance()
                .getReference("RequestFriend/"+variable.account.getId());
        get.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
               LRequest.clear();
                for(DataSnapshot sn:snapshot.getChildren())
                {
                    Friend friend = sn.getValue(Friend.class);
                    LRequest.add(friend);
                }
                LRequest2 = LRequest;
                add_friend.LoadRequest(LRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getFriend ()
    {
        DatabaseReference get = FirebaseDatabase.getInstance()
                .getReference("Friend/"+variable.account.getId());
        get.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LFriend.clear();
                for(DataSnapshot sn:snapshot.getChildren())
                {
                    Friend friend = sn.getValue(Friend.class);
                    LFriend.add(friend);
                }
                friend_ad.LoadFriend(LFriend);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}