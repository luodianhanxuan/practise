package com.wangjg.qywechat;

import cn.felord.AgentDetails;
import cn.felord.DefaultAgent;
import cn.felord.api.ContactBookManager;
import cn.felord.api.WorkWeChatApi;
import cn.felord.domain.GenericResponse;
import cn.felord.domain.contactbook.department.DeptInfo;
import cn.felord.domain.contactbook.user.UserDetail;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

public class ContactBookTests {
    public static void main(String[] args) {
        AgentDetails agentDetails = DefaultAgent
                .of("xxxxx", "xxxxx", "xxxxx");

        WorkWeChatApi workWeChatApi = new WorkWeChatApi(new SimpleMemeoryWeComTokenCacheable());
        ContactBookManager contactBookManager = workWeChatApi.contactBookManager(agentDetails);
        GenericResponse<List<DeptInfo>> listGenericResponse = contactBookManager.departmentApi().deptList();
        List<DeptInfo> deptInfoList = listGenericResponse.getData();
        JSONObject jsonObject = new JSONObject();

        for (DeptInfo deptInfo : deptInfoList) {
            System.out.println(JSON.toJSONString(deptInfo, SerializerFeature.DisableCircularReferenceDetect));
            List<UserDetail> userDetails = contactBookManager.userApi().getDeptUserDetails(deptInfo.getId()).getData();
            System.out.println(JSON.toJSONString(userDetails, SerializerFeature.DisableCircularReferenceDetect));
        }
    }
}
