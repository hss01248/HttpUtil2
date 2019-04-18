package com.hss01248.akuhttpdemo;

import java.util.List;

/**
 * 个人信息
 * Created by my daling on 2016/3/10.
 */
public class Profile {

    public long birthDate;

    public Long uid;

    public Long countryId;

    public String phoneNumber;

    public String avatar;

    public String firstName;

    public String middleName;

    public String lastName;

    public int gender;

    public int occupation;//1学生2雇员3公务员4自由职业者5企业主

    public String nationality;

    public String province;

    public String city;

    public String street;

    public String email;

    public int emailStatus;//<2需要

    public int status; //1 初始状态 2 验证通过 3 拒绝 5正在验证中

    public Long createTime;

    public String referrerCode;

    public boolean facebookAuth;

    public Long referrer;



    public int callStatus;//1正在等待被打电话， 2打通过， 3被风控拒绝

    public String lang;

    public boolean creditable;

    public List<Integer> creditPayPeriods;

    public double minMonthlyInstallmentPayment;//每个月最小月供，低于该值则不能分期

    public double repayThreshold;//每个月还款限度

    public double debtOfNextMonth;//下个月的债务

    public boolean notPaidOff;//是否没有还清，true->没有还清， false，已经还清

    public String creditableMsg;//credit pay权限受限的提示语



}
