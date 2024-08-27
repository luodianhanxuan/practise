package com.wangjg.qywechat;

import cn.felord.WeComTokenCacheable;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class SimpleMemeoryWeComTokenCacheable implements WeComTokenCacheable {

    private final Cache<String, String> cache = CacheBuilder.newBuilder().
            maximumSize(10).build();

    /**
     * 存入accessToken
     * <p>
     * 推荐缓存组织格式 {@code token::qywx::{corpId}::{agentId}}.
     *
     * @param corpId      the corpId
     * @param agentId     the agentId
     * @param accessToken the access token
     * @return the token
     */
    @Override
    public String putAccessToken(String corpId, String agentId, String accessToken) {
        String accessKey = getAccessTokenKey(corpId, agentId);
        cache.put(accessKey, accessToken);
        return accessToken;
    }

    /**
     * 获取accessToken.
     *
     * @param corpId  the corpId
     * @param agentId the agentId
     * @return the token
     */
    @Override
    public String getAccessToken(String corpId, String agentId) {
        String accessKey = getAccessTokenKey(corpId, agentId);
        return cache.getIfPresent(accessKey);
    }

    /**
     * 清除accessToken
     *
     * @param corpId  the corp id
     * @param agentId the agent id
     */
    @Override
    public void clearAccessToken(String corpId, String agentId) {
        String accessTokenKey = getAccessTokenKey(corpId, agentId);
        cache.invalidate(accessTokenKey);
    }

    private String getAccessTokenKey(String corpId, String agentId) {
        return String.format("token::qywx::%s::%s", corpId, agentId);
    }

    /**
     * 放入企业的jsapi_ticket.
     * <p>
     * 推荐缓存组织格式 {@code ticket::qywx::corp::{corpId}::{agentId}}.
     *
     * @param corpId     the corpId
     * @param agentId    the agentId
     * @param corpTicket the corpTicket
     * @return the corpTicket
     */
    @Override
    public String putCorpTicket(String corpId, String agentId, String corpTicket) {
        String corpTicketKey = getCorpTicketKey(corpId, agentId);
        cache.put(corpTicketKey, corpTicket);
        return corpTicket;
    }

    /**
     * 获取企业的jsapi_ticket.
     *
     * @param corpId  the corpId
     * @param agentId the agentId
     * @return the corpTicket
     */
    @Override
    public String getCorpTicket(String corpId, String agentId) {
        String corpTicketKey = getCorpTicketKey(corpId, agentId);
        return cache.getIfPresent(corpTicketKey);
    }

    private String getCorpTicketKey(String corpId, String agentId) {
        return String.format("ticket::qywx::corp::%s::%s", corpId, agentId);
    }

    /**
     * 放入应用的jsapi_ticket.
     * <p>
     * 推荐缓存组织格式 {@code ticket::qywx::agent::{corpId}::{agentId}}.
     *
     * @param corpId      the corpId
     * @param agentId     the agentId
     * @param agentTicket the agentTicket
     * @return the agentTicket
     */
    @Override
    public String putAgentTicket(String corpId, String agentId, String agentTicket) {
        String agentTicketKey = getAgentTicketKey(corpId, agentId);
        cache.put(agentTicketKey, agentTicket);
        return agentTicket;
    }

    /**
     * 获取应用的jsapi_ticket.
     *
     * @param corpId  the corpId
     * @param agentId the agentId
     * @return the agentTicket
     */
    @Override
    public String getAgentTicket(String corpId, String agentId) {
        String agentTicketKey = getAgentTicketKey(corpId, agentId);
        return cache.getIfPresent(agentTicketKey);
    }

    private String getAgentTicketKey(String corpId, String agentId) {
        return String.format("ticket::qywx::agent::%s::%s", corpId, agentId);
    }
}
