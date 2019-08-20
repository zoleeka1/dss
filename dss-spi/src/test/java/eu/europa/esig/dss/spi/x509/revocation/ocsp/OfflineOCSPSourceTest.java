/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.spi.x509.revocation.ocsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.junit.Test;

import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.ExternalResourcesOCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import eu.europa.esig.dss.utils.Utils;

public class OfflineOCSPSourceTest {

	@Test
	public void testOCSPUniversign() {

		String ocspResponse = "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";

		ExternalResourcesOCSPSource ocspSource = new ExternalResourcesOCSPSource(new ByteArrayInputStream(Utils.fromBase64(ocspResponse)));

		CertificateToken userUniversign = DSSUtils.loadCertificateFromBase64EncodedString(
				"MIIEjjCCA3agAwIBAgIQfCokxd5pp8RnZZOUHNbq0zANBgkqhkiG9w0BAQsFADBuMQswCQYDVQQGEwJGUjEgMB4GA1UEChMXQ3J5cHRvbG9nIEludGVybmF0aW9uYWwxHDAaBgNVBAsTEzAwMDIgNDM5MTI5MTY0MDAwMjYxHzAdBgNVBAMTFlVuaXZlcnNpZ24gQ0EgaGFyZHdhcmUwHhcNMTIwNzA2MTkzNzI0WhcNMTcwNzA2MTkzNzI0WjBrMQswCQYDVQQGEwJGUjERMA8GA1UEBxMITkFOVEVSUkUxFzAVBgNVBAoTDkFYQSBGUkFOQ0UgVklFMRcwFQYDVQQLEw4wMDAyIDMxMDQ5OTk1OTEXMBUGA1UEAxMOQVhBIEZSQU5DRSBWSUUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDPbheD3Lk34Rv+SBwS3duDqzKNEHoNen48pzVbt6wFQf9bqpE9YPZh+jb+bnRUXLbv77vaWBfCZGOfrybDv/mqpmTkdYk6F+D7ddwQuF7OVmE+VqeOgoBc+Vo1LDzjowDPOmyH938DB1ZUzI8r2mw60rR9laqLI7kO6OT/9fodJMz1eE2G6LEzXvOj+V0PhDg3FX+vF+pLFUmI53x3L+zM8AZofUCY0oaUpY1hgWR4on5K1Oy0me63UHkslEjFS5jYff4EE+z/eZY7+e3FMqwzFm5al60w1djzsHUVod+CkIASs71VgAYSYOQyckwmKaFs6325JHAu5DH5a62WVqLnAgMBAAGjggEpMIIBJTA7BggrBgEFBQcBAQQvMC0wKwYIKwYBBQUHMAGGH2h0dHA6Ly9zY2FoLm9jc3AudW5pdmVyc2lnbi5ldS8wQgYDVR0gBDswOTA3BgsrBgEEAftLBQEDAjAoMCYGCCsGAQUFBwIBFhpodHRwOi8vZG9jcy51bml2ZXJzaWduLmV1LzBEBgNVHR8EPTA7MDmgN6A1hjNodHRwOi8vY3JsLnVuaXZlcnNpZ24uZXUvdW5pdmVyc2lnbl9jYV9oYXJkd2FyZS5jcmwwDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBkAwHQYDVR0OBBYEFK13mc0NTbq7YVWmGWisLz2JJu57MB8GA1UdIwQYMBaAFGDkMN3uetTQfl0l2f07eyFkT927MA0GCSqGSIb3DQEBCwUAA4IBAQA7DWIeC3qTgW+OOsoYnzuwZxdu+F9eiqiVhq2UXx1vxjJQ6hthfq1Thzj5050fn5GQ/HeSNl05+hfoDpAK0JVWLssq1rrvBAx2lfgNWTG+LF581DVtH1I3NLi+A9YslvCPt51NVGAERhye6BZyugDlQCVhy6dRFhqSDSbi+S7RRqpIl/QDR/pBOwnBePO6qSpwSrDsCJT+N9nFBHmXpRsyFJyPEFZMIAVoluuJq4mCEGLVtqiC4DzvAwCsFBKlnwQ7pSFHO9ztXMYHpYhD/0wDSegwcvAVm7p8/N0PQDaAZQlWXs7McCBHeQPjxVD2xAkk7s9joicJ6kKttfxfEc6w");
		CertificateToken caUniversign = DSSUtils.loadCertificateFromBase64EncodedString(
				"MIIEYTCCA0mgAwIBAgIQIVWN4tmvyrr2CIjMBjGC1zANBgkqhkiG9w0BAQsFADB2MQswCQYDVQQGEwJGUjEgMB4GA1UEChMXQ3J5cHRvbG9nIEludGVybmF0aW9uYWwxHDAaBgNVBAsTEzAwMDIgNDM5MTI5MTY0MDAwMjYxJzAlBgNVBAMTHlVuaXZlcnNpZ24gUHJpbWFyeSBDQSBoYXJkd2FyZTAeFw0xMjA2MTUxMjU2MjVaFw0yMjA2MTUxMjU2MjVaMG4xCzAJBgNVBAYTAkZSMSAwHgYDVQQKExdDcnlwdG9sb2cgSW50ZXJuYXRpb25hbDEcMBoGA1UECxMTMDAwMiA0MzkxMjkxNjQwMDAyNjEfMB0GA1UEAxMWVW5pdmVyc2lnbiBDQSBoYXJkd2FyZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKHfv76grtwK9PRFTBq4BDtmmLaaHDAqkj1wd0lHIN2QH1jM6hKWeS4U/wy8QuYtvw0aFxYxiMWzS/vrj0Sczv5hAt8reE1Eg1uQcx6+aSUBqJ6a+1TbM7PtkHg1ozgbmVXGuiOLyviLhhUo8XmeeGEhux+cyRNiYCs37VPN5OVrKks29dspMkvllkexfrxiPfc+gB58EU+iNEcip/YrZLu4ZqErlCePIVBLyX9skb7QwKXDW8XBIgAzpoBj0U9/4Vxaiyj209xT1Uuz2vKsuT8Hq7I1vt7miYviHg/ovsWXH6yGcNomxX55l0qWQ4z+mGlVhCLDPMKmspY5D+1kSqsCAwEAAaOB8jCB7zA7BgNVHSAENDAyMDAGBFUdIAAwKDAmBggrBgEFBQcCARYaaHR0cDovL2RvY3MudW5pdmVyc2lnbi5ldS8wEgYDVR0TAQH/BAgwBgEB/wIBADBMBgNVHR8ERTBDMEGgP6A9hjtodHRwOi8vY3JsLnVuaXZlcnNpZ24uZXUvdW5pdmVyc2lnbl9wcmltYXJ5X2NhX2hhcmR3YXJlLmNybDAOBgNVHQ8BAf8EBAMCAQYwHQYDVR0OBBYEFGDkMN3uetTQfl0l2f07eyFkT927MB8GA1UdIwQYMBaAFE3Z/Kgtx8hapK1fSa5opNyeihIiMA0GCSqGSIb3DQEBCwUAA4IBAQAQrQJxwn+DBwN+KTt75IuOkaPOnZ+FfmF/1V7zDt3YNz7n1hRXlflbx9wBJn30TwyuTuZ/Cq1gEiA+TJMrsdZPKvagY8a/q7oCm6jYw6cBhopErwV/sZ9R3Ic+fphKSxoEnygpZs4uKMU2bB7nbul+sdJkP/OrIHKfHydMk3ayeAxnnieOj8EU+Z5w3fpekOGOtb4VUTESWU/xQfDZcNaauNRU2DYFi1eDypfVnyD8tORDoFVaAqzdIJuMJ06jJB5fnmNBXU7GOQFLcdK7hy3ZDmPNh5FNGnaQRrlY2st7lXfV3mu9AgHmjPjxrbAwgo1GzLRY0byI9bfitN0sLT+d");
		OCSPToken ocspToken = ocspSource.getRevocationToken(userUniversign, caUniversign);

		assertNotNull(ocspToken);
		assertNotNull(ocspToken.getArchiveCutOff());
		assertNotNull(ocspToken.getThisUpdate());
		assertNotNull(ocspToken.getNextUpdate());
		assertNotNull(ocspToken.getProductionDate());
		assertNotNull(ocspToken.getBasicOCSPResp());
		assertNotNull(ocspToken.getCertId());
		assertNull(ocspToken.getExpiredCertsOnCRL());
		assertTrue(ocspToken.getStatus());
		assertFalse(ocspToken.isCertHashPresent());
		assertFalse(ocspToken.isCertHashMatch());
	}

