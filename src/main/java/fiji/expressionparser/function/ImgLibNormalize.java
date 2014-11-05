package fiji.expressionparser.function;

import fiji.expressionparser.ImgLibUtils;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.nfunk.jep.ParseException;

public final class ImgLibNormalize <T extends RealType<T>> extends SingleOperandAbstractFunction<T> {

	public ImgLibNormalize() {
		numberOfParameters = 1;
	}
	
	@Override
	public final <R extends RealType<R>> Img<FloatType> evaluate(final Img<R> img) throws ParseException {
		Img<FloatType> fimg = ImgLibUtils.copyToFloatTypeImage(img);
		normalize(fimg);
		return fimg;
	}

	@Override
	public <R extends RealType<R>> Img<FloatType> evaluate(R alpha) throws ParseException {
		throw new ParseException("In function "+getFunctionString()
				+": Normalizing is not defined on scalars.");
	}

	@Override
	public String getDocumentationString() {
		return "<h3>Image normalization</h3> " +
		"This function normalizes its input, so that the sum of the output's pixel values " +
		"is equal to 1. " +
		"Syntax: " +
		"<br><code>" + getFunctionString() + "(A)</code><br> ";
	}

	@Override
	public String getFunctionString() {
		return "normalize";
	}

	private void normalize(Img<FloatType> fimg) {
		double total = 0;
		for (FloatType type : fimg) {
			total += type.getRealDouble();
		}
		if (total == 0 || total == 1) return;
		for (FloatType type : fimg) {
			type.setReal(type.getRealDouble() / total);
		}
	}

}
