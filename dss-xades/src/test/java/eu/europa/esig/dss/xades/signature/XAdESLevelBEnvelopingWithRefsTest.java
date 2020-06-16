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
package eu.europa.esig.dss.xades.signature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.Reference;
import org.junit.jupiter.api.BeforeEach;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestAlgoAndValue;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureScope;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import eu.europa.esig.dss.xades.reference.Base64Transform;
import eu.europa.esig.dss.xades.reference.DSSReference;
import eu.europa.esig.dss.xades.reference.DSSTransform;

public class XAdESLevelBEnvelopingWithRefsTest extends AbstractXAdESTestSignature {

	private DocumentSignatureService<XAdESSignatureParameters, XAdESTimestampParameters> service;
	private XAdESSignatureParameters signatureParameters;
	private DSSDocument documentToSign;
	
	private static DSSDocument doc1 = new FileDocument("src/test/resources/sample.xml");
	private static DSSDocument doc2 = new FileDocument("src/test/resources/sampleISO.xml");

	@BeforeEach
	public void init() throws Exception {
		
		documentToSign = new FileDocument("src/test/resources/empty.xml");

		signatureParameters = new XAdESSignatureParameters();
		signatureParameters.bLevel().setSigningDate(new Date());
		signatureParameters.setSigningCertificate(getSigningCert());
		signatureParameters.setCertificateChain(getCertificateChain());
		signatureParameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
		signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
		signatureParameters.setReferences(getReferences());

		service = new XAdESService(getOfflineCertificateVerifier());
	}
	
	private List<DSSReference> getReferences() {
		List<DSSReference> refs = new ArrayList<>();
		
		List<DSSTransform> transforms = new ArrayList<>();
		Base64Transform dssTransform = new Base64Transform();
		transforms.add(dssTransform);

		DSSReference ref1 = new DSSReference();
		ref1.setContents(doc1);
		ref1.setId("r-" + doc1.getName());
		ref1.setTransforms(transforms);
		ref1.setType(Reference.OBJECT_URI);
		ref1.setUri('#' + doc1.getName());
		ref1.setDigestMethodAlgorithm(DigestAlgorithm.SHA256);

		DSSReference ref2 = new DSSReference();
		ref2.setContents(doc2);
		ref2.setId("r-" + doc2.getName());
		ref2.setTransforms(transforms);
		ref2.setType(Reference.OBJECT_URI);
		ref2.setUri('#' + doc2.getName());
		ref2.setDigestMethodAlgorithm(DigestAlgorithm.SHA256);

		refs.add(ref1);
		refs.add(ref2);
		
		return refs;
	}
	
	@Override
	protected List<DSSDocument> getOriginalDocuments() {
		return Arrays.asList(doc1, doc2);
	}
	
	@Override
	protected void verifyOriginalDocuments(SignedDocumentValidator validator, DiagnosticData diagnosticData) {
		super.verifyOriginalDocuments(validator, diagnosticData);
		
		List<DSSDocument> originals = validator.getOriginalDocuments(diagnosticData.getFirstSignatureId());
		assertEquals(2, originals.size());

		DSSDocument orig1 = originals.get(0);
		DSSDocument orig2 = originals.get(1);

		try {
			Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS);
			String firstDocument = new String(canon.canonicalize(DSSUtils.toByteArray(doc1)));
			String secondDocument = new String(canon.canonicalize(DSSUtils.toByteArray(orig1)));
			assertEquals(firstDocument, secondDocument);
	
			firstDocument = new String(canon.canonicalize(DSSUtils.toByteArray(doc2)));
			secondDocument = new String(canon.canonicalize(DSSUtils.toByteArray(orig2)));
			assertEquals(firstDocument, secondDocument);
		} catch (Exception e) {
			fail(e);
		}
		
		assertEquals(doc1.getDigest(DigestAlgorithm.SHA256), orig1.getDigest(DigestAlgorithm.SHA256));
		assertEquals(doc2.getDigest(DigestAlgorithm.SHA256), orig2.getDigest(DigestAlgorithm.SHA256));
	}
	
	@Override
	protected void checkSignatureScopes(DiagnosticData diagnosticData) {
		super.checkSignatureScopes(diagnosticData);
		
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		List<XmlSignatureScope> signerData = signature.getSignatureScopes();
		assertNotNull(signerData);
		assertEquals(2, signerData.size());
		XmlDigestAlgoAndValue digestAlgoAndValueSignatureOne = signerData.get(0).getSignerData().getDigestAlgoAndValue();
		assertNotNull(digestAlgoAndValueSignatureOne);
		
		DigestAlgorithm digestAlgorithm = digestAlgoAndValueSignatureOne.getDigestMethod();
		assertTrue(Arrays.equals(digestAlgoAndValueSignatureOne.getDigestValue(), DSSUtils.digest(digestAlgorithm, doc1)));
		XmlDigestAlgoAndValue digestAlgoAndValueSignatureTwo = signerData.get(1).getSignerData().getDigestAlgoAndValue();
		assertNotNull(digestAlgoAndValueSignatureTwo);
		assertTrue(Arrays.equals(digestAlgoAndValueSignatureTwo.getDigestValue(), DSSUtils.digest(digestAlgorithm, doc2)));
	}

	@Override
	protected String getSigningAlias() {
		return GOOD_USER;
	}

	@Override
	protected DocumentSignatureService<XAdESSignatureParameters, XAdESTimestampParameters> getService() {
		return service;
	}

	@Override
	protected XAdESSignatureParameters getSignatureParameters() {
		return signatureParameters;
	}

	@Override
	protected DSSDocument getDocumentToSign() {
		return documentToSign;
	}
	
}