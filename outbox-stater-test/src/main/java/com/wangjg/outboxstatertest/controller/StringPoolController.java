package com.wangjg.outboxstatertest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/string-pool")
public class StringPoolController {

  @GetMapping("/is-one")
  public boolean isOne(@RequestParam("toTest") String toTest) {
    return StringPool.isOneObj(toTest);
  }

  @GetMapping("/two-is-same")
  public boolean toStringObjEqual(@RequestParam("toTest1") String toTest1, @RequestParam("toTest2") String toTest2) {
    return toTest1 == toTest2;
  }
}
