package com.zijinph.base.auth.authservice.rest;

import com.zijinph.base.auth.authservice.entity.DataRule;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {

    @PostMapping("/hello")
    @GetMapping("/hello")
    public List<DataRule> hello(String userId, String dataId){

        List<DataRule> rules = new ArrayList<>();
        if ("0102".equals(userId)) {
            DataRule rule = new DataRule();
            rule.setRoleCode("0102");
            rule.setDataId(dataId);
            rule.setRightCode("2");
            rule.setRightValue("0102");
            rules.add(rule);
        } else if ("0202".equals(userId)) {
            DataRule rule = new DataRule();
            rule.setRoleCode("0202");
            rule.setDataId(dataId);
            rule.setRightCode("2");
            rule.setRightValue("0202");
            rules.add(rule);
            rule = new DataRule();
            rule.setRoleCode("0202");
            rule.setDataId(dataId);
            rule.setRightCode("5");
            rule.setRightValue("0102");
            rules.add(rule);
        } else {
            DataRule rule = new DataRule();
            rule.setRoleCode(userId);
            rule.setDataId(dataId);
            rule.setRightCode("2");
            rule.setRightValue(userId);
            rules.add(rule);
        }
        return rules;
    }

    @PostMapping("/getDataAuth")
    public List<DataRule> getDataAuth(String userId, String dataId){

        List<DataRule> rules = new ArrayList<>();
        if ("0102".equals(userId)) {
            DataRule rule = new DataRule();
            rule.setRoleCode("0102");
            rule.setDataId(dataId);
            rule.setRightCode("2");
            rule.setRightValue("0102");
            rules.add(rule);
        } else if ("0202".equals(userId)) {
            DataRule rule = new DataRule();
            rule.setRoleCode("0202");
            rule.setDataId(dataId);
            rule.setRightCode("2");
            rule.setRightValue("0202");
            rules.add(rule);
            rule = new DataRule();
            rule.setRoleCode("0202");
            rule.setDataId(dataId);
            rule.setRightCode("5");
            rule.setRightValue("0102");
            rules.add(rule);
        }
        return rules;
    }
}
