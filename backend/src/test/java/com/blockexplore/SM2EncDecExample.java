package com.blockexplore;

import com.blockexplore.config.EnvConfig;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;

import java.security.*;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;

public class SM2EncDecExample {
    //    public static void main(String[] args) throws Exception {
//        Security.addProvider(new BouncyCastleProvider());
//        KeyPair keyPair = generateSM2KeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//        String message = "This is a secret message：将我的全部财产留给王任晔！";
//        byte[] plaintext = message.getBytes();
//        byte[] encryptedData = sm2Encrypt(publicKey, plaintext);
//        String encryptedHexData = Hex.toHexString(encryptedData);
//        System.out.println("Encrypted Data (Hex): " + encryptedHexData);
//        byte[] encryptedData2 = Hex.decode(encryptedHexData);
//        byte[] decryptedData = sm2Decrypt(privateKey, encryptedData2);
//        System.out.println("Decrypted Message: " + new String(decryptedData));
//    }
    private static CryptoSuite cryptoSuite = new CryptoSuite(1);
    private static CryptoKeyPair user;

    // 静态代码块初始化 admin
    static {
        user = cryptoSuite.createKeyPair(EnvConfig.Admin_Private_Key);
    }
    public static PublicKey createPublicKeyFromXY(String xHex, String yHex) throws Exception {
        // 添加BouncyCastle作为安全提供者
        Security.addProvider(new BouncyCastleProvider());

        // 将X和Y的十六进制字符串转换为字节数组
        byte[] xBytes = Hex.decode(xHex);
        byte[] yBytes = Hex.decode(yHex);
        // 获取椭圆曲线参数 (sm2p256v1 对应于 SM2 椭圆曲线)
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

        // 构造椭圆曲线上的点 Q
        ECPoint q = sm2Spec.getCurve().createPoint(new java.math.BigInteger(1, xBytes), new java.math.BigInteger(1, yBytes));

        // 将 BouncyCastle 的 ECCurve 转换为 Java 标准的 EllipticCurve
        EllipticCurve ellipticCurve = EC5Util.convertCurve(sm2Spec.getCurve(), sm2Spec.getSeed());

        // 获取 Java 标准的 EC 参数
        ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, sm2Spec);

        // 将 BouncyCastle 的 ECPoint 转换为 Java 的 ECPoint
        java.security.spec.ECPoint ecPoint = new java.security.spec.ECPoint(q.getAffineXCoord().toBigInteger(), q.getAffineYCoord().toBigInteger());

