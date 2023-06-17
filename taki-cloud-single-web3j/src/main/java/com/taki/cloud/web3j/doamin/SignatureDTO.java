package com.taki.cloud.web3j.doamin;

import lombok.Data;

import java.util.Map;

@Data
public class SignatureDTO {


    private String R;
    private String V;
    private String S;
    private String rand;
    private String value;
    private Map<String,Object> data;


}