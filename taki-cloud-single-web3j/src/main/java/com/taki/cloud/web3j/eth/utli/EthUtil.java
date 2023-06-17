package com.taki.cloud.web3j.eth.utli;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.subgraph.orchid.encoders.Hex;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint160;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class EthUtil {

    private static Logger logger = LoggerFactory.getLogger(EthUtil.class);

    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    /**
     * 离线签名eth
     *
     * @param value //转账的值
     */
    public static String signedEthTransactionData(String privateKey, String to, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String value) {
        //把十进制的转换成ETH的Wei, 1ETH = 10^18 Wei
        BigDecimal realValue = Convert.toWei(value, Convert.Unit.ETHER);
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, realValue.toBigIntegerExact());
        //手续费= (gasPrice * gasLimit ) / 10^18 ether

        Credentials credentials = Credentials.create(privateKey);
        //使用TransactionEncoder对RawTransaction进行签名操作
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //        //转换成0x开头的字符串
        return Numeric.toHexString(signedMessage);
    }

    /**
     * 转账
     *
     * @param value //转账的值
     */
    public static String signedEthContractTransactionData(String privateKey, String contractAddress, String to, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, Double value, Double decimal) {
        //因为每个代币可以规定自己的小数位, 所以实际的转账值=数值 * 10^小数位
        BigDecimal realValue = BigDecimal.valueOf(value * Math.pow(10.0, decimal));

        //0xa9059cbb代表某个代币的转账方法hex(transfer) + 对方的转账地址hex + 转账的值的hex
        String data = "0xa9059cbb" + Numeric.toHexStringNoPrefixZeroPadded(Numeric.toBigInt(to), 64) + Numeric.toHexStringNoPrefixZeroPadded(realValue.toBigInteger(), 64);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, data);
        //手续费= (gasPrice * gasLimit ) / 10^18 ether

        Credentials credentials = Credentials.create(privateKey);
        //使用TransactionEncoder对RawTransaction进行签名操作
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        //转换成0x开头的字符串
        return Numeric.toHexString(signedMessage);
    }

    /**
     * 通过助记词和id生成对应的子账户
     *
     * @param mnemonic 助记词
     * @param id       派生子id
     * @return 子账户key
     */
    private static DeterministicKey generateKeyFromMnemonicAndUid(String mnemonic, int id) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, "");

        DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);

        return hierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(id, false));
    }

    /**
     * 生成地址
     *
     * @param id 用户id
     * @return 地址
     */
    public static String getEthAddress(String mnemonic, int id) {
        DeterministicKey deterministicKey = generateKeyFromMnemonicAndUid(mnemonic, id);
        ECKeyPair ecKeyPair = ECKeyPair.create(deterministicKey.getPrivKey());
        return Keys.getAddress(ecKeyPair);
    }

    /**
     * 生成私钥
     *
     * @param id 用户id
     * @return 私钥
     */
    public static BigInteger getPrivateKey(String mnemonic, int id) {
        return generateKeyFromMnemonicAndUid(mnemonic, id).getPrivKey();
    }


    /**
     * 通过private key生成credentials
     */
    public static Credentials generateCredentials(String privateKey) {
        return Credentials.create(privateKey);
    }



    private static String sendTxUrl="https://mainnet.infura.io/v3/ccf882eebf9f464f9f5c4de907a3bea2";
    /**
     * 发送eth离线交易
     *
     * @param from        eth持有地址
     * @param to          发送目标地址
     * @param amount      金额（单位：eth）
     * @param credentials 秘钥对象
     * @return 交易hash
     */
    public static String sendEthRawTransaction(Web3j web3j, String from, String to, BigInteger gasLimit,
                                               BigInteger gasPrice, BigDecimal amount, Credentials credentials, BigInteger nonce) {

        try {
            //临时替换web3j
//            web3j=new JsonRpc2_0Web3j(new HttpService(sendTxUrl));

            BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");

            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send().getTransactionHash();
        } catch (Exception e) {
            logger.error("【ETH离线转账失败】 错误信息: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 发送eth离线交易
     *
     * @param from        eth持有地址
     * @param to          发送目标地址
     * @param amount      金额（单位：eth）
     * @param credentials 秘钥对象
     * @return 交易hash
     */
    public static String sendEthRawTransaction(Web3j web3j, String from, String to, BigInteger gasLimit,
                                               BigInteger gasPrice, BigDecimal amount, Credentials credentials) {

        try {
            //临时替换web3j
//            web3j=new JsonRpc2_0Web3j(new HttpService(sendTxUrl));
            BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();

            BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");

            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            EthSendTransaction send = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send();
            if(null != send.getError()){
                logger.warn("提币失败，错误信息：{}", send.getError().getMessage());
                return null;
            }
            return send.getTransactionHash();
        } catch (Exception e) {
            logger.warn("【ETH离线转账失败】 错误信息: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 发送eth离线交易
     *
     * @param from        eth持有地址
     * @param to          发送目标地址
     * @param amount      金额（单位：eth）
     * @param credentials 秘钥对象
     * @return 交易hash
     */
    public static String sendNodeEthRawTransaction(Web3j web3j, String from, String to, BigInteger gasLimit,
                                               BigInteger gasPrice, BigDecimal amount, Credentials credentials,long chainId) {

        try {
            //临时替换web3j
//            web3j=new JsonRpc2_0Web3j(new HttpService(sendTxUrl));
            BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();

            BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");

            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);
            EthSendTransaction send = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send();
            if(null != send.getError()){
                logger.warn("提币失败，错误信息：{}", send.getError().getMessage());
                return null;
            }
            return send.getTransactionHash();
        } catch (Exception e) {
            logger.warn("【ETH离线转账失败】 错误信息: {}", e.getMessage());
            return null;
        }
    }
    /**
     * 发送代币离线交易
     *
     * @param fromAddress     代币持有地址
     * @param toAddress       代币目标地址
     * @param amount          金额（单位：代币最小单位）
     * @param contractAddress 代币合约地址
     * @param credentials     秘钥对象
     * @return 交易hash
     */
    public static String sendContractTransaction(Web3j web3j, String fromAddress, String toAddress,
                                                 BigInteger gasLimit, BigInteger gasPrice,
                                                 BigDecimal amount, String contractAddress,
                                                 Credentials credentials, int decimal) {

        try {
            //临时替换web3j
//            web3j=new JsonRpc2_0Web3j(new HttpService(sendTxUrl));

            logger.warn("用户ERC20代币转出，fromAddress：{}，toAddress：{}，gasLimit：{}，gasPrice：{}，amount：{}，coinAddress：{}",
                    fromAddress, toAddress, gasLimit, gasPrice, amount, contractAddress);

            decimal = getTokenDecimal(web3j, contractAddress);

            BigInteger nonce = web3j.ethGetTransactionCount(
                    fromAddress, DefaultBlockParameterName.PENDING).send().getTransactionCount();


            BigInteger value = amount.multiply(BigDecimal.valueOf(Math.pow(10, decimal))).toBigInteger();

            Function function = new Function("transfer",
                    Arrays.<Type>asList(new Address(toAddress),
                            new Uint256(value)),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(function);

            RawTransaction rawTransaction =
                    RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, data);
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            EthSendTransaction send = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send();
            if(null != send.getError()){
                logger.warn("提币失败，错误信息：{}", send.getError().getMessage());
                return null;
            }
            return send.getTransactionHash();
        } catch (Exception e) {
            logger.warn("提币失败，错误信息：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 发送代币离线交易
     *
     * @param fromAddress     代币持有地址
     * @param toAddress       代币目标地址
     * @param amount          金额（单位：代币最小单位）
     * @param contractAddress 代币合约地址
     * @param credentials     秘钥对象
     * @return 交易hash
     */
    public static String sendNodeContractTransaction(Web3j web3j, String fromAddress, String toAddress,
                                                 BigInteger gasLimit, BigInteger gasPrice,
                                                 BigDecimal amount, String contractAddress,
                                                 Credentials credentials, int decimal,long chainId) {

        try {
            //临时替换web3j
//            web3j=new JsonRpc2_0Web3j(new HttpService(sendTxUrl));

            logger.warn("用户ERC20代币转出，fromAddress：{}，toAddress：{}，gasLimit：{}，gasPrice：{}，amount：{}，coinAddress：{}",
                    fromAddress, toAddress, gasLimit, gasPrice, amount, contractAddress);

            decimal = getTokenDecimal(web3j, contractAddress);

            BigInteger nonce = web3j.ethGetTransactionCount(
                    fromAddress, DefaultBlockParameterName.PENDING).send().getTransactionCount();


            BigInteger value = amount.multiply(BigDecimal.valueOf(Math.pow(10, decimal))).toBigInteger();

            Function function = new Function("transfer",
                    Arrays.<Type>asList(new Address(toAddress),
                            new Uint256(value)),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(function);

            RawTransaction rawTransaction =
                    RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, data);
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction,chainId, credentials);

            EthSendTransaction send = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send();
            if(null != send.getError()){
                logger.warn("提币失败，错误信息：{}", send.getError().getMessage());
                return null;
            }
            return send.getTransactionHash();
        } catch (Exception e) {
            logger.warn("提币失败，错误信息：{}", e.getMessage());
            return null;
        }
    }
    /**
     * 发送账户内所有eth
     *
     * @param from        持有地址
     * @param to          目标地址
     * @param credentials 秘钥对象
     * @return 交易hash
     */
    public static String sendAllEth(Web3j web3j, String from, String to, Credentials credentials) {

        try {
            BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            BigInteger gasLimit = BigInteger.valueOf(21000L);
            BigInteger balance = web3j.ethGetBalance(from, DefaultBlockParameterName.PENDING).send().getBalance();

            if (balance.compareTo(gasPrice.multiply(gasLimit)) <= 0) {
                return null;
            }

            BigInteger amount = balance.subtract(gasPrice.multiply(gasLimit));

            RawTransaction transaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amount, "");
            byte[] signMessage = TransactionEncoder.signMessage(transaction, credentials);
            return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send().getTransactionHash();
        } catch (IOException e) {
            logger.error("【ETH转账失败】 错误信息: {}", e.getMessage());
            return null;
        }
    }

    public static String callMethodTransaction(Web3j web3j, Long chainId, String fromAddress, BigInteger gasLimit, BigInteger gasPrice,String methodName, String contractAddress, Credentials credentials,List<Type> intPutParam) {
        try {
            logger.warn("用户ERC20代币转出，fromAddress：{}，gasLimit：{}，gasPrice：{}，coinAddress：{}", new Object[]{fromAddress, gasLimit, gasPrice, contractAddress});
            BigInteger nonce = ((EthGetTransactionCount)web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).send()).getTransactionCount();

            Function function = new Function(methodName, intPutParam, Collections.emptyList());
            String data = FunctionEncoder.encode(function);
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, data);
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
            EthSendTransaction send = (EthSendTransaction)web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send();
            if (null != send.getError()) {
                logger.warn("执行合约方法，错误信息：{}", send.getError().getMessage());
                return null;
            } else {
                return send.getTransactionHash();
            }
        } catch (Exception var21) {
            logger.warn("执行合约方法，错误信息：{}", var21.getMessage());
            return null;
        }
    }

    private static Bytes32 stringToBytes32(String text) {
        byte[] myStringInByte = Numeric.hexStringToByteArray(text);
        return new Bytes32(myStringInByte);
    }

    public static Bytes32 stringToBytes321(String string) {
        byte[] byteValue = string.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return new Bytes32(byteValueLen32);
    }

    public static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();

        for(int i = 0; i < chars.length; ++i) {
            hex.append(Integer.toHexString(chars[i]));
        }
        StringBuilder var10000 = (new StringBuilder()).append(hex.toString());
        String var10001 = "";
        return var10000.append(String.join("", Collections.nCopies(32 - hex.length() / 2, "00"))).toString();
    }


    /**
     * 发送账户内所有某代币
     *
     * @param from        代币拥有地址
     * @param to          代币目标地址
     * @param coinAddress 代币合约地址
     * @param gasLimit    gas值
     * @param gasPrice    gas price
     * @param credentials 秘钥对象
     * @return 交易hash
     */
    public static String sendAllCoin(Web3j web3j, String from, String to,
                                     String coinAddress, BigInteger gasLimit,
                                     BigInteger gasPrice, Credentials credentials, int decimal) {
        try {
            BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
                    .send().getTransactionCount();

            BigInteger value = new BigDecimal(getBalanceOfCoin(
                    web3j, from, coinAddress))
                    .multiply(BigDecimal.valueOf(Math.pow(10, decimal))).toBigInteger();
            System.out.println(value);

            Function transfer = new Function(
                    "transfer",
                    Arrays.<Type>asList(new Address(to),
                            new Uint256(value)),
                    Collections.<TypeReference<?>>emptyList());
            String data = FunctionEncoder.encode(transfer);

            RawTransaction rawTransaction =
                    RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);

            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            return web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).send().getTransactionHash();
        } catch (IOException e) {
            logger.error("【合约代币转账失败】 错误信息: {}", e.getMessage());
            return null;
        }

    }

    /**
     * 获取账户代币余额
     *
     * @param account     账户地址
     * @param coinAddress 代币地址
     * @return 代币余额 （单位：代币最小单位）
     */
    public static String getBalanceOfCoin(Web3j web3j, String account, String coinAddress) {
        int decimal = getTokenDecimal(web3j, coinAddress);
        Function balanceOf = new Function("balanceOf",
                Arrays.<Type>asList(new Address(account)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));

        if (coinAddress == null) {
            return null;
        }
        String value = null;
        try {
            value = web3j.ethCall(Transaction.createEthCallTransaction(account, coinAddress, FunctionEncoder.encode(balanceOf)), DefaultBlockParameterName.PENDING).send().getValue();
        } catch (IOException e) {
            logger.warn("【获取合约代币余额失败】 错误信息: {}", e);
            return "0";
        }
        if (value.equalsIgnoreCase("0x")) {
            return BigDecimal.ZERO.toPlainString();
        }
        BigDecimal balance = new BigDecimal(new BigInteger(value.substring(2), 16).toString(10)).divide(BigDecimal.valueOf(Math.pow(10, decimal)));
        return balance.toPlainString();
    }

    /**
     * 取eth余额
     *
     * @param address 传入查询的地址
     * @return String 余额
     */
    public static String getBalance(Web3j web3j, String address) {
        EthGetBalance ethGetBlance = null;
        try {
            ethGetBlance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        } catch (Exception e) {
            logger.warn("【获取ETH余额失败】 address={} ，错误信息: {}", e);

            return "0";
        }
        // 格式转换 WEI(币种单位) --> ETHER
        String balance =   Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
        return balance;
    }

    public static String getBalancePending(Web3j web3j, String address) {
        EthGetBalance ethGetBlance = null;
        try {
            ethGetBlance = web3j.ethGetBalance(address, DefaultBlockParameterName.PENDING).send();
        } catch (Exception e) {
            logger.error("【获取ETH余额失败】 错误信息: {}", e);
        }
        // 格式转换 WEI(币种单位) --> ETHER
        String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
        return balance;
    }

    /**
     * 获取合约交易估算gas值
     *
     * @param from        发送者
     * @param to          发送目标地址
     * @param coinAddress 代币地址
     * @param value       发送金额（单位：代币最小单位）
     * @return 估算的gas limit
     */
    public static BigInteger getTransactionGasLimit(Web3j web3j, String from, String to, String coinAddress, BigInteger value) {
        Function transfer = new Function(
                "transfer",
                Arrays.<Type>asList(new Address(to),
                        new Uint256(value)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(transfer);
        try {
            EthEstimateGas send = web3j.ethEstimateGas(new Transaction(from, null, BigInteger.valueOf(21000), BigInteger.valueOf(100000), coinAddress, null, data)).send();
            return send.getAmountUsed();
        } catch (Exception e) {
            logger.error("【估算合约交易gas值失败】 错误信息: {}", e);
            return new BigInteger("100000");
        }
    }

    public static BigInteger getEthTransactionGasLimit(Web3j web3j, String from, String to, BigDecimal amount) {
        try {
            return web3j.ethEstimateGas(new Transaction(from, null, null, null, to, Convert.toWei(amount, Convert.Unit.WEI).toBigInteger(), null)).send().getAmountUsed();
        } catch (Exception e) {
            logger.error("【估算交易gas值势必】 错误信息： {}", e);
        }
        return new BigInteger("10000");
    }

    /**
     * 估算gasPrice
     */
    public static BigInteger getEthTransactionGasPrice(Web3j web3j) {
        try {
            return web3j.ethGasPrice().send().getGasPrice().multiply(BigInteger.valueOf(13)).divide(BigInteger.TEN);
        } catch (Exception e) {
            logger.warn("【估算gasPrice失败】 错误信息: {}", e);
        }
        return new BigInteger("200000000000");
    }

    /**
     * 根据txid获取交易信息
     */
    public static org.web3j.protocol.core.methods.response.Transaction getTransactionInfo(Web3j web3j, String txId) {
        org.web3j.protocol.core.methods.response.Transaction transaction = null;
        try {
            transaction = web3j.ethGetTransactionByHash(txId).send().getTransaction().orElse(null);
            logger.warn("【获取交易信息成功】 交易哈希: {}", txId);
        } catch (Exception e) {
            logger.warn("【获取交易信息失败】 交易哈希: {}, 错误信息: {}", txId, e);
            return null;
        }
        return transaction;
    }

    /**
     * 根据txid获取交易信息
     */
    public static TransactionReceipt getTransactionReceipt(Web3j web3j, String txId) {
        TransactionReceipt transaction = null;
        try {
            transaction = web3j.ethGetTransactionReceipt(txId).send().getTransactionReceipt().orElse(null);
            logger.warn("【获取交易信息成功】 交易哈希: {}", txId);
        } catch (Exception e) {
            logger.warn("【获取交易信息失败】 交易哈希: {}, 错误信息: {}", txId, e);
            return null;
        }
        return transaction;
    }
    /**
     * 校验区块确认数 BTC：2 BCC：2 ETH：30 LTC：4 USDT（omni）：2 USDT（erc20）：30 NEO：5 IOTA：1 XLM：1 QTUM：6 BTS：1
     * HSR：10 GXS：1
     * <p>
     * 币种	 充值确认数	 提现确认数	 区块浏览器 BTC 	 1	 6	https://blockchain.info/ BCH 	 1
     * 6	http://blockdozer.com/insight/ LTC	 1	 6	https://bchain.info/LTC/ ETC 	 30	 120
     * https://gastracker.io/ ETH	 12	 120	https://etherchain.org/ USDT	1 	6
     * https://omniexplorer.info/ EOS	 12	120 	https://eospark.com/account/kkcoindotcom
     */
    public static BigInteger getConfirmNumber(Web3j web3j, String txid) {

        BigInteger currentBlockNumber = null;
        try {
            org.web3j.protocol.core.methods.response.Transaction transactionInfo = getTransactionInfo(web3j, txid);
            logger.warn("【开始获取区块高度】 txid:{}，交易信息：{}", txid, JSON.toJSONString(transactionInfo));
            if (transactionInfo == null ||
                    (transactionInfo.getBlockHash() != null &&
                            transactionInfo.getBlockHash().equals(
                                    "0x0000000000000000000000000000000000000000000000000000000000000000"))
                    || transactionInfo.getBlockNumber() == null) {
                return BigInteger.ZERO;
            }
            logger.warn("【开始获取区块高度】 txid:{}", txid);
            currentBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
            logger.warn("【获取区块高度】 txid:{}，blockNumber：{}，transactionInfo.getBlockNumber()：{}",
                    txid, currentBlockNumber, transactionInfo.getBlockNumber());
            return BigInteger.valueOf(currentBlockNumber.intValue() - transactionInfo.getBlockNumber().intValue());
        } catch (Exception e) {
            logger.error("【获取区块高度失败】 txid：{}，错误信息: {}", txid, e);
            return null;
        }
    }

    /**
     * 获取历史交易数据
     */
    public static List<org.web3j.protocol.core.methods.response.Transaction> getHistoryTransactions(Web3j web3j, BigInteger height) {
        List<org.web3j.protocol.core.methods.response.Transaction> transactions = new ArrayList<>();
        try {
            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(height), false).send().getBlock();
            for (EthBlock.TransactionResult transactionResult : block.getTransactions()) {
                org.web3j.protocol.core.methods.response.Transaction transaction = web3j.ethGetTransactionByHash((String) transactionResult.get()).send().getTransaction().get();
                transactions.add(transaction);
            }
            logger.warn("【获取交易数据成功】 区块哈希: {}, 区块高度: {}", block.getHash(), block.getNumber());
        } catch (Exception e) {
            logger.error("【获取交易数据失败】 错误信息: {}", e);
            return null;
        }
        return transactions;
    }

    /**
     * 获取当前区块高度
     */
    public static BigInteger getBlockHeight(Web3j web3j) {
        try {
            return web3j.ethBlockNumber().send().getBlockNumber();
        } catch (Exception e) {
            logger.warn("【获取最新区块高度失败】 错误信息: {}", e);
            return null;
        }
    }

    /**
     * 获取当前区块
     */
    public static EthBlock.Block getCurrentBLock(Web3j web3j) {
        try {
            return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(getBlockHeight(web3j)), false).send().getBlock();
        } catch (Exception e) {
            logger.warn("【获取当前区块失败】 错误信息: {}", e);
            return null;
        }
    }

    /**
     * 根据区块高度获取区块信息
     */
    public static EthBlock.Block getBlockByHeight(Web3j web3j, BigInteger height) {
        try {
            return web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(height), true).send().getBlock();
        } catch (Exception e) {
            logger.warn("【获取区块失败】 height: {} 错误信息: {}", height, e);
            return null;
        }
    }
    /**
//     * 根据区块高度获取区块信息
//     */
//    public static EthBlock.Block getBlockByHeightsss(Web3j web3j, BigInteger height) {
//        try {
//            return web3j.ethGetTransactionByBlockNumberAndIndex(DefaultBlockParameter.valueOf(height), null).send().getTransaction().getBlock();
//        } catch (Exception e) {
//            logger.warn("【获取区块失败】 height: {} 错误信息: {}", height, e);
//            return null;
//        }
//    }

    /**
     * 解析ETH代币交易
     */
    public static Map<String, Object> getTokenTransactionInfo(Web3j web3j,org.web3j.protocol.core.methods.response.Transaction transaction) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String input = transaction.getInput();

        if (!Erc20Util.isTransferFunc(input)) {
            return null;
        }
        int decimal = getTokenDecimal(web3j, transaction.getTo());
        if(StringUtils.isBlank(Erc20Util.getToAddress(input))
                ||Erc20Util.getTransferValue(input, decimal)==null
                || StringUtils.isBlank(transaction.getHash())){
            return null;
        }
        String tokenSymbol = getTokenSymbol(web3j, transaction.getTo());
        result.put("to", Erc20Util.getToAddress(input));
        result.put("amount", Erc20Util.getTransferValue(input, decimal));