        // 创建 ECPublicKeySpec
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);

        // 使用 KeyFactory 生成 PublicKey 对象
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        return keyFactory.generatePublic(pubKeySpec);
    }

    public static void main(String[] args) throws Exception {
        byte[] secretWord = "123456".getBytes();
        System.out.println("secretWord ===>" + Hex.toHexString(secretWord));
        System.out.println("KeyPairPublicKeyToString" + user.getKeyPair().getPublic().toString());
        System.out.println("KeyPairPublicKey" + Hex.toHexString(user.getKeyPair().getPublic().getEncoded()));
        System.out.println("PublicKeyClass" + user.getKeyPair().getPublic().getClass());
        System.out.println("FiscoPublicKey" + user.getHexPublicKey());
        byte[] enMessage = sm2Encrypt(user.getKeyPair().getPublic(), secretWord);
        System.out.println("enMessage ===>" + enMessage);
        System.out.println("deMessage ===>" + Hex.toHexString(sm2Decrypt(user.getKeyPair().getPrivate(), enMessage)));
        PublicKey publicKey = user.getKeyPair().getPublic();
        if (publicKey instanceof BCECPublicKey) {
            // 将公钥转换为BCECPublicKey来获取X和Y坐标
            ECPoint q = ((BCECPublicKey) publicKey).getQ();

            // 获取X和Y坐标，并转换为字节数组
            byte[] xBytes = q.getAffineXCoord().getEncoded();
            byte[] yBytes = q.getAffineYCoord().getEncoded();

            // 将X和Y字节数组转换为16进制字符串
            String xHex = Hex.toHexString(xBytes);
            String yHex = Hex.toHexString(yBytes);

            // 拼接X和Y为一个16进制字符串
            String xyHex = xHex + yHex;

            System.out.println("KeyPairPublicKey (XY Hex): " + xyHex);
        }
        String xHex = "690d6c25feac6057623e53d4be93dc7ef59012d6d543d71fba7de912f6024a27";
        String yHex = "fad16f413917fd04859514776722931bf16398915f6c6fde68995d416ad56901";

        PublicKey publicKey2 = createPublicKeyFromXY(xHex, yHex);
        System.out.println("Generated PublicKey: " + publicKey2);
    }
    public static KeyPair getKeypairByPrivateKey(String privateKey) {
        CryptoKeyPair admin = cryptoSuite.createKeyPair(privateKey);
        return admin.getKeyPair();
    }
    public static KeyPair getKeypairByPublicKey(String publicKey) {
        return null;
    }
    public static KeyPair generateSM2KeyPair() throws Exception {
        CryptoSuite cryptoSuite = new CryptoSuite(1);
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        if (cryptoKeyPair != null) {
            return cryptoKeyPair.getKeyPair();
        }
        return null;
    }

    public static byte[] sm2Encrypt(PublicKey publicKey, byte[] data) throws Exception {
        //获取SM2曲线参数
        //sm2p256v1 是中国国家标准的SM2椭圆曲线。该曲线基于256位的素数域，即 "p-256" 曲线。
        //在密码学中，椭圆曲线的参数决定了整个曲线的结构，这些参数包括曲线的方程形式、生成点（G）、阶（n）等等
        ECNamedCurveParameterSpec sm2Spec = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec("sm2p256v1");
        //构造椭圆曲线的域参数，ECDomainParameters是BouncyCastle中表示椭圆曲线域参数的类
        //sm2Spec.getCurve()：获取椭圆曲线的具体数学方程表示
        //sm2Spec.getG()：曲线上的生成点
        //sm2Spec.getN()：椭圆曲线的阶（即生成点重复操作的次数）
        ECDomainParameters domainParams = new ECDomainParameters(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN());
        //从公钥中提取椭圆曲线上的公钥点Q
        //BCECPublicKey是BouncyCastle中的一个类，用于表示椭圆曲线公钥。SM2的公钥在数学上是椭圆曲线上的一个点，称为Q点
        //在椭圆曲线密码学中，公钥Q是通过私钥和曲线上的生成点G进行点乘计算得到的，即:Q=d*G
        ECPoint q = ((BCECPublicKey) publicKey).getQ();
        //构造用于加密的公钥参数，包含椭圆曲线上的公钥点和曲线参数
        //ECPublicKeyParameters是BouncyCastle中表示椭圆曲线公钥的一个类。它将两个关键部分:q和domainParams组合到一起，表示完整的公钥信息。
        ECPublicKeyParameters pubKeyParameters = new ECPublicKeyParameters(q, domainParams);
        //加密的初始化，创建一个 SM2 加密引擎对象
        SM2Engine engine = new SM2Engine();
        engine.init(true, new ParametersWithRandom(pubKeyParameters, new SecureRandom())); //true是加密，false是解密
        //数据进行加密
        return engine.processBlock(data, 0, data.length);
    }

    public static byte[] sm2Decrypt(PrivateKey privateKey, byte[] encryptedData) throws Exception {
        ECNamedCurveParameterSpec sm2Spec = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECDomainParameters domainParams = new ECDomainParameters(sm2Spec.getCurve(), sm2Spec.getG(), sm2Spec.getN());
        //将通用的Java私钥对象转换为BouncyCastle特定的BCECPrivateKey类型
        BCECPrivateKey ecPrivateKey = (BCECPrivateKey) privateKey;
        ECPrivateKeyParameters privKeyParameters = new ECPrivateKeyParameters(ecPrivateKey.getD(), domainParams);
        SM2Engine engine = new SM2Engine();
        engine.init(false, privKeyParameters); // false表示解密模式， true表示加密模式
        return engine.processBlock(encryptedData, 0, encryptedData.length);
    }
}
