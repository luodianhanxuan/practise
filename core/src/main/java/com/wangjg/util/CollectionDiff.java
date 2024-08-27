package com.wangjg.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionDiff {

  public static <Type, IdType> DiffR<Type> getDiffSetOfList(
      List<Type> lst1,
      List<Type> lst2,
      Function<Type, IdType> function,
      BiFunction<Type, Type, Type> mergeFunction) {

    Set<IdType> lst1IdSet = lst1.stream().map(function).collect(Collectors.toSet());
    Set<IdType> lst2IdSet = lst2.stream().map(function).collect(Collectors.toSet());

    Set<IdType> retainIdSet = new HashSet<>(lst1IdSet);
    retainIdSet.retainAll(lst2IdSet);

    Set<IdType> lst1IdMore = new HashSet<>(lst1IdSet);
    lst1IdMore.removeAll(retainIdSet);

    Set<IdType> lst2IdMore = new HashSet<>(lst2IdSet);
    lst2IdMore.removeAll(retainIdSet);

    List<Type> lst1More =
        lst1.stream()
            .filter(ele -> lst1IdMore.contains(function.apply(ele)))
            .collect(Collectors.toList());

    List<Type> lst2More =
        lst2.stream()
            .filter(ele -> lst2IdMore.contains(function.apply(ele)))
            .collect(Collectors.toList());
    List<Type> mergedList = new ArrayList<>();
    retainIdSet.forEach(
        i -> {
          Type first =
              lst1.stream()
                  .filter(ele -> Objects.equals(i, function.apply(ele)))
                  .findFirst()
                  .orElseThrow(() -> new RuntimeException("ignored"));
          Type second =
              lst2.stream()
                  .filter(ele -> Objects.equals(i, function.apply(ele)))
                  .findFirst()
                  .orElseThrow(() -> new RuntimeException("ignored"));
          Type merged = mergeFunction.apply(first, second);
          if (merged != null) {
            mergedList.add(merged);
          }
        });

    return new DiffR<>(mergedList, lst1More, lst2More);
  }

  @Getter
  @AllArgsConstructor
  public static class DiffR<T> {
    private final List<T> mergedList;
    private final List<T> lst1More;
    private final List<T> lst2More;
  }
}
