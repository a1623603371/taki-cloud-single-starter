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
public abstract class AbstractScanBlock implements ScanBlockTemplate {


    @Override
    public void scanBlock(Web3j web3j, BigInteger blockHeight) {

        EthBlock.Block block = EthUtil.getBlockByHeight(web3j,blockHeight);
        List<EthBlock.TransactionResult> transactions = block.getTransactions();
        transactions.forEach(transaction->{
            processBusiness((EthBlock.TransactionObject) transaction,97);
        });

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
}