	@Test
	public void testOCSP() {

		String ocspResponse = "MIIFmgoBAKCCBZMwggWPBgkrBgEFBQcwAQEEggWAMIIFfDCBk6IWBBRG9FLh93YFoTCS3jTUkgpprd/GtRgPMjAxODAyMTMwNjMyNDhaMFMwUTA8MAkGBSsOAwIaBQAEFMzG72J5OVL0/31Lq6L3ZXYqftwTBBQ0Fhvx02RnYkyjNLwNs1OkfKHxFwIDC0wwggAYDzIwMTgwMjEzMDYzMjQ4WqETMBEwDwYJKwYBBQUHMAEJBAIFADANBgkqhkiG9w0BAQsFAAOCAQEAduyhdmJ1pxB6Y9wavgysICD2UjkusDLDNnj2xlNuYHK+c5OVdQ3XKxyUgdIVqNxxudVAmwtuX+vKFk7xjE16A27agZU2KO4llwpAVKrbYsrWMBj3FS/WJzTBk0G+SXKuSgDt5UkwfcZiUxmoMQBntWJTmVon0Ji2VJMALd0HzzEaVp47qqYGYtJX6L5HjvZIC7r/G5C3lEddBgmfNbSPei3EuBVRlLn8LZNLMk+49P7jNMHmlYt3InfSKwEiU18R1WGgQ5yrkyYxyMfhhBrjYZpptT0zs8AuwdAZb7xA4P7rrHgc6IMhQWXmRaKUWoJdOInKt1N59Hyl+mjk36kFQ6CCA84wggPKMIIDxjCCAq6gAwIBAgICHR0wDQYJKoZIhvcNAQELBQAwRDELMAkGA1UEBhMCTFUxFjAUBgNVBAoTDUx1eFRydXN0IHMuYS4xHTAbBgNVBAMTFEx1eFRydXN0IEdsb2JhbCBSb290MB4XDTE3MDYyNzEyMzMxNloXDTE4MDYyNzEyMzMxNlowXjELMAkGA1UEBhMCTFUxFjAUBgNVBAoTDUx1eFRydXN0IFMuQS4xEzARBgNVBAsTClBraSBlbnRpdHkxIjAgBgNVBAMTGUx1eFRydXN0IFMuQS4gT0NTUCBTZXJ2ZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDSFDnPz4sWi+w+xMVUWq74fDelftPUqXt+bofmeIgKKC9pZ4FNrT6FCd/N7kpRjRbtKkfBUMiv88jwUFcHMC75EESf0OPeqBlubRkeSwTj60FqB8bBK2aIauGQMFSSLF2emfVvBkpw/qjR9/0bxd1R4FQ7rcl4CyrRGd/jfca6vTAi+rqFI5bOaxKT0MUHVtegJtshmj8zHlOBpm+BG2udKcpx79KIHVVKEb2AyFYdZefXV65FkRLri7l7ew08UYHV90Dq1hI/uAc8IHauSKkGE0arCRwNcLrsfVUPoh41HK3pqs7bTFJ2qmZu2mobAdBqDJBnfTxN+oHZ4whVNh5RAgMBAAGjgacwgaQwCQYDVR0TBAIwADAOBgNVHQ8BAf8EBAMCB4AwEwYDVR0lBAwwCgYIKwYBBQUHAwkwHwYDVR0jBBgwFoAUFxWFiQkvJIdvPx0b5PKWeYNIE84wMgYDVR0fBCswKTAnoCWgI4YhaHR0cDovL2NybC5sdXh0cnVzdC5sdS9MVEdSQ0EuY3JsMB0GA1UdDgQWBBRG9FLh93YFoTCS3jTUkgpprd/GtTANBgkqhkiG9w0BAQsFAAOCAQEANR/sq+gfaAtWqUXTbNFkisEan6abNbv9C8Pgt79rlDBkhAOOksyi/9tcfEZd5XqsMobIdaRZzAOlyN5SP5MgyZxSWmNArcqImofpg5RiIzbwewZzTIGaKh/xRrl8LkABKEhJ+tvTwrw0de2ad5nMUy2t2hCdO7Y5dMGzQRdJVnu10LcziEgZSW98C2SfdV3JxkW0rTsSi1vpRmSicpVxeEfbMvnlDKaMF17uM/eeTjqq7hsQOUbXPPbDOM8LcH0UmnkXkW6FVN4QDKjuKDlaL9bsRHJCuikebzwG+FOmKXhpHxMaUWLrRMvbeBIJvmd64LURIdhdjOt+Iho1lI4Oaw==";

		ExternalResourcesOCSPSource ocspSource = new ExternalResourcesOCSPSource(new ByteArrayInputStream(Utils.fromBase64(ocspResponse)));

		CertificateToken userUniversign = DSSUtils.loadCertificateFromBase64EncodedString(
				"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");
		CertificateToken caUniversign = DSSUtils.loadCertificateFromBase64EncodedString(
				"MIID5zCCAs+gAwIBAgICDJcwDQYJKoZIhvcNAQELBQAwRDELMAkGA1UEBhMCTFUxFjAUBgNVBAoTDUx1eFRydXN0IHMuYS4xHTAbBgNVBAMTFEx1eFRydXN0IEdsb2JhbCBSb290MB4XDTExMTEyMjA5MzQyNFoXDTE3MTEyMjA5MzQyNFowTDELMAkGA1UEBhMCTFUxFjAUBgNVBAoTDUx1eFRydXN0IFMuQS4xJTAjBgNVBAMTHEx1eFRydXN0IEdsb2JhbCBRdWFsaWZpZWQgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCo0Cl9yek22xZb2PNZN0gIgx26qgQChKJ3az/r+qhAv/d/jVBLZy5mQvKRQFrvnLZp5gY5RzrgIcMyZxCwCitQ+MyLPiUYZ4mg/lUrHfOfhWRjYaJY4Dz+M69icdzAsKuVrfsurJ/UEmaIkGvgjBzeCd0qd8BXYnnt6GAT9bAsJ7Vh8lDyTho4D1mbude6jmupBTRNqW1TqnuzeJcooI8JAWkWZ0X4gGXzG0iZlC4irIlB/aaQiJoo+8QVr511T+zAJSJv2CpPS68dpL7AZBTw7/fhsyF84HcBf4tCH7Qhl9zFrdaWGdqrZiebSM4SeTBAnuuKUdYuuZyjoAHFnjj3AgMBAAGjgdowgdcwDwYDVR0TBAgwBgEB/wIBADBDBgNVHSAEPDA6MDgGCCuBKwEBAQoDMCwwKgYIKwYBBQUHAgEWHmh0dHBzOi8vcmVwb3NpdG9yeS5sdXh0cnVzdC5sdTALBgNVHQ8EBAMCAQYwHwYDVR0jBBgwFoAUFxWFiQkvJIdvPx0b5PKWeYNIE84wMgYDVR0fBCswKTAnoCWgI4YhaHR0cDovL2NybC5sdXh0cnVzdC5sdS9MVEdSQ0EuY3JsMB0GA1UdDgQWBBQ0Fhvx02RnYkyjNLwNs1OkfKHxFzANBgkqhkiG9w0BAQsFAAOCAQEAGPCWTo1SQy6Kmpwin+GaWVW9h2JO3Qwr6qVwZAQHXPhZGQTD+ghIxN949SGyoMmkGTvJjOSaIs8qJ5lGtDWJuy056rN5FCc2P+0qYofpGdNgSkyRY3xtuKteEPcoi+eFBphY+dlilSPXm3j6Vp/6BRb70Zd35O1Zic95ZUNCxDDVkaDbuN9tjGIBKhnq/ExtpheEdZJPjppVXpsBrCUUpJ0A9oSSFOQewwKSMD/vJKF32M9A2TlRd62XdHeNJrahdU/tO55f9tz3ekl/aKoS2khJxMfBvdvlw2Yc2g02g5rYsBxrSphph6IjqIkALM95jbm9T2Xo2CSROTAtprdhKw==");
		OCSPToken ocspToken = ocspSource.getRevocationToken(userUniversign, caUniversign);

		assertNotNull(ocspToken);
		assertNull(ocspToken.getArchiveCutOff());
		assertNotNull(ocspToken.getThisUpdate());
		assertNotNull(ocspToken.getProductionDate());
		assertNotNull(ocspToken.getBasicOCSPResp());
		assertNotNull(ocspToken.getCertId());
		assertNull(ocspToken.getExpiredCertsOnCRL());
		assertFalse(ocspToken.isCertHashPresent());
		assertNotNull(ocspToken.getReason());
	}

