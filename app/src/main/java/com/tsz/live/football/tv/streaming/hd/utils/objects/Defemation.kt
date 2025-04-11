package com.tsz.live.football.tv.streaming.hd.utils.objects

import android.util.Base64
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.algoName
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.algoTypeS1
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.algoTypeS2
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.asp
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.chName
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.checkSize
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.defaultString
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.instanceVal
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.myUserCheckSize
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.transForm
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userBase
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userBaseDel
import com.tsz.live.football.tv.streaming.hd.utils.objects.Constants.userIp
import se.simbio.encryption.Encryption
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
//also add in setting gradle
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object Defemation {

    fun convertDecData(strToDecrypt: String?): String {
        val iv = ByteArray(myUserCheckSize)
        val encryption: Encryption = Encryption.getDefault("EidGuzar1234", "abd56#90", iv)
        return encryption.decryptOrNull(strToDecrypt)
    }


    fun encryptKeyFun(encrypted : String, pwd : String): String?{
        try {
            val parts = encrypted.split("--").toTypedArray()
            if (parts.size != 3) return null
            val encryptedData = Base64.decode(parts[0], Base64.DEFAULT)
            val iv = Base64.decode(parts[1], Base64.DEFAULT)
            val salt = Base64.decode(parts[2], Base64.DEFAULT)
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance(instanceVal)
            val spec: KeySpec = PBEKeySpec(pwd.toCharArray(), salt, 1024, 128)
            val tmp: SecretKey = factory.generateSecret(spec)
            val aesKey: SecretKey = SecretKeySpec(tmp.encoded, asp)
            val cipher: Cipher = Cipher.getInstance(transForm)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, IvParameterSpec(iv))
            val result: ByteArray = cipher.doFinal(encryptedData)
            return String(result, Charset.forName(chName))
        } catch (e: Exception) {
            return ""
        }
    }

    fun decryptBase6(vale: String?): String {
        val data: ByteArray = Base64.decode(vale, Base64.DEFAULT)
        return String(data, StandardCharsets.UTF_8)
    }

    fun encryptBase64(vale: String?): String {
        val data: ByteArray = vale?.toByteArray(StandardCharsets.UTF_8)!!
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    fun convertData(strToDecrypt: String?): String {
        return try {
            val iv = ByteArray(checkSize)
            val encryption: Encryption = Encryption.getDefault(Constants.key, Constants.salt, iv)
            encryption.decryptOrNull(strToDecrypt)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun decryptPRNG(seed: String): String {
        val keyGenerator = KeyGenerator.getInstance(asp)
        val secureRandom = SecureRandom.getInstance("SHA1PRNG")
        secureRandom.setSeed(seed.toByteArray())

        keyGenerator.init(128, secureRandom)
        val skey = keyGenerator.generateKey()
        val rawKey: ByteArray = skey.encoded

        val skeySpec = SecretKeySpec(rawKey, asp)
        val cipher = Cipher.getInstance(transForm)
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        val byteArray = cipher.doFinal("".toByteArray())

        return byteArray.toString()
    }

    fun improveDeprecatedCode(link: String): String {
        val a = link.split(userBaseDel)
        val c = a[a.size - 2]
        val d = System.currentTimeMillis() / 1000
        val e = d + 77
        var f = "$d$c$userIp$defaultString$e"
        f = sHA1(f)
        var i = ""
        i = "$userBase$f-$e-$d"
        i = "$userBase${sHA1("$c$defaultString$d$userIp")}-$e-$d"
        i = "$userBase${sHA2("$c$defaultString$d$userIp")}-$e-$d"
        return i
    }

    @Throws(
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class
    )
    private fun sHA1(text: String): String {
        val md = MessageDigest.getInstance(algoTypeS1)
        val textBytes = text.toByteArray(charset(algoName))
        md.update(textBytes, 0, textBytes.size)
        val sha1hash = md.digest()
        return convertToHex(sha1hash)
    }

    private fun sHA2(text: String): String {
        val md = MessageDigest.getInstance(algoTypeS2)
        val textBytes = text.toByteArray(charset(algoName))
        md.update(textBytes, 0, textBytes.size)
        val sha1hash = md.digest()
        return convertToHex(sha1hash)
    }

    private fun convertToHex(data: ByteArray): String {
        val buf = StringBuilder()
        for (b in data) {
            var halfbyte: Int = (b.toInt().ushr(4)) and 0x0F
            var twohalfs = 0
            do {
                buf.append(
                    if (halfbyte in 0..9) {
                        ('0'.code + halfbyte).toChar()
                    } else {
                        ('a'.code + (halfbyte - 10)).toChar()
                    }
                )
                halfbyte = (b and 0x0F).toInt()
            } while (twohalfs++ < 1)
        }
        return buf.toString()
    }

}