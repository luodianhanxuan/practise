package com.wangjg.algorithm.trie.arrayimpl;

import java.util.*;

/**
 * @author wangjg
 * 2020/3/17
 */
public class Trie {

    static final int ALPHABET_SIZE = 26;

    static class TrieNode {
        /**
         * 直接儿子结点
         */
        private final TrieNode[] links;

        /**
         * 标识是否到此是一个完整的单词
         */
        private boolean endOfWord;

        /**
         * 代表的字符
         */
        private char symbol;

        public TrieNode(char symbol) {
            this.symbol = symbol;
            links = new TrieNode[ALPHABET_SIZE];
        }

        public TrieNode() {
            links = new TrieNode[ALPHABET_SIZE];
        }

        public void insert(String word) {
            char c = word.charAt(0);
            int index = c - 'a';
            TrieNode node = new TrieNode(c);
            links[index] = node;
            if (word.length() > 1) {
                node.insert(word.substring(1));
            } else {
                node.endOfWord = true;
            }
        }
    }

    TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    public Trie(String words) {
        this.root = new TrieNode();
        this.root.insert(words);
    }

    /**
     * 向字典树插入单词
     */
    public void insert(String word) {
        root.insert(word);
    }

    private TrieNode _startWith(String word) {
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - 'a';
            node = node.links[index];
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * 查看单词是否在字典树中存在
     */
    public boolean search(String word) {
        TrieNode node = _startWith(word);
        return node != null && node.endOfWord;
    }

    /**
     * 查看字典树中是否存在 word 开头的单词
     */
    public boolean startsWith(String word) {
        return _startWith(word) != null;
    }

//    public List<String> suggest(String key) {
//        TrieNode node = _startWith(key);
//        if (node == null) {
//            return new ArrayList<>();
//        }
//
//        TrieNode[] links = node.links;
//        Deque<TrieNode> queue = new LinkedList<>(Arrays.asList(links));
//        while (!queue.isEmpty()) {
//            node = queue.poll();
//            int index = node.symbol - 'a';
//
//        }
//    }

    public static void print(Object... objects) {
        for (Object object : objects) {
            System.out.println(object.toString());
        }
    }

    public static void main(String[] args) {
        Trie obj = new Trie();
        obj.insert("apple");
        boolean res1 = obj.search("apple");
        boolean res2 = obj.search("app");
        boolean res3 = obj.startsWith("app");
        obj.insert("app");
        boolean res4 = obj.search("app");

        print(res1, res2, res3, res4);
    }
}
