package com.taki.cloud.web3j.eth.utli;



import com.taki.cloud.web3j.doamin.SignatureDTO;
import com.taki.cloud.web3j.eth.enums.ClassEnums;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Slf4j
public class SignUtils {

    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019\u0001";


    /**
     * 生成[min,max]之间的随机整数
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomNum(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }


    public static SignatureDTO signSetAuditorStatus(List<Object> params, List<ClassEnums> paramsClzzs,String methodName,String domainSeparatorName,String nameStr, String auditorContracts, Long chainId, String privateKey) {
        String rand = System.currentTimeMillis() + "" + randomNum(150000000, 500000000);
        String strPart1 = getDomainSeparator(auditorContracts, domainSeparatorName,nameStr, new BigInteger(chainId.toString()));
        Uint256 randS = new Uint256(new BigInteger(rand));
        StringBuilder msg2 = new StringBuilder();
        byte[] hash11 = Hash.sha3(methodName.getBytes());
        String str2 = new String(Hex.encode(hash11));
        msg2.append(str2);
        String msg = buildMsg(params,paramsClzzs);
        msg2.append(msg);
        msg2.append(TypeEncoder.encode(randS));
        byte[] hash4 = Hash.sha3(Hex.decode(msg2.toString()));
        String strPart = new String(Hex.encode(hash4));
        StringBuilder sign = new StringBuilder();
        sign.append(strPart1);
        sign.append(strPart);
        Sign.SignatureData signatureData1 = signNewByPrivateKey(sign.toString(), privateKey);
        String v = toHexString(signatureData1.getV(), 0, signatureData1.getV().length, false);
        String r = toHexString(signatureData1.getR(), 0, signatureData1.getR().length, true);
        String s = toHexString(signatureData1.getS(), 0, signatureData1.getS().length, false);
        log.warn("signSetAuditorStatus签名R=" + r);
        log.warn("signSetAuditorStatus签名S=" + s);
        log.warn("signSetAuditorStatus签名V=" + v);
        log.warn("signSetAuditorStatus签名rand=" + rand);
        SignatureDTO signReqVO = new SignatureDTO();
        signReqVO.setR(r);
        signReqVO.setS("0x" + s);
        signReqVO.setV("0x" + v);
        signReqVO.setRand(rand);
        return signReqVO;
    }


    private static String buildMsg(List<Object> params, List<ClassEnums> paramsClzzs) {

        StringBuilder msgBuilder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {

            Object object = params.get(i);
        ClassEnums paramClazz = paramsClzzs.get(0);

          if (ClassEnums.ADDRESS.compareTo(paramClazz) == 0){
            Address address = new Address((String) object);
              msgBuilder.append(TypeEncoder.encode(address));

          }
          if (ClassEnums.UINT256.compareTo(paramClazz) == 0){
              Uint256 uint256 = new Uint256(new BigInteger(object.toString()) );
              msgBuilder.append(TypeEncoder.encode(uint256));
          }
          if (ClassEnums.LIST.compareTo(paramClazz) == 0){
              StringBuilder str = new StringBuilder();
              List<Object> objs = (List<Object>) object;

              objs.forEach(obj->{
                  str.append(TypeEncoder.encode((Type) obj));
              });
              byte[] bytes = Hash.sha3(Hex.decode(str.toString()));
              String bytess = new String(Hex.encode(bytes));
              msgBuilder.append(bytess);

          }
        }



        return msgBuilder.toString();

    }




    public static SignatureDTO signNotifyRewards(String auditorContracts, Long chainId, String privateKey,Integer epoch) {
        StringBuilder msg2 = new StringBuilder();
        byte[] hash11 = Hash.sha3("notifyRewards(uint256 epoch)".getBytes());
        String str2 = new String(Hex.encode(hash11));
        String strPart1 = getDomainSeparator(auditorContracts, "","StakingRewards", new BigInteger(chainId.toString()));
         Uint256 epochIndx = new Uint256(epoch);
        msg2.append(str2);
        msg2.append(TypeEncoder.encode(epochIndx));
        byte[] hash4 = Hash.sha3(Hex.decode(msg2.toString()));
        String strPart2 = new String(Hex.encode(hash4));
        StringBuilder msg3 = new StringBuilder();
        msg3.append(strPart1);
        msg3.append(strPart2);
        Sign.SignatureData signatureData1 = signNewByPrivateKey(msg3.toString(), privateKey);
        String v = toHexString(signatureData1.getV(), 0, signatureData1.getV().length, false);
        String r = toHexString(signatureData1.getR(), 0, signatureData1.getR().length, true);
        String s = toHexString(signatureData1.getS(), 0, signatureData1.getS().length, false);
        log.warn("signSetAuditorStatus签名R=" + r);
        log.warn("signSetAuditorStatus签名S=" + s);
        log.warn("signSetAuditorStatus签名V=" + v);
        SignatureDTO signReqVO = new SignatureDTO();
        signReqVO.setR(r);
        signReqVO.setS("0x" + s);
        signReqVO.setV("0x" + v);
        return signReqVO;
    }


    public static SignatureDTO signSetAuditorStatus2(Integer iss, String auditorContracts, Long chainId, String privateKey, Uint256 statLotteriesCount, List<Uint256> uint256List) {
        String rand = System.currentTimeMillis() + "" + randomNum(150000000, 500000000);
        StringBuilder msg2 = new StringBuilder();
        byte[] hash11 = Hash.sha3("syncWinSheets(uint256 iss,uint256[] sheets,uint256 statLotteriesCount,uint256 rand)".getBytes());
        String str2 = new String(Hex.encode(hash11));
        String strPart1 = getDomainSeparator(auditorContracts, "","Lottery", new BigInteger(chainId.toString()));
        Uint256 issS = new Uint256(iss);
        msg2.append(str2);
        msg2.append(TypeEncoder.encode(issS));


        StringBuilder str = new StringBuilder();
        for (int i = 0; i < uint256List.size(); i++) {
            str.append(TypeEncoder.encode(uint256List.get(i)));
        }

        byte[] bytes = Hash.sha3(Hex.decode(str.toString()));
        String bytess = new String(Hex.encode(bytes));
        msg2.append(bytess);
        msg2.append(TypeEncoder.encode(statLotteriesCount));
        Uint256 randS = new Uint256(new BigInteger(rand));
        msg2.append(TypeEncoder.encode(randS));
        byte[] hash4 = Hash.sha3(Hex.decode(msg2.toString()));
        String strPart2 = new String(Hex.encode(hash4));
        StringBuilder msg3 = new StringBuilder();
        msg3.append(strPart1);
        msg3.append(strPart2);
        Sign.SignatureData signatureData1 = signNewByPrivateKey(msg3.toString(), privateKey);
        String v = toHexString(signatureData1.getV(), 0, signatureData1.getV().length, false);
        String r = toHexString(signatureData1.getR(), 0, signatureData1.getR().length, true);
        String s = toHexString(signatureData1.getS(), 0, signatureData1.getS().length, false);
        log.warn("signSetAuditorStatus签名R=" + r);
        log.warn("signSetAuditorStatus签名S=" + s);
        log.warn("signSetAuditorStatus签名V=" + v);
        log.warn("signSetAuditorStatus签名rand=" + rand);
        SignatureDTO signReqVO = new SignatureDTO();
        signReqVO.setR(r);
        signReqVO.setS("0x" + s);
        signReqVO.setV("0x" + v);
        signReqVO.setRand(rand);
        return signReqVO;
    }


    public static Sign.SignatureData signNewByPrivateKey(String message, String privateKey) {
        try {
            byte[] msgBuffer = new byte[0];
            msgBuffer = Hex.decode(message);
//            byte[] msgPrefix =  ("u0019Ethereum Signed Message:n" + msgBuffer.length).getBytes("UTF-8");
            byte[] msgPrefix = (PERSONAL_MESSAGE_PREFIX).getBytes("UTF-8");
            byte[] msg = new byte[msgPrefix.length + msgBuffer.length];
            System.arraycopy(msgPrefix, 0, msg, 0, msgPrefix.length);
            System.arraycopy(msgBuffer, 0, msg, msgPrefix.length, msgBuffer.length);
//            String privateKey = "e52cd8b08b12c21f1d2ae387c9b8a2fa47250b90774efe0a8b39845e27b1a76b";
            Credentials credentials = Credentials.create(privateKey);
            Sign.SignatureData signatureData = Sign.signMessage(msg, credentials.getEcKeyPair());
            return signatureData;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (withPrefix) {
            stringBuilder.append("0x");
        }

        for (int i = offset; i < offset + length; ++i) {
            stringBuilder.append(String.format("%02x", input[i] & 255));
        }

        return stringBuilder.toString();
    }

    public static String getDomainSeparator(String verifyContractAddress,String domainSeparatorName, String nameStr, BigInteger chainId) {
        byte[] hash2 = Hash.sha3(domainSeparatorName.getBytes());
        String str1 = new String(Hex.encode(hash2));
        StringBuilder msg = new StringBuilder();
        Address verifyContract = new Address(verifyContractAddress);
        msg.append(str1);
        // Uint256 chainIds = new Uint256(chainId);
        msg.append(new String(Hex.encode(Hash.sha3(nameStr.getBytes()))));
        //msg.append("0000000000000000" + chainId);
        Uint256 chainIdS = new Uint256(chainId);
        msg.append(TypeEncoder.encode(chainIdS));
        msg.append(TypeEncoder.encode(verifyContract));
        byte[] hash3 = Hash.sha3(Hex.decode(msg.toString()));
        String strPart1 = new String(Hex.encode(hash3));
        return strPart1;
    }



//    public static String getDomainSeparator(String verifyContractAddress, String nameStr, BigInteger chainId) {
//        byte[] hash2 = Hash.sha3("EIP712Domain(string name,uint256 chainId,address verifyingContract)".getBytes());
//        String str1 = new String(Hex.encode(hash2));
//        StringBuilder msg = new StringBuilder();
//        Address verifyContract = new Address(verifyContractAddress);
//        msg.append(str1);
//        // Uint256 chainIds = new Uint256(chainId);
//        msg.append(new String(Hex.encode(Hash.sha3(nameStr.getBytes()))));
//        //msg.append("0000000000000000" + chainId);
//        Uint256 chainIdS = new Uint256(chainId);
//        msg.append(TypeEncoder.encode(chainIdS));
//        msg.append(TypeEncoder.encode(verifyContract));
//        byte[] hash3 = Hash.sha3(Hex.decode(msg.toString()));
//        String strPart1 = new String(Hex.encode(hash3));
//        return strPart1;
//    }


    public static void main(String[] args) {
        Integer a = 10;


    }
}
