package com.wangjg.algorithm.bag;

import com.google.common.collect.Lists;
import java.util.*;
import org.apache.commons.lang3.tuple.Pair;

public class FindClosestSubSetSum {

  /**
   * 集合中挑选一组等于目标值的组合
   *
   * @param nums 集合
   * @param target 目标值
   */
  private List<Integer> selectEqualList(List<Integer> nums, int target) {
    List<Integer> res = new ArrayList<>();
    int sum = 0;
    int maxNum = 0;
    for (Integer num : nums) {
      sum += num;
      if (num > maxNum) {
        maxNum = num;
      }
    }

    if (sum <= target) {
      if (Objects.equals(target, sum)) {
        res.addAll(nums);
      }
      return res;
    }

    int bound = target + maxNum;
    Map<Integer, Pair<Boolean, List<Integer>>> allSumValueInfo = getAllSumValueInfo(nums, bound);
    if (allSumValueInfo.containsKey(target)) {
      return allSumValueInfo.get(target).getRight();
    } else {
      return Lists.newArrayList();
    }
  }

  private static Map<Integer, Pair<Boolean, List<Integer>>> getAllSumValueInfo(
          List<Integer> nums, int bound) {

    Map<Integer, Pair<Boolean, List<Integer>>> allSumValueInfo = new HashMap<>();
    allSumValueInfo.put(0, Pair.of(true, new ArrayList<>()));
    for (int num : nums) {
      for (int newSum = bound; newSum >= num; newSum--) {
        int existedSumVal = newSum - num;
        if (allSumValueInfo.containsKey(existedSumVal) && allSumValueInfo.get(existedSumVal).getLeft()) {
          List<Integer> lastSumPath = allSumValueInfo.get(existedSumVal).getRight();
          List<Integer> sumPath = Lists.newArrayList(lastSumPath);
          sumPath.add(num);
          allSumValueInfo.put(newSum, Pair.of(true, sumPath));
        }
      }
    }
    return allSumValueInfo;
  }

  public static void main(String[] args) {
    FindClosestSubSetSum findClosestSubSet = new FindClosestSubSetSum();
    List<Integer> nums = Lists.newArrayList(1, 3, 1, 4, 10);
    System.out.println("--------------15--------------");
    findClosestSubSet.selectEqualList(nums, 15).forEach(System.out::println);
    System.out.println("--------------13--------------");
    findClosestSubSet.selectEqualList(nums, 13).forEach(System.out::println);
    System.out.println("--------------17--------------");
    findClosestSubSet.selectEqualList(nums, 17).forEach(System.out::println);
    System.out.println("--------------18--------------");
    findClosestSubSet.selectEqualList(nums, 18).forEach(System.out::println);
    System.out.println("--------------19--------------");
    findClosestSubSet.selectEqualList(nums, 19).forEach(System.out::println);
  }
}
