package fiji.expressionparser.function;


import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.nfunk.jep.ParseException;

import fiji.expressionparser.ImgLibUtils;

public final class ImgLibGaussConv <T extends RealType<T>> extends TwoOperandsAbstractFunction<T> {

	public ImgLibGaussConv() {
		numberOfParameters = 2;
	}

	public String toString() {
		return "Gaussian convolution";
	}

	@Override
	public String getDocumentationString() {
		return "<h3>Gaussian convolution</h3> " +
				"This function implements the isotropic gaussian convolution, as coded " +
				"in ImgLib, effectively implementing a gaussian filter. " +
				"Syntax: " +
				"<br><code>" + getFunctionString() + "(A, sigma)</code><br> " +
				"with A an image and sigma a number. Sigma is the standard deviation " +
				"of the gaussian kernel applied to image A.<br> " +
				"Input image is converted to <i>FloatType</i> then convolved. " +
				"If the source image is a 3D image, the convolution will be made in 3D as well. ";				
	}
	
	@Override
	public String getFunctionString() {
		return "gauss";
	}

	@Override
	public final <R extends RealType<R>> Img<FloatType> evaluate(final Img<R> img, final R alpha) throws ParseException {
		RandomAccessibleInterval<FloatType> fimg = ImgLibUtils.copyToFloatTypeImage(img);
		Gauss3 gaussian_fiter = new Gauss3();
		try {
			Gauss3.gauss(alpha.getRealDouble(), fimg, fimg);
		}
		catch (IncompatibleTypeException e) {
			throw new RuntimeException(e);
		}
		return (Img<FloatType>)fimg;
	}

	@Override
	public final <R extends RealType<R>> float evaluate(final R t1, final R t2) throws ParseException{
			throw new ParseException("In function "+getFunctionString()
					+": Arguments must be one image and one number, got 2 numbers.");
	}

	@Override
	public final <R extends RealType<R>> Img<FloatType> evaluate(final Img<R> img1, final Img<R> img2) throws ParseException {
		throw new ParseException("In function "+getFunctionString()
				+": Arguments must be one image and one number, got 2 images.");
	}

	@Override
	public final <R extends RealType<R>> Img<FloatType> evaluate(final R alpha, Img<R> img) throws ParseException {
		throw new ParseException("In function "+getFunctionString()
			+": First argument must be one image and second one a number, in this order.");
	}

	
}
