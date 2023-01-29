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
            throw new Exception("User already exists");
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
            Group group=new Group("Group "+ ++numberOfGroups,users.size());
            groupMap.put(group,users);
            return group;
        }
    }


    public int createMessage(String content) {
        Message message=new Message(++numberOfMessages,content);
        message.setTimestamp(new Date());
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
            throw new Exception("Group does not exist");
        }
        boolean present=false;
        for(User user:groupMap.get(group)){
            if(user.equals(sender)){
                present=true;
            }
        }

        if(!present){
            throw new Exception("You are not allowed to send message");
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


    public String changeAdmin(User approver, User user, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.

        if(!groupMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!groupMap.get(0).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
          boolean isUser=false;
        for(User user1:groupMap.get(group)){
            if (user1.equals(user1)){
                isUser=true;
            }
        }

        if(!isUser){
            throw new Exception("User is not a participant");
        }
         List<User> users=groupMap.get(group);
        for( User user1: users){
            if(user1.equals(user)){
               Collections.swap(users,users.indexOf(user),users.indexOf(user1));
            }
        }

       return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
      boolean isGroupPerson=false;
      Group group1=null;
        for(Group group:groupMap.keySet()){
            for(User user1:groupMap.get(group)){
                if(user1.equals(user)){
                    isGroupPerson=true;
                    group1=group;
                }
            }
        }
        if(!isGroupPerson){
            throw new Exception("User not found");
        }

        if(groupMap.get(group1).get(0).equals(user)){
            throw new Exception("Cannot remove admin");
        }
//
//        deleting msgs from list of groupMessages
        for(Group group:groupMessagesList.keySet()){
            for(Message message:groupMessagesList.get(group)){
                if(userMessageList.get(user).contains(message)){
                    groupMessagesList.remove(message);
                }
            }
        }
//        deleting msgs from list of created msgs;
        for(Message message:messageList){
            if(userMessageList.get(user).contains(message)){
                messageList.remove(message);
            }
            }
        userMessageList.remove(user);
        groupMap.remove(user);
//If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

            return groupMap.get(group1).size()+messageList.size()+groupMessagesList.get(group1).size();
    }


}
