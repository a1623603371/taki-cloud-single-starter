package com.taki.cloud.web3j.eth.utli;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.StaticArray;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.crypto.Sign.SignatureData;
import org.web3j.utils.Numeric;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CryptoUtils {
    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
    private static final String PRIVATE_KEY = "e62248374af86aa480f9cebd44f04cd02b915130d4fbda885a201488257b0a17";

    public CryptoUtils() {
    }

    public static boolean validate(String signature, String message, String address) {
        String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
        byte[] msgHash = new byte[0];

        try {
            msgHash = Hash.sha3((prefix + message).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var12) {
            var12.printStackTrace();
        }

        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v = (byte)(v + 27);
        }

        SignatureData sd = new SignatureData(v, Arrays.copyOfRange(signatureBytes, 0, 32), Arrays.copyOfRange(signatureBytes, 32, 64));
        String addressRecovered = null;
        boolean match = false;

        for(int i = 0; i < 4; ++i) {
            BigInteger publicKey = Sign.recoverFromSignature((byte)i, new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())), msgHash);
            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                System.out.println(addressRecovered);
                if (addressRecovered.equalsIgnoreCase(address)) {
                    match = true;
                    break;
                }
            }
        }

        return match;
    }

    public static boolean validateByByte(String signature, byte[] msgHash, String address) {
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v = (byte)(v + 27);
        }

        SignatureData sd = new SignatureData(v, Arrays.copyOfRange(signatureBytes, 0, 32), Arrays.copyOfRange(signatureBytes, 32, 64));
        String addressRecovered = null;
        boolean match = false;

        for(int i = 0; i < 4; ++i) {
            BigInteger publicKey = Sign.recoverFromSignature((byte)i, new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())), msgHash);
            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                System.out.println(addressRecovered);
                if (addressRecovered.equalsIgnoreCase(address)) {
                    match = true;
                    break;
                }
            }
        }

        return match;
    }

    public static boolean validateByMessage(String signature, String message, String address) {
        byte[] msgHash = Hash.sha3(Hex.decode("1901" + message));
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v = (byte)(v + 27);
        }

        SignatureData sd = new SignatureData(v, Arrays.copyOfRange(signatureBytes, 0, 32), Arrays.copyOfRange(signatureBytes, 32, 64));
        String addressRecovered = null;
        boolean match = false;

        for(int i = 0; i < 4; ++i) {
            BigInteger publicKey = Sign.recoverFromSignature((byte)i, new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())), msgHash);
            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                System.out.println(addressRecovered);
                if (addressRecovered.equalsIgnoreCase(address)) {
                    match = true;
                    break;
                }
            }
        }

        return match;
    }

    public static SignatureData sign(String message) {
        try {
            byte[] msgBuffer = new byte[0];
            msgBuffer = message.getBytes("UTF-8");
            byte[] msgPrefix = ("\u0019Ethereum Signed Message:\n" + msgBuffer.length).getBytes("UTF-8");
            byte[] msg = new byte[msgPrefix.length + msgBuffer.length];
            System.arraycopy(msgPrefix, 0, msg, 0, msgPrefix.length);
            System.arraycopy(msgBuffer, 0, msg, msgPrefix.length, msgBuffer.length);
            String privateKey = "dc39e437710f21e630571b3ca0a70c338e6e9c652cefa904bd004605a2a4655a";
            Credentials credentials = Credentials.create(privateKey);
            SignatureData signatureData = Sign.signMessage(msg, credentials.getEcKeyPair());
            return signatureData;
        } catch (UnsupportedEncodingException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static SignatureData signNew1(String message) {
        try {
            byte[] msgBuffer = new byte[0];
            msgBuffer = Hex.decode(message);
            byte[] msgPrefix = "\u0019Ethereum Signed Message:\n".getBytes("UTF-8");
            byte[] msg = new byte[msgPrefix.length + msgBuffer.length];
            System.arraycopy(msgPrefix, 0, msg, 0, msgPrefix.length);
            System.arraycopy(msgBuffer, 0, msg, msgPrefix.length, msgBuffer.length);
            String privateKey = "e52cd8b08b12c21f1d2ae387c9b8a2fa47250b90774efe0a8b39845e27b1a76b";
            Credentials credentials = Credentials.create(privateKey);
            SignatureData signatureData = Sign.signMessage(msg, credentials.getEcKeyPair());
            return signatureData;
        } catch (UnsupportedEncodingException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static SignatureData signNewByPrivateKey(String message, String privateKey) {
        try {
            byte[] msgBuffer = new byte[0];
            msgBuffer = Hex.decode(message);
            byte[] msgPrefix = "\u0019Ethereum Signed Message:\n".getBytes("UTF-8");
            byte[] msg = new byte[msgPrefix.length + msgBuffer.length];
            System.arraycopy(msgPrefix, 0, msg, 0, msgPrefix.length);
            System.arraycopy(msgBuffer, 0, msg, msgPrefix.length, msgBuffer.length);
            Credentials credentials = Credentials.create(privateKey);
            SignatureData signatureData = Sign.signMessage(msg, credentials.getEcKeyPair());
            return signatureData;
        } catch (UnsupportedEncodingException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static String signSetAuditorStatus(String verifyContractAddress, String nameStr, String chainId, String auditorContracts, int enableStatus, String rand, String privateKey) {
        StringBuilder msg2 = new StringBuilder();
        byte[] hash11 = Hash.sha3("SetAuditorStatus(address auditor,bool enableStatus,uint256 rand)".getBytes());
        String str2 = new String(Hex.encode(hash11));
        String strPart1 = getDomainSeparator(verifyContractAddress, nameStr, chainId);
        Address auditorContract = new Address(auditorContracts);
        Uint256 enableStatusInt = new Uint256((long)enableStatus);
        msg2.append(str2);
        msg2.append(TypeEncoder.encode(auditorContract));
        msg2.append(TypeEncoder.encode(enableStatusInt));
        msg2.append("00000000000000000000000000000000" + rand.substring(2));
        byte[] hash4 = Hash.sha3(Hex.decode(msg2.toString()));
        String strPart2 = new String(Hex.encode(hash4));
        StringBuilder msg3 = new StringBuilder();
        msg3.append(strPart1);
        msg3.append(strPart2);
        SignatureData signatureData1 = signNewByPrivateKey(msg3.toString(), privateKey);
        String v = toHexString(signatureData1.getV(), 0, signatureData1.getV().length, false);
        String r = toHexString(signatureData1.getR(), 0, signatureData1.getR().length, true);
        String s = toHexString(signatureData1.getS(), 0, signatureData1.getS().length, false);
        String signReqVO = r + s + v;
        return signReqVO;
    }

    public static String getDomainSeparator(String verifyContractAddress, String nameStr, String chainId) {
        byte[] hash2 = Hash.sha3("EIP712Domain(string name,uint256 chain,address verifyingContract)".getBytes());
        String str1 = new String(Hex.encode(hash2));
        StringBuilder msg = new StringBuilder();
        Address verifyContract = new Address(verifyContractAddress);
        msg.append(str1);
        msg.append(new String(Hex.encode(Hash.sha3(nameStr.getBytes()))));
        msg.append("0000000000000000" + chainId.substring(2));
        msg.append(TypeEncoder.encode(verifyContract));
        byte[] hash3 = Hash.sha3(Hex.decode(msg.toString()));
        String strPart1 = new String(Hex.encode(hash3));
        return strPart1;
    }

    public static void main(String[] arg) {
        byte[] hash2 = Hash.sha3("EIP712Domain(string name)".getBytes());
        String str1 = new String(Hex.encode(hash2));
        StringBuilder msg = new StringBuilder();
        msg.append(str1);
        msg.append(new String(Hex.encode(Hash.sha3("ippSwap".getBytes()))));
        byte[] hash3 = Hash.sha3(Hex.decode(msg.toString()));
        System.out.println("msg:" + msg.toString());
        String strPart1 = new String(Hex.encode(hash3));
        byte[] hash11 = Hash.sha3("updatePoolConfig(uint256 test)".getBytes());
        String str2 = new String(Hex.encode(hash11));
        StringBuilder msg2 = new StringBuilder();
        Uint256 timestamp = new Uint256(1L);
        msg2.append(str2);
        msg2.append(new String(Hex.encode(Hash.sha3("updatePoolConfig".getBytes()))));
        msg2.append(TypeEncoder.encode(timestamp));
        byte[] hash4 = Hash.sha3(Hex.decode(msg2.toString()));
        System.out.println("msg2:" + msg2.toString());
        String strPart2 = new String(Hex.encode(hash4));
        byte[] digest = Hash.sha3(Hex.decode(strPart1 + strPart2));
        StringBuilder msg3 = new StringBuilder();
        msg3.append(strPart1);
        msg3.append(strPart2);
        System.out.println("msg3:" + msg3.toString());
        String signature1 = "0x576ed207bd09d3619ec8ddb272f81be778e0e5997183757e4bbe145a76d965f13c9aee7a8c9a42c49dfb290216c04419da670b61c6e55c6cdd4c89ccdb5e5e411c";
        System.out.println(validateByByte(signature1, digest, "0xe724AEbeC57CcD5dc0A8C8D1f305D53C2179164b"));
        System.out.println(validateByMessage(signature1, msg3.toString(), "0xe724AEbeC57CcD5dc0A8C8D1f305D53C2179164b"));
        List<Address> tokens = new ArrayList();
        tokens.add(new Address("0x6202640aca6cb703955c781e8fda436da70c2b29"));
        tokens.add(new Address("0x5142640aca6cb703955c781e8fda436da70c2b29"));
        StaticArray<Address> staticArray = new StaticArray<Address>(Address.class, tokens) {
        };
        byte[] hash111 = Hash.sha3(Hex.decode(TypeEncoder.encode(staticArray)));
        String str1111 = new String(Hex.encode(hash111));
        System.out.println("str1111:" + str1111);
        getDomainSeparator("0x6815d98cd44a1a670fd144b321295b004867f845", "PayVault", "0xde1aa88295e1fcf982742f773e0419c5a9c134c994a9059e");
    }

    public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (withPrefix) {
            stringBuilder.append("0x");
        }

        for(int i = offset; i < offset + length; ++i) {
            stringBuilder.append(String.format("%02x", input[i] & 255));
        }

        return stringBuilder.toString();
    }



}
