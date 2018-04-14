package ssl

import java.io.{ByteArrayOutputStream, InputStream}
import java.security.{KeyFactory, KeyStore, SecureRandom}
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

import javax.net.ssl.{KeyManagerFactory, SSLContext, SSLEngine}
import play.server.api.SSLEngineProvider

class MySSLEngineProvider extends SSLEngineProvider{
  override def createSSLEngine(): SSLEngine = {
    // FIXME how would end users do this? TestUtils.loadCert? issue #89
    val keyEncoded = read(this.getClass.getResourceAsStream("/certs/server1.key"))
      .replace("-----BEGIN PRIVATE KEY-----\n", "")
      .replace("-----END PRIVATE KEY-----\n", "")
      .replace("\n", "")

    val decodedKey = Base64.getDecoder.decode(keyEncoded)

    val spec = new PKCS8EncodedKeySpec(decodedKey)

    val kf = KeyFactory.getInstance("RSA")
    val privateKey = kf.generatePrivate(spec)

    val fact = CertificateFactory.getInstance("X.509")
    //    val is = new FileInputStream(TestUtils.loadCert("server1.pem"))
    val cer = fact.generateCertificate(this.getClass.getResourceAsStream("/certs/server1.pem"))

    val ks = KeyStore.getInstance("PKCS12")
    ks.load(null)
    ks.setKeyEntry("private", privateKey, Array.empty, Array(cer))

    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(ks, null)

    val context = SSLContext.getInstance("TLS")
    context.init(keyManagerFactory.getKeyManagers, null, new SecureRandom)

    context.createSSLEngine()
  }

  private def read(in: InputStream): String = {
    val bytes: Array[Byte] = {
      val baos = new ByteArrayOutputStream(math.max(64, in.available()))
      val buffer = Array.ofDim[Byte](32 * 1024)

      var bytesRead = in.read(buffer)
      while (bytesRead >= 0) {
        baos.write(buffer, 0, bytesRead)
        bytesRead = in.read(buffer)
      }
      baos.toByteArray
    }
    new String(bytes, "UTF-8")
  }
}
