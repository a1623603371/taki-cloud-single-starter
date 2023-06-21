package com.taki.cloud.web3j.eth.utli;


import com.subgraph.orchid.encoders.Hex;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @ClassName MetaMaskUtil
 * @Description TODO
 * @Author Long
 * @Date 2023/5/1 15:41
 * @Version 1.0
 */
public class MetaMaskUtil {
    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    public MetaMaskUtil() {
    }

    public static void main(String[] args) {
        String signature = "0x6caea57e210ed9b5775a2f49cd56bffe54cf5abf8592e7cd47ccd131c2ce321d09724eb80bf96a64240fb05cce21100bb0121d1cdf69bc68c8ab08ec6fb341861b";
        String message = "1";
        System.out.println("message:" + message);
        String address = "0x6Cae1bbeAbBE40EEe243cCf6B3d3a96D5579f237";
        Boolean result = loginNew(address, "0xc1b25b281ad0175ee483561a7bdcb0114b287cf67f64e8601584b8f652f8cb7b", "0x4ae216ee205311697b4841add5d3a2f61ccd51feead480e335ae02b7ca8bdfbc", "0x1b", "167992121157030261");
        System.out.println(result);
    }

    public static boolean validate(String signature, String message, String address) {
        String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
        byte[] msgHash = Hash.sha3((prefix + message).getBytes());
        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v = (byte)(v + 27);
        }

        Sign.SignatureData sd = new Sign.SignatureData(v, Arrays.copyOfRange(signatureBytes, 0, 32), Arrays.copyOfRange(signatureBytes, 32, 64));
        String addressRecovered = null;
        boolean match = false;

        for(int i = 0; i < 4; ++i) {
            BigInteger publicKey = Sign.recoverFromSignature((byte)i, new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())), msgHash);
            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);
                if (addressRecovered.equals(address)) {
                    match = true;
                    break;
                }
            }
        }

        return match;
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
