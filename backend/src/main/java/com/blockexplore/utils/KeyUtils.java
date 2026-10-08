package com.blockexplore.utils;
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

public class KeyUtils {
    // 测试用例
//    private static CryptoSuite cryptoSuite = new CryptoSuite(1);
//    private static CryptoKeyPair user;
//
//    // 静态代码块初始化 admin
//    static {
//        user = cryptoSuite.createKeyPair(EnvConfig.Admin_Private_Key);
//    }
//    public static void main(String[] args) throws Exception {
//        // 假设 user 是已经定义的对象，拥有 hex 格式的公钥和私钥
//        // 测试加密
//        String encrypt = encrypt(user.getHexPublicKey(), "hello world");
//        System.out.println("Encrypted Message: " + encrypt);
//
//        // 测试解密
//        String decrypt = decrypt(user.getHexPrivateKey(), encrypt);
//        System.out.println("Decrypted Message: " + decrypt);
//
//        // 打印用户的公钥和私钥
//        System.out.println("Hex Public Key: " + user.getHexPublicKey());
//        System.out.println("Hex Private Key: " + user.getHexPrivateKey());
//
//        // 打印从用户的十六进制公钥生成的公钥对象
//        PublicKey publicKeyFromHex = createPublicKeyFromXY(user.getHexPublicKey());
//        System.out.println("Generated Public Key from Hex: " + publicKeyFromHex);
//
//        // 打印从用户的十六进制私钥生成的私钥对象
//        PrivateKey privateKeyFromHex = createPrivateKeyFromHex(user.getHexPrivateKey());
//        System.out.println("Generated Private Key from Hex: " + privateKeyFromHex);
//
//        // 打印用户的 KeyPair 公钥和私钥
//        System.out.println("KeyPair Public Key: " + user.getKeyPair().getPublic());
//
//        // 确保 KeyPair 私钥输出私钥 D 值而不是 X 和 Y
//        PrivateKey privateKey = createPrivateKeyFromHex(user.getHexPrivateKey());
//        if (privateKey instanceof BCECPrivateKey) {
//            BCECPrivateKey ecPrivateKey = (BCECPrivateKey) privateKey;
//            System.out.println("KeyPair Private Key D: " + ecPrivateKey.getD().toString(16));
//        } else {
//            System.out.println("KeyPair Private Key: " + privateKey);
//        }
//    }


    public static String encrypt(String publicKey , String content) throws Exception {
        PublicKey enKey = createPublicKeyFromXY(publicKey);
        byte[] encryptedData = sm2Encrypt(enKey, content.getBytes());
        return Hex.toHexString(encryptedData); // 返回加密后的十六进制字符串
    }
    public static String decrypt(String privateKey, String encryptedHex) throws Exception {
        PrivateKey deKey = createPrivateKeyFromHex(privateKey);
        byte[] encryptedData = Hex.decode(encryptedHex); // 将十六进制加密数据转换为字节数组
        byte[] decryptedData = sm2Decrypt(deKey, encryptedData); // 调用解密方法
        return new String(decryptedData); // 返回解密后的明文
    }
    public static PrivateKey createPrivateKeyFromHex(String privateKeyHex) throws Exception {
        // 添加 BouncyCastleProvider，只需要添加一次，通常在应用启动时添加即可
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        // 将16进制私钥字符串转换为字节数组
        byte[] privateKeyBytes = Hex.decode(privateKeyHex);

        // 获取 SM2 的椭圆曲线参数
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");

        // 将私钥字节数组转换为大整数表示的私钥
        java.math.BigInteger privateKeyInt = new java.math.BigInteger(1, privateKeyBytes);

        // 使用椭圆曲线参数构造 ECPrivateKeySpec
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        java.security.spec.ECPrivateKeySpec priKeySpec = new java.security.spec.ECPrivateKeySpec(privateKeyInt,
                EC5Util.convertSpec(EC5Util.convertCurve(sm2Spec.getCurve(), sm2Spec.getSeed()), sm2Spec));

        // 生成私钥对象并返回
        return keyFactory.generatePrivate(priKeySpec);
    }

    public static PublicKey createPublicKeyFromXY(String publicKey) throws Exception {
        String xHex = publicKey.substring(0, 64);
        String yHex = publicKey.substring(64);
        Security.addProvider(new BouncyCastleProvider());
        byte[] xBytes = Hex.decode(xHex);
        byte[] yBytes = Hex.decode(yHex);
        ECNamedCurveParameterSpec sm2Spec = ECNamedCurveTable.getParameterSpec("sm2p256v1");
        ECPoint q = sm2Spec.getCurve().createPoint(new java.math.BigInteger(1, xBytes), new java.math.BigInteger(1, yBytes));
        EllipticCurve ellipticCurve = EC5Util.convertCurve(sm2Spec.getCurve(), sm2Spec.getSeed());
        ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, sm2Spec);
        java.security.spec.ECPoint ecPoint = new java.security.spec.ECPoint(q.getAffineXCoord().toBigInteger(), q.getAffineYCoord().toBigInteger());
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(ecPoint, ecParameterSpec);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        return keyFactory.generatePublic(pubKeySpec);
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
