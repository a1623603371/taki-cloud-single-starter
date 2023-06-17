package com.taki.cloud.web3j.eth.template;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Numeric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName Contract
 * @Description TODO
 * @Author Long
 * @Date 2023/6/17 15:37
 * @Version 1.0
 */
@Slf4j
public class MyContract  extends Contract {
    private static final String BINARY = "Bin file was not provided";

    public static Credentials credentials = Credentials.create("1234654");


    protected MyContract( String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, credentials, gasProvider);
    }

    protected MyContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static MyContract load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MyContract(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MyContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MyContract(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    /***
     * @description:  执行方法
     * @param methodName 合约方法名称
     * @param   intPutParam 方法参数
     * @param  outputReference 方法返回数据
     * @return  java.util.List<org.web3j.abi.datatypes.Type>
     * @author Long
     * @date: 2023/6/17 17:33
     */
    public List<Type> callMethod(String methodName,List<Type> intPutParam, List<TypeReference<?>> outputReference){

        final Function function = new Function(methodName, intPutParam, outputReference);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, data);
        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            return FunctionReturnDecoder.decode(ethCall.getValue(),function.getOutputParameters()) ;
        } catch (InterruptedException e) {
            log.error("【查询失败】 请求中断 错误信息", e);
            return null;
        } catch (ExecutionException e) {
            log.error("【查询失败】 执行合约方法失败 错误信息", e);
            return null;
        }

    }

}
