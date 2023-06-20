package com.taki.cloud.web3j.eth.template;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;

import java.math.BigInteger;
import java.util.List;

/**
 * @ClassName SscanBlockUtlis
 * @Description TODO
 * @Author Long
 * @Date 2023/6/20 17:39
 * @Version 1.0
 */
public interface ScanBlockTemplate<T> {


   void scanTopic (Web3j web3j, String topic, BigInteger startBlockHeight, BigInteger endBlockHeight);


}
