package com.taki.cloud.web3j.eth.template;

import com.taki.cloud.web3j.eth.utli.EthUtil;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.util.List;

/**
 * @ClassName AbstractScanBlock
 * @Description TODO
 * @Author Long
 * @Date 2023/6/21 22:07
 * @Version 1.0
 */
public abstract class AbstractScanBlock<T> implements ScanBlockTemplate<T> {



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
     * @description:
     * @param
     * @return  void
     * @author Long
     * @date: 2023/6/22 13:04
     */
    public void execute(Integer chainId){
        T t = before();
        scanBlock(getWeb3j(chainId),getStartBlock(t));
    }



    @Override
    public void scanBlock(Web3j web3j, BigInteger blockHeight) {

        EthBlock.Block block = EthUtil.getBlockByHeight(web3j,blockHeight);
        List<EthBlock.TransactionResult> transactions = block.getTransactions();
        transactions.forEach(transaction->{
            processBusiness((EthBlock.TransactionObject) transaction,97);
        });

    }


    @Override
    public void hash(Web3j web3j, String hash) {
        EthBlock.TransactionResult transactionResult = (EthBlock.TransactionResult) EthUtil.getTransactionInfo(web3j,hash);
        processBusiness((EthBlock.TransactionObject) transactionResult,97);

    }

    /***
     * @description:  业务处理
     * @param transaction
     * @param  chainId
     * @return  void
     * @author Long
     * @date: 2023/6/21 22:10
     */
    abstract  void  processBusiness(EthBlock.TransactionObject transaction,Integer chainId);


    /***
     * @description: 根据 chainId 获取 web3j
     * @param
     * @return  org.web3j.protocol.Web3j
     * @author Long
     * @date: 2023/6/20 19:35
     */
    abstract  Web3j getWeb3j(Integer chainId);


    /**
     * 开始 扫快高度
     * @return
     */
    abstract BigInteger getStartBlock(T t);
}