	@Test
	public void testOCSPCertHash() {

		CertificateToken user = DSSUtils.loadCertificate(new File("src/test/resources/sk_user.cer"));
		CertificateToken caToken = DSSUtils.loadCertificate(new File("src/test/resources/sk_ca.cer"));
		assertTrue(user.isSignedBy(caToken));

		OCSPSource ocspSource = new ExternalResourcesOCSPSource("/sk_ocsp.bin");

		OCSPToken ocspToken = ocspSource.getRevocationToken(user, caToken);

		assertNotNull(ocspToken);
		assertNotNull(ocspToken.getRevocationDate());

		assertTrue(ocspToken.isCertHashPresent());
		assertTrue(ocspToken.isCertHashMatch());
	}

	@Test
	public void pss() {
		String user = "MIIIujCCBnKgAwIBAgIQMvhgOMxbzNo2TwepM/QLKTA9BgkqhkiG9w0BAQowMKANMAsGCWCGSAFlAwQCA6EaMBgGCSqGSIb3DQEBCDALBglghkgBZQMEAgOiAwIBQDBbMQswCQYDVQQGEwJERTEVMBMGA1UEChMMRC1UcnVzdCBHbWJIMRwwGgYDVQQDExNELVRSVVNUIENBIDMtMSAyMDE2MRcwFQYDVQRhEw5OVFJERS1IUkI3NDM0NjAeFw0xOTAxMTcwNjE3NDRaFw0yMjAxMTcwMDAwMDBaMGoxCzAJBgNVBAYTAkRFMRgwFgYDVQQDEw9NYXR0aGlhcyBIZWxsZXIxETAPBgNVBCoTCE1hdHRoaWFzMQ8wDQYDVQQEEwZIZWxsZXIxHTAbBgNVBAUTFERUUldNNjQ1NzcxNDE5NDQyMzIyMIIBpDANBgkqhkiG9w0BAQEFAAOCAZEAMIIBjAKCAYEAlM6XiJL+ARZpciuaIm1i4yBpg4kF9FZY1RWZKJz1l8fp+O4DUVdE/FlHzCBufxER5AmxqXA8dbiZo83ODYocAGGOu1Ettnr519Q25KGGeqOW6P9AIIn27HEincF5kX81zP3AMbihKsjrW3+4jiYlC3goiDQ1D/6Ete4NNnUU/t686fmXASjCFFf/fLz9K63nU08TNcIRHQVvjPzNUcVhRObxFrdE3KeQ36Kjy1Gab7zCyfUxpUDj4b3zo0a6cSAHOYDpslbX3CPh3Gdogb5scg+O5JZC90CKjP2XI6k/yzhhfytySaAEcf7wUjs7hHEijsQdhpMdB3AZ+1OeAdgtgn/P1TWrSbATxrXtHG1XHIY8p1e6xCILmYwQovVnKbgMuA2uTmPdlCYuqdClkajfjlseKSosHyHx7u2G+TgV1V5yfXUlqhlkFkVncqhrJg4buLJl4teg2bZRWtickRwUYW+Q4k1TsR9DFofsYap1sl+Jy9zOJuPUsAbVT5pTiaGBAgUAxcotnaOCA4cwggODMB8GA1UdIwQYMBaAFPvt361L8CW10nrdn5odL20JUCHHMIHNBggrBgEFBQcBAwSBwDCBvTAIBgYEAI5GAQEwCAYGBACORgEEMIGRBgYEAI5GAQUwgYYwQRY7aHR0cDovL3d3dy5kLXRydXN0Lm5ldC9pbnRlcm5ldC9maWxlcy9ELVRSVVNUX1BLSV9EU19ERS5wZGYTAkRFMEEWO2h0dHA6Ly93d3cuZC10cnVzdC5uZXQvaW50ZXJuZXQvZmlsZXMvRC1UUlVTVF9QS0lfRFNfRU4ucGRmEwJFTjATBgYEAI5GAQYwCQYHBACORgEGATCB+gYIKwYBBQUHAQEEge0wgeowMwYIKwYBBQUHMAGGJ2h0dHA6Ly9kdHItY2EtMy0xLTIwMTYub2NzcC5kLXRydXN0Lm5ldDBCBggrBgEFBQcwAoY2aHR0cDovL3d3dy5kLXRydXN0Lm5ldC9jZ2ktYmluL0QtVFJVU1RfQ0FfMy0xXzIwMTYuY3J0MG8GCCsGAQUFBzAChmNsZGFwOi8vZGlyZWN0b3J5LmQtdHJ1c3QubmV0L0NOPUQtVFJVU1QlMjBDQSUyMDMtMSUyMDIwMTYsTz1ELVRydXN0JTIwR21iSCxDPURFP2NBQ2VydGlmaWNhdGU/YmFzZT8wcQYDVR0gBGowaDAJBgcEAIvsQAECMFsGCysGAQQBpTQCgRYBMEwwSgYIKwYBBQUHAgEWPmh0dHA6Ly93d3cuZC10cnVzdC5uZXQvaW50ZXJuZXQvZmlsZXMvRC1UUlVTVF9Sb290X1BLSV9DUFMucGRmMIHwBgNVHR8EgegwgeUwgeKggd+ggdyGaWxkYXA6Ly9kaXJlY3RvcnkuZC10cnVzdC5uZXQvQ049RC1UUlVTVCUyMENBJTIwMy0xJTIwMjAxNixPPUQtVHJ1c3QlMjBHbWJILEM9REU/Y2VydGlmaWNhdGVyZXZvY2F0aW9ubGlzdIYyaHR0cDovL2NybC5kLXRydXN0Lm5ldC9jcmwvZC10cnVzdF9jYV8zLTFfMjAxNi5jcmyGO2h0dHA6Ly9jZG4uZC10cnVzdC1jbG91ZGNybC5uZXQvY3JsL2QtdHJ1c3RfY2FfMy0xXzIwMTYuY3JsMB0GA1UdDgQWBBQ4aQJEqXGUOav7kBJgEC2BFM8PPjAOBgNVHQ8BAf8EBAMCBkAwPQYJKoZIhvcNAQEKMDCgDTALBglghkgBZQMEAgOhGjAYBgkqhkiG9w0BAQgwCwYJYIZIAWUDBAIDogMCAUADggIBAIxbA2SgrJ0PFUhD6Dw3LTqLPGbCEnJty67qa3tVDY670MHenvI5k+55cn+mZp07+5IDo8b67F00qoSxb++zTLBIWUxMAlV1frjSJ5hmK3WWAoQFIkvgBCVTBmoJTtf4cA1yI0XXBRrrjd4yokcZAyOgd84y25K+jEcta1+hZ+4QKwfFd69YJbUT50brD8aFvPgsHGxm9noNX1+nyeA7EzJMaqQq3uwB88FfQSH2mdXgtyUqh/pA7XODY+ZCPORf1gVZYlu9K3s8xokQ/6oBPp+MPlHcgsxyufrLM7ZHu5Ko7USpkuetHgg3jIsETght2s7ypcwPjukt6aL1GrGxUBXwd/GO8elr6d5Th0YBcYLDCCsLs84pagNrbu3E22tx7/ZEqVNLrDvyIGG+aFQvygfZxVxD7uVEl3dGZKcuZih4Pr+iaBQv1ptjQovF9xkhTh6r3CPftApxZdwb0wH1ag4dIanTZN9RWQWnsHVkeDC7OixdEaPl+ZLVwigdJ+0xuNKV1ZW1DAtgcv8fiL2ItlW6xlEFubfD5Ql1CpGdiYtgMU4PqkK4z98VHq7XYTwmTxbbiQ5aJOOPrawysj55Lei6zXDmFwiwVS9lJV22y1BsUw7U/0tWAVTVkYkX0a/OevKeYLnIOVD57L+1ToaDRgocV0Mml1hfwfXem0GCSM7g";
		String ca = "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";
		String ocspResponse = "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";

		ExternalResourcesOCSPSource ocspSource = new ExternalResourcesOCSPSource(new ByteArrayInputStream(Utils.fromBase64(ocspResponse)));

		CertificateToken certUser = DSSUtils.loadCertificateFromBase64EncodedString(user);
		CertificateToken certCA = DSSUtils.loadCertificateFromBase64EncodedString(ca);
		OCSPToken ocspToken = ocspSource.getRevocationToken(certUser, certCA);
		ocspToken.initInfo();

		assertEquals(SignatureAlgorithm.RSA_SSA_PSS_SHA256_MGF1, ocspToken.getSignatureAlgorithm());
		assertTrue(ocspToken.isSignatureValid());
		assertTrue(ocspToken.isValid());
		assertTrue(ocspToken.isCertHashPresent());
		assertTrue(ocspToken.isCertHashMatch());
		assertFalse(ocspToken.isUseNonce());
		assertTrue(ocspToken.getStatus());
		assertNull(ocspToken.getReason());
		assertNull(ocspToken.getRevocationDate());
	}

}