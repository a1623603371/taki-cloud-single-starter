package com.taki.cloud.web3j.eth.template;

import com.taki.cloud.web3j.eth.utli.EthUtil;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

/**
 * @ClassName AbstractScanBlock
 * @Description TODO
 * @Author Long
 * @Date 2023/6/20 19:03
 * @Version 1.0
 */
@Slf4j
public abstract   class AbstractScanTopicBlock<T>  implements ScanBlockTopicTemplate<T> {

    /***
     * @description:  执行之前 的操作
     * @param
     * @return  void
     * @author Long
     * @date: 2023/6/20 19:30
     */
    abstract  T  before(Object ... objs);

    /***
     * @description:  执行之前 的操作
     * @param
     * @return  void
     * @author Long
     * @date: 2023/6/20 19:30
     */
    abstract  void after(Object ... objs);



    /***
     * @description: 实际执行方法
     * @param
     * @return  void
     * @author Long
     * @date: 2023/6/20 19:33
     */
    public void execute(Integer chainId){
      T t =  before();
        scanTopic(getWeb3j(chainId),getTopic(),getStartBlock(t),getEndStartBlock());
        after(t);
    }


    @Override
    public void hashTopic(Web3j web3j, String topic, String hash) {

        TransactionReceipt transactionReceipt = EthUtil.getTransactionReceipt(web3j, hash);
        int count = 10;

        while(transactionReceipt == null && count > 0) {
            transactionReceipt = EthUtil.getTransactionReceipt(web3j, hash);

            try {
                log.info("添加swap流动性查询次数:{},hash:{}", count, hash);
                Thread.sleep(500L);
                --count;
            } catch (InterruptedException var6) {
                throw new RuntimeException(var6);
            }
        }
        log.info("添加swap流动性记录数据:,hash:{}",hash);
        if (transactionReceipt != null && transactionReceipt.getLogs() != null) {
            transactionReceipt.getLogs().forEach((log) -> {
                if (topic.equals(log.getTopics().get(0))) {
                   processBusiness(log,params());
                }
            });
        }

    }

    @Override
    public void scanTopic(Web3j web3j, String topic, BigInteger startBlockHeight, BigInteger endBlockHeight) {
        EthLog ethLog = null;
        String contractAddress = getContractAddress();
        try {
            ethLog = EthUtil.ethGetLogs(web3j, startBlockHeight, endBlockHeight, contractAddress, topic);
        } catch (Exception var13) {
            log.error("获取log日志失败,错误异常:{}",var13.getMessage());

        }
        ethLog.getLogs().forEach(log -> {
            processBusiness((Log) log,  params());

        });
    }

    /***
     * @description: 获取合约地址
     * @param
     * @return  java.lang.String
     * @author Long
     * @date: 2023/6/20 19:15
     */
    abstract  String getContractAddress();

    /***
     * @description: 使用 实现该方法 得到 返回值集合
     * @param
     * @return  java.util.List<org.web3j.abi.TypeReference<?>>
     * @author Long
     * @date: 2023/6/20 19:11
     */
    abstract List<TypeReference<?>> getOutputParameters ();

    /***
     * @description:  处理业务
     * @param log
     * @param  params
     * @return  void
     * @author Long
     * @date: 2023/6/20 19:21
     */
    abstract  void  processBusiness(Log log,Object ... params);


    /***
     * @description:  设置 参数
     * @param
     * @return  java.lang.Object[]
     * @author Long
     * @date: 2023/6/20 19:26
     */
    abstract  Object[]  params();
    
    /*** 
     * @description: 根据 chainId 获取 web3j
     * @param
     * @return  org.web3j.protocol.Web3j
     * @author Long
     * @date: 2023/6/20 19:35
     */ 
    abstract  Web3j getWeb3j(Integer chainId);

    /**
     *  topic
     * @return
     */
    abstract String getTopic();

    /**
     * 开始 扫快高度
     * @return
     */
    abstract BigInteger getStartBlock(T t);

    /**
     * 结束 高度
     * @return
     */
    abstract  BigInteger getEndStartBlock();
}