//        result.put("amount", getErc20Util.getTransferValue(input, decimal));
        result.put("txid", transaction.getHash());
        result.put("from", transaction.getFrom());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", tokenSymbol);
        logger.info("【获取代币交易】 {}", new Gson().toJson(result));
        return result;
    }

    /**
     * 解析ETH 721代币交易
     */
    public static Map<String, Object> getERC1155TokenTransactionInfo( Web3j web3j,org.web3j.protocol.core.methods.response.Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        String input = transaction.getInput();
        if (!Erc20Util.isSafeTransferFrom1155(input)) {
            return null;
        }
        logger.info("解析ERC1155转账交易记录:{}",JSON.toJSONString(transaction));
        String contractAddress = transaction.getTo();
        String from = "0x" + input.substring(34,74);
        String to = "0x" + input.substring(98,138);
        String token =input.substring(139,202);
        String value = input.substring(203,266);
        BigInteger tokenId = new BigInteger(token, 16);
        Integer nftValue = Integer.parseInt(value,16);
        BigDecimal amount=BigDecimal.ZERO;
        String tokenSymbol = getTokenSymbol(web3j, transaction.getTo());
        result.put("to", to);
        result.put("from", from);
        result.put("amount", amount);
        result.put("tokenId",tokenId);
        result.put("txid", transaction.getHash());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", tokenSymbol);
        result.put("nftValue",nftValue);
        result.put("contractAddress",contractAddress);
        result.put("schemaName", "ERC1155");
        logger.info("【获取代币交易】 {}", new Gson().toJson(result));
        return result;
    }

    public static Map<String, Object> getERC721TokenTransactionInfo( Web3j web3j,org.web3j.protocol.core.methods.response.Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        String input = transaction.getInput();
        if (!Erc20Util.isSafeTransferFrom721(input)) {
            return null;
        }
        logger.info("解析ERC721转账交易记录:{}",JSON.toJSONString(transaction));
        String contractAddress = transaction.getTo();
        String from = "0x" + input.substring(34,74);
        String to = "0x" + input.substring(98,138);
        String token =input.substring(139,202);
        BigInteger tokenId = new BigInteger(token, 16);
        BigDecimal amount=BigDecimal.ZERO;
        String tokenSymbol = getTokenSymbol(web3j, transaction.getTo());
        result.put("to", to);
        result.put("from", from);
        result.put("amount", amount);
        result.put("tokenId",tokenId);
        result.put("txid", transaction.getHash());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", tokenSymbol);
        result.put("contractAddress", contractAddress);
        result.put("schemaName", "ERC721");
        logger.info("【获取代币交易】 {}", new Gson().toJson(result));
        return result;
    }

    /**
     * 解析ETH交易
     */
    public static Map<String, Object> getEthTransactionInfo(org.web3j.protocol.core.methods.response.Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        result.put("to", transaction.getTo());
        result.put("amount", Convert.fromWei(transaction.getValue().toString(10), Convert.Unit.ETHER));
        result.put("txid", transaction.getHash());
        result.put("from", transaction.getFrom());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", "ETH");
        logger.info("【获取ETH交易】 {}", new Gson().toJson(result));
        return result;
    }
    /**
     * 解析ETH交易
     */
    public static Map<String, Object> getOtherTransactionInfo(org.web3j.protocol.core.methods.response.Transaction transaction,String symbol) {
        Map<String, Object> result = new HashMap<>();
        result.put("to", transaction.getTo());
        result.put("amount", Convert.fromWei(transaction.getValue().toString(10), Convert.Unit.ETHER));
        result.put("txid", transaction.getHash());
        result.put("from", transaction.getFrom());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", symbol);
        logger.info("【获取ETH类交易】 {}", new Gson().toJson(result));
        return result;
    }
    /**
     * 解析BNB交易
     */
    public static Map<String, Object> getBscTransactionInfo(org.web3j.protocol.core.methods.response.Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        result.put("to", transaction.getTo());
        result.put("amount", Convert.fromWei(transaction.getValue().toString(10), Convert.Unit.ETHER));
        result.put("txid", transaction.getHash());
        result.put("from", transaction.getFrom());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", "BNB");
        logger.info("【获取BSC交易】 {}", new Gson().toJson(result));
        return result;
    }

    /**
     * 解析Heco交易
     */
    public static Map<String, Object> getHecoTransactionInfo(org.web3j.protocol.core.methods.response.Transaction transaction) {
        Map<String, Object> result = new HashMap<>();
        result.put("to", transaction.getTo());
        result.put("amount", Convert.fromWei(transaction.getValue().toString(10), Convert.Unit.ETHER));
        result.put("txid", transaction.getHash());
        result.put("from", transaction.getFrom());
        result.put("height", transaction.getBlockNumber());
        result.put("txFee", Convert.fromWei(transaction.getGasPrice().multiply(transaction.getGas()).toString(10), Convert.Unit.ETHER));
        result.put("gas", transaction.getGas());
        result.put("gasPrice", transaction.getGasPrice());
        result.put("symbol", "HT");
        logger.info("【获取Heco交易】 {}", new Gson().toJson(result));
        return result;
    }


    /**
     * 查询代币符号
     */
    public static final String getTokenSymbol(Web3j web3j, String contractAddress) {
        String methodName = "symbol";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            logger.error("【查询代币符号失败】 请求中断 错误信息： {}", e);
            return null;
        } catch (ExecutionException e) {
            logger.error("【查询代币符号失败】 执行合约方法失败 错误信息: {}", e);
            return null;
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return "";
        }
        return results.get(0).getValue().toString();
    }

    /**
     * 查询代币名称
     */
    public static final String getTokenName(Web3j web3j, String contractAddr) {
        String methodName = "name";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddr, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            logger.error("【查询代币名称失败】 请求中断 错误信息： {}", e);
            return null;
        } catch (ExecutionException e) {
            logger.error("【查询代币名称失败】 执行合约方法失败 错误信息: {}", e);
            return null;
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || results.size() <= 0) {
            return "";
        }
        return results.get(0).getValue().toString();
    }

    /**
     * 查询代币精度
     */
    public static final int getTokenDecimal(Web3j web3j, String contractAddr) {
        String methodName = "decimals";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddr, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            logger.warn("【获取精度失败】 请求中断，错误信息： {}", e);
            return 0;
        } catch (ExecutionException e) {
            logger.warn("【获取精度失败】 执行合约方法失败，错误信息: {}", e);
            return 0;
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return 0;
        }
        //logger.warn("【获取精度信息】results：{}", JSON.toJSONString(results));
        return Integer.parseInt(results.get(0).getValue().toString());
    }

    public static boolean isValidPrivateKey(String ethPrivKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(ethPrivKey);
        return cleanPrivateKey.length() == Keys.PRIVATE_KEY_LENGTH_IN_HEX;
    }

    public static boolean isValidAddress(String ethAddr) {
        String cleanInput = Numeric.cleanHexPrefix(ethAddr);
        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (Exception e) {
            return false;
        }
        return cleanInput.length() == Keys.ADDRESS_LENGTH_IN_HEX;
    }

    public static BigDecimal getTokenServiceFee(Web3j web3j, String txid) throws Exception {
        org.web3j.protocol.core.methods.response.Transaction transactionInfo = EthUtil.getTransactionInfo(web3j, txid);
        if (transactionInfo == null) throw new Exception("获取交易信息为空");
        try {
            EthEstimateGas send = web3j.ethEstimateGas(new Transaction(transactionInfo.getFrom(),
                    transactionInfo.getNonce(), transactionInfo.getGasPrice(), transactionInfo.getGas(), transactionInfo.getTo(),
                    transactionInfo.getValue(), transactionInfo.getInput())).send();
            logger.warn("ERC20 获取 EthEstimateGas 信息，txid: {}", txid);
            return Convert.fromWei(send.getAmountUsed().multiply(transactionInfo.getGasPrice()).toString(10), Convert.Unit.ETHER);
        } catch (Exception e) {
            logger.error("ERC20 计算手续费发生异常 txid: {}，异常信息: {}", txid, e);
            return Convert.fromWei(transactionInfo.getGas().multiply(transactionInfo.getGasPrice()).toString(10), Convert.Unit.ETHER);
        }
    }
    /**
     * 查询代币符号
     */
    public static final String callTokenMethod(Web3j web3j, String contractAddress,String methodName,String type) {
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        if("Utf8String".equals(type)){
            TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
            };
            outputParameters.add(typeReference);
        }else if("Address".equals(type)){
            TypeReference<Address> typeReference = new TypeReference<Address>() {
            };
            outputParameters.add(typeReference);
        }else if("Uint".equals(type)){
            TypeReference<Uint> typeReference = new TypeReference<Uint>() {
            };
            outputParameters.add(typeReference);
        }else if("Uint256".equals(type)){
            TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
            };
            outputParameters.add(typeReference);
        }else if("Uint160".equals(type)){
            TypeReference<Uint160> typeReference = new TypeReference<Uint160>() {
            };
            outputParameters.add(typeReference);
        }else if("Array".equals(type)) {
            TypeReference<Array> typeReference = new TypeReference<Array>() {
            };
            outputParameters.add(typeReference);
        }else if("StaticArray".equals(type)) {
            TypeReference<StaticArray> typeReference = new TypeReference<StaticArray>() {
            };
            outputParameters.add(typeReference);
        }
        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            logger.error("【查询代币方法失败】 请求中断 错误信息： {}", e);
            return null;
        } catch (ExecutionException e) {
            logger.error("【查询代币方法失败】 执行合约方法失败 错误信息: {}", e);
            return null;
        }
