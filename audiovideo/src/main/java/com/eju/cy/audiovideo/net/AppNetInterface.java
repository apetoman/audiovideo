package com.eju.cy.audiovideo.net;



import com.eju.cy.audiovideo.dto.CallRecordsDto;
import com.eju.cy.audiovideo.dto.RoomDto;
import com.eju.cy.audiovideo.dto.SigDto;
import com.eju.cy.audiovideo.dto.UpdateStatusDto;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AppNetInterface {


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-11
     * @ Time: 16:51
     * @ Description：  生成房间号
     */

    @Multipart
    @POST("av/rtc/gen_room/")
    Observable<RoomDto> getRoom(@Part("appType") RequestBody appType,
                                @Part("calling") RequestBody calling,
                                @Part("listening") RequestBody listening,
                                @Part("platform") RequestBody platform);


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-12
     * @ Time: 06:56
     * @ Description： 查询状态
     */

    @GET("av/rtc/by_call/")
    Observable<CallRecordsDto> byCall();

    /**
     * @ Name: Caochen
     * @ Date: 2020-03-13
     * @ Time: 12:10
     * @ Description： 签名
     */
    @Multipart
    @POST("av/usersig/gen_user_sig/")
    Observable<SigDto> genUserSig(@Part("appType") RequestBody appType);


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-13
     * @ Time: 12:10
     * @ Description：  更改通话状态  av/rtc/update_talk_status/
     */


    @Multipart
    @POST("av/rtc/update_talk_status/")
    Observable<UpdateStatusDto> updateTalkStatus(@Part("id") RequestBody id, @Part("talk_status") RequestBody talk_status);


    /**
     * @ Name: Caochen
     * @ Date: 2020-03-13
     * @ Time: 12:10
     * @ Description：  更改房间状态  av/rtc/update_talk_status/
     */


    @Multipart
    @POST("av/rtc/update_room_status/")
    Observable<UpdateStatusDto> updateRoomStatus(@Part("id") RequestBody id, @Part("room_status") RequestBody room_status);


}
