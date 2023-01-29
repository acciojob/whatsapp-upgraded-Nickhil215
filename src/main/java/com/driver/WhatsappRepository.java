package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {
    int numberOfGroups=0;
    int numberOfMessages=0;
    Map<String,User> userMap=new HashMap<>();
    Map<Group, List<User>> groupMap=new HashMap<>();
    List<Message> messageList=new ArrayList<>();

    Map<User,List<Message>> userMessageList=new HashMap<>();

    Map<Group,List<Message>> groupMessagesList=new HashMap<>();
    public String createUser(String name, String mobile) throws Exception {
        if(userMap.containsKey(mobile)){
            throw new Exception("Mobile number already registared");
        }
        User user=new User(name,mobile);
        userMap.put(mobile,user);
        return "SUCCESS";
    }


    public Group createGroup(List<User> users) {
        /*Create a group by providing a list of users. The list should contain at least 2 users, where the first user is the admin.
    A group has exactly one admin.
    A user can belong to exactly one group and has a unique name.
    If there are only 2 users, the group is a personal chat, and the group name should be kept as the name of the second user (other than the admin).
    If there are 2 or more users, the name of the group will be "Group count".
    Note that a personal chat is not considered a group and the count is not updated for personal chats.
    For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}. If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.
          */

        if(users.size()==2){
            Group group=new Group(users.get(1).getName(),users.size());
            groupMap.put(group,users);
            return group;
        }else {
            Group group=new Group("Group "+numberOfGroups,users.size());
            groupMap.put(group,users);
            return group;
        }
    }


    public int createMessage(String content) {
        Message message=new Message(++numberOfMessages,content,new Date());
        messageList.add(message);
        return message.getId();
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        /*  Send a message by providing the message, sender, and group.
          If the mentioned group does not exist, the application will throw an exception.
          If the sender is not a member of the group, the application will throw an exception.
          If the message is sent successfully, the application will return the final number of messages in that group.
      */
        if(!groupMap.containsKey(group)){
            throw new Exception("Grop not found");
        }
        boolean present=false;
        for(User user:groupMap.get(group)){
            if(user.equals(sender)){
                present=true;
            }
        }

        if(!present){
            throw new Exception("User not found");
        }

        if(groupMessagesList.containsKey(group)){
            groupMessagesList.get(group).add(message);
        }else{
            List<Message> list=new ArrayList<>();
            list.add(message);
            groupMessagesList.put(group,list);
        }

        if (userMessageList.containsKey(sender)){
            userMessageList.get(sender).add(message);
        }else{
            List<Message> list=new ArrayList<>();
            list.add(message);
            userMessageList.put(sender,list);
        }

        return groupMessagesList.get(group).size();
    }


}