//        String string1 = ethCall.getValue().substring(2,66);
//        String string2 =ethCall.getValue().substring(67,130);
//        String string3 =ethCall.getValue().substring(131,194);

//        List<Type> results = FunctionReturnDecoder.decode(string1, function.getOutputParameters());
//        List<Type> results2 = FunctionReturnDecoder.decode(string2, function.getOutputParameters());
//        List<Type> results3 = FunctionReturnDecoder.decode(string3, function.getOutputParameters());
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return "";
        }
        return results.get(0).getValue().toString();
    }
    public static final String getAllPairs(Web3j web3j, String contractAddress,String type,BigInteger i) {
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Address> typeReference = new TypeReference<Address>() {
            };
            outputParameters.add(typeReference);

        Function function = new Function("allPairs", Arrays.<Type>asList(new Uint(i)), outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            logger.error("【查询代币方法失败】 请求中断 错误信息： {}", e);
            return null;
        } catch (ExecutionException e) {
            logger.error("【查询代币方法失败】 执行合约方法失败 错误信息: {}", e);
            return null;
        }
        List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        if (null == results || 0 == results.size()) {
            return "";
        }
        return results.get(0).getValue().toString();
    }

    public static final String[] getReservers(Web3j web3j, String contractAddress,String methodName) {
        List<Type> inputParameters = Arrays.<Type>asList(
                new Address("0xBcBf05c0706F28E23D3cF5C0bc0fc48eDBc00a3E"));
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        TypeReference<Uint> typeReference = new TypeReference<Uint>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction("0x0000000000000000000000000000000000000000", contractAddress, data);

        EthCall ethCall = null;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            logger.error("【查询代币方法失败】 请求中断 错误信息： {}", e);
            return null;
        } catch (ExecutionException e) {
            logger.error("【查询代币方法失败】 执行合约方法失败 错误信息: {}", e);
            return null;
        }
        String string1 = ethCall.getValue().substring(2,66);
        String string2 =ethCall.getValue().substring(67,130);
        String string3 =ethCall.getValue().substring(131,194);

        List<Type> results = FunctionReturnDecoder.decode(string1, function.getOutputParameters());
        List<Type> results2 = FunctionReturnDecoder.decode(string2, function.getOutputParameters());
        List<Type> results3 = FunctionReturnDecoder.decode(string3, function.getOutputParameters());
        String[] str = {results.get(0).getValue().toString(),results2.get(0).getValue().toString(),results3.get(0).getValue().toString()};
        return str;
    }


    public static Boolean loginNew(String address, String r, String s, String v, String rand) {
        byte[] hash2 = Hash.sha3("EIP712Domain(string name)".getBytes());
        String str1 = new String(Hex.encode(hash2));
        StringBuilder msg = new StringBuilder();
        msg.append(str1);
        msg.append(new String(Hex.encode(Hash.sha3("ippSwap".getBytes()))));
        byte[] hash3 = Hash.sha3(Hex.decode(msg.toString()));
        System.out.println("msg:" + msg.toString());
        String strPart1 = new String(Hex.encode(hash3));
        byte[] hash11 = Hash.sha3("updatePoolConfig(uint256 rand)".getBytes());
        String str2 = new String(Hex.encode(hash11));
        StringBuilder msg2 = new StringBuilder();
        Uint256 message = new Uint256(new BigInteger(rand));
        msg2.append(str2);
        msg2.append(TypeEncoder.encode(message));
        byte[] hash4 = Hash.sha3(Hex.decode(msg2.toString()));
        System.out.println("msg2:" + msg2.toString());
        String strPart2 = new String(Hex.encode(hash4));
        byte[] digest = Hash.sha3(Hex.decode("1901" + strPart1 + strPart2));
        StringBuilder msg3 = new StringBuilder();
        msg3.append(strPart1);
        msg3.append(strPart2);
        System.out.println("msg3:" + msg3.toString());
        String signature1 = r + s.replace("0x", "") + v.replace("0x", "");
        System.out.println(signature1);
        if (!CryptoUtils.validateByByte(signature1, digest, address)) {
            return false;
        } else {
            return !CryptoUtils.validateByMessage(signature1, msg3.toString(), address) ? false : true;
        }
    }


}
