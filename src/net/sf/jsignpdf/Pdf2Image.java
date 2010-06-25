package net.sf.jsignpdf;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for converting a page in PDF to a {@link BufferedImage} object.
 * 
 * @author Josef Cacek
 */
public class Pdf2Image {

	private final Logger logger = LoggerFactory.getLogger(Pdf2Image.class);

	private BasicSignerOptions options;

	/**
	 * Constructor - gets an options object with configured input PDF and
	 * possibly deconding (owner) password.
	 * 
	 * @param anOpts
	 */
	public Pdf2Image(BasicSignerOptions anOpts) {
		if (anOpts == null)
			throw new IllegalArgumentException("Options have to be not-null");
		options = anOpts;
	}

	/**
	 * Returns an image preview of given page.
	 * 
	 * @param aPage
	 *            Page to preview (counted from 1)
	 * @return image or null if error occures.
	 */
	public BufferedImage getImageForPage(int aPage) {
		logger.debug("Trying to get image for page {}", aPage);
		BufferedImage tmpResult = null;
		PDDocument tmpDoc = null;
		try {
			tmpDoc = PDDocument.load(options.getInFile());
			if (tmpDoc.isEncrypted()) {
				tmpDoc.decrypt(options.getPdfOwnerPwdStr());
			}
			int resolution;
			try {
				resolution = Toolkit.getDefaultToolkit().getScreenResolution();
			} catch (HeadlessException e) {
				resolution = 96;
			}

			PDPage page = (PDPage) tmpDoc.getDocumentCatalog().getAllPages().get(aPage - 1);
			tmpResult = page.convertToImage(BufferedImage.TYPE_INT_RGB, resolution);
		} catch (Exception e) {
			logger.warn("Conversion of PDF page to image failed.", e);
		} finally {
			if (tmpDoc != null) {
				try {
					tmpDoc.close();
				} catch (Exception e) {
					logger.warn("Can't close document correctly.", e);
				}
			}
		}
		return tmpResult;
	}

}