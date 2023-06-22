package com.taki.cloud.web3j.eth.template;

import org.web3j.protocol.Web3j;

import java.math.BigInteger;

/**
 * @ClassName ScanBlockTemplate
 * @Description TODO
 * @Author Long
 * @Date 2023/6/21 18:18
 * @Version 1.0
 */
public interface ScanBlockTemplate<T> {

    /***
     * @description:  扫描 块 交易
     * @param web3j
     * @param block
     * @param
     * @return  void
     * @author Long
     * @date: 2023/6/21 18:18
     */
  void scanBlock(Web3j web3j, BigInteger block);

  /***
   * @description:  通过hash 查询交易
   * @param web3j
   * @param  hash
   * @return  void
   * @author Long
   * @date: 2023/6/22 12:56
   */
  void hash(Web3j web3j,String hash);
}
