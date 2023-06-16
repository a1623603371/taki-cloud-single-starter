package com.taki.cloud.web3j.eth.utli;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WalletUtils
 * @Description TODO
 * @Author Long
 * @Date 2023/6/16 19:07
 * @Version 1.0
 */
public class WalletUtils {

    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);



    /*** 
     * @description: 创建钱包
     * @param 
     * @return  java.util.Map<java.lang.String,java.lang.String>
     * @author Long
     * @date: 2023/6/16 19:12
     */ 
    public static Map<String,String> createWallet()  throws MnemonicException.MnemonicLengthException {
     return    createWallet(DeterministicSeed.MAX_SEED_ENTROPY_BITS , 32);
    }



    /**
     * 创建钱包
     * @throws MnemonicException.MnemonicLengthException
     */
    public static Map<String,String> createWallet(int bits, int bit)  throws MnemonicException.MnemonicLengthException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] entropy = new byte[bits / bit];
        secureRandom.nextBytes(entropy);
        //生成12位助记词
        List<String> str = MnemonicCode.INSTANCE.toMnemonic(entropy);
        //使用助记词生成钱包种子
        byte[] seed = MnemonicCode.toSeed(str, "");
        DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
        DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
        DeterministicKey deterministicKey = deterministicHierarchy
                .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
        byte[] bytes = deterministicKey.getPrivKeyBytes();
        ECKeyPair keyPair = ECKeyPair.create(bytes);
        //通过公钥生成钱包地址
        String address = Keys.getAddress(keyPair.getPublicKey());
        Map<String,String> map = new HashMap<>();
        map.put("mnemonicCode", JSON.toJSONString(str));
        map.put("address","0x"+address);
        map.put("privateKey",keyPair.getPrivateKey().toString(16));
        map.put("publicKey",keyPair.getPublicKey().toString(16));
        return map;
    }
}
