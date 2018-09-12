package com.hss01248.ebusticket;

import android.text.TextUtils;

/**
 * Created by hss on 2018/9/11.
 */

public class TicketLeftInfo {

    /**
     * prices : -1,5.0,5.0,5.0,-1,-1,5.0,5.0,5.0,5.0,5.0,-1,-1,-1,5.0,5.0,5.0,5.0,5.0,5.0
     * tickets : -1,0,0,0,-1,-1,0,0,0,0,0,-1,-1,-1,0,0,0,0,0,0
     */

    public String prices;
    public String tickets;


    public int getTicketNum(){
        if(TextUtils.isEmpty(tickets)){
            return 0;
        }
        String ti = tickets.replace("-1,","");
        String[] days = ti.split(",");
        int count = 0;
        for (int j = 0; j<days.length; j++) {
            String str = days[j];
            if(j==0){
                continue;
            }
            try {
                int i = Integer.parseInt(str);
                if(i >0){
                    count += i;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return count;

    }
}
