package com.wangjg.test.net.bio.chatroom.simple.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author wangjg
 * 2019/10/8
 */
public class UserInputHandler implements Runnable {
    private ChatClient chatClient;

    public UserInputHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void run() {
        try {
            // 等待用户输入消息
            BufferedReader consoleReader =
                    new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();

                // 向服务器发送消息
                chatClient.send(input);

                // 检查用户是否准备退出
                if (chatClient.readyToQuit(input)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
