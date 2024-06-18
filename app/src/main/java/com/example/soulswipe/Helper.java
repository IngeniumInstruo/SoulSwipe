package com.example.soulswipe;

import java.util.HashMap;

public class Helper {
    private static Helper instance;
    public static synchronized Helper getInstance(){
        if(instance == null){
            instance = new Helper();
        }
        return instance;
    }
    public HashMap<String,String> compareUid(String authUid, String partnerUid){
        HashMap<String,String> uidMap = new HashMap<>();
        String objectUid = "";
        int result = authUid.compareTo(partnerUid);

        if (result > 0) {
            objectUid = authUid + partnerUid;
            uidMap.put("user1",authUid);
            uidMap.put("user2",partnerUid);
        } else if (result < 0) {
            objectUid = partnerUid + authUid;
            uidMap.put("user1",partnerUid);
            uidMap.put("user2",authUid);
        }
        uidMap.put("objectUid",objectUid);
        return uidMap;
    }
}
